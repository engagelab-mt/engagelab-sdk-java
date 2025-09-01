package io.github.engagelab.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.github.engagelab.bean.plan.*;

import java.time.LocalDate;

/**
 *<a href="https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/push-plan-api">REST API - Push Plan</a>
 */
public interface PushPlanClient {

    /**
     * 创建或更新推送计划
     * @param param 推送计划参数
     * @return 推送计划结果
     */
    @RequestLine("POST /v4/push_plan")
    @Headers("Content-Type: application/json; charset=utf-8")
    PushPlanResult createOrUpdate(PushPlanParam param);

    /**
     * 分页查询推送计划列表
     * @param pageIndex 分页页码（从1开始）
     * @param pageSize 每页数据条数，最大支持100条
     * @param sendSource 发送来源标识：0-API，1-Web控制台（可选，可传null）
     * @param searchDescription 模糊匹配计划描述或计划ID（可选，可传null）
     * @return 推送计划列表结果
     */
    @RequestLine("GET /v4/push_plan/list?page_index={pageIndex}&page_size={pageSize}&send_source={sendSource}&search_description={searchDescription}")
    PushPlanListResult queryList(@Param("pageIndex") Integer pageIndex, 
                                 @Param("pageSize") Integer pageSize,
                                 @Param("sendSource") Integer sendSource,
                                 @Param("searchDescription") String searchDescription);

    /**
     * 根据推送计划查询消息ID
     * @param planIds 推送计划ID列表，多个ID用英文逗号分隔，最多支持1000个
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 推送计划对应的消息ID查询结果
     */
    @RequestLine("GET /v4/status/plan/msg/?plan_ids={planIds}&start_date={startDate}&end_date={endDate}")
    PushPlanMsgQueryResult queryMsgByPlan(@Param("planIds") String planIds,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * 删除推送计划
     * @param planId 推送计划唯一标识符
     * @return 删除结果
     */
    @RequestLine("DELETE /v4/push_plan/{planId}")
    PushPlanDeleteResult delete(@Param("planId") String planId);
}
