package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.engagelab.bean.push.message.custom.CustomMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushParam {

    @JsonProperty("from")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String from;

    @JsonProperty("to")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object to;

    @JsonProperty("request_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestId;

    @JsonProperty("custom_args")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> customArgs;

    @JsonProperty("body")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("platform")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object platform;

        @JsonProperty("notification")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private NotificationMessage notification;

        @JsonProperty("message")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CustomMessage custom;

        @JsonProperty("options")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Options options;
    }

}
