package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 消息撤回结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushWithdrawResult {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("msg_id")
    private String msgId;
}
