package io.github.engagelab.nativecheck;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import feign.*;
import io.github.engagelab.exception.ApiErrorException;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.zip.*;
import org.junit.jupiter.api.Assertions;

/** Native live-service support. Runtime inputs are environment variables only. */
final class FullQA implements Client {
  static final ObjectMapper JSON = FullActions.JSON;
  final NativeSupport.Config cfg;
  final String prefix;
  FullActions actions;
  final okhttp3.OkHttpClient http;
  final Client delegate;
  Map<String, Object> last;
  String network = "";
  final Map<String, Object> results = qm(), states = qm();
  final List<String[]> owned = new ArrayList<>();
  final Map<String, String> aliases = new LinkedHashMap<>();
  final List<String> fixtures = new ArrayList<>();

  FullQA() {
    cfg = new NativeSupport.Config(System.getenv());
    String lm = cfg.env.getOrDefault("ENGAGELAB_LIFECYCLE_MESSAGE_ID", "").trim(),
        lr = cfg.env.getOrDefault("ENGAGELAB_LIFECYCLE_REGISTRATION_ID", "").trim();
    if (lm.isEmpty() != lr.isEmpty())
      throw new NativeSupport.Blocked("Provide both lifecycle message ID and registration ID");
    cfg.env.put("ENGAGELAB_LIFECYCLE_MESSAGE_ID", lm);
    cfg.env.put("ENGAGELAB_LIFECYCLE_REGISTRATION_ID", lr);
    prefix = cfg.run;
    http =
        new okhttp3.OkHttpClient.Builder()
            .connectTimeout((long) (cfg.timeout * 1000), TimeUnit.MILLISECONDS)
            .readTimeout((long) (cfg.timeout * 1000), TimeUnit.MILLISECONDS)
            .callTimeout((long) (cfg.timeout * 1000), TimeUnit.MILLISECONDS)
            .followRedirects(false)
            .build();
    delegate = new feign.okhttp.OkHttpClient(http);
    actions =
        new FullActions(
            cfg.base,
            cfg.key,
            cfg.secret,
            cfg.env.getOrDefault("ENGAGELAB_GROUP_KEY", "unused"),
            cfg.env.getOrDefault("ENGAGELAB_GROUP_SECRET", "unused"),
            this);
    for (String rid : cfg.env.getOrDefault("ENGAGELAB_FIXTURE_REGISTRATION_IDS", "").split(","))
      if (!rid.isEmpty()) fixtures.add(rid);
  }

  static Map<String, Object> qm(Object... v) {
    Map<String, Object> r = new LinkedHashMap<>();
    for (int i = 0; i + 1 < v.length; i += 2) r.put(qtxt(v[i]), v[i + 1]);
    return r;
  }

  static Object qarr(Object... v) {
    return new ArrayList<>(Arrays.asList(v));
  }

  @SuppressWarnings("unchecked")
  static List<Object> qlistOf(Object v) {
    return v instanceof List ? (List<Object>) v : Collections.emptyList();
  }

  static Object qarg(Object[] v, int i) {
    return v != null && i < v.length ? v[i] : null;
  }

  static Object qg(Object value, Object... keys) {
    for (Object k : keys) {
      if (value instanceof Map) {
        Map<?, ?> m = (Map<?, ?>) value;
        if (m.containsKey(qtxt(k))) value = m.get(qtxt(k));
        else {
          Object found = null;
          for (Map.Entry<?, ?> e : m.entrySet())
            if (FullContract.key(qtxt(e.getKey())).equals(FullContract.key(qtxt(k)))) {
              found = e.getValue();
              break;
            }
          value = found;
        }
      } else {
        List<Object> a = qlistOf(value);
        int i = (int) qnum(k);
        value = i >= 0 && i < a.size() ? a.get(i) : null;
      }
    }
    return value;
  }

  @SuppressWarnings("unchecked")
  static Object qput(Object v, Object key, Object value) {
    ((Map<String, Object>) v).put(qtxt(key), value);
    return null;
  }

  @SuppressWarnings("unchecked")
  static Object qdrop(Object v, Object k) {
    ((Map<String, Object>) v).remove(qtxt(k));
    return null;
  }

  static Object qappend(Object v, Object x) {
    qlistOf(v).add(x);
    return null;
  }

  static String qtxt(Object v) {
    return v == null ? "" : String.valueOf(v);
  }

  static long qnum(Object v) {
    try {
      return Long.parseLong(qtxt(v));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  static String qcat(Object a, Object b) {
    return qtxt(a) + qtxt(b);
  }

  static long qsize(Object v) {
    if (v instanceof String) return ((String) v).codePointCount(0, ((String) v).length());
    if (v instanceof Map) return ((Map<?, ?>) v).size();
    return qlistOf(v).size();
  }

  static boolean qyes(Object v) {
    return Boolean.TRUE.equals(v);
  }

  static boolean qno(Object v) {
    return Boolean.FALSE.equals(v);
  }

  static boolean qnonempty(Object v) {
    return v != null
        && (!(v instanceof String || v instanceof Map || v instanceof List) || qsize(v) > 0);
  }

  static boolean qeq(Object a, Object b) {
    if (a == null || b == null) return a == null && b == null;
    if (a instanceof Boolean || b instanceof Boolean) return a.equals(b);
    if (a instanceof Number || b instanceof Number) {
      try {
        return new BigDecimal(qtxt(a)).compareTo(new BigDecimal(qtxt(b))) == 0;
      } catch (NumberFormatException ignored) {
        return false;
      }
    }
    if (a instanceof Map && b instanceof Map) {
      Map<?, ?> aa = (Map<?, ?>) a, bb = (Map<?, ?>) b;
      if (!aa.keySet().equals(bb.keySet())) return false;
      for (Object k : aa.keySet()) if (!qeq(aa.get(k), bb.get(k))) return false;
      return true;
    }
    if (a instanceof List && b instanceof List) {
      List<Object> aa = qlistOf(a), bb = qlistOf(b);
      if (aa.size() != bb.size()) return false;
      for (int i = 0; i < aa.size(); i++) if (!qeq(aa.get(i), bb.get(i))) return false;
      return true;
    }
    return a.equals(b);
  }

  static boolean qboth(Object... v) {
    for (Object x : v) if (!qyes(x)) return false;
    return true;
  }

  static boolean qeither(Object... v) {
    for (Object x : v) if (qyes(x)) return true;
    return false;
  }

  static boolean qincludes(Object a, Object v) {
    if (a instanceof Map) return ((Map<?, ?>) a).containsKey(qtxt(v));
    if (a instanceof String) return ((String) a).contains(qtxt(v));
    for (Object x : qlistOf(a)) if (qeq(x, v)) return true;
    return false;
  }

  static boolean qsubset(Object a, Object b) {
    for (Object v : qlistOf(a)) if (!qincludes(b, v)) return false;
    return true;
  }

  static boolean qsets(Object a, Object b) {
    return qsubset(a, b) && qsubset(b, a);
  }

  static boolean qsame(Object a, Object b) {
    if (b instanceof Map) {
      if (!(a instanceof Map)) return false;
      Map<String, Object> actual = qm();
      for (Map.Entry<?, ?> e : ((Map<?, ?>) a).entrySet())
        actual.put(FullContract.key(qtxt(e.getKey())), e.getValue());
      for (Map.Entry<?, ?> e : ((Map<?, ?>) b).entrySet()) {
        String key = qtxt(e.getKey());
        if (Arrays.asList("mobile", "target_converted", "fileMd5", "file_md5").contains(key))
          continue;
        String k = FullContract.key(key);
        if (!actual.containsKey(k) || !qsame(actual.get(k), e.getValue())) return false;
      }
      return true;
    }
    if (b instanceof List) {
      if (!(a instanceof List) || qsize(a) != qsize(b)) return false;
      for (int i = 0; i < qsize(b); i++) if (!qsame(qg(a, i), qg(b, i))) return false;
      return true;
    }
    return qeq(a, b);
  }

  static Object qkeys(Object v) {
    return v instanceof Map ? new ArrayList<>(((Map<?, ?>) v).keySet()) : qarr();
  }

  static Object qids(Object v, Object key) {
    Object r = qarr();
    for (Object x : qlistOf(v)) qappend(r, qg(x, key));
    return r;
  }

  static String qjoined(Object v) {
    List<String> r = new ArrayList<>();
    for (Object x : qlistOf(v)) r.add(qtxt(x));
    return String.join(",", r);
  }

  static String qquery(Object v) {
    List<String> r = new ArrayList<>();
    for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet())
      r.add(enc(qtxt(e.getKey())) + "=" + enc(qtxt(e.getValue())));
    return String.join("&", r);
  }

  static String enc(String v) {
    try {
      return URLEncoder.encode(v, "UTF-8");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  static Object qspan(Object v) {
    Object r = qarr();
    for (long i = 0; i < qnum(v); i++) qappend(r, i);
    return r;
  }

  static Object qtake(Object v, Object n) {
    List<Object> a = qlistOf(v);
    return new ArrayList<>(a.subList(0, Math.min(a.size(), (int) qnum(n))));
  }

  static Object qwithout(Object v, Object x) {
    Object r = qarr();
    for (Object z : qlistOf(v)) if (!qeq(z, x)) qappend(r, z);
    return r;
  }

  static boolean qintersects(Object a, Object b) {
    for (Object v : qlistOf(a)) if (qincludes(b, v)) return true;
    return false;
  }

  static boolean qge(Object a, Object b) {
    return qnum(a) >= qnum(b);
  }

  static boolean qgt(Object a, Object b) {
    return qnum(a) > qnum(b);
  }

  static String qleft(Object a, Object n) {
    String s = qtxt(a);
    return s.substring(0, Math.min(s.length(), (int) qnum(n)));
  }

  static String qrepeat(Object a, Object n) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < qnum(n); i++) s.append(qtxt(a));
    return s.toString();
  }

  static long qadd(Object a, Object b) {
    return qnum(a) + qnum(b);
  }

  static long qmaxi(Object a, Object b) {
    return Math.max(qnum(a), qnum(b));
  }

  static Object qcoalesce(Object... v) {
    for (Object x : v) if (qnonempty(x)) return x;
    return null;
  }

  static Object qfirstOrSelf(Object v) {
    return v instanceof List ? qg(v, 0) : v;
  }

  static boolean qisInteger(Object v) {
    return v instanceof Byte
        || v instanceof Short
        || v instanceof Integer
        || v instanceof Long
        || v instanceof java.math.BigInteger;
  }

  static boolean qhasKeysShallow(Object v, Object keys) {
    if (!(v instanceof Map)) return false;
    for (Object k : qlistOf(keys)) if (!((Map<?, ?>) v).containsKey(qtxt(k))) return false;
    return true;
  }

  static boolean hasKey(Object v, String key) {
    if (v instanceof Map) {
      if (((Map<?, ?>) v).containsKey(key)) return true;
      for (Object x : ((Map<?, ?>) v).values()) if (hasKey(x, key)) return true;
    }
    for (Object x : qlistOf(v)) if (hasKey(x, key)) return true;
    return false;
  }

  static boolean qhasKeysDeep(Object v, Object keys) {
    for (Object k : qlistOf(keys)) if (!hasKey(v, qtxt(k))) return false;
    return true;
  }

  static boolean qhmosNonzero(Object v) {
    if (v instanceof Map) {
      for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet()) {
        Object x = e.getValue();
        if (qtxt(e.getKey()).equalsIgnoreCase("hmos")) {
          if (x instanceof Map) {
            for (Object value : ((Map<?, ?>) x).values()) if (qnum(value) > 0) return true;
          } else if (qnum(x) > 0) return true;
        }
        if (qhmosNonzero(x)) return true;
      }
    }
    for (Object x : qlistOf(v)) if (qhmosNonzero(x)) return true;
    return false;
  }

