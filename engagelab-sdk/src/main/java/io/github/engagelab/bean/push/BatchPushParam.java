package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.engagelab.bean.push.message.custom.CustomMessage;
import io.github.engagelab.bean.push.message.liveactivity.LiveActivityMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
import io.github.engagelab.bean.push.to.To;
import io.github.engagelab.enums.Platform;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchPushParam {

    private List<BatchPushRequest> requests;

    @Data
    public static class BatchPushRequest {
        @JsonProperty("target")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String target;

        @JsonProperty("platform")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Platform platform;

        @JsonProperty("notification")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private NotificationMessage notification;

        @JsonProperty("message")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CustomMessage custom;

        @JsonProperty("options")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Options options;

        @JsonProperty("custom_args")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> customArgs;
    }
}
