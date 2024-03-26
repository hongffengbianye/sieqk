package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MakeUpCardDetail {
private String id;
private String parent_id;
private int userId;
private String pDate;
private String onTime;
private String offTime;
private String scheduleOn;
private String scheduleOff;
private String isLastOff;
}
