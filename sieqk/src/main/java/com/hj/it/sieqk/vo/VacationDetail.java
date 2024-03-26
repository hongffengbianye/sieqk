package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VacationDetail {
private String id;
private String type;
private String lastFlag;
private String rejectFlag;
private String appName;
private String instanceFormType;
private String instanceId;
private int userId;
private String zhName;
private String orgName;
private String telephone;
private String socialBase;
private String contractBase;
private int annualLeBalance;
private String transferLeBalance;
private String leType;
private String leTypeReally;
private String leReason;
private String leDays;
private String poId;
private String poName;
private String isMuBirths;
private String beginDate;
private String beginTime;
private String endDate;
private String endTime;
private String isMorning;
private String leAttach;
private String leAttachList;
}
