package io.github.engagelab;

import io.github.engagelab.api.ScheduleApi;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.schedule.SchedulePushGetResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ScheduleResponseCompatibilityTest {
    private final MockWebServer server = new MockWebServer();

    @BeforeEach
    void startServer() throws Exception {
        server.start();
    }

    @AfterEach
    void closeServer() throws Exception {
        server.shutdown();
    }

    private ScheduleApi scheduleApi() {
        String host = server.url("/").toString().replaceAll("/$", "");
        return new ScheduleApi.Builder()
                .setHost(host)
                .setAppKey("key")
                .setMasterSecret("secret")
                .build();
    }

    @Test
    void getSchedulePushNormalizesFlattenedPushResponse() throws Exception {
        server.enqueue(new MockResponse().setBody("[{\"schedule_id\":\"s1\",\"name\":\"test\",\"enabled\":true,"
                + "\"push\":{\"audience\":{\"registration_id\":[\"rid1\"]},\"platform\":\"android\","
                + "\"notification\":{\"alert\":\"hello\"},"
                + "\"options\":{\"time_to_live\":60,\"plan_id\":\"plan1\"}}}]")
                .addHeader("Content-Type", "application/json"));

        List<SchedulePushGetResult> results = scheduleApi().getSchedulePush("s1");

        assertEquals(1, results.size());
        PushParam push = results.get(0).getPush();
        assertNotNull(push);
        assertEquals("rid1", ((List<?>) ((Map<?, ?>) push.getTo()).get("registration_id")).get(0));
        assertNotNull(push.getBody());
        assertEquals("android", push.getBody().getPlatform());
        assertEquals("hello", push.getBody().getNotification().getAlert());
        assertEquals(60, push.getBody().getOptions().getTimeToLive());
        assertEquals("plan1", push.getBody().getOptions().getPlanId());

        RecordedRequest request = server.takeRequest();
        assertEquals("/v4/schedules/s1", request.getPath());
    }

    @Test
    void getSchedulePushStillAcceptsToBodyResponse() {
        server.enqueue(new MockResponse().setBody("[{\"schedule_id\":\"s1\",\"push\":{\"to\":\"all\","
                + "\"body\":{\"platform\":\"all\",\"notification\":{\"alert\":\"hello\"}}}}]")
                .addHeader("Content-Type", "application/json"));

        PushParam push = scheduleApi().getSchedulePush("s1").get(0).getPush();

        assertEquals("all", push.getTo());
        assertEquals("all", push.getBody().getPlatform());
        assertEquals("hello", push.getBody().getNotification().getAlert());
    }
}