  static Object qparse(byte[] b) {
    try {
      return b.length == 0 ? null : JSON.readValue(b, Object.class);
    } catch (IOException e) {
      return null;
    }
  }

  static Object qclone(Object v) {
    return JSON.convertValue(v, Object.class);
  }

  static Object qtree(Object v) {
    return JSON.convertValue(v, Object.class);
  }

  void pause() throws InterruptedException {
    Thread.sleep((long) (cfg.interval * 1000));
  }

  public Response execute(Request req, Request.Options opts) throws IOException {
    try {
      pause();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted");
    }
    Response r =
        delegate.execute(
            req,
            new Request.Options(
                (long) (cfg.timeout * 1000),
                TimeUnit.MILLISECONDS,
                (long) (cfg.timeout * 1000),
                TimeUnit.MILLISECONDS,
                false));
    byte[] b = r.body() == null ? new byte[0] : Util.toByteArray(r.body().asInputStream());
    if (r.body() != null) r.body().close();
    URI u = URI.create(req.url());
    Map<String, Object> query = qm();
    if (u.getRawQuery() != null)
      for (String entry : u.getRawQuery().split("&")) {
        String[] pair = entry.split("=", 2);
        String k = URLDecoder.decode(pair[0], "UTF-8"),
            v = pair.length > 1 ? URLDecoder.decode(pair[1], "UTF-8") : "";
        if (!query.containsKey(k)) query.put(k, qarr());
        qappend(query.get(k), v);
      }
    byte[] sent = req.body() == null ? new byte[0] : req.body();
    String text = new String(sent, StandardCharsets.ISO_8859_1);
    String contentType = "";
    for (Map.Entry<String, Collection<String>> e : req.headers().entrySet())
      if (e.getKey().equalsIgnoreCase("Content-Type")) contentType = String.join(";", e.getValue());
    last =
        qm(
            "status",
            r.status(),
            "raw",
            qparse(b),
            "request",
            qparse(sent),
            "path",
            u.getPath(),
            "query",
            query,
            "multipart_valid",
            contentType.contains("multipart/form-data; boundary=")
                && text.contains("name=\"language\"")
                && text.contains("name=\"file\"")
                && states.containsKey("voice_bytes")
                && text.contains(
                    new String((byte[]) states.get("voice_bytes"), StandardCharsets.ISO_8859_1)));
    return r.toBuilder().body(b).build();
  }

  ArrayNode adapt(String op, Object args) {
    List<Object> a = new ArrayList<>(qlistOf(qclone(args)));
    Map<String, String[]> fields = new HashMap<>();
    fields.put("status", new String[] {"messageIds"});
    fields.put("lifecycle", new String[] {"messageId", "registrationIds"});
    fields.put("users", new String[] {"timeUnit", "startTime", "duration"});
    fields.put("plan_status", new String[] {"planIds", "startDate", "endDate"});
    fields.put(
        "plan_list", new String[] {"pageIndex", "pageSize", "sendSource", "searchDescription"});
    fields.put("plan_msgs", new String[] {"planIds", "startDate", "endDate"});
    fields.put("plan_delete", new String[] {"planId"});
    fields.put("schedule_list", new String[] {"page"});
    fields.put("schedule_msgs", new String[] {"scheduleId"});
    if (fields.containsKey(op)) {
      Map<String, Object> p = qm();
      String[] keys = fields.get(op);
      for (int i = 0; i < keys.length && i < a.size(); i++) p.put(keys[i], a.get(i));
      a = qlistOf(qarr(p));
    }
    if (op.equals("plan_batch_delete"))
      a = qlistOf(qarr(new ArrayList<>(Arrays.asList(qtxt(qg(a, 0)).split(",")))));
    if (op.equals("schedule_update")) {
      Object p = qg(a, 1);
      qput(p, "scheduleId", qg(a, 0));
      a = qlistOf(qarr(p));
    }
    return JSON.valueToTree(a);
  }

  Object sdk(Object op, Object args) throws Exception {
    last = null;
    network = "";
    try {
      Object data = actions.call(qtxt(op), adapt(qtxt(op), args));
      Map<String, Object> r = last == null ? qm() : new LinkedHashMap<>(last);
      r.put("ok", true);
      Object view =
          data instanceof io.github.engagelab.bean.plan.PushPlanMsgQueryResult
              ? ((io.github.engagelab.bean.plan.PushPlanMsgQueryResult) data).getResults()
              : data;
      r.put("data", qtree(view));
      return r;
    } catch (ApiErrorException e) {
      Map<String, Object> r = last == null ? qm() : new LinkedHashMap<>(last);
      r.putAll(
          qm(
              "ok",
              false,
              "error_code",
              e.getApiError().getError().getCode(),
              "error_message",
              e.getApiError().getError().getMessage()));
      return r;
    } catch (feign.RetryableException e) {
      network = "SDK transport unavailable";
      return qm("ok", false, "network", network);
    }
  }

  Object request(String key, String secret, String method, String path, Object body)
      throws Exception {
    pause();
    okhttp3.Request.Builder b =
        new okhttp3.Request.Builder()
            .url(cfg.base + path)
            .header("Authorization", okhttp3.Credentials.basic(key, secret))
            .header("Content-Type", "application/json");
    okhttp3.RequestBody rb =
        body == null
            ? null
            : okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"), JSON.writeValueAsBytes(body));
    if (method.equals("POST") && rb == null) rb = okhttp3.RequestBody.create(null, new byte[0]);
    b.method(method, rb);
    try (okhttp3.Response r = http.newCall(b.build()).execute()) {
      return qm(
          "status", r.code(), "data", qparse(r.body() == null ? new byte[0] : r.body().bytes()));
    } catch (IOException e) {
      throw new NativeSupport.Blocked("QA control transport unavailable");
    }
  }

  Object rest(Object method, Object path, Object... body) throws Exception {
    return request(cfg.key, cfg.secret, qtxt(method), qtxt(path), qarg(body, 0));
  }

  Object require(Object ok, Object why) {
    if (!qyes(ok)) throw new NativeSupport.Blocked(qtxt(why));
    return null;
  }

  Object write() {
    cfg.write();
    return null;
  }

  Object target() {
    return cfg.target;
  }

  String need(Object k) {
    return cfg.need(qtxt(k));
  }

  Object env(Object k, Object def) {
    return cfg.env.containsKey(qtxt(k)) ? cfg.env.get(qtxt(k)) : def;
  }

  Object envNumber(Object k, Object def) {
    return qnum(env(k, def));
  }

