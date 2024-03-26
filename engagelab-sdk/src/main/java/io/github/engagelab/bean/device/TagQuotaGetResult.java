package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TagQuotaGetResult {

    @JsonProperty("data")
    private Data data;

    @lombok.Data
    public static class Data {
        @JsonProperty("totalTagQuota")
        private Long totalTagQuota;

        @JsonProperty("useTagQuota")
        private Long useTagQuota;

        @JsonProperty("totalAliasQuota")
        private Long totalAliasQuota;

        @JsonProperty("useAliasQuota")
        private Long useAliasQuota;

        @JsonProperty("tagUidQuotaDetail")
        private List<TagUidQuotaDetail> tagUidQuotaDetails;

        @lombok.Data
        public static class TagUidQuotaDetail {
            @JsonProperty("tagName")
            private String tagName;

            @JsonProperty("totalUidQuota")
            private Long totalUidQuota;

            @JsonProperty("useUidQuota")
            private Long useUidQuota;
        }
    }

}
