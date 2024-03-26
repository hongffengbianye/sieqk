package com.hj.it.sieqk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Apply {

private String id;
private String instanceId;
private String taskId;
private String businessKey;
private String appName;
private String instanceType;
private String instanceFormType;
private String title;
private String startUserId;
private String startZhName;
private String assignee;
private String wfState;
private Date startTime;
private String opinion;
private String flag;


}
