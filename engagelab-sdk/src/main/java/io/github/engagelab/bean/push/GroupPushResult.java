package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.engagelab.exception.ApiErrorException;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupPushResult {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, ApiErrorException.ApiError.Error> errors = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, PushResult> successes = new HashMap<>();

    @JsonProperty("group_msgid")
    private String groupMsgId;

    @JsonAnySetter
    public void handleUnknown(String key, JsonNode value) {
        if (key.equals("group_msgid")) {
            setGroupMsgId(value.asText());
        } else if (value.has("error")) {
            ApiErrorException.ApiError.Error errorDetail = new ApiErrorException.ApiError.Error();
            errorDetail.setCode(value.get("error").get("code").asInt());
            errorDetail.setMessage(value.get("error").get("message").asText());
            errors.put(key, errorDetail);
        } else if (value.has("msg_id")) {
            PushResult successDetail = new PushResult();
            successDetail.setMsgId(value.get("msg_id").asText());
            successDetail.setRequestId(value.get("request_id").asText());
            successes.put(key, successDetail);
        }
    }

}
