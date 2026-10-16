package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TagStatusGetResult {
    @JsonProperty("result")
    private Boolean result;
}
