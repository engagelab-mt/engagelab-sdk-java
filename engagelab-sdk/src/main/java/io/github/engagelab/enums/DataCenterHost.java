package io.github.engagelab.enums;

import lombok.Getter;

public enum DataCenterHost {
    SG("SG", "https://pushapi-sgp.engagelab.com"),
    HK("HK", "https://pushapi-hk.engagelab.com"),
    VA("VA", "https://pushapi-usva.engagelab.com"),
    FFM("FFM", "https://pushapi-defra.engagelab.com");

    private final String area;

    @Getter
    private final String url;

    DataCenterHost(String area, String url) {
        this.area = area;
        this.url = url;
    }
}