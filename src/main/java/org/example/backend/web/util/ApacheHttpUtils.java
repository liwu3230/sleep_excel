package org.example.backend.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
public class ApacheHttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpUtils.class);
    private static volatile CloseableHttpClient httpClient;
    private static volatile CloseableHttpClient httpClientJson;
    private static volatile CloseableHttpClient httpClientOpentsdb;
    private static final int connectTimeout = 120 * 1000; //NOPMD
    private static final int maxTotal = 500; //NOPMD
    private static final int connectionRequestTimeout = 120 * 1000; //NOPMD
    private static final int maxConnPerRoute = 100; //NOPMD
    private final static Object syncLock = new Object(); //NOPMD
    private final static int defaultRetry = 3; //NOPMD
    private final static Charset defaultCharset = StandardCharsets.UTF_8; //NOPMD


    /**
     * 获取网络文件大小
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static long getFileLength(String url) {
        //url = RegexUtils.redirectUrl(url);
        long length = 0;
        URL urlObject;
        try {
            urlObject = new URL(url);
            HttpURLConnection urlcon = (HttpURLConnection) urlObject.openConnection();
            //根据响应获取文件大小
            length = urlcon.getContentLengthLong();
            urlcon.disconnect();
        } catch (Exception e) {
            logger.error("getFileLength", e);
        }
        return length;
    }

    public static CloseableHttpClient instance(int maxTotal, int connectTimeout, int connectionRequestTimeout, Collection<Header> headers) {
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
                    connManager.setMaxTotal(maxTotal);
                    connManager.setValidateAfterInactivity(1000);
                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setConnectTimeout(connectTimeout)
                            .setConnectionRequestTimeout(connectTimeout)
                            .setSocketTimeout(connectionRequestTimeout)
                            .build();
                    httpClient = HttpClients.custom()
                            .setConnectionManager(connManager)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .setMaxConnPerRoute(maxConnPerRoute)
                            .setDefaultHeaders(headers)
                            .build();
                }
            }
        }
        return httpClient;
    }


    public static CloseableHttpClient instanceJson(int maxTotal, int connectTimeout, int connectionRequestTimeout, Collection<Header> headers) {
        if (httpClientJson == null) {
            synchronized (syncLock) {
                if (httpClientJson == null) {
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
                    connManager.setMaxTotal(maxTotal);
                    connManager.setValidateAfterInactivity(1000);

                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setConnectTimeout(connectTimeout)
                            .setConnectionRequestTimeout(connectTimeout)
                            .setSocketTimeout(connectionRequestTimeout)
                            .build();
                    httpClientJson = HttpClients.custom()
                            .setConnectionManager(connManager)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .setMaxConnPerRoute(maxConnPerRoute)
                            .setDefaultHeaders(headers)
                            .build();
                }
            }
        }
        return httpClientJson;
    }

    public static CloseableHttpClient instanceHttpClientOpentsdb(int maxTotal, int connectTimeout, int connectionRequestTimeout, Collection<Header> headers) {
        if (httpClientOpentsdb == null) {
            synchronized (syncLock) {
                if (httpClientOpentsdb == null) {
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
                    connManager.setMaxTotal(maxTotal);
                    connManager.setValidateAfterInactivity(1000);
                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setConnectTimeout(connectTimeout)
                            .setConnectionRequestTimeout(connectTimeout)
                            .setSocketTimeout(connectionRequestTimeout)
                            .build();
                    httpClientOpentsdb = HttpClients.custom()
                            .setConnectionManager(connManager)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .setMaxConnPerRoute(maxConnPerRoute)
                            .setDefaultHeaders(headers)
                            .build();
                }
            }
        }
        return httpClientOpentsdb;
    }

    public static CloseableHttpClient instanceDefault() {
        return instance(maxTotal, connectTimeout, connectionRequestTimeout, null);
    }

    public static CloseableHttpClient instanceDefault(int connectionTimeout, int connectionRequestTimeout) {
        return instance(maxTotal, connectionTimeout, connectionRequestTimeout, null);
    }

    public static CloseableHttpClient instanceJson() {
        Header header = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
        List<Header> headers = new ArrayList<>(1);
        headers.add(header);
        return instanceJson(maxTotal, connectTimeout, connectionRequestTimeout, headers);
    }

    /**
     * 用在不需要太长时间超时的请求
     *
     * @return
     */
    public static CloseableHttpClient instanceHttpClientOpentsdb() {
        return instanceHttpClientOpentsdb(maxTotal, 1000, 3000, null);
    }

    public static String post(String url) {
        return post(url, "");
    }

    public static String post(String url, String body) {
        return post(instanceDefault(), url, body, defaultCharset, 0);
    }

    public static String post(String url, String body, Map<String, String> headers) {
        return post(instanceDefault(), url, body, headers, defaultCharset, 0);
    }

    public static String postByJson(String url, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        return post(instanceJson(), url, new StringEntity(body, defaultCharset), headers, defaultCharset, 0);
    }

    /**
     * 调用容器接口专用
     *
     * @param url
     * @param body
     * @return
     */
    public static String postByJsonForK8s(String url, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put("appId", "service_manage");
        return post(instanceJson(), url, new StringEntity(body, defaultCharset), headers, defaultCharset, 0);
    }

    /**
     * 调用容器接口专用
     *
     * @param url
     * @param body
     * @return
     */
    public static String postByJsonForK8s(String url, String body, String userName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put("appId", "service_manage");
        headers.put("operator", userName);
        return post(instanceJson(), url, new StringEntity(body, defaultCharset), headers, defaultCharset, 0);
    }


    public static String post(String url, String body, Charset charset) {
        return post(instanceDefault(), url, body, charset, 0);
    }

    public static String postRetry(String url, String body) {
        return post(instanceDefault(), url, body, defaultCharset, defaultRetry);
    }

    public static String postRetry(String url, String body, Charset charset, int retry) {
        return post(instanceDefault(), url, body, charset, retry);
    }

    public static String post(CloseableHttpClient httpClient, String url, String body, Charset charset, int retry) {
        Map<String, String> headers = new HashMap<>();
        if (isJSON(body)) {
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
        }
        return post(httpClient, url, new StringEntity(body, charset), headers, charset, retry);
    }

    public static String post(CloseableHttpClient httpClient, String url, String body, Map<String, String> headers, Charset charset, int retry) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (isJSON(body)) {
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
        }
        return post(httpClient, url, new StringEntity(body, charset), headers, charset, retry);
    }


    public static String post(String url, List<NameValuePair> nameValuePairs) {
        return post(instanceDefault(), url, nameValuePairs, defaultCharset, 0);
    }

    public static String post(String url, Map<String, String> params, int connectTimeout, int connectionRequestTimeout) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (params != null) {
            nameValuePairs = params.entrySet().stream().map(v -> new BasicNameValuePair(v.getKey(), v.getValue())).collect(Collectors.toList());
        }
        return post(instanceDefault(connectTimeout, connectionRequestTimeout), url, nameValuePairs, defaultCharset, 0);
    }

    public static String post(String url, Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (params != null) {
            nameValuePairs = params.entrySet().stream().map(v -> new BasicNameValuePair(v.getKey(), v.getValue())).collect(Collectors.toList());
        }
        return post(instanceDefault(), url, nameValuePairs, defaultCharset, 0);
    }

    public static String post(String url, List<NameValuePair> nameValuePairs, int retry) {
        return post(instanceDefault(), url, nameValuePairs, defaultCharset, retry);
    }

    public static String post(String url, List<NameValuePair> nameValuePairs, Charset charset, int retry) {
        return post(instanceDefault(), url, nameValuePairs, charset, retry);
    }

    public static String post(CloseableHttpClient httpClient, String url, List<NameValuePair> nameValuePairs, Charset charset, int retry) {
        return post(httpClient, url, new UrlEncodedFormEntity(nameValuePairs, charset), null, charset, retry);
    }

    public static String post(CloseableHttpClient httpClient, String url, HttpEntity httpEntity, Map<String, String> headers, Charset charset, int retry) {
        CloseableHttpResponse response = null;
        String result = null;
        if (httpEntity == null) {
            return null;
        }
        //url = RegexUtils.redirectUrl(url);
        try {
            HttpPost httpPost = new HttpPost(url);
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(httpPost::addHeader);
            }
            httpPost.setEntity(httpEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
        } catch (Exception e) {
            logger.error("http post请求异常,url=" + url, e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                result = post(httpClient, url, httpEntity, headers, charset, retry);
            }
        } finally {
            closeResponse(response);
        }
        return result;
    }

    public static void getFile(String url, File file, int retry) {
        getFile(instanceDefault(), url, file, retry);
    }

    public static void getFile(CloseableHttpClient httpClient, String url, File file, int retry) {
        CloseableHttpResponse response = null;
        FileOutputStream fos = null;
        //url = RegexUtils.redirectUrl(url);
        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                entity.writeTo(bos);
                bos.flush();
            }
            EntityUtils.consumeQuietly(entity);
        } catch (Exception e) {
            logger.error("http file请求异常", e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                getFile(httpClient, url, file, retry);
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            closeResponse(response);
        }
    }

    public static String getRetry(String url, Charset charset, int retry) {
        return get(instanceDefault(), url, charset, retry);
    }

    public static String getByObject(String url, Object object) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        StringBuffer fullUrl = new StringBuffer(url);
        if (fullUrl.indexOf("?") < 0) {
            fullUrl.append("?");
        } else {
            fullUrl.append("&");
        }
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {

            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (Object item : array) {
                    fullUrl.append(key + "=" + item + "&");
                }
            } else {
                fullUrl.append(key + "=" + value + "&");
            }

        }
        return get(fullUrl.toString());
    }

    public static String getRetry(String url) {
        return get(instanceDefault(), url, defaultCharset, defaultRetry);
    }

    public static String get(String url) {
        return get(instanceDefault(), url, defaultCharset, 0);
    }

    public static String getForK8s(String url, String userName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("appId", "service_manage");
        if (T.isNotBlank(userName)) {
            headers.put("operator", userName);
        }
        return get(instanceDefault(), url, defaultCharset, 0, headers);
    }


    public static HttpResponse getWithResponse(String url) {
        return getWithResponse(instanceDefault(), url, defaultCharset, 0);
    }

    public static String delete(String url) {
        return delete(instanceDefault(), url, defaultCharset, 0);
    }

    public static String get(String url, int connectTimeout, int connectionRequestTimeout) {
        return get(instanceDefault(connectTimeout, connectionRequestTimeout), url, defaultCharset, 0);
    }

    public static String get(String url, Charset charset) {
        return get(instanceDefault(), url, charset, 0);
    }

    public static HttpResponse getWithResponse(CloseableHttpClient httpClient, String url, Charset charset, int retry) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);

        } catch (Exception e) {
            logger.error("http请求异常", e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                return getWithResponse(httpClient, url, charset, retry);
            }
        } finally {
            closeResponse(response);
        }
        return response;
    }

    public static String get(CloseableHttpClient httpClient, String url, Charset charset, int retry) {
        return get(httpClient, url, charset, retry, null);
    }

    public static String get(CloseableHttpClient httpClient, String url, Charset charset, int retry, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        //url = RegexUtils.redirectUrl(url);
        try {
            HttpGet httpGet = new HttpGet(url);

            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(httpGet::addHeader);
            }
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() >= 499) {
                throw new Exception("status code is " + response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
        } catch (Exception e) {
            logger.error("http请求异常", e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                return get(httpClient, url, charset, retry);
            }
        } finally {
            closeResponse(response);
        }
        return null;
    }

    public static String delete(CloseableHttpClient httpClient, String url, Charset charset, int retry) {
        CloseableHttpResponse response = null;
        //url = RegexUtils.redirectUrl(url);
        try {
            HttpDelete httpDelete = new HttpDelete(url);
            response = httpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
        } catch (Exception e) {
            logger.error("http请求异常", e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                return get(httpClient, url, charset, retry);
            }
        } finally {
            closeResponse(response);
        }
        return null;
    }

    public static String get(String url, Map<String, String> params) {
        return get(instanceDefault(), url, params, defaultCharset, 0);
    }

    public static String get(String url, Map<String, String> params, int connectTimeout, int connectionRequestTimeout) {
        return get(instanceDefault(connectTimeout, connectionRequestTimeout), url, params, defaultCharset, 0);
    }

    public static String get(String url, Map<String, String> params, Charset charset, int retry) {
        return get(instanceDefault(), url, params, charset, retry);
    }

    public static String getRetry(String url, Map<String, String> params) {
        return get(instanceDefault(), url, params, defaultCharset, defaultRetry);
    }

    public static Pair<Integer, String> getResponse(String url, Map<String, String> params, int connectTimeout, int connectionRequestTimeout) {
        return getResponse(instanceDefault(connectTimeout, connectionRequestTimeout), url, params);
    }

    /**
     * opentsdb查询 忽略4xx错误，只处理5xx错误
     *
     * @param url
     * @param params
     * @return
     */
    public static String queryOpenTsdb(String url, Map<String, String> params) {
        CloseableHttpResponse response = null;
        try {
            RequestBuilder requestBuilder = RequestBuilder.get(url)
                    .addParameters(mapToNameValuePair(params).toArray(new NameValuePair[params.size()]));
            response = instanceHttpClientOpentsdb().execute(requestBuilder.build());
            if (response.getStatusLine().getStatusCode() >= 499) {
                throw new Exception("status code is " + response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, defaultCharset);
            }
        } catch (Exception e) {
            logger.error("http请求异常", e);
        } finally {
            closeResponse(response);
        }
        return null;
    }


    public static String get(CloseableHttpClient httpClient, String url, Map<String, String> params, Charset charset, int retry) {
        CloseableHttpResponse response = null;
        //url = RegexUtils.redirectUrl(url);
        try {
            RequestBuilder requestBuilder = RequestBuilder.get(url)
                    .addParameters(mapToNameValuePair(params).toArray(new NameValuePair[params.size()]));
            response = httpClient.execute(requestBuilder.build());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
        } catch (Exception e) {
            logger.error("http请求异常", e);
            if (retry > 0) {
                retry--;
                closeResponse(response);
                return get(httpClient, url, params, charset, retry);
            }
        } finally {
            closeResponse(response);
        }
        return null;
    }

    public static Pair<Integer, String> getResponse(CloseableHttpClient httpClient, String url, Map<String, String> params) {
        CloseableHttpResponse response = null;
        int respCode = 0;
        try {
            RequestBuilder requestBuilder = RequestBuilder.get(url)
                    .addParameters(mapToNameValuePair(params).toArray(new NameValuePair[params.size()]));
            response = httpClient.execute(requestBuilder.build());
            respCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String respMsg = "";
            if (entity != null) {
                respMsg = EntityUtils.toString(entity, defaultCharset);
            }
            return Pair.of(respCode, respMsg);
        } catch (Exception e) {
            logger.error("http请求异常", e);
            return Pair.of(respCode, e.getMessage());
        } finally {
            closeResponse(response);
        }
    }

    private static void closeResponse(CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    private static List<NameValuePair> mapToNameValuePair(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        List<NameValuePair> nameValuePairs = Lists.newArrayList();
        params.forEach((key, val) -> nameValuePairs.add(new BasicNameValuePair(key, val)));
        return nameValuePairs;
    }

    public static boolean isJSON(String input) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
