package io.github.engagelab.bean.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulePushUpdateParam extends SchedulePushParam{

    private String scheduleId;

}
