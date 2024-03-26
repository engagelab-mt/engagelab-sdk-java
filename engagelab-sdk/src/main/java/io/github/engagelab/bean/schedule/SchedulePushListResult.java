package io.github.engagelab.bean.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulePushListResult {

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("total_pages")
    private Integer totalPage;

    @JsonProperty("page")
    private Integer currentPage;

    @JsonProperty("schedules")
    private List<Detail> schedules;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        @JsonProperty("schedule_id")
        private String scheduleId;

        @JsonProperty("name")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String name;

        @JsonProperty("enabled")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean enabled;

        // 返回的格式不规范，无法解析成对象
        @JsonProperty("trigger")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object trigger;

        // 返回的格式不规范，无法解析成对象
        @JsonProperty("push")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object push;
    }

}
