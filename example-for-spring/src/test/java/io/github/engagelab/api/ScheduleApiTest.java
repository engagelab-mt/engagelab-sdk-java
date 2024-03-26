package io.github.engagelab.api;

import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.to.To;
import io.github.engagelab.bean.schedule.*;
import io.github.engagelab.constants.ApiConstants;
import io.github.engagelab.enums.Platform;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduleApiTest {

    @Autowired
    private ScheduleApi scheduleApi;

    @Test
    public void schedulePushTest() {
        SchedulePushParam param = new SchedulePushParam();
        param.setName("schedule_test");
        param.setEnabled(true);
        param.setTrigger(buildTrigger());
        param.setPush(buildPush());

        SchedulePushResult result = scheduleApi.schedulePush(param);
        log.info("schedulePush result:{}", result);
    }

    @Test
    public void updateSchedulePushTest() {
        String scheduleId = "dd4da991-8578-4fe2-aa3f-50a26bd6354a";
        SchedulePushUpdateParam param = new SchedulePushUpdateParam();
        param.setScheduleId(scheduleId);
        param.setName("schedule_test_update");
        param.setEnabled(true);
        param.setTrigger(buildTrigger());
        param.setPush(buildPush());
        SchedulePushUpdateResult result = scheduleApi.updateSchedulePush(param);
        log.info("updateSchedulePush result:{}", result);
    }

    @Test
    public void deleteSchedulePushTest() {
        String scheduleId = "dd4da991-8578-4fe2-aa3f-50a26bd6354a";
        scheduleApi.deleteSchedulePush(scheduleId);
        log.info("deleteSchedulePush success");
    }

    @Test
    public void getSchedulePushTest() {
        String scheduleId = "f76e4c02-2a10-4e56-b9e8-af697618c09e";
        List<SchedulePushGetResult> result = scheduleApi.getSchedulePush(scheduleId);
        log.info("getSchedulePush result:{}", result);
    }

    @Test
    public void listSchedulePushTest() {
        SchedulePushListParam param = new SchedulePushListParam();
        param.setPage(1);
        SchedulePushListResult result = scheduleApi.listSchedulePush(param);
        log.info("listSchedulePush result:{}", result);
    }

    @Test
    public void getSchedulePushDetailTest() {
        String scheduleId = "f76e4c02-2a10-4e56-b9e8-af697618c09e";
        SchedulePushDetailGetParam param = new SchedulePushDetailGetParam();
        param.setScheduleId(scheduleId);
        SchedulePushDetailGetResult result = scheduleApi.getSchedulePushDetail(param);
        log.info("getSchedulePushDetail result:{}", result);
    }

    private SchedulePushParam.Trigger buildTrigger() {
        SchedulePushParam.Trigger.Single single = new SchedulePushParam.Trigger.Single();
        single.setTime(LocalDateTime.now().plusDays(1));
        single.setZoneType(1);

        SchedulePushParam.Trigger trigger = new SchedulePushParam.Trigger();
        trigger.setSingle(single);
        return trigger;
    }

    private PushParam buildPush() {
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

        param.setBody(body);
        return param;
    }

}
