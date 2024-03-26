package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MakeUpCard {
private String id;
private int userId;
private String zhName;
private String pReason;
private String pType;
private String pPeriod;
private String pDetail;
}
