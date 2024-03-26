package com.hj.it.sieqk.uitl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 30000;

    private static final Logger logger = LogManager.getLogger(HttpUtils.class);

    static {

        connMgr = new PoolingHttpClientConnectionManager();

        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();

        configBuilder.setConnectTimeout(MAX_TIMEOUT);

        configBuilder.setSocketTimeout(MAX_TIMEOUT);

        configBuilder.setConnectionRequestTimeout(3000);

        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    public static String doPostWithJson(String url, String json, String logTitle, Map<String,String> headers) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("请求地址:" + url);
        logger.info("[before]" + logTitle + ":请求header:" + JsonUtils.obj2String(headers));
        logger.info("[before]" + logTitle + ":请求内容:" + json);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {

            httpClient = HttpClients.createDefault();


            httpPost.setConfig(requestConfig);


            StringEntity requestEntity = new StringEntity(json, "utf-8");
            requestEntity.setContentEncoding("UTF-8");
            requestEntity.setContentType("application/json");
            httpPost.setHeader("Content-type", "application/json");


            if (headers!= null) {
                for (Map.Entry<String, String> headerEn : headers.entrySet()) {
                    httpPost.setHeader(headerEn.getKey(),headerEn.getValue());
                }
            }

            httpPost.setEntity(requestEntity);


            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();


            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            logger.info("[after]status:" + statusCode +":" + logTitle + ":响应内容:" + httpStr);
        } catch (Exception e) {
            logger.error("http_post_json请求异常", e);
            throw new HttpException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("[after] " + logTitle + ":请求共消耗时间:" + (endTime - startTime) + "ms:url:" + url);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("http请求异常", e);
                }
            }
        }

        return httpStr;
    }


    public static String doPostWithJson2(String url, String json, String logTitle, Map<String,String> headers) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("请求地址:" + url);
        logger.info("[before]" + logTitle + ":请求header:" + JsonUtils.obj2String(headers));
        logger.info("[before]" + logTitle + ":请求内容:" + json);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {

            httpClient = HttpClients.createDefault();
            httpPost.setConfig(requestConfig);
            StringEntity requestEntity = new StringEntity(json);
            //requestEntity.setContentEncoding("UTF-8");
            requestEntity.setContentType("application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Content-type", "text/plain");

            if (headers!= null) {
                for (Map.Entry<String, String> headerEn : headers.entrySet()) {
                    httpPost.setHeader(headerEn.getKey(),headerEn.getValue());
                }
            }

            httpPost.setEntity(requestEntity);

            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity,"GBK");
            logger.info("[after]status:" + statusCode +":" + logTitle + ":响应内容:" + httpStr);
        } catch (Exception e) {
            logger.error("http_post_json请求异常", e);
            throw new HttpException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("[after] " + logTitle + ":请求共消耗时间:" + (endTime - startTime) + "ms:url:" + url);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("http请求异常", e);
                }
            }
        }
        return httpStr;
    }


    public static String doPostWithJson(String url, String json, String logTitle) throws HttpException {
        return doPostWithJson(url, json, logTitle,null);
    }

    public static String doPostWithXML(String url, String xml, String logTitle) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("请求地址:" + url);
        logger.info("[before]" + logTitle + ":请求内容:" + xml);
        String returnValue = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {

            httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);


            StringEntity requestEntity = new StringEntity(xml, "utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "text/xml");
            httpPost.setEntity(requestEntity);


            returnValue = httpClient.execute(httpPost, responseHandler);
            logger.info("[after] " + logTitle + ":响应内容:" + returnValue);
        } catch (Exception e) {
            logger.error("http post xml请求异常", e);
            throw new HttpException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("请求共消耗时间:" + (endTime - startTime) + "ms:url:" + url);
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnValue;
    }


    public static String doPostWithJson(String url, String json) throws HttpException  {
        return doPostWithJson(url, json, "");
    }

    public static String doPost(String apiUrl, Map<String, String> params) throws HttpException {
        return doPost(apiUrl, params, "");
    }


    public static String doPost(String apiUrl, Map<String, String> params, String logTitle) throws HttpException {
        return doPost(apiUrl, params, logTitle, Charset.forName("UTF-8"), Charset.forName("UTF-8"), null);
    }


    public static String doPost(String apiUrl, Map<String, String> params, String logTitle, Map<String,String> headers) throws HttpException {
        return doPost(apiUrl, params, logTitle, Charset.forName("UTF-8"), Charset.forName("UTF-8"), headers);
    }



    public static String doPost(String apiUrl, Map<String, String> params, String logTitle,
                                Charset requestCharset,Charset rspCharset,Map<String,String> headers) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("请求地址:" + apiUrl);
        logger.info("[before]" + logTitle + ":请求内容:" + JsonUtils.obj2String(params));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
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
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();


            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, rspCharset);
            logger.info("[after]status:" + statusCode +":" + logTitle + ":响应内容:" + httpStr);
        } catch (IOException e) {
            logger.error("http请求异常", e);
            httpStr = ExcepSSLPost.doPost(apiUrl,params,logTitle,requestCharset,headers);
//            throw new HttpException("http请求异常", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("请求共消耗时间:" + (endTime - startTime) + "ms:url:" + apiUrl);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("http请求异常", e);
                }
            }
        }
        return httpStr;
    }



    public static String doPost(String apiUrl, List<NameValuePair> pairList, String logTitle) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("请求地址:" + apiUrl);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);






            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();


            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            logger.info("[after] " + logTitle + ":响应内容:" + httpStr);
        } catch (IOException e) {
            logger.error("http请求异常", e);
            throw new HttpException("http请求异常", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("请求共消耗时间:" + (endTime - startTime) + "ms:url:" + apiUrl);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("http请求异常", e);
                }
            }
        }
        return httpStr;
    }


    public static String doPost(String apiUrl,String logTitle) throws HttpException {
        long startTime = System.currentTimeMillis();
        logger.info("[before]" + logTitle +  ":请求URL:" +apiUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            logger.info("[after] " + logTitle + ":响应内容:" + httpStr);
        } catch (IOException e) {
            logger.error("http请求异常", e);
            throw new HttpException("http请求异常", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("请求共消耗时间:" + (endTime - startTime) + "ms:url:" + apiUrl);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("http请求异常", e);
                }
            }
        }
        return httpStr;
    }

    private static boolean isSuccess(int statusCode) {

        if (statusCode >= HttpStatus.SC_OK && statusCode <= HttpStatus.SC_PARTIAL_CONTENT) {
            return true;
        }
        if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
            return true;
        }
        return false;
    }

    public static String doGet(String url, Map<String, String> params) throws HttpException {
        return doGet(url, params, "");
    }


    public static String doGet(String url, Map<String, String> params, String logTitle) throws HttpException {
        long startTime = System.currentTimeMillis();
        String apiUrl = url;
        logger.info("请求地址:" + apiUrl);
        logger.info("[before]" + logTitle + ":请求内容:" + JsonUtils.obj2String(params));
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        logger.info("get请求全地址:" + apiUrl);
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpPost = new HttpGet(apiUrl);
            httpPost.setConfig(requestConfig);
            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();


            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
            logger.info("[after] " + logTitle + ":响应内容:" + result);
        } catch (IOException e) {
            logger.error("http请求异常", e);
            throw new HttpException("http请求异常", e);
        }finally {
            long endTime = System.currentTimeMillis();
            logger.info("请求共消耗时间:" + (endTime - startTime) + "ms:url:" + apiUrl);
        }
        return result;
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {

            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
