package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.engagelab.bean.push.message.custom.CustomMessage;
import io.github.engagelab.bean.push.message.notification.NotificationMessage;
import io.github.engagelab.bean.push.options.Options;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupPushParam extends PushParam{

}
