package io.github.engagelab.config;

import feign.Logger;
import feign.okhttp.OkHttpClient;
import io.github.engagelab.api.*;
import io.github.engagelab.enums.DataCenterHost;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class EngagelabApiConfig {

    @Value("${engagelab.api.app-key}")
    private String appKey;

    @Value("${engagelab.api.master-secret}")
    private String masterSecret;

    @Value("${engagelab.api.group-app-key}")
    private String groupAppKey;

    @Value("${engagelab.api.group-master-secret}")
    private String groupMasterSecret;

    @Bean
    public PushApi pushApi() {
        return new PushApi.Builder()
                .setHost(DataCenterHost.SG.getUrl())
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public DeviceApi deviceApi() {
        return new DeviceApi.Builder()
                .setHost(DataCenterHost.HK.getUrl())
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public StatusApi statusApi() {
        return new StatusApi.Builder()
                .setHost(DataCenterHost.SG.getUrl())
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public ScheduleApi scheduleApi() {
        return new ScheduleApi.Builder()
                .setHost(DataCenterHost.SG.getUrl())
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    // sdk use feign-okhttp default
    // okhttp config doc：https://square.github.io/okhttp/5.x/okhttp/okhttp3/-ok-http-client/-builder/index.html
    @Bean("okHttpClient")
    public OkHttpClient okHttpClient() {
        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient().newBuilder()
                // .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy_host", proxy_port))) // set proxy
                .connectTimeout(5, TimeUnit.SECONDS) // set connect timeout
                .build();
        return new OkHttpClient(okHttpClient);
    }

    @Bean
    public GroupPushApi groupPushApi(@Qualifier("okHttpClient") OkHttpClient okHttpClient) {
        return new GroupPushApi.Builder()
                .setHost(DataCenterHost.SG.getUrl())
                .setClient(okHttpClient)
                .setAppKey(groupAppKey)
                .setMasterSecret(groupMasterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

}
