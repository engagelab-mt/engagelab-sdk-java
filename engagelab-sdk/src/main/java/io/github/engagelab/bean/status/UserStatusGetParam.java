package io.github.engagelab.bean.status;

import io.github.engagelab.enums.TimeUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserStatusGetParam {

    @NonNull
    private TimeUnit timeUnit;

    @NonNull
    private String startTime;

    @NonNull
    private Integer duration;

}
