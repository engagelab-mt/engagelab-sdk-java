package io.github.engagelab.bean.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageLifecycleGetResult {

    @JsonProperty("message_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String messageId;

    @JsonProperty("registration_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String registrationId;

    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    @JsonProperty("error_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

    @JsonProperty("error_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long errorCode;

    @JsonProperty("itime")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long itime;

    @JsonProperty("channel")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String channel;

}
