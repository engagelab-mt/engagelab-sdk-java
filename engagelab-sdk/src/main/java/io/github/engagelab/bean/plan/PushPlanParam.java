package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 推送计划创建/更新请求参数
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushPlanParam {

    /**
     * 推送计划唯一标识符
     * 格式规则：字母（区分大小写）、数字、下划线组合，禁止以下划线开头
     * 长度限制：最大 50 字符
     */
    @JsonProperty("plan_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planId;

    /**
     * 推送计划描述信息
     * 内容要求：需包含推送场景、目标用户、推送内容等业务关键信息
     * 长度建议：不超过 128 字符
     */
    @JsonProperty("plan_description")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planDescription;
}
