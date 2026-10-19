package io.github.engagelab.nativecheck;

import static io.github.engagelab.nativecheck.NativeSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.engagelab.api.*;
import io.github.engagelab.bean.device.*;
import io.github.engagelab.bean.plan.*;
import io.github.engagelab.bean.push.*;
import java.util.*;
import okhttp3.mockwebserver.*;
import org.junit.jupiter.api.*;

class NativeContractTest {
  MockWebServer server;
  String host;

  @BeforeEach
  void start() throws Exception {
    server = new MockWebServer();
    server.start();
    host = server.url("/").toString().replaceAll("/$", "");
  }

  @AfterEach
  void stop() throws Exception {
    server.shutdown();
  }

  @Test
  void environmentParameters() {
    for (String target : Arrays.asList("qa", "prod")) {
      Map<String, String> e = new HashMap<>();
      e.put("TEST_ENV", target);
      e.put(
          "ENGAGELAB_BASE_URL",
          target.equals("qa") ? "http://api.qa.jpushoa.com" : "https://pushapi-sgp.engagelab.com");
      e.put("ENGAGELAB_APP_KEY", "fixture");
      e.put("ENGAGELAB_MASTER_SECRET", "fixture");
      e.put("ENGAGELAB_HTTP_TIMEOUT", "7");
      Config c = new Config(e);
      assertEquals(7, c.timeout);
      assertThrows(Blocked.class, c::write);
      e.put("TEST_ENV", target.equals("qa") ? "prod" : "qa");
      assertThrows(Blocked.class, () -> new Config(e));
    }
  }

  @Test
  void fieldPassthrough() throws Exception {
    server.enqueue(new MockResponse().setBody("{\"msg_id\":\"fixture-message\"}"));
    PushApi api =
        new PushApi.Builder().setHost(host).setAppKey("fixture").setMasterSecret("fixture").build();
    assertEquals("fixture-message", api.validate(payload("r1", "contract")).getMsgId());
    String body = server.takeRequest().getBody().readUtf8();
    assertTrue(JSON.readTree(body).at("/body/options/auto_truncation").isBoolean());
    assertFalse(JSON.readTree(body).at("/body/options/auto_truncation").asBoolean(true));
    assertEquals(0, JSON.readTree(body).at("/body/notification/android/badge_set_num").asInt(-1));
    assertFalse(JSON.readTree(body).at("/body/notification/android/is_fold").asBoolean(true));
  }

  @Test
  void page101() throws Exception {
    List<Map<String, String>> items = new ArrayList<>();
    for (int i = 0; i < 101; i++) items.add(Collections.singletonMap("plan_id", "p" + i));
    Map<String, Object> raw = new HashMap<>();
    raw.put("total", 101);
    raw.put("push_plan_info", items);
    server.enqueue(new MockResponse().setBody(JSON.writeValueAsString(raw)));
    PushPlanApi api =
        new PushPlanApi.Builder()
            .setHost(host)
            .setAppKey("fixture")
            .setMasterSecret("fixture")
            .build();
    PushPlanListResult result = api.queryList(new PushPlanListParam(1, 101, null, "fixture"));
    assertEquals(101, result.getTotal());
    assertEquals(101, result.getPushPlanInfo().size());
    assertTrue(server.takeRequest().getPath().contains("page_size=101"));
  }

  @Test
  void groupDynamicKeys() throws Exception {
    server.enqueue(
        new MockResponse()
            .setBody(
                "{\"group_msgid\":\"g1\",\"app-a\":{\"msg_id\":\"m1\",\"request_id\":\"r1\"},\"app-b\":{\"msg_id\":\"m2\",\"request_id\":\"r2\"}}"));
    GroupPushApi api =
        new GroupPushApi.Builder()
            .setHost(host)
            .setAppKey("fixture")
            .setMasterSecret("fixture")
            .build();
    GroupPushParam p = new GroupPushParam();
    p.setBody(payload("r1", "contract").getBody());
    p.setTo(Collections.singletonMap("registration_id", Collections.singletonList("r1")));
    GroupPushResult result = api.push(p);
    assertEquals("g1", result.getGroupMsgId());
    assertEquals(new HashSet<>(Arrays.asList("app-a", "app-b")), result.getSuccesses().keySet());
    assertTrue(result.getErrors().isEmpty());
    assertEquals("/v4/grouppush", server.takeRequest().getPath());
  }

  @Test
  void tokenSandbox() throws Exception {
    server.enqueue(
        new MockResponse()
            .setBody(
                "{\"results\":[{\"token\":\"fixture\",\"registration_id\":\"r1\",\"is_new\":false,\"code\":0}]}"));
    DeviceApi api =
        new DeviceApi.Builder()
            .setHost(host)
            .setAppKey("fixture")
            .setMasterSecret("fixture")
            .build();
    DeviceTokenRegisterParam p = new DeviceTokenRegisterParam();
    p.setPlatform("ios");
    p.setTokens(Collections.singletonList("fixture"));
    p.setApnsProduction(false);
    DeviceTokenRegisterResult result = api.registerToken(p);
    assertEquals("r1", result.getResults().get(0).getRegistrationId());
    assertEquals(false, result.getResults().get(0).getIsNew());
    assertFalse(
        JSON.readTree(server.takeRequest().getBody().readUtf8())
            .path("apns_production")
            .asBoolean(true));
  }
}
