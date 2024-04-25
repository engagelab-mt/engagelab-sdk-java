package io.github.engagelab.config;

import feign.Logger;
import io.github.engagelab.api.DeviceApi;
import io.github.engagelab.api.PushApi;
import io.github.engagelab.api.ScheduleApi;
import io.github.engagelab.api.StatusApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngagelabApiConfig {

    @Value("${engagelab.api.app-key}")
    private String appKey;

    @Value("${engagelab.api.master-secret}")
    private String masterSecret;

    @Bean
    public PushApi pushApi() {
        return new PushApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public DeviceApi deviceApi() {
        return new DeviceApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public StatusApi statusApi() {
        return new StatusApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public ScheduleApi scheduleApi() {
        return new ScheduleApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

}
