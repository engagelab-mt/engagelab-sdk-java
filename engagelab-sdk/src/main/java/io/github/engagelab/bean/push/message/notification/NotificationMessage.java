package io.github.engagelab.bean.push.message.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationMessage {

    @JsonProperty("alert")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alert;

    @JsonProperty("android")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Android android;

    @JsonProperty("ios")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private IOS ios;

    @JsonProperty("hmos")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Hmos hmos;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Android {
        @JsonProperty("alert")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String alert;

        @JsonProperty("title")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String title;

        @JsonProperty("builder_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer builderId;

        @JsonProperty("channel_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String channelId;

        @JsonProperty("priority")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer priority;

        @JsonProperty("category")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;

        @JsonProperty("style")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer style;

        @JsonProperty("big_text")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String bigText;

        @JsonProperty("inbox")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> inbox;

        @JsonProperty("big_pic_path")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String bigPicture;

        @JsonProperty("extras")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> extras;

        @JsonProperty("intent")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Intent intent;

        @JsonProperty("large_icon")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String largeIcon;

        @JsonProperty("small_icon")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String smallIcon;

        @JsonProperty("sound")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String sound;

        @JsonProperty("badge_add_num")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer badgeAddNumber;

        @JsonProperty("badge_class")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String badgeClass;

        @JsonProperty("display_foreground")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String displayForeground;

        @JsonProperty("group_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String groupId;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Intent {
            @JsonProperty("url")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String url;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IOS {
        @JsonProperty("alert")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object alert;

        @JsonProperty("sound")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object sound;

        @JsonProperty("badge")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object badge;

        @JsonProperty("content-available")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean contentAvailable;

        @JsonProperty("mutable-content")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean mutableContent;

        @JsonProperty("category")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;

        @JsonProperty("extras")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> extras;

        @JsonProperty("thread-id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String threadId;

        @JsonProperty("interruption-level")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String interruptionLevel;
    }

    /**
     * 鸿蒙（HarmonyOS）平台通知配置
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hmos {
        /**
         * 通知内容（必填）
         * <ul>
         *   <li>指定后会覆盖上级统一指定的 alert 信息</li>
         *   <li>可为空字符串，表示不展示到通知栏</li>
         *   <li>各推送通道对此字段的限制详见推送限制</li>
         * </ul>
         */
        @JsonProperty("alert")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String alert;

        /**
         * 通知标题（可选）
         * <ul>
         *   <li>指定后，通知中原展示 App 名称的位置将展示该 title</li>
         *   <li>各推送通道对此字段的限制详见推送限制</li>
         * </ul>
         */
        @JsonProperty("title")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String title;

        /**
         * 通知栏消息分类条目（必填）
         * <ul>
         *   <li>厂商必填字段，实际效果依赖 ROM 对 category 的处理策略</li>
         *   <li>SDK 内部未进行必填校验，请开发者务必填写</li>
         *   <li>值需符合鸿蒙官方「云端 category」取值规则</li>
         * </ul>
         */
        @JsonProperty("category")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;

        /**
         * 通知栏大图标（可选）
         * <ul>
         *   <li>需传递 HTTPS 图片地址，例如：https://example.com/image.png</li>
         *   <li>图片大小 ≤ 30KB，长 × 宽 < 12800 像素</li>
         * </ul>
         */
        @JsonProperty("large_icon")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String largeIcon;

        /**
         * 指定跳转页面（必填）
         * <p>支持以下三种跳转方式：</p>
         * <ol>
         *   <li>应用首页：action.system.home</li>
         *   <li>Deeplink：scheme://test?key1=val1&key2=val2</li>
         *   <li>Action 跳转：com.test.action</li>
         * </ol>
         * <p>示例：{"url":"action.system.home"}</p>
         * <p>⚠️ 厂商必填字段，SDK 未校验</p>
         */
        @JsonProperty("intent")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Intent intent;

        /**
         * 角标累加值（可选）
         * <ul>
         *   <li>不填表示不改变角标</li>
         *   <li>取值范围：1–99</li>
         *   <li>新角标 = 原角标 + badge_add_num</li>
         *   <li>示例：原角标 2，设置 1 → 显示 3</li>
         * </ul>
         */
        @JsonProperty("badge_add_num")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer badgeAddNum;

        /**
         * 角标设置值（可选）
         * <ul>
         *   <li>不填表示不改变角标</li>
         *   <li>取值范围：0–99</li>
         *   <li>设置后直接覆盖原角标值</li>
         * </ul>
         */
        @JsonProperty("badge_set_num")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer badgeSetNum;

        /**
         * 测试消息标识（可选）
         * <ul>
         *   <li>false：正常消息（默认）</li>
         *   <li>true：测试消息</li>
         * </ul>
         */
        @JsonProperty("test_message")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean testMessage;

        /**
         * 华为回执 ID（可选）
         * <ul>
         *   <li>用于指定本次下行消息的回执配置</li>
         *   <li>可在鸿蒙回执参数配置中查看</li>
         * </ul>
         */
        @JsonProperty("receipt_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String receiptId;

        /**
         * 扩展字段（可选）
         * <p>自定义 JSON Key / Value，用于业务扩展</p>
         */
        @JsonProperty("extras")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> extras;

        /**
         * 通知栏样式类型（可选）
         * <ul>
         *   <li>默认值：0</li>
         *   <li>0：普通样式</li>
         *   <li>3：多行文本样式</li>
         * </ul>
         */
        @JsonProperty("style")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer style;

        /**
         * 多行文本内容（可选）
         * <ul>
         *   <li>仅当 style = 3 时必填</li>
         *   <li>最多支持 3 条</li>
         *   <li>单条最大长度 1024，超出以 ... 截断</li>
         * </ul>
         */
        @JsonProperty("inbox_content")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String[] inboxContent;

        /**
         * 推送类型（可选）
         * <ul>
         *   <li>默认值：0</li>
         *   <li>支持值：0 通知消息、2 通知拓展消息、10 VoIP 呼叫消息</li>
         *   <li>其它值将报错</li>
         * </ul>
         */
        @JsonProperty("push_type")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer pushType;

        /**
         * 扩展字段（可选）
         * <ul>
         *   <li>对应华为 extraData 字段</li>
         *   <li>当 push_type = 2 或 10 时必填</li>
         *   <li>push_type = 0 时忽略</li>
         * </ul>
         */
        @JsonProperty("extra_data")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String extraData;

        /**
         * 前台是否展示通知（可选）
         * <ul>
         *   <li>"1"：APP 前台时展示通知</li>
         *   <li>"0"：APP 前台时不展示通知</li>
         * </ul>
         */
        @JsonProperty("display_foreground")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String displayForeground;

        /**
         * 跳转页面配置
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Intent {
            /**
             * 跳转 URL
             * <p>支持三种格式：</p>
             * <ul>
             *   <li>应用首页：action.system.home</li>
             *   <li>Deeplink：scheme://test?key1=val1&key2=val2</li>
             *   <li>Action 跳转：com.test.action</li>
             * </ul>
             */
            @JsonProperty("url")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String url;
        }
    }

}
