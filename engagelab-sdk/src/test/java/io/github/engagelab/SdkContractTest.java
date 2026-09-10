package io.github.engagelab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.engagelab.api.AppApi;
import io.github.engagelab.api.DeviceApi;
import io.github.engagelab.api.GroupPushApi;
import io.github.engagelab.api.ImageApi;
import io.github.engagelab.api.PushApi;
import io.github.engagelab.api.StatusApi;
import io.github.engagelab.api.VoiceApi;
import io.github.engagelab.bean.app.AppVipStatusResult;
import io.github.engagelab.bean.device.DeviceTokenRegisterParam;
import io.github.engagelab.bean.device.DeviceTokenRegisterResult;
import io.github.engagelab.bean.image.ImageParam;
import io.github.engagelab.bean.push.BatchPushParam;
import io.github.engagelab.bean.push.BatchPushResult;
import io.github.engagelab.bean.push.GroupPushParam;
import io.github.engagelab.bean.push.GroupPushResult;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.message.custom.CustomMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
import io.github.engagelab.bean.status.MessageLifecycleGetResult;
import io.github.engagelab.bean.status.MessageStatusGetResult;
import io.github.engagelab.bean.status.PlanDetailGetParam;
import io.github.engagelab.enums.DataCenterHost;
import io.github.engagelab.enums.Platform;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SdkContractTest {
    private final MockWebServer server = new MockWebServer();
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void startServer() throws Exception {
        server.start();
    }

    @AfterEach
    void closeServer() throws Exception {
        server.shutdown();
    }

    private String host() {
        return server.url("/").toString().replaceAll("/$", "");
    }

    @Test
    void dataCenterConstantsMatchOfficialHosts() {
        assertEquals("https://pushapi-jpn.engagelab.com", DataCenterHost.JPN.getUrl());
        assertEquals("https://pushapi-bra.engagelab.com", DataCenterHost.BRA.getUrl());
    }

    @Test
    void pushValidateSerializesNewFields() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"msg_id\":\"m1\"}"));
        PushApi api = new PushApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();

        NotificationMessage.Android android = new NotificationMessage.Android();
        android.setBadgeSetNumber(0);
        android.setFold(false);
        NotificationMessage notification = new NotificationMessage();
        notification.setAndroid(android);
        CustomMessage message = new CustomMessage();
        message.setContent(Collections.singletonMap("key", "value"));
        message.setTestMessage(false);
        message.setReceiptId("receipt");
        Options options = new Options();
        options.setAutoTruncation(false);
        PushParam.Body body = new PushParam.Body();
        body.setPlatform("all");
        body.setCustom(message);
        body.setNotification(notification);
        body.setVoip(Collections.singletonMap("call_id", "1"));
        body.setOptions(options);
        PushParam param = new PushParam();
        param.setTo("all");
        param.setBody(body);

        api.validate(param);
        RecordedRequest request = server.takeRequest();
        assertEquals("/v4/push/validate", request.getPath());
        JsonNode json = mapper.readTree(request.getBody().readUtf8());
        assertFalse(json.at("/body/options/auto_truncation").asBoolean());
        assertEquals(0, json.at("/body/notification/android/badge_set_num").asInt());
        assertFalse(json.at("/body/notification/android/is_fold").asBoolean());
        assertEquals("1", json.at("/body/voip/call_id").asText());
        assertTrue(json.at("/body/message/msg_content").isObject());
    }

    @Test
    void deviceTokenAndAppVipContracts() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"results\":[{\"token\":\"t1\",\"registration_id\":\"r1\",\"is_new\":true,\"code\":0},{\"token\":\"\",\"is_new\":false,\"code\":21003,\"message\":\"invalid fcm token format\"}]}"));
        server.enqueue(new MockResponse().setBody("{\"vip_status\":1,\"vip_end_time\":1775059200}"));
        DeviceApi deviceApi = new DeviceApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();
        AppApi appApi = new AppApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();

        DeviceTokenRegisterParam param = new DeviceTokenRegisterParam();
        param.setPlatform("android");
        param.setTokens(Collections.singletonList("t1"));
        DeviceTokenRegisterResult tokenResult = deviceApi.registerToken(param);
        assertEquals("r1", tokenResult.getResults().get(0).getRegistrationId());
        assertEquals(21003, tokenResult.getResults().get(1).getCode());
        RecordedRequest tokenRequest = server.takeRequest();
        assertEquals("/v4/devices/token/registration_id", tokenRequest.getPath());

        AppVipStatusResult vip = appApi.getVipStatus();
        assertEquals(1, vip.getVipStatus());
        assertEquals(1775059200L, vip.getVipEndTime());
        assertEquals("/v4/app/vip/status", server.takeRequest().getPath());
    }

    @Test
    void voiceCreateUsesMultipart() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"language\":\"en\",\"file_url\":\"https://example.com/v.mp3\"}"));
        VoiceApi api = new VoiceApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();
        File file = File.createTempFile("voice-", ".mp3");
        try {
            Files.write(file.toPath(), "voice".getBytes("UTF-8"));
            assertEquals("https://example.com/v.mp3", api.create("en", file).getFileUrl());
        } finally {
            assertTrue(file.delete() || !file.exists());
        }
        RecordedRequest request = server.takeRequest();
        assertEquals("/v4/voices", request.getPath());
        assertTrue(request.getHeader("Content-Type").startsWith("multipart/form-data; boundary="));
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("name=\"language\""));
        assertTrue(body.contains("name=\"file\""));
    }

    @Test
    void tagCountUsesRepeatedTagQueryAndSinglePlatform() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"tagsCount\":{}}"));
        DeviceApi api = new DeviceApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();

        api.getTagCount(Arrays.asList("tag-a", "tag-b"), Platform.android);

        RecordedRequest request = server.takeRequest();
        assertEquals("tag-a", request.getRequestUrl().queryParameterValues("tags").get(0));
        assertEquals("tag-b", request.getRequestUrl().queryParameterValues("tags").get(1));
        assertEquals("android", request.getRequestUrl().queryParameter("platform"));
    }

    @Test
    void batchResponsePreservesPerTargetErrorAndRateLimit() {
        server.enqueue(new MockResponse().setBody("{\"rate_limit_info\":{\"message\":\"limited\",\"rate_limit_occurred\":true},\"results\":{\"r1\":{\"target\":\"r1\",\"success\":false,\"error\":{\"code\":23008,\"message\":\"Rate limit exceeded\"}}}}"));
        PushApi api = new PushApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();

        BatchPushResult result = api.batchPushByRegId(new BatchPushParam());

        assertTrue(result.getRateLimitInfo().getRateLimitOccurred());
        assertEquals(23008, result.getResults().get("r1").getError().getCode());
    }

    @Test
    void statusPlanAndBatchLifecycleUseOfficialContracts() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"plan-a\":{\"sub\":{\"notification\":{\"sub_hmos\":{\"harmonyos\":{\"delivered\":12}}},\"voip\":{\"delivered\":2}}}}"));
        server.enqueue(new MockResponse().setBody("[{\"message_id\":\"m1\",\"registration_id\":\"r1\",\"error_code\":1001,\"itime\":1775059200,\"channel\":\"FCM\"}]"));
        StatusApi api = new StatusApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();
        PlanDetailGetParam param = new PlanDetailGetParam();
        param.setPlanIds(Arrays.asList("plan-a", "plan-b"));
        param.setStartDate("2026-09-01");
        param.setEndDate("2026-09-02");

        Map<String, MessageStatusGetResult> plan = api.getPlanDetail(param);
        assertEquals(12, plan.get("plan-a").getSub().getNotification().getHmos().getHarmonyOS().getDeliver());
        RecordedRequest planRequest = server.takeRequest();
        assertEquals("plan-a,plan-b", planRequest.getRequestUrl().queryParameter("plan_ids"));
        assertEquals("2026-09-01", planRequest.getRequestUrl().queryParameter("start_date"));

        List<MessageLifecycleGetResult> lifecycle = api.getBatchMessageLifecycle(Arrays.asList("m1", "m2"));
        assertEquals(1001L, lifecycle.get(0).getErrorCode());
        assertEquals("m1,m2", server.takeRequest().getRequestUrl().queryParameter("message_ids"));
    }

    @Test
    void imageUsesJsonUrl() throws Exception {
        ImageApi api = new ImageApi.Builder().setHost(host()).setAppKey("key").setMasterSecret("secret").build();
        server.enqueue(new MockResponse().setBody("{\"big_picture_id\":\"big-1\"}"));
        ImageParam valid = new ImageParam();
        valid.setBigPictureUrl("https://example.com/big.jpg");
        assertEquals("big-1", api.uploadOppoImage(valid).getBigPictureId());
        RecordedRequest request = server.takeRequest();
        assertEquals("/v4/image/oppo", request.getPath());
        assertEquals("https://example.com/big.jpg", mapper.readTree(request.getBody().readUtf8()).get("big_picture_url").asText());
    }

    @Test
    void groupPushUsesAbsolutePathAndDecodesDynamicAppKeys() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"app-key-a\":{\"request_id\":\"req-1\",\"msg_id\":\"msg-1\"},\"group_msgid\":\"group-1\"}"));
        GroupPushApi api = new GroupPushApi.Builder().setHost(host()).setAppKey("group-key").setMasterSecret("secret").build();

        GroupPushResult result = api.push(new GroupPushParam());

        assertEquals("group-1", result.getGroupMsgId());
        assertEquals("msg-1", result.getSuccesses().get("app-key-a").getMsgId());
        assertEquals("/v4/grouppush", server.takeRequest().getPath());
    }
}
