package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckRecords {
    private Integer id;

    private String date;

    private String onTime;

    private String offTime;

    private String jobNum;

    private String dayWeek;
}
