package io.github.engagelab.nativecheck;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import io.github.engagelab.api.*;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

final class NativeSupport {
  static final ObjectMapper JSON = new ObjectMapper();

  static class Blocked extends RuntimeException {
    Blocked(String text) {
      super(text);
    }
  }

  static String need(Map<String, String> e, String key) {
    String value = e.get(key);
    if (value == null || value.trim().isEmpty()) throw new Blocked("Missing " + key);
    return value.trim();
  }

  static class Config {
    final Map<String, String> env;
    final String target, base, key, secret, mode, run;
    final double timeout, wait, interval;

    Config(Map<String, String> e) {
      env = new HashMap<>(e);
      target = NativeSupport.need(e, "TEST_ENV");
      base = NativeSupport.need(e, "ENGAGELAB_BASE_URL").replaceAll("/+$", "");
      URI u;
      try {
        u = URI.create(base);
      } catch (Exception ex) {
        throw new Blocked("Invalid API origin");
      }
      String h = u.getHost();
      boolean qa = h != null && (h.endsWith(".qa.jpushoa.com") || h.endsWith(".txqa.jpushoa.com"));
      if ((!target.equals("qa") && !target.equals("prod"))
          || h == null
          || u.getUserInfo() != null
          || !(u.getScheme().equals("http") || u.getScheme().equals("https"))
          || !u.getPath().isEmpty()
          || u.getQuery() != null
          || u.getFragment() != null
          || target.equals("qa") != qa
          || (target.equals("prod")
              && (!u.getScheme().equals("https")
                  || Arrays.asList("localhost", "127.0.0.1", "[::1]").contains(h))))
        throw new Blocked("TEST_ENV and API origin mismatch");
      key = NativeSupport.need(e, "ENGAGELAB_APP_KEY");
      secret = NativeSupport.need(e, "ENGAGELAB_MASTER_SECRET");
      mode = e.getOrDefault("ENGAGELAB_MUTATION_MODE", "read_only");
      if (!Arrays.asList("read_only", "isolated_write").contains(mode))
        throw new Blocked("Invalid mutation mode");
      timeout = number(e, "ENGAGELAB_HTTP_TIMEOUT", "25");
      wait = number(e, "ENGAGELAB_WAIT_TIMEOUT", "30");
      interval = number(e, "ENGAGELAB_REQUEST_INTERVAL", "1.1");
      String prefix = e.getOrDefault("ENGAGELAB_RESOURCE_PREFIX", "sdknative");
      if (!prefix.matches("[A-Za-z][A-Za-z0-9_]{0,15}"))
        throw new Blocked("Invalid resource prefix");
      run = prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    static double number(Map<String, String> e, String key, String fallback) {
      try {
        double v = Double.parseDouble(e.getOrDefault(key, fallback));
        if (!Double.isFinite(v) || v <= 0 || v > 86400) throw new NumberFormatException();
        return v;
      } catch (NumberFormatException ex) {
        throw new Blocked("Invalid duration " + key);
      }
    }

    void write() {
      if (!mode.equals("isolated_write")) throw new Blocked("Requires isolated_write");
    }

    String need(String k) {
      return NativeSupport.need(env, k);
    }
  }

  static class Wire implements Client {
    final Config c;
    final Client delegate;
    JsonNode last, sent;
    int status;
    final List<String> checks = new ArrayList<>();

    Wire(Config c) {
      this.c = c;
      delegate =
          new feign.okhttp.OkHttpClient(
              new okhttp3.OkHttpClient.Builder()
                  .connectTimeout((long) (c.timeout * 1000), TimeUnit.MILLISECONDS)
                  .readTimeout((long) (c.timeout * 1000), TimeUnit.MILLISECONDS)
                  .callTimeout((long) (c.timeout * 1000), TimeUnit.MILLISECONDS)
                  .followRedirects(false)
                  .build());
    }

    public Response execute(Request req, Request.Options opts) throws IOException {
      pause();
      sent = req.body() == null ? null : JSON.readTree(req.body());
      Response r =
          delegate.execute(
              req,
              new Request.Options(
                  (long) (c.timeout * 1000),
                  TimeUnit.MILLISECONDS,
                  (long) (c.timeout * 1000),
                  TimeUnit.MILLISECONDS,
                  false));
      byte[] body = r.body() == null ? new byte[0] : Util.toByteArray(r.body().asInputStream());
      if (r.body() != null) r.body().close();
      status = r.status();
      last = body.length == 0 ? null : JSON.readTree(body);
      return r.toBuilder().body(body).build();
    }

