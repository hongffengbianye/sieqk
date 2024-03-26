package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkOvertimeDeTail {

private String id;
private String parent_id;
private String userId;
private String otDate;
private String otHours;
private String otReason;
private String poName;
private String otArea;
}