  Object unique() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  Object now() {
    return Instant.now().getEpochSecond();
  }

  Object dateTime(Object ts) {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.ofHours(8))
        .format(Instant.ofEpochSecond(qnum(ts)));
  }

  Object date(Object ts) {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .withZone(ZoneOffset.ofHours(8))
        .format(Instant.ofEpochSecond(qnum(ts)));
  }

  Object sleepUntil(Object ts) throws InterruptedException {
    long ms = qnum(ts) * 1000 - System.currentTimeMillis();
    if (ms > 0) Thread.sleep(ms);
    return null;
  }

  Object state(Object k, Object v) {
    if (v != null) states.put(qtxt(k), v);
    return states.get(qtxt(k));
  }

  Object own(Object kind, Object value) throws Exception {
    owned.add(new String[] {qtxt(kind), qtxt(value)});
    Path folder =
        Paths.get(
            cfg.env.getOrDefault("ENGAGELAB_FULL_RESULTS_DIR", ".test-results/full"), "resources");
    Files.createDirectories(folder);
    JSON.writeValue(
        folder.resolve(prefix + ".json").toFile(), qm("run_id", prefix, "owned", owned));
    return null;
  }

  Object ownedValues(Object kind, Object max) {
    Object r = qarr();
    for (String[] v : owned) if (v[0].equals(qtxt(kind)) && qsize(r) < qnum(max)) qappend(r, v[1]);
    return r;
  }

  Object rememberAlias(Object rid) throws Exception {
    String r = qtxt(rid);
    for (String[] v : owned) if (v[0].equals("device") && v[1].equals(r)) return null;
    if (!aliases.containsKey(r)) aliases.put(r, qtxt(qg(device(r), "data", "alias")));
    return null;
  }

  Object fixtureRID(Object index) {
    int i = (int) qnum(index);
    return i < fixtures.size() ? fixtures.get(i) : null;
  }

  Object addFixture(Object rid) {
    fixtures.add(qtxt(rid));
    return null;
  }

  Object waitFor(Object kind, Object value) throws Exception {
    if (!qnonempty(value)) return false;
    long end = System.nanoTime() + (long) (cfg.wait * 1e9);
    while (true) {
      boolean ready = false;
      switch (qtxt(kind)) {
        case "plans":
          ready =
              qeq(
                  qg(listPlans(qg(value, "prefix"), 101, 1, null), "data", "total"),
                  qg(value, "count"));
          break;
        case "device_absent":
          ready = qeq(qg(device(value), "data", "error", "code"), 27002);
          break;
        case "schedule":
          Object r = rest("GET", "/v4/schedules/" + qtxt(value) + "/msg-ids", (Object) null);
          ready = qnonempty(qcoalesce(qg(r, "data", "msg_ids"), qg(r, "data", "msgIds")));
          break;
        case "messages":
          Object m =
              rest(
                  "GET",
                  "/v4/status/detail?" + qquery(qm("message_ids", qjoined(value))),
                  (Object) null);
          ready = true;
          for (Object mid : qlistOf(value)) ready = ready && qnonempty(qg(m, "data", mid));
          break;
        default:
          throw new IllegalArgumentException("Unknown readback condition");
      }
      if (ready) return true;
      if (System.nanoTime() > end) return false;
      pause();
    }
  }

  void record(Object sid, Map<String, Object> checks, boolean ready, String why) {
    String status = "pass", reason = "";
    for (Object v : checks.values()) if (!qyes(v)) status = "no_pass";
    if (!ready) {
      status = "block";
      reason = why;
    } else if (!network.isEmpty()) {
      status = "block";
      reason = network;
    }
    results.put(qtxt(sid), qm("status", status, "checks", checks, "reason", reason));
  }

  interface Checked {
    boolean run() throws Exception;
  }

  void safe(List<String> failed, String label, Checked fn) {
    try {
      if (!fn.run()) failed.add(label);
    } catch (Exception e) {
      failed.add(label);
    }
  }

  void clean() {
    List<String> failed = new ArrayList<>();
    List<Object> plans = qlistOf(ownedValues("plan", 10000));
    for (int i = 0; i < plans.size(); i += 10) {
      List<Object> chunk = plans.subList(i, Math.min(i + 10, plans.size()));
      safe(
          failed,
          "plan_delete",
          () -> {
            rest("DELETE", "/v4/push_plan/batch/" + qjoined(chunk), (Object) null);
            return true;
          });
    }
    if (!plans.isEmpty())
      safe(failed, "plan_readback", () -> qyes(waitFor("plans", qm("prefix", prefix, "count", 0))));
    for (int i = owned.size() - 1; i >= 0; i--) {
      String kind = owned.get(i)[0], val = owned.get(i)[1];
      if (kind.equals("plan")) continue;
      safe(
          failed,
          kind + "_readback",
          () -> {
            switch (kind) {
              case "alias":
                String p = "/v4/aliases/" + val + "?platform=android";
                rest("DELETE", p, (Object) null);
                return !qnonempty(qg(rest("GET", p, (Object) null), "data", "registration_ids"));
              case "tag":
                rest("DELETE", "/v4/tags/" + val + "?platform=android", (Object) null);
                return !qincludes(qg(rest("GET", "/v4/tags/", (Object) null), "data", "tags"), val);
              case "device":
                rest("DELETE", "/v4/devices/" + val, (Object) null);
                return qyes(waitFor("device_absent", val));
              case "schedule":
                rest("DELETE", "/v4/schedules/" + val, (Object) null);
                return qeq(qg(rest("GET", "/v4/schedules/" + val, (Object) null), "status"), 404);
              case "voice":
                rest("DELETE", "/v4/voices/" + val, (Object) null);
                return qeq(qg(rest("GET", "/v4/voices/" + val, (Object) null), "status"), 404);
              default:
                return false;
            }
          });
    }
    for (Map.Entry<String, String> e : aliases.entrySet())
      safe(
          failed,
          "fixture_alias_restore",
          () -> {
            rest("POST", "/v4/devices/" + e.getKey(), qm("alias", e.getValue()));
            return qeq(qtxt(qg(device(e.getKey()), "data", "alias")), e.getValue());
          });
    if (states.containsKey("wav"))
      try {
        Files.deleteIfExists(Paths.get(qtxt(states.get("wav"))));
      } catch (Exception e) {
        failed.add("wav_cleanup");
      }
    for (Object result : results.values()) {
      @SuppressWarnings("unchecked")
      Map<String, Object> r = (Map<String, Object>) result;
      qput(qg(r, "checks"), "owned_resource_cleanup", failed.isEmpty());
      if (!failed.isEmpty() && qeq(r.get("status"), "pass")) {
        r.put("status", "no_pass");
        r.put("reason", "Cleanup not verified: " + String.join(",", failed));
      }
    }
  }

  interface Flow {
    void run(FullQA c) throws Exception;
  }

  static void execute(
      Map<String, Map<String, Object>> cache, String group, String sid, List<String> ids, Flow flow)
      throws Exception {
    if (!cache.containsKey(group)) {
      Map<String, Object> res = qm();
      FullQA c = null;
      try {
        c = new FullQA();
        flow.run(c);
      } catch (Exception | AssertionError e) {
        String status = e instanceof NativeSupport.Blocked ? "block" : "no_pass",
            reason =
                e instanceof NativeSupport.Blocked
                    ? e.getMessage()
                    : "Native workflow exception: " + e.getClass().getSimpleName();
        for (String id : ids)
          if (c == null || !c.results.containsKey(id))
            res.put(id, qm("status", status, "checks", qm(), "reason", reason));
      } finally {
        if (c != null) {
          c.clean();
          res.putAll(c.results);
        }
      }
      cache.put(group, res);
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> r = (Map<String, Object>) cache.get(group).get(sid);
    List<String> checks = new ArrayList<>();
    for (Object key : qlistOf(qkeys(qg(r, "checks"))))
      checks.add((qyes(qg(r, "checks", key)) ? "" : "FAILED:") + qtxt(key));
    FullContract.save(sid, "integration", qtxt(r.get("status")), qtxt(r.get("reason")), checks);
    Assertions.assertEquals(
        "pass", qtxt(r.get("status")), qtxt(r.get("reason")) + " " + String.join(",", checks));
  }

  Object authControl() throws Exception {
    FullActions old = actions;
    actions =
        new FullActions(cfg.base, "000000000000000000000000", "invalid-sdk-secret", "", "", this);
    Object r;
    try {
      r = sdk("vip", qarr());
    } finally {
      actions = old;
    }
    Object raw =
        request(
            "000000000000000000000000", "invalid-sdk-secret", "GET", "/v4/app/vip/status", null);
    return qm("verified", errorsEqual(r, raw));
  }

  Object apnsConfig() {
    String token = need("ENGAGELAB_APNS_TOKEN"), prod = need("ENGAGELAB_APNS_PRODUCTION");
    need("ENGAGELAB_BUNDLE_ID");
    require(
        token.matches("[0-9a-fA-F]{64}") && Arrays.asList("true", "false").contains(prod),
        "Real 64-hex APNs token and explicit channel required");
    return qm("token", token, "production", prod.equals("true"));
  }

  Object groupConfig() {
    need("ENGAGELAB_GROUP_KEY");
    need("ENGAGELAB_GROUP_SECRET");
    return qm(
        "apps",
        new ArrayList<>(Arrays.asList(need("ENGAGELAB_GROUP_APP_KEYS").split(","))),
        "alias",
        need("ENGAGELAB_GROUP_TARGET_ALIAS"));
  }

  Object memberAliasBinding(Object app, Object alias) throws Exception {
    Object secret =
        qg(qparse(need("ENGAGELAB_GROUP_APP_SECRETS").getBytes(StandardCharsets.UTF_8)), app);
    require(qnonempty(secret), "Group member control credential missing");
    return request(
        qtxt(app),
        qtxt(secret),
        "GET",
        "/v4/aliases/" + enc(qtxt(alias)) + "?platform=android",
        null);
  }

  Object imageResource() throws Exception {
    String u = need("ENGAGELAB_OPPO_IMAGE_URL");
    byte[] b;
    int status;
    try (okhttp3.Response r =
        http.newCall(new okhttp3.Request.Builder().url(u).build()).execute()) {
      status = r.code();
      b = r.body() == null ? new byte[0] : r.body().bytes();
    } catch (IOException e) {
      throw new NativeSupport.Blocked("Image download unavailable");
    }
    byte[] magic = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    boolean valid =
        status == 200
            && b.length > 24
            && b.length < 1048576
            && Arrays.equals(Arrays.copyOf(b, 8), magic)
            && ByteBuffer.wrap(b, 16, 8).getInt() == 984
            && ByteBuffer.wrap(b, 20, 4).getInt() == 369;
    require(valid, "Reachable 984x369 PNG below 1MiB required");
    return qm("url", u, "valid", valid);
  }

  Object removeFile(Object path) throws Exception {
    Files.delete(Paths.get(qtxt(path)));
    return null;
  }

  String adb(String... args) throws Exception {
    List<String> cmd =
        new ArrayList<>(
            Arrays.asList(
                qtxt(env("ENGAGELAB_ADB_PATH", "adb")), "-s", need("ENGAGELAB_ADB_SERIAL")));
    if (args.length > 0 && args[0].equals("shell")) {
      List<String> quoted = new ArrayList<>();
      for (int i = 1; i < args.length; i++) quoted.add("'" + args[i].replace("'", "'\"'\"'") + "'");
      cmd.add("shell");
      cmd.add(String.join(" ", quoted));
    } else cmd.addAll(Arrays.asList(args));
    Process p;
    try {
      p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
    } catch (IOException e) {
      throw new NativeSupport.Blocked("Declared ADB executable unavailable");
    }
    ExecutorService reader =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r, "native-adb-reader");
              t.setDaemon(true);
              return t;
            });
    Future<byte[]> captured = reader.submit(() -> readAll(p.getInputStream()));
    byte[] raw;
    try {
      raw = captured.get(20, TimeUnit.SECONDS);
      if (!p.waitFor(2, TimeUnit.SECONDS)) throw new TimeoutException();
    } catch (TimeoutException e) {
      p.destroyForcibly();
      throw new NativeSupport.Blocked("ADB command exceeded observation bound");
    } finally {
      reader.shutdownNow();
    }
    require(p.exitValue() == 0, "Declared ADB device or command unavailable");
    return new String(raw, StandardCharsets.UTF_8);
  }

  static byte[] readAll(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[8192];
    int n;
    while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
    return out.toByteArray();
  }

  List<String> notifications(String marker) throws Exception {
    String pkg = need("ENGAGELAB_ANDROID_PACKAGE");
    List<String> out = new ArrayList<>();
    for (String line : adb("shell", "cmd", "notification", "list").split("\n")) {
      String k = line.trim();
      if (k.contains("|" + pkg + "|")) {
        String text = adb("shell", "cmd", "notification", "get", k);
        if (text.contains(marker)) out.add(k);
      }
    }
    return out;
  }

  boolean relevantLog(String uid, String marker, Object... ids) throws Exception {
    for (String line : adb("logcat", "-d", "--uid=" + uid, "-v", "epoch", "-t", "2000").split("\n"))
      if ((line.contains(marker)
              || (ids.length > 0 && qnonempty(ids[0]) && line.contains(qtxt(ids[0]))))
          && (line.contains("onNotification")
              || line.contains("UserReceiver")
              || line.contains("MTNotificationBusiness"))) return true;
    return false;
  }

  Object physicalNotifications() throws Exception {
    write();
    String rid = need("ENGAGELAB_TEST_REGISTRATION_ID"), pkg = need("ENGAGELAB_ANDROID_PACKAGE");
    require(
        need("ENGAGELAB_DEVICE_APP_KEY").equals(cfg.key),
        "Physical app differs from SDK application");
    require(!adb("shell", "pidof", pkg).trim().isEmpty(), "Physical QA Demo not running");
    require(qeq(qg(device(rid), "status"), 200), "Physical RID absent from QA app");
    Matcher matcher =
        Pattern.compile("(?:userId|appId)=(\\d+)").matcher(adb("shell", "dumpsys", "package", pkg));
    require(matcher.find(), "Physical app UID unavailable");
    String uid = matcher.group(1), marker = prefix + "_physical";
    Object sent = sdk("push", qarr(payload(rid, marker, null))), mid = qg(sent, "data", "msg_id");
    boolean posted = false,
        pushOK = false,
        validateOK = false,
        withdrawOK = false,
        logControl = false;
    try {
      if (qnonempty(mid)) {
        long end = System.nanoTime() + (long) (cfg.wait * 1e9);
        while (System.nanoTime() < end) {
          if (!notifications(marker).isEmpty()) {
            posted = true;
            break;
          }
          Thread.sleep(1000);
        }
      }
      boolean persisted = qyes(waitFor("messages", qarr(qtxt(mid))));
      pushOK =
          qboth(
              qeq(qg(sent, "status"), 200),
              posted,
              persisted,
              qeq(mid, qg(sent, "raw", "msg_id")),
              qsame(qg(sent, "data"), qg(sent, "raw")));
      logControl = relevantLog(uid, marker, mid);
      Object control = sdk("device_status", qarr(qm("registration_ids", qarr(rid))));
      String valmarker = marker + "_validate";
      Object validation = payload(rid, valmarker, null);
      qput(qg(validation, "body", "options"), "time_to_live", 10);
      Object validated = sdk("validate", qarr(validation));
      boolean unexpected = false;
      long end = System.nanoTime() + 12000000000L;
      while (System.nanoTime() < end) {
        unexpected = unexpected || !notifications(valmarker).isEmpty();
        Thread.sleep(1000);
      }
      Object after = sdk("device_status", qarr(qm("registration_ids", qarr(rid))));
      validateOK =
          qboth(
              qeq(qg(validated, "status"), 200),
              !unexpected,
              qyes(qg(control, "data", 0, "online")),
              qyes(qg(after, "data", 0, "online")),
              posted,
              logControl,
              !relevantLog(uid, valmarker),
              qeq(qtxt(qg(validated, "data", "msg_id")), qtxt(qg(validated, "raw", "msg_id")))
                  && qeq(
                      qtxt(qg(validated, "data", "request_id")),
                      qtxt(qg(validated, "raw", "request_id"))));
      if (posted && !notifications(marker).isEmpty()) {
        Object withdrawn = sdk("withdraw", qarr(qtxt(mid)));
        end = System.nanoTime() + (long) (cfg.wait * 1e9);
        while (System.nanoTime() < end && !notifications(marker).isEmpty()) Thread.sleep(1000);
        withdrawOK =
            qboth(
                qeq(qg(withdrawn, "status"), 200),
                notifications(marker).isEmpty(),
                qeq(qg(withdrawn, "data", "msg_id"), mid),
                qsame(qg(withdrawn, "data"), qg(withdrawn, "raw")));
      }
    } finally {
      if (qnonempty(mid)) rest("DELETE", "/v4/push/withdraw/" + qtxt(mid), (Object) null);
    }
    return qm(
        "push",
        pushOK,
        "push_ready",
        posted,
        "validate",
        validateOK,
        "validate_ready",
        posted && logControl,
        "withdraw",
        withdrawOK,
        "withdraw_ready",
        posted);
  }

  Object voiceFile() throws Exception {
    Path path =
        Paths.get(qtxt(env("ENGAGELAB_VOICE_FILE", "src/test/resources/native/voice/en.zip")));
    require(Files.isRegularFile(path), "Valid voice ZIP resource missing: ENGAGELAB_VOICE_FILE");
    int count = 0;
    boolean valid = true;
    try (ZipFile zip = new ZipFile(path.toFile())) {
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry e = entries.nextElement();
        count++;
        valid = valid && e.getName().endsWith(".mp3") && e.getSize() > 0;
      }
    }
    require(valid && count >= 34, "Expected complete nonempty MP3 voice ZIP");
    states.put("voice_bytes", Files.readAllBytes(path));
    return path.toString();
  }

  static Object qdecodeJSON(Object text) {
    return qparse(qtxt(text).getBytes(StandardCharsets.UTF_8));
  }

  Object device(Object... params) throws Exception {
    FullQA c = this;
    Object rid = null;
    rid = qarg(params, 0);
    return c.rest("GET", qcat("/v4/devices/", qtxt(rid)), null);
  }

  Object relation(Object... params) throws Exception {
    FullQA c = this;
    Object rid = null;
    Object tag = null;
    tag = qarg(params, 0);
    rid = qarg(params, 1);
    return c.rest(
        "GET", qcat(qcat(qcat("/v4/tags/", qtxt(tag)), "/registration_ids/"), qtxt(rid)), null);
  }

  Object seedDevice(Object... params) throws Exception {
    FullQA c = this;
    Object r = null;
    Object rid = null;
    c.write();
    c.require(qeq(c.target(), "qa"), "Backend-only fixtures require QA");
    r =
        c.rest(
            "POST",
            "/v4/devices/token/registration_id",
            qm("platform", "android", "tokens", qarr(qcat(qcat(c.prefix, "_"), c.unique()))));
    rid = qg(r, "data", "results", 0, "registration_id");
    c.require(
        qboth(
            qeq(qg(r, "status"), 200), qnonempty(rid), qyes(qg(r, "data", "results", 0, "is_new"))),
        "Cannot prepare exclusively owned backend device");
    c.own("device", rid);
    return rid;
  }

  Object seedTag(Object... params) throws Exception {
    FullQA c = this;
    Object r = null;
    Object rid = null;
    Object tag = null;
    tag = qarg(params, 0);
    rid = qarg(params, 1);
    c.own("tag", tag);
    r = c.rest("POST", qcat("/v4/tags/", qtxt(tag)), qm("registration_ids", qm("add", qarr(rid))));
    c.require(qeq(qg(r, "status"), 200), "Isolated tag prerequisite rejected");
    return r;
  }

  Object seedAlias(Object... params) throws Exception {
    FullQA c = this;
    Object alias = null;
    Object r = null;
    Object rid = null;
    alias = qarg(params, 0);
    rid = qarg(params, 1);
    c.rememberAlias(rid);
    c.own("alias", alias);
    r = c.rest("POST", qcat("/v4/devices/", qtxt(rid)), qm("alias", alias));
    c.require(qeq(qg(r, "status"), 200), "Isolated alias prerequisite rejected");
    return r;
  }

  Object listPlans(Object... params) throws Exception {
    FullQA c = this;
    Object count = null;
    Object page = null;
    Object prefix = null;
    Object q = null;
    Object source = null;
    prefix = qarg(params, 0);
    count = qarg(params, 1);
    page = qarg(params, 2);
    source = qarg(params, 3);
    q = qm("page_index", page, "page_size", count, "search_description", prefix);
    if (qyes(qnonempty(source))) {
      qput(q, "send_source", source);
    }
    return c.rest("GET", qcat("/v4/push_plan/list?", qquery(q)), null);
  }

  Object seedPlan(Object... params) throws Exception {
    FullQA c = this;
    Object description = null;
    Object pid = null;
    Object r = null;
    pid = qarg(params, 0);
    description = qarg(params, 1);
    c.own("plan", pid);
    r = c.sdk("plan_create", qarr(qm("plan_id", pid, "plan_description", description)));
    c.require(
        qboth(qyes(qg(r, "ok")), qeq(qg(r, "data", "plan_id"), pid)),
        "Cannot prepare owned plan through native SDK");
    return r;
  }

  Object seedPlans(Object... params) throws Exception {
    FullQA c = this;
    Object count = null;
    Object first = null;
    Object i = null;
    Object r = null;
    count = qarg(params, 0);
    first = null;
    for (Object item1 : qlistOf(qspan(count))) {
      i = item1;
      r = c.seedPlan(qcat(qcat(c.prefix, "_"), qtxt(i)), c.prefix);
      if (qyes(qeq(i, 0))) {
        first = r;
      }
    }
    c.waitFor("plans", qm("prefix", c.prefix, "count", count));
    return first;
  }

  Object planIDs(Object... params) throws Exception {
    FullQA c = this;
    return c.ownedValues("plan", 101);
  }

  Object batchPlanPair(Object... params) throws Exception {
    FullQA c = this;
    Object exclude = null;
    exclude = qarg(params, 0);
    return qtake(qwithout(c.planIDs(), exclude), 2);
  }

  Object hasKeys(Object... params) throws Exception {
    FullQA c = this;
    Object keys = null;
    Object value = null;
    value = qarg(params, 0);
    keys = qarg(params, 1);
    return qhasKeysDeep(value, keys);
  }

  Object join(Object... params) throws Exception {
    FullQA c = this;
    Object values = null;
    values = qarg(params, 0);
    return qjoined(values);
  }

  Object noneIn(Object... params) throws Exception {
    FullQA c = this;
    Object left = null;
    Object right = null;
    left = qarg(params, 0);
    right = qarg(params, 1);
    return qno(qintersects(left, right));
  }

  Object errorsEqual(Object... params) throws Exception {
    FullQA c = this;
    Object raw = null;
    Object sdk = null;
    sdk = qarg(params, 0);
    raw = qarg(params, 1);
    return qboth(
        qno(qg(sdk, "ok")),
        qeq(qg(sdk, "status"), qg(raw, "status")),
        qeq(qg(sdk, "error_code"), qg(raw, "data", "error", "code")),
        qeq(qg(sdk, "error_message"), qg(raw, "data", "error", "message")));
  }

  Object planBoundary(Object... params) throws Exception {
    FullQA c = this;
    Object candidate = null;
    Object length = null;
    Object payload = null;
    Object pid = null;
    Object r = null;
    Object raw = null;
    length = qarg(params, 0);
    pid = null;
    if (qyes(qeq(length, 1))) {
      for (Object item2 :
          qlistOf(
              qarr(
                  "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                  "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                  "6", "7", "8", "9"))) {
        candidate = item2;
        if (qyes(qno(qnonempty(pid)))) {
          r = c.listPlans(candidate, 10000, 1, null);
          if (qyes(
              qboth(
                  qge(qsize(qg(r, "data", "push_plan_info")), qnum(qg(r, "data", "total"))),
                  qno(qincludes(qids(qg(r, "data", "push_plan_info"), "plan_id"), candidate))))) {
            pid = candidate;
          }
        }
      }
    } else {
      pid = qleft(qcat(qcat(c.prefix, "_"), qrepeat("x", length)), length);
    }
    if (qyes(qno(qnonempty(pid)))) {
      return qm("ready", false, "verified", false);
    }
    payload = qm("plan_id", pid, "plan_description", c.prefix);
    c.own("plan", pid);
    r = c.sdk("plan_create", qarr(payload));
    if (qyes(qeq(length, 51))) {
      raw = c.rest("POST", "/v4/push_plan", payload);
      return qm(
          "ready",
          true,
          "verified",
          qboth(qyes(c.errorsEqual(r, raw)), qeq(qg(r, "error_code"), 27304)));
    }
    raw = c.listPlans(c.prefix, 101, 1, null);
    return qm(
        "ready",
        true,
        "verified",
        qboth(
            qyes(qg(r, "ok")),
            qincludes(qids(qg(raw, "data", "push_plan_info"), "plan_id"), pid),
            qeq(qsize(pid), length)));
  }

  Object targetRID(Object... params) throws Exception {
    FullQA c = this;
    Object index = null;
    Object rid = null;
    index = qarg(params, 0);
    rid = c.fixtureRID(index);
    if (qyes(qno(qnonempty(rid)))) {
      rid = c.seedDevice();
      c.addFixture(rid);
    }
    c.require(qeq(qg(c.device(rid), "status"), 200), "Declared QA target absent from application");
    return rid;
  }

  Object batchResultsValid(Object... params) throws Exception {
    FullQA c = this;
    Object k = null;
    Object r = null;
    Object targets = null;
    Object v = null;
    Object valid = null;
    Object values = null;
    r = qarg(params, 0);
    targets = qarg(params, 1);
    values = qg(r, "data", "results");
    valid = qsets(qkeys(values), targets);
    for (Object item3 : qlistOf(qkeys(values))) {
      k = item3;
      v = qg(values, k);
      valid =
          qboth(valid, qyes(qg(v, "success")), qnonempty(qg(v, "msg_id")), qeq(qg(v, "target"), k));
    }
    return valid;
  }

  Object batch(Object... params) throws Exception {
    FullQA c = this;
    Object k = null;
    Object linked = null;
    Object mids = null;
    Object op = null;
    Object r = null;
    Object requests = null;
    Object t = null;
    Object targets = null;
    Object v = null;
    op = qarg(params, 0);
    targets = qarg(params, 1);
    requests = qarr();
    for (Object item4 : qlistOf(targets)) {
      t = item4;
      qappend(
          requests,
          qm(
              "target",
              t,
              "platform",
              "android",
              "notification",
              qm("alert", c.prefix),
              "options",
              qm("time_to_live", 30, "third_party_channel", qm("distribution_new", "mtpush"))));
    }
    r = c.sdk(op, qarr(qm("requests", requests)));
    mids = qarr();
    for (Object item5 : qlistOf(qkeys(qg(r, "data", "results")))) {
      k = item5;
      v = qg(r, "data", "results", k);
      if (qyes(qyes(qg(v, "success")))) {
        qappend(mids, qtxt(qg(v, "msg_id")));
      }
    }
    linked = false;
    if (qyes(qeq(qsize(mids), qsize(targets)))) {
      linked = c.waitFor("messages", mids);
    }
    qput(r, "messages_persisted", linked);
    return r;
  }

  Object negative(Object... params) throws Exception {
    FullQA c = this;
    Object base = null;
    Object control = null;
    Object controls = null;
    Object kind = null;
    Object name = null;
    Object op = null;
    Object p = null;
    Object path = null;
    Object payload = null;
    Object r = null;
    Object raw = null;
    Object types = null;
    kind = qarg(params, 0);
    base =
        qm(
            "to",
            qm("registration_id", qarr("sdk_full_negative_nonexistent")),
            "body",
            qm("platform", qarr("ios"), "options", qm("apns_production", false)));
    types =
        qm(
            "voip",
            qm("call_id", c.prefix),
            "message",
            qm("msg_content", c.prefix),
            "notification",
            qm("alert", c.prefix),
            "live_activity",
            qm(
                "ios",
                qm(
                    "event",
                    "start",
                    "attributes-type",
                    "SdkFull",
                    "attributes",
                    qm(),
                    "content-state",
                    qm("n", 1))));
    controls = true;
    op = "validate";
    path = "/v4/push/validate";
    payload = qm();
    if (qyes(qeq(kind, "platform"))) {
      payload =
          qm(
              "to",
              qm("registration_id", qarr("sdk_full_negative_nonexistent")),
              "body",
              qm("platform", "bad-platform", "notification", qm("alert", c.prefix)));
      op = "push_raw";
      path = "/v4/push";
    } else {
      if (qyes(qno(qeq(kind, "empty")))) {
        qput(qg(base, "to"), "registration_id", qarr(c.negativeTarget()));
        for (Object item6 : qlistOf(qarr("voip", kind))) {
          name = item6;
          p = qclone(base);
          qput(qg(p, "body"), name, qg(types, name));
          control = c.rest("POST", path, p);
          controls = qboth(controls, qeq(qg(control, "status"), 200));
        }
        payload = base;
        qput(qg(payload, "body"), "voip", qg(types, "voip"));
        qput(qg(payload, "body"), kind, qg(types, kind));
      }
    }
    raw = c.rest("POST", path, payload);
    r = c.sdk(op, qarr(payload));
    return qm(
        "verified",
        qboth(qyes(c.errorsEqual(r, raw)), qno(qg(r, "ok"))),
        "controls_valid",
        controls);
  }

  Object vipValid(Object... params) throws Exception {
    FullQA c = this;
    Object r = null;
    r = qarg(params, 0);
    return qboth(
        qisInteger(qg(r, "data", "vip_status")), qisInteger(qg(r, "data", "vip_end_time")));
  }

  Object payload(Object... params) throws Exception {
    FullQA c = this;
    Object marker = null;
    Object options = null;
    Object plan = null;
    Object rid = null;
    rid = qarg(params, 0);
    marker = qarg(params, 1);
    plan = qarg(params, 2);
    options = qm("time_to_live", 30, "third_party_channel", qm("distribution_new", "mtpush"));
    if (qyes(qnonempty(plan))) {
      qput(options, "plan_id", plan);
    }
    return qm(
        "request_id",
        marker,
        "to",
        qm("registration_id", qarr(rid)),
        "body",
        qm(
            "platform",
            qarr("android"),
            "notification",
            qm("alert", marker, "android", qm("title", marker)),
            "options",
            options));
  }

  Object scheduleOne(Object... params) throws Exception {
    FullQA c = this;
    Object created = null;
    Object kind = null;
    Object param = null;
    Object pid = null;
    Object push = null;
    Object raw = null;
    Object rid = null;
    Object sid = null;
    Object trigger = null;
    Object when = null;
    kind = qarg(params, 0);
    when = qarg(params, 1);
    rid = qarg(params, 2);
    pid = qarg(params, 3);
    trigger = qm("single", qm("time", when, "zone_type", 0));
    if (qyes(qeq(kind, "intelligent"))) {
      trigger = qm("intelligent", qm("backup_time", when));
    }
    push = c.payload(rid, qcat(qcat(c.prefix, "_"), qtxt(kind)), pid);
    qdrop(push, "request_id");
    param =
        qm(
            "name",
            qcat(qcat(c.prefix, "_"), qtxt(kind)),
            "enabled",
            true,
            "trigger",
            trigger,
            "push",
            push);
    created = c.sdk("schedule_create", qarr(param));
    sid = qg(created, "data", "schedule_id");
    raw = qm();
    if (qyes(qnonempty(sid))) {
      c.own("schedule", sid);
      raw = c.rest("GET", qcat("/v4/schedules/", qtxt(sid)), null);
    }
    return qm(
        "sdk",
        created,
        "id",
        sid,
        "param",
        param,
        "read",
        raw,
        "detail",
        qfirstOrSelf(qg(raw, "data")));
  }

  Object prepareSchedules(Object... params) throws Exception {
    FullQA c = this;
    Object afterDelete = null;
    Object afterUpdate = null;
    Object deadline = null;
    Object delete = null;
    Object deleted = null;
    Object deletedMessages = null;
    Object detail = null;
    Object disabledMessages = null;
    Object known = null;
    Object listed = null;
    Object messages = null;
    Object pid = null;
    Object positive = null;
    Object positiveIDs = null;
    Object positiveReady = null;
    Object records = null;
    Object rid = null;
    Object sid = null;
    Object smart = null;
    Object tomorrow = null;
    Object update = null;
    Object updateParam = null;
    Object updated = null;
    Object when = null;
    rid = c.targetRID(0);
    pid = qcat(c.prefix, "_schedule_plan");
    c.seedPlan(pid, c.prefix);
    deadline = qadd(c.now(), qmaxi(45, c.envNumber("ENGAGELAB_SCHEDULE_DELAY", 45)));
    when = c.dateTime(deadline);
    tomorrow = c.dateTime(qadd(deadline, 86400));
    positive = c.scheduleOne("positive", when, rid, pid);
    update = c.scheduleOne("update", when, rid, pid);
    delete = c.scheduleOne("delete", when, rid, pid);
    smart = c.scheduleOne("intelligent", tomorrow, rid, pid);
    sid = qg(positive, "id");
    detail = c.sdk("schedule_get", qarr(sid));
    listed = c.listOwnedSchedule(sid);
    updateParam = qclone(qg(update, "param"));
    qput(updateParam, "enabled", false);
    qput(updateParam, "name", qcat(qtxt(qg(updateParam, "name")), "_updated"));
    qput(qg(updateParam, "push", "body", "notification"), "alert", qcat(c.prefix, "_updated"));
    updated = c.sdk("schedule_update", qarr(qg(update, "id"), updateParam));
    afterUpdate = c.rest("GET", qcat("/v4/schedules/", qtxt(qg(update, "id"))), null);
    deleted = c.sdk("schedule_delete", qarr(qg(delete, "id")));
    afterDelete = c.rest("GET", qcat("/v4/schedules/", qtxt(qg(delete, "id"))), null);
    c.sleepUntil(qadd(deadline, qmaxi(15, c.envNumber("ENGAGELAB_SCHEDULE_OBSERVATION", 15))));
    c.waitFor("schedule", sid);
    messages = c.sdk("schedule_msgs", qarr(sid));
    records = qg(messages, "data", "MsgIds");
    positiveIDs = c.scheduleIDs(records);
    positiveReady = qnonempty(positiveIDs);
    disabledMessages =
        c.rest("GET", qcat(qcat("/v4/schedules/", qtxt(qg(update, "id"))), "/msg-ids"), null);
    deletedMessages =
        c.rest("GET", qcat(qcat("/v4/schedules/", qtxt(qg(delete, "id"))), "/msg-ids"), null);
    known = qarr(qg(positive, "id"), qg(update, "id"));
    if (qyes(qeq(qg(smart, "read", "status"), 200))) {
      qappend(known, qg(smart, "id"));
    }
    return qm(
        "create",
        qboth(
            qyes(qg(positive, "sdk", "ok")),
            qyes(c.scheduleMatches(qg(positive, "detail"), qg(positive, "param")))),
        "create_ready",
        true,
        "get",
        qboth(
            qyes(qg(detail, "ok")),
            qyes(c.scheduleMatches(qfirstOrSelf(qg(detail, "data")), qg(positive, "param")))),
        "get_ready",
        true,
        "list",
        qboth(
            qeq(qg(listed, "status"), 200),
            qincludes(qids(qg(listed, "data", "schedules"), "schedule_id"), sid),
            qyes(c.scheduleListMatches(qg(listed, "data"), qg(listed, "raw")))),
        "list_ready",
        true,
        "intelligent",
        qboth(
            qyes(qg(smart, "sdk", "ok")),
            qeq(qg(smart, "detail", "trigger", "intelligent", "backup_time"), tomorrow)),
        "intelligent_ready",
        qeq(qg(smart, "read", "status"), 200),
        "messages",
        qboth(
            qeq(qg(messages, "status"), 200),
            positiveReady,
            qeq(qnum(qg(messages, "data", "count")), qsize(records)),
            qeq(records, qg(messages, "raw", "MsgIds")),
            c.waitFor("messages", positiveIDs)),
        "messages_ready",
        positiveReady,
        "update",
        qboth(
            qyes(qg(updated, "ok")),
            qyes(c.scheduleMatches(qfirstOrSelf(qg(afterUpdate, "data")), updateParam)),
            qeq(
                qsize(
                    qcoalesce(
                        qg(disabledMessages, "data", "msg_ids"),
                        qg(disabledMessages, "data", "msgIds"))),
                0)),
        "update_ready",
        positiveReady,
        "delete",
        qboth(
            qyes(qg(deleted, "ok")),
            qeq(qg(afterDelete, "status"), 404),
            qeither(
                qeq(qg(deletedMessages, "status"), 404),
                qeq(
                    qsize(
                        qcoalesce(
                            qg(deletedMessages, "data", "msg_ids"),
                            qg(deletedMessages, "data", "msgIds"))),
                    0))),
        "delete_ready",
        positiveReady);
  }

  Object prepareStatistics(Object... params) throws Exception {
    FullQA c = this;
    Object batch = null;
    Object batchReady = null;
    Object end = null;
    Object i = null;
    Object life = null;
    Object lifeMID = null;
    Object lifeRID = null;
    Object lifeReady = null;
    Object linked = null;
    Object linkedIDs = null;
    Object message = null;
    Object messageReady = null;
    Object mid = null;
    Object mids = null;
    Object p = null;
    Object pids = null;
    Object plan = null;
    Object planReady = null;
    Object rids = null;
    Object sent = null;
    Object start = null;
    Object users = null;
    Object v = null;
    rids = qarr(c.targetRID(0), c.targetRID(1));
    pids = qarr(qcat(c.prefix, "_stats_a"), qcat(c.prefix, "_stats_b"));
    mids = qarr();
    for (Object item7 : qlistOf(qspan(2))) {
      i = item7;
      c.seedPlan(qg(pids, i), c.prefix);
      sent =
          c.sdk(
              "push",
              qarr(c.payload(qg(rids, i), qcat(qcat(c.prefix, "_stats_"), qtxt(i)), qg(pids, i))));
      c.require(
          qboth(qyes(qg(sent, "ok")), qnonempty(qg(sent, "data", "msg_id"))),
          "Statistics requires accepted owned messages");
      qappend(mids, qtxt(qg(sent, "data", "msg_id")));
    }
    c.waitFor("messages", mids);
    start = c.date(qadd(c.now(), -172800));
    end = c.date(c.now());
    message = c.sdk("status", qarr(mids));
    lifeMID = qcoalesce(c.env("ENGAGELAB_LIFECYCLE_MESSAGE_ID", ""), qg(mids, 0));
    lifeRID = qcoalesce(c.env("ENGAGELAB_LIFECYCLE_REGISTRATION_ID", ""), qg(rids, 0));
    life = c.sdk("lifecycle", qarr(lifeMID, qarr(lifeRID)));
    batch = c.sdk("batch_lifecycle", qarr(mids));
    users = c.sdk("users", qarr("DAY", start, 3));
    plan = c.sdk("plan_status", qarr(pids, start, end));
    linked = c.sdk("plan_msgs", qarr(qg(pids, 0), start, end));
    lifeReady = c.lifecycleReady(qg(life, "raw", lifeRID));
    batchReady = qnonempty(qg(batch, "raw"));
    for (Object item8 : qlistOf(qlistOf(qg(batch, "raw")))) {
      v = item8;
      batchReady =
          qboth(
              batchReady,
              qincludes(mids, qtxt(qg(v, "message_id"))),
              qincludes(rids, qg(v, "registration_id")));
    }
    planReady = true;
    for (Object item9 : qlistOf(pids)) {
      p = item9;
      planReady =
          qboth(
              planReady,
              qgt(qnum(qcoalesce(qg(plan, "raw", p, "targets"), qg(plan, "raw", p, "target"))), 0));
    }
    messageReady = true;
    for (Object item10 : qlistOf(mids)) {
      mid = item10;
      messageReady = qboth(messageReady, qnonempty(qg(message, "raw", mid)));
    }
    linkedIDs = qg(linked, "data", qg(pids, 0), "msg_ids");
    return qm(
        "message",
        qboth(
            qeq(qg(message, "status"), 200),
            messageReady,
            qsame(qg(message, "data"), qg(message, "raw"))),
        "message_ready",
        messageReady,
        "lifecycle",
        qboth(
            qeq(qg(life, "status"), 200),
            qsame(qg(life, "data"), qg(life, "raw")),
            c.lifecycleDefaults(qg(life, "data", lifeRID), qg(life, "raw", lifeRID))),
        "lifecycle_ready",
        lifeReady,
        "batch_lifecycle",
        qboth(qeq(qg(batch, "status"), 200), qsame(qg(batch, "data"), qg(batch, "raw"))),
        "batch_lifecycle_ready",
        batchReady,
        "users",
        qboth(qeq(qg(users, "status"), 200), qsame(qg(users, "data"), qg(users, "raw"))),
        "users_ready",
        qhmosNonzero(qg(users, "raw")),
        "plan",
        qboth(qeq(qg(plan, "status"), 200), qsame(qg(plan, "data"), qg(plan, "raw"))),
        "plan_ready",
        planReady,
        "plan_messages",
        qboth(
            qeq(qg(linked, "status"), 200),
            qincludes(linkedIDs, qg(mids, 0)),
            qsame(qg(linked, "data"), qg(linked, "raw"))),
        "plan_messages_ready",
        qnonempty(linkedIDs));
  }

  Object invalidAuth(Object... params) throws Exception {
    FullQA c = this;
    return c.authControl();
  }

  Object apnsToken(Object... params) throws Exception {
    FullQA c = this;
    Object again = null;
    Object baseline = null;
    Object cleanup = null;
    Object config = null;
    Object created = null;
    Object parsed = null;
    Object payload = null;
    Object r = null;
    Object raw = null;
    Object rid = null;
    Object verified = null;
    config = c.apnsConfig();
    payload =
        qm(
            "platform",
            "ios",
            "tokens",
            qarr(qg(config, "token")),
            "apns_production",
            qg(config, "production"));
    r = c.sdk("token", qarr(payload));
    raw = qg(r, "raw", "results");
    parsed = qg(r, "data", "results");
    rid = qg(raw, 0, "registration_id");
    created = qyes(qg(raw, 0, "is_new"));
    if (qyes(qboth(qnonempty(rid), created))) {
      c.own("device", rid);
    }
    verified =
        qboth(
            qeq(qg(r, "status"), 200),
            qeq(qsize(raw), 1),
            qeq(qsize(parsed), 1),
            qnonempty(rid),
            qeq(qg(parsed, 0, "token"), qg(config, "token")),
            qeq(qg(parsed, 0, "registration_id"), rid),
            qeq(qg(parsed, 0, "is_new"), created),
            qeither(qeq(qg(parsed, 0, "code"), null), qeq(qg(parsed, 0, "code"), 0)),
            qeq(qg(r, "request", "apns_production"), qg(config, "production")));
    baseline = c.device(rid);
    again = c.sdk("token", qarr(payload));
    verified =
        qboth(
            verified,
            qeq(qg(baseline, "status"), 200),
            qeq(qg(again, "data", "results", 0, "registration_id"), rid),
            qno(qg(again, "data", "results", 0, "is_new")));
    cleanup = false;
    if (qyes(qboth(qnonempty(rid), created))) {
      c.rest("DELETE", qcat("/v4/devices/", qtxt(rid)), null);
      cleanup = c.waitFor("device_absent", rid);
    } else {
      if (qyes(qnonempty(rid))) {
        cleanup = qeq(c.device(rid), baseline);
      }
    }
    return qm("verified", verified, "cleanup", cleanup);
  }

  Object groupPush(Object... params) throws Exception {
    FullQA c = this;
    Object alias = null;
    Object app = null;
    Object apps = null;
    Object binding = null;
    Object config = null;
    Object k = null;
    Object parsed = null;
    Object payload = null;
    Object r = null;
    Object valid = null;
    config = c.groupConfig();
    apps = qg(config, "apps");
    alias = qg(config, "alias");
    c.require(qge(qsize(apps), 2), "Two explicit group members required");
    for (Object item11 : qlistOf(apps)) {
      app = item11;
      binding = c.memberAliasBinding(app, alias);
      c.require(
          qeq(qsize(qg(binding, "data", "registration_ids")), 1),
          "Group alias must resolve exclusively to one prepared test target per app");
    }
    payload =
        qm(
            "request_id",
            c.prefix,
            "to",
            qm("alias", qarr(alias)),
            "body",
            qm(
                "platform",
                "android",
                "notification",
                qm("alert", c.prefix),
                "options",
                qm("time_to_live", 30, "third_party_channel", qm("distribution_new", "mtpush"))));
    r = c.sdk("group", qarr(payload));
    for (Object item12 : qlistOf(qkeys(qg(r, "raw")))) {
      k = item12;
      c.require(
          qno(qeq(qg(r, "raw", k, "error", "code"), 21038)),
          "Group member lacks push permission: 21038");
    }
    parsed = qg(r, "data", "successes");
    valid =
        qboth(
            qeq(qg(r, "status"), 200),
            qsets(qkeys(parsed), apps),
            qeq(qsize(qg(r, "data", "errors")), 0),
            qnonempty(qg(r, "data", "group_msgid")),
            qeq(qg(r, "data", "group_msgid"), qg(r, "raw", "group_msgid")));
    for (Object item13 : qlistOf(apps)) {
      app = item13;
      valid =
          qboth(
              valid,
              qnonempty(qg(parsed, app, "msg_id")),
              qsame(qg(parsed, app), qg(r, "raw", app)));
    }
    return qm(
        "verified",
        valid,
        "explicit_target",
        qeq(qg(r, "request", "to"), qm("alias", qarr(alias))));
  }

  Object imageUpload(Object... params) throws Exception {
    FullQA c = this;
    Object r = null;
    Object resource = null;
    resource = c.imageResource();
    r = c.sdk("image", qarr(qm("big_picture_url", qg(resource, "url"))));
    return qm(
        "resource_valid",
        qg(resource, "valid"),
        "verified",
        qboth(
            qeq(qg(r, "status"), 200),
            qnonempty(qg(r, "data", "big_picture_id")),
            qeq(qg(r, "data", "big_picture_id"), qg(r, "raw", "big_picture_id"))),
        "prerequisite",
        qeq(qg(r, "status"), 200));
  }

  Object prepareVoice(Object... params) throws Exception {
    FullQA c = this;
    Object detail = null;
    Object fileURL = null;
    Object found = null;
    Object item = null;
    Object language = null;
    Object listed = null;
    Object path = null;
    Object r = null;
    Object upload = null;
    r = c.sdk("voices", qarr());
    c.require(qyes(qg(r, "ok")), "Voice list prerequisite unavailable");
    language = c.env("ENGAGELAB_VOICE_LANGUAGE", "en");
    c.require(
        qno(qincludes(qids(qg(r, "data"), "language"), language)),
        "Voice language occupied; existing template preserved");
    c.own("voice", language);
    c.state("voiceLanguage", language);
    path = c.voiceFile();
    upload = c.sdk("voice_create", qarr(language, path));
    fileURL = qg(upload, "data", "file_url");
    detail = c.sdk("voice_get", qarr(language));
    listed = c.sdk("voices", qarr());
    found = false;
    for (Object item14 : qlistOf(qlistOf(qg(listed, "data")))) {
      item = item14;
      found =
          qeither(
              found,
              qboth(qeq(qg(item, "language"), language), qeq(qg(item, "file_url"), fileURL)));
    }
    return qm(
        "upload_verified",
        qboth(
            qeq(qg(upload, "status"), 200),
            qnonempty(fileURL),
            qeq(fileURL, qg(upload, "raw", "file_url")),
            qyes(qg(upload, "multipart_valid"))),
        "detail_verified",
        qboth(qeq(qg(detail, "status"), 200), qeq(qg(detail, "data", "file_url"), fileURL)),
        "list_verified",
        qboth(qeq(qg(listed, "status"), 200), found));
  }

  Object deleteVoice(Object... params) throws Exception {
    FullQA c = this;
    Object detail = null;
    Object language = null;
    Object listed = null;
    Object r = null;
    language = c.state("voiceLanguage", null);
    r = c.sdk("voice_delete", qarr(language));
    detail = c.rest("GET", qcat("/v4/voices/", qtxt(language)), null);
    listed = c.sdk("voices", qarr());
    return qm(
        "sdk_ok",
        qg(r, "ok"),
        "verified",
        qboth(
            qeq(qg(detail, "status"), 404),
            qno(qincludes(qids(qg(listed, "data"), "language"), language))));
  }

  Object negativeTarget(Object... params) throws Exception {
    FullQA c = this;
    Object config = null;
    Object r = null;
    Object rid = null;
    c.write();
    rid = c.state("negativeRID", null);
    if (qyes(qno(qnonempty(rid)))) {
      config = c.apnsConfig();
      r =
          c.rest(
              "POST",
              "/v4/devices/token/registration_id",
              qm(
                  "platform",
                  "ios",
                  "tokens",
                  qarr(qg(config, "token")),
                  "apns_production",
                  qg(config, "production")));
      rid = qg(r, "data", "results", 0, "registration_id");
      c.require(
          qboth(qeq(qg(r, "status"), 200), qnonempty(rid)),
          "Cannot establish legal APNs sandbox validation target");
      if (qyes(qyes(qg(r, "data", "results", 0, "is_new")))) {
        c.own("device", rid);
      }
      c.state("negativeRID", rid);
    }
    return rid;
  }

  Object scheduleMatches(Object... params) throws Exception {
    FullQA c = this;
    Object actual = null;
    Object audience = null;
    Object body = null;
    Object push = null;
    Object want = null;
    actual = qarg(params, 0);
    want = qarg(params, 1);
    push = qg(actual, "push");
    audience = qcoalesce(qg(push, "to"), qg(push, "audience"));
    body = qcoalesce(qg(push, "body"), push);
    return qboth(
        qeq(qg(actual, "name"), qg(want, "name")),
        qeq(qg(actual, "enabled"), qg(want, "enabled")),
        qsame(qg(actual, "trigger"), qg(want, "trigger")),
        qsame(audience, qg(want, "push", "to")),
        qsame(body, qg(want, "push", "body")));
  }

  Object scheduleListMatches(Object... params) throws Exception {
    FullQA c = this;
    Object a = null;
    Object actual = null;
    Object b = null;
    Object i = null;
    Object k = null;
    Object raw = null;
    Object valid = null;
    actual = qarg(params, 0);
    raw = qarg(params, 1);
    valid =
        qboth(
            qeq(qg(actual, "page"), qg(raw, "page")),
            qeq(qg(actual, "total_count"), qg(raw, "total_count")),
            qeq(qg(actual, "total_pages"), qg(raw, "total_pages")),
            qeq(qsize(qg(actual, "schedules")), qsize(qg(raw, "schedules"))));
    for (Object item15 : qlistOf(qspan(qsize(qg(raw, "schedules"))))) {
      i = item15;
      a = qg(actual, "schedules", i);
      b = qg(raw, "schedules", i);
      for (Object item16 : qlistOf(qarr("schedule_id", "name", "enabled", "trigger", "push"))) {
        k = item16;
        valid = qboth(valid, qsame(qg(a, k), qg(b, k)));
      }
    }
    return valid;
  }

  Object listOwnedSchedule(Object... params) throws Exception {
    FullQA c = this;
    Object listed = null;
    Object pages = null;
    Object sid = null;
    sid = qarg(params, 0);
    listed = c.sdk("schedule_list", qarr(1));
    pages = qnum(qg(listed, "data", "total_pages"));
    if (qyes(
        qboth(
            qno(qincludes(qids(qg(listed, "data", "schedules"), "schedule_id"), sid)),
            qgt(pages, 1)))) {
      listed = c.sdk("schedule_list", qarr(pages));
      if (qyes(
          qboth(
              qno(qincludes(qids(qg(listed, "data", "schedules"), "schedule_id"), sid)),
              qgt(pages, 2)))) {
        listed = c.sdk("schedule_list", qarr(qadd(pages, -1)));
      }
    }
    return listed;
  }

  Object scheduleIDs(Object... params) throws Exception {
    FullQA c = this;
    Object decoded = null;
    Object item = null;
    Object mid = null;
    Object records = null;
    Object result = null;
    records = qarg(params, 0);
    result = qarr();
    for (Object item17 : qlistOf(qlistOf(records))) {
      item = item17;
      decoded = qdecodeJSON(qtxt(item));
      mid = qg(decoded, "msg_id");
      if (qyes(qeither(qeq(decoded, null), qisInteger(decoded)))) {
        mid = item;
      }
      if (qyes(qboth(qnonempty(mid), qno(qeq(qtxt(mid), "0"))))) {
        qappend(result, qtxt(mid));
      }
    }
    return result;
  }

  Object lifecycleReady(Object... params) throws Exception {
    FullQA c = this;
    Object record = null;
    record = qarg(params, 0);
    return qboth(
        qnonempty(record),
        qeither(
            qnonempty(qg(record, "status")),
            qboth(qgt(qnum(qg(record, "itime")), 0), qnonempty(qg(record, "channel"))),
            qboth(
                qgt(qnum(qg(record, "error_code")), 0),
                qno(qincludes(qarr(21003, 21061), qg(record, "error_code"))))));
  }

  Object lifecycleDefaults(Object... params) throws Exception {
    FullQA c = this;
    Object parsed = null;
    Object raw = null;
    Object valid = null;
    parsed = qarg(params, 0);
    raw = qarg(params, 1);
    valid = true;
    if (qyes(qno(qincludes(qkeys(raw), "error_code")))) {
      valid = qeither(qeq(qg(parsed, "error_code"), null), qeq(qg(parsed, "error_code"), 0));
    }
    if (qyes(qno(qincludes(qkeys(raw), "error_message")))) {
      valid = qboth(valid, qeq(qtxt(qg(parsed, "error_message")), ""));
    }
    return valid;
  }
}
