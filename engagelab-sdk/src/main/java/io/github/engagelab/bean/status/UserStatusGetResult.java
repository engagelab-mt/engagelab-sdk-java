package io.github.engagelab.bean.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserStatusGetResult {

    @JsonProperty("TimeUnit")
    private String timeUnit;

    @JsonProperty("start")
    private String start;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("items")
    private List<Item> items;

    @Data
    public static class Item {
        @JsonProperty("time")
        private String time;

        @JsonProperty("android")
        private Platform android;

        @JsonProperty("ios")
        private Platform iOS;

        @Data
        public static class Platform {
            @JsonProperty("new")
            private Long newCount;

            @JsonProperty("active")
            private Long activeCount;

            @JsonProperty("online")
            private Long onlineCount;
        }
    }

}
