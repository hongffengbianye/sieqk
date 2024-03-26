package com.hj.it.sieqk.uitl;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JsonUtils {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String SPACE = "   ";


    public static <T> String mapToJson(Map<String, T> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }

    public static <T> String listToJson(List<T> list){
        Gson gson = new Gson();
        String jsonStr=gson.toJson(list);
        return jsonStr;
    }

    public static final Map<String, String> json2map(String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> object : jsonObject.entrySet()) {
            JsonElement v = object.getValue();
            if (v.isJsonPrimitive() ) {
                map.put(object.getKey(), v.getAsString());
            } else {
                map.put(object.getKey(), v.toString());
            }
        }
        return map;
    }

    public static final <T> T  string2obj(String jsonStr,Class<T> type, String dateFormat) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(dateFormat);
        Gson gson = builder.create();
        return gson.fromJson(jsonStr, type);
    }

    public static final <T> T  string2obj(String jsonStr,Class<T> type) {
        return string2obj(jsonStr,type, DEFAULT_DATE_FORMAT);
    }

    public static final <T> T  string2obj(String jsonStr,Type type, String dateFormat) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(dateFormat);
        Gson gson = builder.create();
        return gson.fromJson(jsonStr, type);
    }

    public static final <T> T  string2obj(String jsonStr,Type type) {
        return string2obj(jsonStr,type, DEFAULT_DATE_FORMAT);
    }

    public static final String obj2String(Object obj, boolean isFormat)  {
        if (isFormat) {
            return obj2String(obj, DEFAULT_DATE_FORMAT, true, false);
        } else {
            return obj2String(obj, DEFAULT_DATE_FORMAT);
        }
    }


    public static final String obj2StringNoEscaping(Object obj)  {
        return obj2String(obj,DEFAULT_DATE_FORMAT, false,true);
    }

    public static final String obj2String(Object obj)  {
        return obj2String(obj, DEFAULT_DATE_FORMAT);
    }

    public static final String obj2String(Object obj, String dateFormat)  {
        return obj2String(obj, dateFormat, false, false);
    }

    public static final String obj2String(Object obj, String dateFormat,
                                          boolean isFormat, boolean disableHtmlEscaping)  {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return obj.toString();
        }
        GsonBuilder builder = new GsonBuilder();
        if (isFormat) {
            builder.setPrettyPrinting();
        }
        if (disableHtmlEscaping) {
            builder.disableHtmlEscaping();
        }
        builder.setDateFormat(dateFormat);
        Gson gson = builder.create();
        return gson.toJson(obj);
    }
}
