package com.hj.it.sieqk.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hj.it.sieqk.dao.DkDao;
import com.hj.it.sieqk.dao.KqDao;
import com.hj.it.sieqk.uitl.FileUtil;
import com.hj.it.sieqk.uitl.HttpException;
import com.hj.it.sieqk.uitl.JsonUtils;
import com.hj.it.sieqk.uitl.MyHttpUtil;
import com.hj.it.sieqk.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/apply")
public class ApplyController {
    @Autowired
    private KqDao kqDao;

    @Autowired
    private DkDao dkDao;

    @Value("${config}")
    private String config;
    @Value("${tmTime}")
    private String tmTime;

    @Value("${jobNum}")
    private String jobNum;

    @Value("${clockInStartDate}")
    private String clockInStartDate;

    @RequestMapping(value = {"/synchronization"})
    @ResponseBody
    public String createPayOrder(HttpServletRequest request) throws HttpException, IOException, ParseException {
        List<Apply> oldApplyList = kqDao.findApply();
        String token = getToken();
        Map<String, String> headers = getRequestHead(token);
        String applyUrl = "https://my.chinasie.com/attcenter/api/process/ap/%s/10";
        String applyParam = "{\"instanceId\":null,\"title\":null,\"start\":null,\"end\":null}";
        String applyStr = MyHttpUtil.post(String.format(applyUrl,0), applyParam, headers);
        Map<String,String> resultMap = JsonUtils.json2map(applyStr);
        int countSize = Integer.parseInt(resultMap.get("countSize"));
        String data = resultMap.get("data");
        List<Apply> applyList = JSON.parseObject(data, new TypeReference<List<Apply>>(){});
        Map<String, Apply> oldApplyListMap = oldApplyList.stream().collect(Collectors.toMap(Apply::getBusinessKey, a ->a,(k1,k2)->k1));
        List<VacationDetail> vacationDetailList = new ArrayList<>();
        List<WorkOvertime> workOvertimeList = new ArrayList<>();
        List<MakeUpCard> makeUpCardList = new ArrayList<>();
        List<WorkOvertimeDeTail> workOvertimeDetailList = new ArrayList<>();
        List<MakeUpCardDetail> makeUpCardDetailList = new ArrayList<>();
        saveDetail(headers, applyList, vacationDetailList, workOvertimeList, makeUpCardList, workOvertimeDetailList, makeUpCardDetailList, oldApplyListMap);
        int page = 0;
        if (countSize % 10 == 0) {
            page = countSize / 10;
        } else {
            page = countSize / 10 + 1;
        }
        for (int i = 1; i < page; i++) {
            applyStr = MyHttpUtil.post(String.format(applyUrl,i), applyParam, headers);
            Map<String,String> resultMap1 = JsonUtils.json2map(applyStr);
            String data1 = resultMap1.get("data");
            List<Apply> applyList1 = JSON.parseObject(data1, new TypeReference<List<Apply>>(){});
            saveDetail(headers, applyList1, vacationDetailList, workOvertimeList, makeUpCardList, workOvertimeDetailList, makeUpCardDetailList, oldApplyListMap);
            applyList.addAll(applyList1);
        }
        if (applyList.size() > 0) {
            kqDao.insertApply(applyList);
        }
        if (makeUpCardList.size() > 0) {
            kqDao.insertMakeUpCard(makeUpCardList);
        }
        if (makeUpCardDetailList.size() > 0) {
            kqDao.insertMakeUpCardDetail(makeUpCardDetailList);
        }
        if (vacationDetailList.size() > 0) {
            kqDao.insertVacationDetail(vacationDetailList);
        }
        if (workOvertimeList.size() > 0) {
            kqDao.insertWorkOvertime(workOvertimeList);
        }
        if (workOvertimeDetailList.size() > 0) {
            kqDao.insertWorkOvertimeDeTaile(workOvertimeDetailList);
        }
        return "success";
    }

