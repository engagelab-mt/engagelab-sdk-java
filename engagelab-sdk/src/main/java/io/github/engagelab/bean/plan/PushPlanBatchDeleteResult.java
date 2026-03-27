package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量删除推送计划响应结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushPlanBatchDeleteResult {

    /**
     * 被删除的推送计划标识符列表
     */
    @JsonProperty("plan_ids")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> planIds;
}
