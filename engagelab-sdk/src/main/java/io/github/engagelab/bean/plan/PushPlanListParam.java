package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推送计划列表查询请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushPlanListParam {

    /**
     * 分页页码（从 1 开始计数）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageIndex;

    /**
     * 每页数据条数，最大支持 100 条
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    /**
     * 发送来源标识：0-API，1-Web控制台
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer sendSource;

    /**
     * 模糊匹配计划描述或计划ID（支持中英文、数字、下划线）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String searchDescription;
}