    private void saveDetail(Map<String, String> headers, List<Apply> applyList, List<VacationDetail> vacationDetailList,
                            List<WorkOvertime> workOvertimeList, List<MakeUpCard> makeUpCardList,
                            List<WorkOvertimeDeTail> workOvertimeDetailList,
                            List<MakeUpCardDetail> makeUpCardDetailList, Map<String, Apply> oldApplyMap) throws IOException, ParseException {
        String vacationDetailUrl = "https://my.chinasie.com/attcenter//api/leave/findById/%s";
        String workOvertimeDetailUrl = "https://my.chinasie.com/attcenter//api/ot/info/%s";
        String makeUpCardDetailUrl = "https://my.chinasie.com/attcenter//api/patch/info/%s";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 此处时间是转 TM后加班按双倍算
        Date date = df.parse(tmTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        for (int i = 0; i <applyList.size(); i++) {
            if (oldApplyMap.containsKey(applyList.get(i).getBusinessKey())) {
                applyList.remove(applyList.get(i));
                i--;
                continue;
            }
            Apply apply = applyList.get(i);
            apply.setId(getID());
            String businessKey = apply.getBusinessKey().substring(apply.getBusinessKey().lastIndexOf(".")+1, apply.getBusinessKey().length());
            String instanceType = apply.getInstanceType();
            if (Objects.equals("休假", instanceType)) {
                String response = MyHttpUtil.get(String.format(vacationDetailUrl,businessKey), headers);
                Map<String,String> vacationDetailMap = JsonUtils.json2map(response);
                String vacationDetailData = vacationDetailMap.get("data");
                VacationDetail vacationDetail = JSON.parseObject(vacationDetailData, VacationDetail.class);
                vacationDetail.setId(getID());
                vacationDetailList.add(vacationDetail);
            }else if(Objects.equals("加班", instanceType)) {
                String response = MyHttpUtil.get(String.format(workOvertimeDetailUrl,businessKey), headers);
                Map<String,String> workOvertimeMap = JsonUtils.json2map(response);
                String workOvertimeData = workOvertimeMap.get("data");
                WorkOvertime workOvertime = JSON.parseObject(workOvertimeData, WorkOvertime.class);
                workOvertime.setId(getID());
                workOvertimeList.add(workOvertime);
                String details = workOvertime.getDetails();
                List<WorkOvertimeDeTail> workOvertimeDetailList1 = JSON.parseObject(details, new TypeReference<List<WorkOvertimeDeTail>>(){});
                if (workOvertimeDetailList1 != null) {
                    Date startTime = apply.getStartTime();

                    workOvertimeDetailList1.forEach(entity -> {
                        entity.setId(getID());
                        entity.setParent_id(workOvertime.getId());
                        if (startTime.getTime() > cal.getTimeInMillis()) {
                            entity.setOtHours(String.valueOf(2*Double.parseDouble(entity.getOtHours())));
                        }
                    });
                    workOvertimeDetailList.addAll(workOvertimeDetailList1);
                }
            }else if(Objects.equals("补卡", instanceType)) {
                String response = MyHttpUtil.get(String.format(makeUpCardDetailUrl,businessKey), headers);
                Map<String,String> makeUpCardMap = JsonUtils.json2map(response);
                String makeUpCardData = makeUpCardMap.get("data");
                MakeUpCard makeUpCard = JSON.parseObject(makeUpCardData, MakeUpCard.class);
                makeUpCard.setId(getID());
                makeUpCardList.add(makeUpCard);
                String details = makeUpCard.getPDetail();
                List<MakeUpCardDetail> makeUpCardDetailList1 = JSON.parseObject(details, new TypeReference<List<MakeUpCardDetail>>(){});
                if (makeUpCardDetailList1 != null) {
                    makeUpCardDetailList1.forEach(entity -> {
                        entity.setId(getID());
                        entity.setParent_id(makeUpCard.getId());
                    });
                    makeUpCardDetailList.addAll(makeUpCardDetailList1);
                }
            }
        }
    }

    private String getToken() throws IOException {
//        String logUrl = "https://my.chinasie.com/certification/api/certificate/login";
        String logUrl = "https://my.chinasie.com/certification/api/v1/certificate/login";
        // TODO 修改账号密码即可，账号密码可以用过burp suite抓包获取
        String loginResponse = MyHttpUtil.post(logUrl, config, null);
        System.out.println(loginResponse);
        Map<String,String> resultMap = JsonUtils.json2map(loginResponse);
        return resultMap.get("data");
    }

    public static String getID(){
    UUID id=UUID.randomUUID();
    String[] idd=id.toString().split("-");
    return idd[0]+ DateUtil.now();

    }

    @RequestMapping(value = {"/clockInData"})
    @ResponseBody
    public void clockInData(HttpServletRequest request) throws IOException {
        String token = getToken();
        Map<String, String> headers = getRequestHead(token);
        String applyUrl = "https://my.chinasie.com/attcenter/api/attendance/de/%s/%s/0";
        List<CheckRecords> checkRecordsList = new ArrayList<>();
        String date = clockInStartDate;
        // 日期小于当前日期，继续执行
        while (compareDate(date)) {
            CheckRecords checkRecords = getCheckRecord(date, headers, applyUrl);
            checkRecordsList.add(checkRecords);
            date = addDay(date);
        }
        if (!CollectionUtils.isEmpty(checkRecordsList)) {
            FileUtil.writeFile("D:\\result\\" + jobNum + "_"+clockInStartDate + ".txt", JsonUtils.listToJson(checkRecordsList));
        }
    }

    public static void main(String[] args) {
        String dateStr = "2024-03-19";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == Calendar.SUNDAY) {
            System.out.printf("星期天");
        } else if (dayWeek == Calendar.MONDAY) {
            System.out.printf("星期1");
        }else if (dayWeek == Calendar.TUESDAY) {
            System.out.printf("星期2");
        }else if (dayWeek == Calendar.WEEK_OF_MONTH) {
            System.out.printf("星期3");
        }else if (dayWeek == Calendar.THURSDAY) {
            System.out.printf("星期4");
        }else if (dayWeek == Calendar.FRIDAY) {
            System.out.printf("星期5");
        }else if (dayWeek == Calendar.SATURDAY) {
            System.out.printf("星期6");
        }
    }
    public boolean compareDate(String date) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return date.compareTo("2023-12-31") < 1;
//        return date.compareTo(currentDate) < 1;
    }

    public String addDay(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
           Date date = df.parse(dateStr);
           Calendar calendar = Calendar.getInstance();
           calendar.setTime(date);
           calendar.add(Calendar.DAY_OF_MONTH, 1);
            return df.format(calendar.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private CheckRecords getCheckRecord(String date, Map<String, String> headers, String applyUrl) throws IOException {
        String url = String.format(applyUrl, jobNum, date);
        String applyStr = MyHttpUtil.get(url, headers);
        Map<String,String> resultMap = JsonUtils.json2map(applyStr);
        String data = resultMap.get("data");
        String dayWeek = getDayWeek(date);
        if ("null".equals(data)) {
            return CheckRecords.builder()
                    .date(date.replace("-", ""))
                    .onTime("")
                    .offTime("")
                    .jobNum(jobNum)
                    .dayWeek(dayWeek)
                    .build();
        }
        Map<String,String> dataMap;
        if (data.contains("[")) {
            JSONArray jsonArray = JSONUtil.parseArray(data);
            dataMap = JsonUtils.json2map(jsonArray.get(0).toString());
        }else {
            dataMap = JsonUtils.json2map(data.toString());
        }
        return CheckRecords.builder()
                .date(date.replace("-", ""))
                .onTime(dataMap.get("onTime"))
                .offTime(dataMap.get("offTime"))
                .jobNum(jobNum)
                .dayWeek(dayWeek)
                .build();
    }

    private String getDayWeek(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(df.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == Calendar.SUNDAY) {
           return "星期天";
        } else if (dayWeek == Calendar.MONDAY) {
            return "星期一";
        }else if (dayWeek == Calendar.TUESDAY) {
            return "星期二";
        }else if (dayWeek == Calendar.WEEK_OF_MONTH) {
            return "星期三";
        }else if (dayWeek == Calendar.THURSDAY) {
            return "星期四";
        }else if (dayWeek == Calendar.FRIDAY) {
            return "星期五";
        }else if (dayWeek == Calendar.SATURDAY) {
            return "星期六";
        }
        return null;
    }

    private static Map<String, String> getRequestHead(String token) {
        Map<String,String> headers = new HashMap<>();
        String authorization = "Bearer " + token;
        headers.put("Authorization", authorization);
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
