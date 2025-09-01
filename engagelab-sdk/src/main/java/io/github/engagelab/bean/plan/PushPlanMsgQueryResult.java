package io.github.engagelab.bean.plan;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据推送计划查询消息ID响应结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushPlanMsgQueryResult {

    /**
     * 推送计划对应的消息ID映射
     * key: 推送计划ID
     * value: 该计划对应的消息ID列表信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, PlanMsgInfo> results = new HashMap<>();

    /**
     * 处理动态键值对，将推送计划ID作为key，消息信息作为value
     * @param planId 推送计划ID
     * @param planMsgInfo 计划消息信息
     */
    @JsonAnySetter
    public void handleUnknown(String planId, PlanMsgInfo planMsgInfo) {
        results.put(planId, planMsgInfo);
    }

    /**
     * 计划消息信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlanMsgInfo {
        
        /**
         * 消息ID列表
         */
        @JsonProperty("msg_ids")
        private List<String> msgIds;
    }
}
