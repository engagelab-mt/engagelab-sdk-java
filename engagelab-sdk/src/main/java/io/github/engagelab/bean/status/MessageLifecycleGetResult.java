package io.github.engagelab.bean.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageLifecycleGetResult {

    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    @JsonProperty("error_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

}
