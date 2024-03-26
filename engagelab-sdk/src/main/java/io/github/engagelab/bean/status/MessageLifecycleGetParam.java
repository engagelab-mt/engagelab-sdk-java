package io.github.engagelab.bean.status;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class MessageLifecycleGetParam {

    @NonNull
    private String messageId;

    @NonNull
    private List<String> registrationIds;

}
