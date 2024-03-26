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

        @JsonProperty("small_icon_uri")
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

}
