package io.github.engagelab.api;

import io.github.engagelab.bean.device.*;
import io.github.engagelab.enums.Platform;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DeviceApiTest {

    @Autowired
    private DeviceApi deviceApi;

    @Test
    public void getDeviceStatus() {
        DeviceStatusGetResult deviceStatus = deviceApi.getDeviceStatus();
        log.info("deviceStatus:{}", deviceStatus);
    }

    @Test
    public void getDevice() {
        DeviceGetResult result = deviceApi.getDevice("13165ffa4e1a3e38068");
        log.info("getDevice result:{}", result);
    }

    @Test
    public void setDevice() {
        DeviceSetParam.Tags tags = new DeviceSetParam.Tags();
        tags.setAdd(Arrays.asList("13333333333", "13444444444"));
        DeviceSetParam param = new DeviceSetParam();
        param.setAlias("alias_test");
        param.setTags(tags);
        deviceApi.setDevice("13165ffa4e1a3e38068", param);
        log.info("setDevice success");
    }

    @Test
    public void getTags() {
        TagsGetResult result = deviceApi.getTags();
        log.info("getTags result:{}", result);
    }

    @Test
    public void setTag() {
        TagSetParam.RegistrationIds registrationIds = new TagSetParam.RegistrationIds();
        registrationIds.setAdd(Arrays.asList("13165ffa4e1a3e38068"));
        TagSetParam param = new TagSetParam();
        param.setRegistrationIds(registrationIds);
        deviceApi.setTag("13333333333", param);
        log.info("setTag success");
    }

    @Test
    public void deleteTag() {
        deviceApi.deleteTag("13333333333", Arrays.asList(Platform.android, Platform.ios));
        log.info("deleteTag success");
    }

    @Test
    public void getTagCount() {
        TagsCountGetResult result = deviceApi.getTagCount(Arrays.asList("13444444444", "13333333333"), Arrays.asList(Platform.android, Platform.ios));
        log.info("getTagCount:{}", result);
    }

    @Test
    public void getTag() {
        TagsGetResult result = deviceApi.getTagStatus("13333333333", "13165ffa4e1a3e38068");
        log.info("getTagStatus:{}", result);
    }

    @Test
    public void getTagStatus() {
        TagQuotaGetResult result = deviceApi.getTagQuota(Arrays.asList("13444444444", "13333333333"), Arrays.asList(Platform.android, Platform.ios));
        log.info("getTagStatus result:{}", result);
    }

    @Test
    public void getAliasStatus() {
        AliasStatusGetResult result = deviceApi.getAliasStatus("alias_test", Arrays.asList(Platform.android, Platform.ios));
        log.info("getAliasStatus result:{}", result);
    }

    @Test
    public void deleteAlas() {
        deviceApi.deleteAlas("alias_test", Arrays.asList(Platform.android, Platform.ios));
        log.info("deleteAlas success");
    }

}
