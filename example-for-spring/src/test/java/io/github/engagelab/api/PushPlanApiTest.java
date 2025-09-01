package io.github.engagelab.api;

import io.github.engagelab.bean.plan.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PushPlanApiTest {

    @Autowired
    private PushPlanApi pushPlanApi;

    @Test
    public void createOrUpdateTest() {
        PushPlanParam param = new PushPlanParam();
        param.setPlanId("push_20231001_001");
        param.setPlanDescription("双十一大促活动推送计划，覆盖全量用户");

        PushPlanResult result = pushPlanApi.createOrUpdate(param);
        log.info("创建推送计划结果: {}", result);
    }

    @Test
    public void updatePushPlanTest() {
        PushPlanParam param = new PushPlanParam();
        param.setPlanId("push_20231001_001");
        param.setPlanDescription("更新后的双十一大促活动推送计划，覆盖全量用户");

        PushPlanResult result = pushPlanApi.createOrUpdate(param);
        log.info("更新推送计划结果: {}", result);
    }

    @Test
    public void queryListTest() {
        // 1. 仅必填参数查询
        PushPlanListParam basicParam = new PushPlanListParam();
        basicParam.setPageIndex(1);
        basicParam.setPageSize(20);
        PushPlanListResult basicResult = pushPlanApi.queryList(basicParam);
        log.info("查询推送计划列表结果（仅必填参数）: {}", basicResult);

        // 2. 完整参数查询
        PushPlanListParam fullParam = new PushPlanListParam();
        fullParam.setPageIndex(1);
        fullParam.setPageSize(10);
        fullParam.setSendSource(0);
        fullParam.setSearchDescription("测试");
        PushPlanListResult fullResult = pushPlanApi.queryList(fullParam);
        log.info("查询推送计划列表结果（完整参数）: {}", fullResult);

        // 3. 部分可选参数查询
        PushPlanListParam partialParam = new PushPlanListParam();
        partialParam.setPageIndex(1);
        partialParam.setPageSize(20);
        partialParam.setSendSource(null);
        partialParam.setSearchDescription("双十一");
        PushPlanListResult partialResult = pushPlanApi.queryList(partialParam);
        log.info("查询推送计划列表结果（部分可选参数）: {}", partialResult);
    }

    @Test
    public void queryMsgByPlanWithParamTest() {
        PushPlanMsgQueryParam param = new PushPlanMsgQueryParam();
        param.setPlanIds("push_20231001_001");
        param.setStartDate(LocalDate.of(2025, 8, 20));
        param.setEndDate(LocalDate.of(2025, 9, 1));
        PushPlanMsgQueryResult result = pushPlanApi.queryMsgByPlan(param);
        log.info("根据推送计划查询消息ID结果（参数对象）: {}", result);
    }

    @Test
    public void deleteTest() {
        PushPlanDeleteParam param = new PushPlanDeleteParam();
        param.setPlanId("push_20231001_001");
        PushPlanDeleteResult result = pushPlanApi.delete(param);
        log.info("删除推送计划结果: {}", result);
    }

    @Test
    public void fullWorkflowTest() {
        // 完整工作流测试：创建 -> 查询 -> 更新 -> 查询消息 -> 删除

        // 1. 创建推送计划
        PushPlanParam createParam = new PushPlanParam();
        createParam.setPlanId("workflow_test_plan");
        createParam.setPlanDescription("工作流测试推送计划");

        PushPlanResult createResult = pushPlanApi.createOrUpdate(createParam);
        log.info("1. 创建推送计划: {}", createResult);

        // 2. 查询推送计划列表
        PushPlanListParam listParam = new PushPlanListParam();
        listParam.setPageIndex(1);
        listParam.setPageSize(10);
        listParam.setSendSource(1);
        listParam.setSearchDescription("工作流");
        PushPlanListResult listResult = pushPlanApi.queryList(listParam);
        log.info("2. 查询推送计划列表: {}", listResult);

        // 3. 更新推送计划
        createParam.setPlanDescription("更新后的工作流测试推送计划");
        PushPlanResult updateResult = pushPlanApi.createOrUpdate(createParam);
        log.info("3. 更新推送计划: {}", updateResult);

        // 4. 查询消息ID（可能为空，因为没有实际推送）
        PushPlanMsgQueryParam msgParam = new PushPlanMsgQueryParam();
        msgParam.setPlanIds("workflow_test_plan");
        msgParam.setStartDate(LocalDate.of(2025, 8, 20));
        msgParam.setEndDate(LocalDate.of(2025, 9, 1));
        PushPlanMsgQueryResult msgResult = pushPlanApi.queryMsgByPlan(msgParam);
        log.info("4. 查询消息ID: {}", msgResult);

        // 5. 删除推送计划
        PushPlanDeleteParam deleteParam = new PushPlanDeleteParam();
        deleteParam.setPlanId("workflow_test_plan");
        PushPlanDeleteResult deleteResult = pushPlanApi.delete(deleteParam);
        log.info("5. 删除推送计划: {}", deleteResult);
    }
}
