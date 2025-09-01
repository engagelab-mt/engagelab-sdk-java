package io.github.engagelab.bean.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推送计划删除请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushPlanDeleteParam {

    /**
     * 推送计划唯一标识符
     */
    private String planId;
}