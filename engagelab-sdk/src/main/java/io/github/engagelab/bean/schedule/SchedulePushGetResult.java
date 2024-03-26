package io.github.engagelab.bean.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulePushGetResult extends SchedulePushParam {

    @JsonProperty("schedule_id")
    private String scheduleId;

}