    void pause() {
      try {
        Thread.sleep((long) (c.interval * 1000));
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Interrupted");
      }
    }

    JsonNode rest(String method, String path) throws Exception {
      pause();
      HttpURLConnection conn = (HttpURLConnection) new URL(c.base + path).openConnection();
      conn.setRequestMethod(method);
      conn.setInstanceFollowRedirects(false);
      conn.setConnectTimeout((int) (c.timeout * 1000));
      conn.setReadTimeout((int) (c.timeout * 1000));
      conn.setRequestProperty(
          "Authorization",
          "Basic "
              + Base64.getEncoder()
                  .encodeToString((c.key + ":" + c.secret).getBytes(StandardCharsets.UTF_8)));
      try {
        status = conn.getResponseCode();
        InputStream stream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        try (InputStream in = stream) {
          return in == null ? JSON.nullNode() : JSON.readTree(in);
        }
      } finally {
        conn.disconnect();
      }
    }

    interface Condition {
      boolean ready() throws Exception;
    }

    void until(Condition fn) throws Exception {
      long end = System.nanoTime() + (long) (c.wait * 1e9);
      while (!fn.ready()) {
        if (System.nanoTime() > end) fail("Readback deadline exceeded");
        pause();
      }
    }

    void ok(boolean condition, String label) {
      assertTrue(condition, label);
      checks.add(label);
    }

    DeviceApi device() {
      return new DeviceApi.Builder()
          .setHost(c.base)
          .setAppKey(c.key)
          .setMasterSecret(c.secret)
          .setClient(this)
          .setLoggerLevel(feign.Logger.Level.NONE)
          .build();
    }

    PushPlanApi plan() {
      return new PushPlanApi.Builder()
          .setHost(c.base)
          .setAppKey(c.key)
          .setMasterSecret(c.secret)
          .setClient(this)
          .setLoggerLevel(feign.Logger.Level.NONE)
          .build();
    }

    PushApi push() {
      return new PushApi.Builder()
          .setHost(c.base)
          .setAppKey(c.key)
          .setMasterSecret(c.secret)
          .setClient(this)
          .setLoggerLevel(feign.Logger.Level.NONE)
          .build();
    }
  }

  interface Action {
    void run(Wire w) throws Exception;
  }

  static void scenario(String name, Action action) throws Exception {
    Config c = null;
    Wire w = null;
    String status = "pass", reason = "";
    try {
      c = new Config(System.getenv());
      w = new Wire(c);
      action.run(w);
    } catch (Throwable error) {
      status = error instanceof Blocked ? "block" : "no_pass";
      reason =
          error instanceof Blocked || error instanceof AssertionError
              ? error.getMessage()
              : error.getClass().getSimpleName();
      throw new AssertionError(status + ": " + reason);
    } finally {
      Map<String, Object> row = new LinkedHashMap<>();
      row.put("run_id", c == null ? "configuration_blocked" : c.run);
      row.put("scenario_id", name);
      row.put("language", "Java");
      row.put("suite", "integration");
      row.put("environment", c == null ? "unresolved" : c.target);
      row.put("base_url", c == null ? "" : c.base);
      row.put("status", status);
      row.put("reason", reason);
      row.put("checks", w == null ? Collections.emptyList() : w.checks);
      Path dir = Paths.get(System.getenv().getOrDefault("ENGAGELAB_RESULTS_DIR", ".test-results"));
      Files.createDirectories(dir);
      JSON.writerWithDefaultPrettyPrinter().writeValue(dir.resolve(name + ".json").toFile(), row);
    }
  }

  static PushParam payload(String rid, String run) {
    NotificationMessage.Android a = new NotificationMessage.Android();
    a.setAlert("native SDK test");
    a.setBadgeSetNumber(0);
    a.setFold(false);
    NotificationMessage n = new NotificationMessage();
    n.setAndroid(a);
    Options o = new Options();
    o.setAutoTruncation(false);
    PushParam.Body b = new PushParam.Body();
    b.setPlatform(Collections.singletonList("android"));
    b.setNotification(n);
    b.setOptions(o);
    PushParam p = new PushParam();
    p.setRequestId(run);
    p.setTo(Collections.singletonMap("registration_id", Collections.singletonList(rid)));
    p.setBody(b);
    return p;
  }
}
