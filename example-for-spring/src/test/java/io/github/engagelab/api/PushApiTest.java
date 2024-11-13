package io.github.engagelab.api;

import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.PushResult;
import io.github.engagelab.bean.push.message.liveactivity.LiveActivityMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.to.To;
import io.github.engagelab.constants.ApiConstants;
import io.github.engagelab.enums.Platform;
import io.github.engagelab.enums.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PushApiTest {

    @Autowired
    private PushApi pushApi;

    @Test
    public void pushTst() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();
        // 通知内容
        NotificationMessage.Android android = new NotificationMessage.Android();
        android.setAlert("this is android alert");
        android.setTitle("this is android title");
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("this is alert");
        notificationMessage.setAndroid(android);
        body.setNotification(notificationMessage);

        // 目标人群
        To to = new To();
        to.setRegistrationIdList(Arrays.asList("1104a89793af2cfc030", "1104a89793af2cfc030"));
        // 指定目标
        // param.setTo(to);
        // 或者发送所有人
        param.setTo(ApiConstants.To.ALL);

        // 指定平台
        body.setPlatform(Arrays.asList(Platform.android, Platform.ios));
        // 或者发送所有平台
        // param.setPlatform(ApiConstants.Platform.ALL);

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("result:{}", result);
    }

    @Test
    public void liveActivityTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();
        LiveActivityMessage liveActivityMessage = new LiveActivityMessage();
        LiveActivityMessage.LiveActivityIOS iOS = new LiveActivityMessage.LiveActivityIOS();
        iOS.setEvent(Event.update);

        Map<String, Object> contentState = new HashMap<>();
        contentState.put("eventStr", "xxxxxxx");
        contentState.put("eventTime", System.currentTimeMillis());
        iOS.setContentState(contentState);

        liveActivityMessage.setIOS(iOS);

        // 填充消息
        body.setLiveActivity(liveActivityMessage);
        // 指定平台
        body.setPlatform(Collections.singletonList(Platform.ios));

        // 目标人群
        To to = new To();
        to.setLiveActivityId("LiveActivity-2");
        param.setTo(to);

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("result:{}", result);
    }

}
