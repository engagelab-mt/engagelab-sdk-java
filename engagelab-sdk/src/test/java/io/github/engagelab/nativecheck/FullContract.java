package io.github.engagelab.nativecheck;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import io.github.engagelab.api.PushApi;
import io.github.engagelab.bean.push.*;
import io.github.engagelab.enums.DataCenterHost;
import io.github.engagelab.exception.ApiErrorException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import okhttp3.mockwebserver.*;

/** Native HTTP assertions; all inputs and expectations are compiled test code. */
final class FullContract {
  static class Spec {
    String id, label, op, args, response, body, expected, extra;
    int status;
    double timeout, delay;

    Spec(
        String id,
        String label,
        String op,
        String args,
        int status,
        String response,
        String body,
        String expected,
        String extra,
        double timeout,
        double delay) {
      this.id = id;
      this.label = label;
      this.op = op;
      this.args = args;
      this.status = status;
      this.response = response;
      this.body = body;
      this.expected = expected;
      this.extra = extra;
      this.timeout = timeout;
      this.delay = delay;
    }
  }

  static final ObjectMapper JSON = FullActions.JSON;

  static String key(String k) {
    k = k.toLowerCase().replaceAll("[^a-z0-9]", "");
    if (k.equals("targets")) return "target";
    if (k.equals("clicks")) return "click";
    return k;
  }

  static boolean contains(JsonNode actual, JsonNode expected) {
    if (expected == null) return true;
    if (actual == null) return false;
    if (expected.isObject()) {
      if (!actual.isObject()) return false;
      Map<String, JsonNode> vals = new HashMap<>();
      actual.fields().forEachRemaining(e -> vals.put(key(e.getKey()), e.getValue()));
      Iterator<Map.Entry<String, JsonNode>> entries = expected.fields();
      while (entries.hasNext()) {
        Map.Entry<String, JsonNode> e = entries.next();
        if (!vals.containsKey(key(e.getKey()))
            || !contains(vals.get(key(e.getKey())), e.getValue())) return false;
      }
      return true;
    }
    if (expected.isArray()) {
      if (!actual.isArray() || actual.size() != expected.size()) return false;
      for (int i = 0; i < expected.size(); i++)
        if (!contains(actual.get(i), expected.get(i))) return false;
      return true;
    }
    if (actual.isNumber() && expected.isNumber())
      return actual.decimalValue().compareTo(expected.decimalValue()) == 0;
    return actual.equals(expected);
  }

  static void save(String id, String layer, String status, String reason, List<String> checks)
      throws Exception {
    Path dir =
        Paths.get(
            System.getenv().getOrDefault("ENGAGELAB_FULL_RESULTS_DIR", ".test-results/full"),
            layer);
    Files.createDirectories(dir);
    Map<String, Object> row = new LinkedHashMap<>();
    row.put("scenario_id", id);
    row.put("language", "Java");
    row.put("layer", layer);
    row.put("status", status);
    row.put("reason", reason);
    row.put("checks", checks);
    JSON.writerWithDefaultPrettyPrinter().writeValue(dir.resolve(id + ".json").toFile(), row);
  }

  static void ok(boolean b, String label, List<String> checks) {
    assertTrue(b, label);
    checks.add(label);
  }

