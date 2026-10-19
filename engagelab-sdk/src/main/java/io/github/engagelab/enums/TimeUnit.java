package io.github.engagelab.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TimeUnit {

    DAY("day", "为day时，point不能存在"),
    WEEK("WEEK", "为week时，point取值范围[MON,TUE,WED,THU,FRI,SAT,SUN]"),
    MONTH("MONTH", "为month时，point取值范围[01,02,03......31]");

    private String value;
    private String description;

    TimeUnit(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonCreator
    public static TimeUnit fromValue(String value) {
        for (TimeUnit timeUnit : values()) {
            if (timeUnit.name().equalsIgnoreCase(value) || timeUnit.value.equalsIgnoreCase(value)) {
                return timeUnit;
            }
        }
        throw new IllegalArgumentException("Unsupported time unit: " + value);
    }

}
