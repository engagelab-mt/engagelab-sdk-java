package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推送计划列表查询响应结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushPlanListResult {

    /**
     * 推送计划信息列表
     */
    @JsonProperty("push_plan_info")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PushPlanInfo> pushPlanInfo;

    /**
     * 总记录数
     */
    @JsonProperty("total")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer total;

    /**
     * 推送计划信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PushPlanInfo {

        /**
         * 推送计划ID
         */
        @JsonProperty("push_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String pushId;

        /**
         * 推送计划描述
         */
        @JsonProperty("plan_description")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String planDescription;

        /**
         * 使用次数
         */
        @JsonProperty("count")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer count;

        /**
         * 创建时间，返回的是13位时间戳
         */
        @JsonProperty("create_time")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long createTime;

        /**
         * 最后使用时间，返回的是13位时间戳
         */
        @JsonProperty("last_used_time")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long lastUsedTime;
    }
}
