package com.hj.it.sieqk.uitl;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExcepSSLPost {
    private static Logger logger = LogManager.getLogger(ExcepSSLPost.class);

    /**
     * 跳过SSL发送post请求
     * @param url
     * @param map
     * @return
     */
    public static String sendPost(String url, Map<String,String> map){
        String charset="utf-8";
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");
            //设置参数
            StringEntity requestEntity = new StringEntity(JSON.toJSONString(map), "utf-8");
            httpPost.setEntity(requestEntity);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            logger.error("出现异常:{}",ex.getMessage());
        }finally {
            if (null != httpPost){
                httpPost.releaseConnection();
            }
        }
        return result;
    }

    public static String doPost(String apiUrl, Map<String, String> params, String logTitle,
                                Charset requestCharset,Map<String,String> headers) throws HttpException {
        String charset="utf-8";
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(apiUrl);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                pairList.add(pair);
            }

            if (headers!= null) {
                for (Map.Entry<String, String> headerEn : headers.entrySet()) {
                    httpPost.setHeader(headerEn.getKey(),headerEn.getValue());
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, requestCharset));
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
            logger.info("[after]status:" + statusCode +":" + logTitle + ":响应内容:" + result);
        }catch(Exception ex){
            logger.error("出现异常:{}",ex.getMessage());
        }finally {
            if (null != httpPost){
                httpPost.releaseConnection();
            }
        }
        return result;
    }
}
