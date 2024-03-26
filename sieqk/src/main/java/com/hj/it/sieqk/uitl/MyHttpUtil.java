package com.hj.it.sieqk.uitl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyHttpUtil {

    public static String post(String url, Map<String, Object> map) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost();
        try {
            httpPost.setURI(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        List<NameValuePair> list = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry entry : entrySet) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            list.add(new BasicNameValuePair(key, value));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        String result = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            result = EntityUtils.toString(response.getEntity());
        }else {
            return null;
        }
        httpPost.abort();
        httpClient.getConnectionManager().shutdown();
        return result;
    }

    public static String post(String url, String data, Map<String,String> headers) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        if (headers!= null) {
            for (Map.Entry<String, String> headerEn : headers.entrySet()) {
                httpPost.setHeader(headerEn.getKey(),headerEn.getValue());
            }
        }else {
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
        }
        httpPost.setEntity(new StringEntity(data, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        String result = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            result = EntityUtils.toString(response.getEntity());
        }else {
            return null;
        }
        httpPost.abort();
        httpClient.getConnectionManager().shutdown();
        return result;
    }

    public static String get(String url, Map<String,String> headers) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpGet.setConfig(requestConfig);
        for (Map.Entry<String, String> headerEn : headers.entrySet()) {
            httpGet.setHeader(headerEn.getKey(),headerEn.getValue());
        }
        HttpResponse response = httpClient.execute(httpGet);
        String result = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            result = EntityUtils.toString(response.getEntity());
        }else {
            return null;
        }
        httpGet.abort();
        httpClient.getConnectionManager().shutdown();
        return result;
    }
}
