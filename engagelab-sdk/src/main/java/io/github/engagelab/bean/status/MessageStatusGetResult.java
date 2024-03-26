package io.github.engagelab.bean.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageStatusGetResult {

    @JsonProperty("targets")
    private Long target;

    @JsonProperty("sent")
    private Long sent;

    @JsonProperty("delivered")
    private Long deliver;

    @JsonProperty("impressions")
    private Long impression;

    @JsonProperty("clicks")
    private Long click;

    @JsonProperty("sub")
    private Sub sub;

    @Data
    public static class Sub {
        @JsonProperty("notification")
        private Message notification;

        @JsonProperty("message")
        private Message custom;

        @Data
        public static class Message {
            @JsonProperty("target")
            private Long target;

            @JsonProperty("sent")
            private Long sent;

            @JsonProperty("delivered")
            private Long deliver;

            @JsonProperty("impressions")
            private Long impression;

            @JsonProperty("click")
            private Long click;

            @JsonProperty("sub_android")
            private Android android;

            @JsonProperty("sub_ios")
            private IOS ios;

            @Data
            public static class Android {
                @JsonProperty("engageLab_android")
                private Channel engageLabAndroid;

                @JsonProperty("xiaomi")
                private Channel xiaomi;

                @JsonProperty("huawei")
                private Channel huawei;

                @JsonProperty("honor")
                private Channel honor;

                @JsonProperty("meizu")
                private Channel meizu;

                @JsonProperty("oppo")
                private Channel oppo;

                @JsonProperty("vivo")
                private Channel vivo;

                @JsonProperty("fcm")
                private Channel fcm;
            }

            @Data
            public static class IOS {
                @JsonProperty("engageLab_ios")
                private Channel engageLabIOS;

                @JsonProperty("apns")
                private Channel apns;
            }

            @Data
            public static class Channel {
                @JsonProperty("targets")
                private Long target;

                @JsonProperty("sent")
                private Long sent;

                @JsonProperty("delivered")
                private Long deliver;

                @JsonProperty("impressions")
                private Long impression;

                @JsonProperty("clicks")
                private Long click;
            }

        }

    }

}
