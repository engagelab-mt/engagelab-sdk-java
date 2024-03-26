package io.github.engagelab.bean.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulePushDetailGetResult {

    @JsonProperty("count")
    private Long count;

    // 返回的格式不规范，无法解析成对象
    @JsonProperty("MsgIds")
    private List<Object> details;

}
