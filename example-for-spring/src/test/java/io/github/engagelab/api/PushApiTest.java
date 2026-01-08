package io.github.engagelab.api;

import io.github.engagelab.bean.push.BatchPushParam;
import io.github.engagelab.bean.push.BatchPushResult;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.PushWithdrawResult;
import io.github.engagelab.bean.push.PushResult;
import io.github.engagelab.bean.push.message.custom.CustomMessage;
import io.github.engagelab.bean.push.message.liveactivity.LiveActivityMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
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

        // 自定义
        // Options options = new Options();
        // 推送计划
        // options.setPlanId("push_20231001_001");
        // body.setOptions(options);

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

    @Test
    public void batchPushByRegIdTest() {
        BatchPushParam.BatchPushRequest request = new BatchPushParam.BatchPushRequest();
        request.setTarget("1a0018970ab68766688");
        request.setPlatform(Platform.android);

        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("批量推送测试-regId");

        request.setNotification(notificationMessage);

        BatchPushParam batchPushParam = new BatchPushParam() {{
            setRequests(Arrays.asList(request));
        }};
        BatchPushResult result = pushApi.batchPushByRegId(batchPushParam);
        log.info("result:{}", result);
    }

    @Test
    public void batchPushByAliasTest() {
        BatchPushParam.BatchPushRequest request = new BatchPushParam.BatchPushRequest();
        request.setTarget("test");
        request.setPlatform(Platform.android);

        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("批量推送测试-alias");

        request.setNotification(notificationMessage);

        BatchPushParam batchPushParam = new BatchPushParam() {{
            setRequests(Arrays.asList(request));
        }};
        BatchPushResult result = pushApi.batchPushByAlias(batchPushParam);
        log.info("result:{}", result);
    }

    @Test
    public void withdrawPushTest() {
        // 消息撤回示例
        // 注意：
        // 1. 只支持撤回一天内的消息
        // 2. 不支持重复撤回
        // 3. msgId 是推送时返回的消息ID
        String msgId = "7034983649"; // 替换为实际的消息ID

        PushWithdrawResult result = pushApi.withdrawPush(msgId);
        log.info("withdraw result - requestId:{}, msgId:{}", result.getRequestId(), result.getMsgId());
    }

    @Test
    public void pushHmosTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        // 鸿蒙通知消息配置
        NotificationMessage.Hmos hmos = new NotificationMessage.Hmos();

        // 必填字段
        hmos.setAlert("这是鸿蒙通知内容");
        hmos.setCategory("IM");  // 消息分类，需符合鸿蒙官方规则

        // Intent 配置（必填）
        NotificationMessage.Hmos.Intent intent = new NotificationMessage.Hmos.Intent();
        intent.setUrl("action.system.home");  // 跳转应用首页
        hmos.setIntent(intent);

        // 可选字段
        hmos.setTitle("鸿蒙推送标题");
        hmos.setLargeIcon("https://example.com/icon.png");  // 大图标 HTTPS 地址
        hmos.setBadgeAddNum(1);  // 角标累加 1
        hmos.setTestMessage(false);  // 非测试消息
        hmos.setDisplayForeground("1");  // 前台展示通知

        // 扩展字段
        Map<String, Object> extras = new HashMap<>();
        extras.put("key1", "value1");
        extras.put("key2", "value2");
        hmos.setExtras(extras);

        // 设置通知消息
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("通用通知内容");
        notificationMessage.setHmos(hmos);
        body.setNotification(notificationMessage);

        // 目标人群
        To to = new To();
        to.setRegistrationIdList(Arrays.asList("hmos_registration_id_1", "hmos_registration_id_2"));
        // param.setTo(to);
        param.setTo(ApiConstants.To.ALL);

        // 指定平台（鸿蒙平台需要添加到 Platform 枚举中）
        body.setPlatform(Collections.singletonList(Platform.hmos));

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("hmos push result:{}", result);
    }

    @Test
    public void pushHmosWithMultiLineTextTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        // 鸿蒙多行文本样式通知
        NotificationMessage.Hmos hmos = new NotificationMessage.Hmos();

        // 基础配置
        hmos.setAlert("多行文本通知");
        hmos.setTitle("多行文本标题");
        hmos.setCategory("IM");

        // Intent
        NotificationMessage.Hmos.Intent intent = new NotificationMessage.Hmos.Intent();
        intent.setUrl("scheme://app?page=chat");  // Deeplink 跳转
        hmos.setIntent(intent);

        // 多行文本样式
        hmos.setStyle(3);  // 3 表示多行文本样式
        hmos.setInboxContent(new String[]{
                "第一条消息内容",
                "第二条消息内容",
                "第三条消息内容"
        });

        // 角标设置
        hmos.setBadgeSetNum(5);  // 直接设置角标为 5

        // 设置通知消息
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("通用通知内容");
        notificationMessage.setHmos(hmos);
        body.setNotification(notificationMessage);

        // 目标人群：发送给所有人
        param.setTo(ApiConstants.To.ALL);

        // 指定鸿蒙平台
        body.setPlatform(Collections.singletonList(Platform.hmos));

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("hmos multi-line push result:{}", result);
    }

    @Test
    public void pushHmosWithVoIPTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        // 鸿蒙 VoIP 呼叫消息
        NotificationMessage.Hmos hmos = new NotificationMessage.Hmos();

        // 基础配置
        hmos.setAlert("VoIP 呼叫通知");
        hmos.setTitle("来电提醒");
        hmos.setCategory("VOIP");

        // Intent
        NotificationMessage.Hmos.Intent intent = new NotificationMessage.Hmos.Intent();
        intent.setUrl("com.example.voip.action");  // Action 跳转
        hmos.setIntent(intent);

        // VoIP 消息类型配置
        hmos.setPushType(10);  // 10 表示 VoIP 呼叫消息
        hmos.setExtraData("{\"callerId\":\"user123\",\"callerName\":\"张三\"}");  // VoIP 必填扩展数据

        // 回执配置
        hmos.setReceiptId("receipt_voip_001");

        // 前台展示
        hmos.setDisplayForeground("1");

        // 设置通知消息
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("VoIP 呼叫");
        notificationMessage.setHmos(hmos);
        body.setNotification(notificationMessage);

        // 目标人群：发送给所有人
        param.setTo(ApiConstants.To.ALL);

        // 指定鸿蒙平台
        body.setPlatform(Collections.singletonList(Platform.hmos));

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("hmos voip push result:{}", result);
    }

    @Test
    public void pushHmosExtendedNotificationTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        // 鸿蒙通知拓展消息
        NotificationMessage.Hmos hmos = new NotificationMessage.Hmos();

        // 基础配置
        hmos.setAlert("通知拓展消息");
        hmos.setTitle("拓展消息标题");
        hmos.setCategory("PROMOTION");

        // Intent
        NotificationMessage.Hmos.Intent intent = new NotificationMessage.Hmos.Intent();
        intent.setUrl("action.system.home");
        hmos.setIntent(intent);

        // 通知拓展消息类型
        hmos.setPushType(2);  // 2 表示通知拓展消息
        hmos.setExtraData("{\"promotionId\":\"promo_2026\",\"discount\":\"20%\"}");  // 拓展消息必填

        // 大图标
        hmos.setLargeIcon("https://cdn.example.com/promo-icon.png");

        // 测试消息标识
        hmos.setTestMessage(true);  // 标记为测试消息

        // 扩展字段
        Map<String, Object> extras = new HashMap<>();
        extras.put("campaign_id", "spring_sale_2026");
        extras.put("expire_time", "2026-02-01");
        hmos.setExtras(extras);

        // 设置通知消息
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("促销活动");
        notificationMessage.setHmos(hmos);
        body.setNotification(notificationMessage);

        // 目标人群：发送给所有人
        param.setTo(ApiConstants.To.ALL);

        // 指定鸿蒙平台
        body.setPlatform(Collections.singletonList(Platform.hmos));

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("hmos extended notification push result:{}", result);
    }

    @Test
    public void pushHmosWithSegTest() {
        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        // 鸿蒙通知消息配置
        NotificationMessage.Hmos hmos = new NotificationMessage.Hmos();

        // 必填字段
        hmos.setAlert("用户分群推送通知");
        hmos.setTitle("分群推送");
        hmos.setCategory("IM");

        // Intent 配置（必填）
        NotificationMessage.Hmos.Intent intent = new NotificationMessage.Hmos.Intent();
        intent.setUrl("action.system.home");
        hmos.setIntent(intent);

        // 可选字段
        hmos.setBadgeAddNum(1);
        hmos.setDisplayForeground("1");

        // 扩展字段
        Map<String, Object> extras = new HashMap<>();
        extras.put("segment_type", "test_segment");
        extras.put("campaign", "spring_promotion");
        hmos.setExtras(extras);

        // 设置通知消息
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert("用户分群推送");
        notificationMessage.setHmos(hmos);
        body.setNotification(notificationMessage);

        // 目标人群：使用用户分群
        To to = new To();
        To.Seg seg = new To.Seg();
        seg.setId("6acd5efbf2a9b9179c1eeb79");  // 替换为实际的分群 ID
        to.setSeg(seg);
        param.setTo(to);

        // 指定鸿蒙平台
        body.setPlatform(Collections.singletonList(Platform.hmos));

        // 发送
        param.setBody(body);
        PushResult result = pushApi.push(param);
        log.info("hmos segment push result:{}", result);
    }
}