  static File wav() throws Exception {
    byte[] b = new byte[1644];
    java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN);
    bb.put("RIFF".getBytes(StandardCharsets.US_ASCII))
        .putInt(1636)
        .put("WAVEfmt ".getBytes(StandardCharsets.US_ASCII))
        .putInt(16)
        .putShort((short) 1)
        .putShort((short) 1)
        .putInt(8000)
        .putInt(16000)
        .putShort((short) 2)
        .putShort((short) 16)
        .put("data".getBytes(StandardCharsets.US_ASCII))
        .putInt(1600);
    File f = File.createTempFile("sdk-contract-", ".wav");
    Files.write(f.toPath(), b);
    return f;
  }

  static void run(Spec f) throws Exception {
    List<String> checks = new ArrayList<>();
    MockWebServer server = new MockWebServer();
    File voice = null;
    String verdict = "pass", reason = "";
    List<RecordedRequest> records = Collections.synchronizedList(new ArrayList<>());
    try {
      JsonNode reply = JSON.readTree(f.response);
      JsonNode extra = JSON.readTree(f.extra);
      ArrayNode args = (ArrayNode) JSON.readTree(f.args);
      if (extra.asText().equals("accessors")) {
        BatchPushResult.SinglePushResult r = new BatchPushResult.SinglePushResult();
        r.setMsg_id(123L);
        ok(r.getMsgId() == 123L, "old_setter_new_getter", checks);
        r.setMsgId(456L);
        ok(r.getMsg_id() == 456L, "new_setter_old_getter", checks);
        return;
      }
      server.setDispatcher(
          new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
              records.add(request);
              JsonNode r = reply;
              try {
                if (reply.isTextual() && reply.asText().equals("ECHO")) {
                  JsonNode p = JSON.readTree(request.getBody().clone().readUtf8());
                  ObjectNode v = JSON.createObjectNode();
                  v.put("request_id", p.path("request_id").asText());
                  v.put("msg_id", String.valueOf(p.path("request_id").asInt() + 100));
                  r = v;
                }
              } catch (Exception ex) {
                return new MockResponse().setResponseCode(500);
              }
              MockResponse response =
                  new MockResponse()
                      .setResponseCode(f.status)
                      .setHeader("Content-Type", "application/json")
                      .setBody(r.isTextual() ? r.asText() : r.toString());
              if (f.delay > 0)
                response.setHeadersDelay((long) (f.delay * 1000), TimeUnit.MILLISECONDS);
              return response;
            }
          });
      server.start();
      String base = server.url("/").toString().replaceAll("/$", "");
      okhttp3.OkHttpClient http =
          new okhttp3.OkHttpClient.Builder()
              .callTimeout((long) (f.timeout * 1000), TimeUnit.MILLISECONDS)
              .build();
      feign.Client transport = new feign.okhttp.OkHttpClient(http);
      FullActions actions =
          new FullActions(base, "test-key", "test-secret", "group-key", "group-secret", transport);
      if (extra.asText().equals("logs")) {
        defaultLogs(server, args, records, checks, transport);
        return;
      }
      if (f.op.equals("voice_create")) {
        voice = wav();
        args.set(1, JSON.getNodeFactory().textNode(voice.getAbsolutePath()));
      }
      if (extra.asText().equals("host")) {
        String dc = args.get(0).asText();
        String wanted =
            dc.equals("japan")
                ? DataCenterHost.JPN.getUrl()
                : dc.equals("brazil") ? DataCenterHost.BRA.getUrl() : DataCenterHost.SG.getUrl();
        List<String> urls = new ArrayList<>();
        PushApi api =
            new PushApi.Builder()
                .setHost(wanted)
                .setAppKey("test-key")
                .setMasterSecret("test-secret")
                .setClient(
                    (request, options) -> {
                      urls.add(request.url());
                      return feign.Response.builder()
                          .request(request)
                          .status(200)
                          .reason("OK")
                          .headers(Collections.emptyMap())
                          .body(
                              "{\"msg_id\":\"123\",\"request_id\":\"r1\"}", StandardCharsets.UTF_8)
                          .build();
                    })
                .build();
        api.validate(new PushParam());
        ok(
            urls.equals(Collections.singletonList(wanted + "/v4/push/validate")),
            "configured_datacenter_request",
            checks);
        return;
      }
      if (extra.asText().equals("concurrent")) {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Object>> futures = new ArrayList<>();
        try {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey("test-key")
                  .setMasterSecret("test-secret")
                  .setClient(transport)
                  .build();
          for (int i = 0; i < 20; i++) {
            final int n = i;
            futures.add(
                pool.submit(
                    () -> {
                      String raw =
                          "{\"request_id\":\""
                              + n
                              + "\",\"to\":{\"registration_id\":[\"rid"
                              + n
                              + "\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"body"
                              + n
                              + "\"}}}";
                      return api.validate(JSON.readValue(raw, PushParam.class));
                    }));
          }
          for (int i = 0; i < 20; i++) {
            JsonNode r = JSON.valueToTree(futures.get(i).get(10, TimeUnit.SECONDS));
            ok(
                r.path("request_id").asText().equals(String.valueOf(i))
                    && r.path("msg_id").asText().equals(String.valueOf(i + 100)),
                "concurrent_response_isolation",
                checks);
          }
          ok(records.size() == 20, "twenty_real_requests", checks);
          for (RecordedRequest r : records) {
            JsonNode b = JSON.readTree(r.getBody().clone().readUtf8());
            String id = b.path("request_id").asText();
            ok(
                b.at("/to/registration_id/0").asText().equals("rid" + id)
                    && b.at("/body/notification/alert").asText().equals("body" + id),
                "concurrent_request_isolation",
                checks);
          }
        } finally {
          pool.shutdownNow();
        }
        return;
      }
      Object actual = null;
      Throwable error = null;
      long start = System.nanoTime();
      try {
        actual = actions.call(f.op, args);
      } catch (Throwable ex) {
        error = ex;
      }
      if (extra.asText().equals("timeout")) {
        ok(error != null, "timeout_error", checks);
        ok(records.size() == 5, "existing_retry_limit", checks);
        ok((System.nanoTime() - start) / 1e9 < 3, "bounded_timeout_with_retries", checks);
        return;
      }
      if (f.status >= 400) {
        ok(error instanceof ApiErrorException, "structured_api_error", checks);
        ApiErrorException e = (ApiErrorException) error;
        ok(e.getStats() == f.status, "error_http_status", checks);
        ok(
            e.getApiError().getError().getCode() == reply.path("error").path("code").asInt()
                && e.getApiError()
                    .getError()
                    .getMessage()
                    .equals(reply.path("error").path("message").asText()),
            "business_code_and_message",
            checks);
      } else ok(error == null, "SDK_success", checks);
      ok(!records.isEmpty(), "HTTP_observed", checks);
      RecordedRequest r = records.get(records.size() - 1);
      String expectedAuth =
          "Basic "
              + Base64.getEncoder()
                  .encodeToString(
                      (f.op.equals("group")
                              ? "group-group-key:group-secret"
                              : "test-key:test-secret")
                          .getBytes(StandardCharsets.UTF_8));
      ok(expectedAuth.equals(r.getHeader("Authorization")), "authentication", checks);
      String[] path = FullContractTest.METHOD_PATHS.get(f.op);
      String pattern = path[1].replaceAll("\\{[^}]+\\}", "[^/]+").replaceAll("/$", "");
      ok(
          r.getMethod().equals(path[0])
              && r.getRequestUrl().encodedPath().replaceAll("/$", "").matches(pattern),
          "method_and_path",
          checks);
      JsonNode body = JSON.readTree(f.body), expected = JSON.readTree(f.expected);
      if (!body.isNull())
        ok(
            contains(JSON.readTree(r.getBody().clone().readUtf8()), body),
            "request_fields_exact",
            checks);
      if (!expected.isNull())
        ok(contains(JSON.valueToTree(actual), expected), "parsed_fields_exact", checks);
      if (extra.asText().equals("no-tags"))
        ok(
            JSON.readTree(r.getBody().clone().readUtf8()).path("tags").isMissingNode()
                || JSON.readTree(r.getBody().clone().readUtf8()).path("tags").isNull(),
            "no_tag_clearing_instruction",
            checks);
      if (extra.isArray() && extra.get(0).asText().equals("query")) {
        Map<String, List<String>> q = new HashMap<>();
        for (String k : r.getRequestUrl().queryParameterNames())
          q.put(k, r.getRequestUrl().queryParameterValues(k));
        ok(contains(JSON.valueToTree(q), extra.get(1)), "query_values", checks);
      }
      if (extra.asText().equals("multipart")) {
        ok(
            r.getHeader("Content-Type").startsWith("multipart/form-data; boundary=")
                && r.getBody()
                        .clone()
                        .readByteString()
                        .indexOf(okio.ByteString.of(Files.readAllBytes(voice.toPath())))
                    >= 0,
            "multipart_file_bytes",
            checks);
      }

    } catch (Throwable ex) {
      verdict = "no_pass";
      reason = ex instanceof AssertionError ? ex.getMessage() : ex.getClass().getSimpleName();
      throw ex;
    } finally {
      try {
        server.shutdown();
      } finally {
        if (voice != null) Files.deleteIfExists(voice.toPath());
        save(f.id, "contract", verdict, reason, checks);
      }
    }
  }

  static void defaultLogs(
      MockWebServer server,
      ArrayNode args,
      List<RecordedRequest> records,
      List<String> checks,
      feign.Client transport)
      throws Exception {
    server.setDispatcher(
        new Dispatcher() {
          int sequence;

          @Override
          public MockResponse dispatch(RecordedRequest request) {
            records.add(request);
            boolean fail = sequence++ > 0;
            return new MockResponse()
                .setResponseCode(fail ? 400 : 200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    fail
                        ? "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}"
                        : "{\"request_id\":\"r1\",\"msg_id\":\"123\"}");
          }
        });
    ByteArrayOutputStream stdout = new ByteArrayOutputStream(),
        stderr = new ByteArrayOutputStream();
    PrintStream oldOut = System.out, oldErr = System.err;
    ApiErrorException failure = null;
    Object successful = null;
    try (PrintStream out = new PrintStream(stdout, true, "UTF-8");
        PrintStream err = new PrintStream(stderr, true, "UTF-8")) {
      System.setOut(out);
      System.setErr(err);
      try {
        System.out.println("capture-active-stdout");
        System.err.println("capture-active-stderr");
        // Use the public builder's default BASIC logging, rather than forcing NONE.
        PushApi api =
            new PushApi.Builder()
                .setHost(server.url("/").toString().replaceAll("/$", ""))
                .setAppKey("test-key")
                .setMasterSecret("test-secret")
                .setClient(transport)
                .build();
        PushParam param = JSON.convertValue(args.get(0), PushParam.class);
        successful = api.validate(param);
        try {
          api.validate(param);
        } catch (ApiErrorException e) {
          failure = e;
          e.printStackTrace(System.err);
        }
      } finally {
        System.setOut(oldOut);
        System.setErr(oldErr);
      }
    }
    ok(
        failure != null
            && failure.getStats() == 400
            && failure.getApiError().getError().getCode() == 21003,
        "success_and_error_controls",
        checks);
    String out = stdout.toString("UTF-8"),
        err = stderr.toString("UTF-8"),
        rendered = out + err + String.valueOf(failure) + JSON.writeValueAsString(successful);
    ok(
        out.contains("capture-active-stdout") && err.contains("capture-active-stderr"),
        "stdout_stderr_observers_active",
        checks);
    ok(
        !rendered.contains("test-secret")
            && !rendered.contains(
                Base64.getEncoder()
                    .encodeToString("test-key:test-secret".getBytes(StandardCharsets.UTF_8))),
        "default_logs_and_exceptions_credentials_redacted",
        checks);
    String auth =
        "Basic "
            + Base64.getEncoder()
                .encodeToString("test-key:test-secret".getBytes(StandardCharsets.UTF_8));
    ok(
        records.size() == 2
            && records.stream()
                .allMatch(
                    r ->
                        auth.equals(r.getHeader("Authorization"))
                            && r.getPath().equals("/v4/push/validate")),
        "two_authenticated_public_API_requests",
        checks);
  }
}
