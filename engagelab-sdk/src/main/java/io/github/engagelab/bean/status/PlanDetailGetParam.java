package io.github.engagelab.bean.status;

import lombok.Data;

import java.util.List;

@Data
public class PlanDetailGetParam {
    private List<String> planIds;
    private String startDate;
    private String endDate;
}
