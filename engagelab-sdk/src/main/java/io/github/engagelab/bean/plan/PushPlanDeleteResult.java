package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 推送计划删除响应结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushPlanDeleteResult {

    /**
     * 被删除的推送计划唯一标识符
     */
    @JsonProperty("plan_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planId;
}
