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
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DeviceApiTest {

    @Autowired
    private DeviceApi deviceApi;

    @Test
    public void getDeviceStatus() {
        DeviceStatusGetParam param = new DeviceStatusGetParam();
        param.setRegistrationIds(Arrays.asList("170976fa8a92af7c976"));
        List<DeviceStatusGetResult> deviceStatus = deviceApi.getDeviceStatus(param);
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

    @Test
    public void deleteDevice() {
        // 删除用户示例
        // 注意：
        // 1. 删除操作是异步执行的
        // 2. 将删除用户的所有相关数据，包括绑定的标签、别名、设备信息和时区信息
        // 3. 一旦删除用户，就无法将其恢复
        // 4. 不支持批量删除
        // 5. 请务必确认registration_id正确，删除前建议先查询标签和别名信息
        String registrationId = "18071adc031ee85262b"; // 替换为实际的registration_id
        
        deviceApi.deleteDevice(registrationId);
        log.info("deleteDevice result - success");
    }
}
