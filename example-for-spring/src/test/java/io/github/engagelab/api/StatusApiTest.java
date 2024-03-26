package io.github.engagelab.api;

import io.github.engagelab.bean.status.*;
import io.github.engagelab.enums.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class StatusApiTest {

    @Autowired
    private StatusApi statusApi;

    @Test
    public void getUserStatusTest() {
        UserStatusGetParam param = new UserStatusGetParam();
        param.setTimeUnit(TimeUnit.DAY);
        param.setStartTime("2024-01-01");
        param.setDuration(7);
        UserStatusGetResult result = statusApi.getUserStatus(param);
        log.info("getUserStatus result:{}", result);
    }

    @Test
    public void getMessageStatusTest() {
        MessageStatusGetParam param = new MessageStatusGetParam();
        param.setMessageIds(Arrays.asList("693834885", "693834884"));
        Map<String, MessageStatusGetResult> result = statusApi.getMessageStatus(param);
        log.info("getMessageStatus result:{}", result);
    }

    @Test
    public void getMessageLifecycleTest() {
        MessageLifecycleGetParam param = new MessageLifecycleGetParam();
        param.setMessageId("693834885");
        param.setRegistrationIds(Arrays.asList("13165ffa4e1a3e38068", "101d855909559c0dc45"));
        Map<String, MessageLifecycleGetResult> result = statusApi.getMessageLifecycle(param);
        log.info("getMessageLifecycle result:{}", result);
    }

}
