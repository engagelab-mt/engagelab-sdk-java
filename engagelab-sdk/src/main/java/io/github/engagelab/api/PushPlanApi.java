package io.github.engagelab.api;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.engagelab.bean.plan.*;
import io.github.engagelab.client.PushPlanClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import lombok.NonNull;

/**
 * 推送计划API
 * <a href="https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/push-plan-api">REST API - Push Plan</a>
 */
public class PushPlanApi {

    private final PushPlanClient pushPlanClient;

    protected PushPlanApi(@NonNull PushPlanClient pushPlanClient) {
        this.pushPlanClient = pushPlanClient;
    }

    /**
     * 创建或更新推送计划
     *
     * @param param 推送计划参数
     * @return 推送计划结果
     */
    public PushPlanResult createOrUpdate(@NonNull PushPlanParam param) {
        return pushPlanClient.createOrUpdate(param);
    }

    /**
     * 分页查询推送计划列表
     *
     * @param param 查询参数
     * @return 推送计划列表结果
     */
    public PushPlanListResult queryList(PushPlanListParam param) {
        return pushPlanClient.queryList(param.getPageIndex(), param.getPageSize(), param.getSendSource(), param.getSearchDescription());
    }

    /**
     * 根据推送计划查询消息ID（使用参数对象的便捷方法）
     *
     * @param param 查询参数
     * @return 推送计划对应的消息ID查询结果
     */
    public PushPlanMsgQueryResult queryMsgByPlan(@NonNull PushPlanMsgQueryParam param) {
        return pushPlanClient.queryMsgByPlan(param.getPlanIds(), param.getStartDate(), param.getEndDate());
    }

    /**
     * 删除推送计划
     *
     * @param param 删除参数
     * @return 删除结果
     */
    public PushPlanDeleteResult delete(PushPlanDeleteParam param) {
        return pushPlanClient.delete(param.getPlanId());
    }

    public static class Builder {

        private String host;
        private Client client = new OkHttpClient();
        private String appKey;
        private String masterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        // 设置主机地址
        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setClient(@NonNull Client client) {
            this.client = client;
            return this;
        }

        public Builder setAppKey(@NonNull String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setMasterSecret(@NonNull String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder setLoggerLevel(@NonNull Logger.Level loggerLevel) {
            this.loggerLevel = loggerLevel;
            return this;
        }

        public PushPlanApi build() {
            PushPlanClient pushPlanClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(PushPlanClient.class, host);
            return new PushPlanApi(pushPlanClient);
        }
    }
}
