package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 根据推送计划查询消息ID请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushPlanMsgQueryParam {

    /**
     * 推送计划ID列表，多个ID用英文逗号分隔，最多支持1000个
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planIds;

    /**
     * 开始日期（格式：yyyy-MM-dd）
     * 必须满足：1. 当前日期前推30天内 2. 结束日期>=开始日期
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期（格式：yyyy-MM-dd）
     * 必须满足：1. 与开始日期间隔≤31天 2. 不早于开始日期
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
