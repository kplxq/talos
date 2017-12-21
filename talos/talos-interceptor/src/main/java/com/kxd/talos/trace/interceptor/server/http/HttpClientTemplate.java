/**
 * Copyright 2012-2017 Kaixindai Financing Services Jiangsu Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kxd.talos.trace.interceptor.server.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kxd.framework.lang.AppException;
import com.kxd.talos.trace.core.Talos;

/**
 * 向服务端post xml流
 * 
 */
public class HttpClientTemplate {
	private HttpClientRequestInterceptor requestInterceptor;
	private HttpClientResponseInterceptor responseInterceptor;
	private Talos talos = null;

    private final Logger logger = LoggerFactory.getLogger(HttpClientTemplate.class);

    private int maxConnPerRoute = 128;

    private int maxTotalConn = 384;

    /** 默认等待连接建立超时，单位:毫秒 */
    private int CONN_TIME_OUT = 1000;

    /** 默认等待数据返回超时，单位:毫秒 */
    private int SO_TIME_OUT = 5000;

    /** 线程安全的存放HttpClient的map */
    private ConcurrentHashMap<String, DefaultHttpClient> httpClientMap = new ConcurrentHashMap<String, DefaultHttpClient>();

    private final String PARAMETER_SEPARATOR = "&";

    private final String NAME_VALUE_SEPARATOR = "=";

    private final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    /**
     * 线程安全的HttpClient连接管理器
     */
    private ThreadSafeClientConnManager connectionManager = null;


    
    public HttpClientTemplate(Talos talos){
    	this.talos = talos;
    	this.requestInterceptor = HttpClientRequestInterceptor.create(talos);
    	this.responseInterceptor = HttpClientResponseInterceptor.create(talos);
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
        try {
            connectionManager.setMaxTotal(maxTotalConn);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Key[httpclient.max_total] Not Found in systemConfig.properties", e);
        }
        // 每条通道的并发连接数设置（连接池）
        try {
            connectionManager.setDefaultMaxPerRoute(maxConnPerRoute);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Key[httpclient.default_max_connection] Not Found in systemConfig.properties", e);
        }
    }

    /**
     * 以Get方式执行http请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws RscException
     */
    public String executeGet(final String url, String enchode, int timeOut, int soTimeOut) {
        String result = execute(new HttpGet(url), url, enchode, timeOut, soTimeOut);
        logger.debug("[HttpClientTemplate:executeGet()]: [url={}]: [response={}] send successful!", url, result);
        return result;
    }

    public String executeGet(final String url)  {
        return executeGet(url, null, 0, 0);
    }

    /**
     * 以Post方式执行http请求
     * 
     * @param url
     * @param params
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws RscException
     */
    public String executePost(String url, List<? extends NameValuePair> params, String encoding,
            boolean isUrlEncode, int connTimeOut, int soTimeOut)  {
        if (encoding == null) {
            encoding = DEFAULT_CONTENT_ENCODING;
        }
        if (connTimeOut == 0) {
            connTimeOut = CONN_TIME_OUT;
        }
        if (soTimeOut == 0) {
            soTimeOut = SO_TIME_OUT;
        }
        HttpPost httpPost = new HttpPost(url);
        StringEntity reqEntity;
        try {
            if (isUrlEncode) {
                reqEntity = new UrlEncodedFormEntity(params, encoding);
            } else {
                reqEntity = createRequestStringEntity(params, encoding);
            }
        } catch (IOException ioe) {
            AppException re = new AppException("OTHER_EXCEPTION", ioe.getMessage(), ioe);
            throw re;
        }
        httpPost.setEntity(reqEntity);
        String result = execute(httpPost, url, encoding, connTimeOut, soTimeOut);

        logger.debug("[HttpClientTemplate:executePost()]: [url={}]: [response={}] send successful!", url, result);
        return result;
    }
    
    public String executeUploadSingleFile(String url,Map<String,Object> params,String encoding, int connTimeOut, int soTimeOut){
        if (encoding == null) {
            encoding = DEFAULT_CONTENT_ENCODING;
        }
        if (connTimeOut == 0) {
            connTimeOut = CONN_TIME_OUT;
        }
        if (soTimeOut == 0) {
            soTimeOut = SO_TIME_OUT;
        }
        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity();  
		try {
			for(Map.Entry<String, Object> parameter:params.entrySet()){
				if(parameter.getValue() instanceof File){
		            reqEntity.addPart(parameter.getKey(),new FileBody((File)parameter.getValue()));//file1为请求后台的File upload;属性      
				}
				else if(parameter.getValue() instanceof String){
		            reqEntity.addPart(parameter.getKey(),new StringBody((String)parameter.getValue()));//file1为请求后台的File upload;属性      
				}
			}
		} catch (IOException ioe) {
			AppException re = new AppException("OTHER_EXCEPTION",
					ioe.getMessage(), ioe);
			throw re;
		}
	      httpPost.setEntity(reqEntity);
	      String result = execute(httpPost, url, encoding, connTimeOut, soTimeOut);
	      return result;

    }

    public String executePost(String url, List<? extends NameValuePair> params, final boolean isUrlEncode) {
        return executePost(url, params, null, isUrlEncode, 0, 0);
    }

    public String executePost(String url, List<? extends NameValuePair> params)  {
        return executePost(url, params, false);
    }

    public String executePost(String url, String params, String encoding, int timeOut, int soTimeOut){
        if (encoding == null) {
            encoding = DEFAULT_CONTENT_ENCODING;
        }
        if (timeOut == 0) {
            timeOut = CONN_TIME_OUT;
        }
        if (soTimeOut == 0) {
            soTimeOut = SO_TIME_OUT;
        }
        HttpPost httpPost = new HttpPost(url);
        StringEntity reqEntity;
        try {
            reqEntity = new StringEntity(params, encoding);
        } catch (Exception e) {
            AppException re = new AppException("OTHER_EXCEPTION", e.getMessage(), e);
            throw re;
        }
        httpPost.setEntity(reqEntity);
        String result = execute(httpPost, url, encoding, timeOut, soTimeOut);
        logger.debug("[HttpClientTemplate:executePost()]:" + " [url=" + url + "]: [params=" + params + "]: [response="
                + result + "] send successful!");
        return result;
    }

    public String executePost(final String url, final String params)  {
        return executePost(url, params, null, 0, 0);
    }

    /**
     * 创建不进行url encode的请求具体内容
     * 
     * @param params
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    private StringEntity createRequestStringEntity(final List<? extends NameValuePair> params,
            final String encoding) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : params) {
            final String encodedName = parameter.getName();
            final String value = parameter.getValue();
            final String encodedValue = value != null ? value : "";
            if (result.length() > 0) {
                result.append(PARAMETER_SEPARATOR);
            }
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        StringEntity reqEntity = new StringEntity(result.toString(), encoding);
        return reqEntity;
    }

    /**
     * 传入HttpRequest以执行http请求
     * 
     * @param httpReq
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws RscException
     */
    private String execute(HttpRequestBase httpReq, String url, String enchode, int timeOut, int soTimeOut)
            throws AppException {
        HttpClient httpclient = getHttpClient(url, timeOut, soTimeOut);
        StringBuilder sb = new StringBuilder();
        HttpResponse response = null;

        try {
            response = httpclient.execute(httpReq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                logger.debug("Response content length: " + entity.getContentLength());
            } else {
                return null;
            }
            if (enchode == null || "".equals(enchode)) {
                enchode = DEFAULT_CONTENT_ENCODING;
            }
            // 显示结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), enchode));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if (entity != null) {
                entity.consumeContent();
            }

        } catch (Exception e) {

            // 释放HttpClient连接
            if (httpReq != null) {
                httpReq.abort();
            }
            if (SocketTimeoutException.class.isInstance(e)) {
                logger.error("socket timeout error!", e);
                throw new AppException("SO_TIME_OUT_EXCEPTION", "socket timeout!", e);
            } else if (ConnectTimeoutException.class.isInstance(e) || ConnectException.class.isInstance(e)) {
                logger.error("connection timeout error!", e);
                throw new AppException("TIME_OUT_EXCEPTION", "connection timeout!", e);
            } else {
                logger.error("other exception error!", e);
                AppException re = new AppException("OTHER_EXCEPTION", e.getMessage(), e);
                throw re;
            }

        }
        return sb.toString();

    }

    /**
     * 以流的方式向服务端post Xml字符串
     * 
     * @throws RscException
     */
    public String postMsgStream(String url, String msg, String enCode, int timeOut, int soTimeOut) {
        String uid = "";
        if(!"".endsWith(msg) && null != msg){
            uid = msg.substring(msg.indexOf("<UId>"), msg.indexOf("</UId>") + 6);
        }
        
        StringBuilder result = new StringBuilder();
        HttpClient httpclient = getHttpClient(url, timeOut, soTimeOut);
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;

        try {
            StringEntity myEntity = new StringEntity(msg, enCode);
            httppost.addHeader("Content-Type", "text/xml;charset=utf-8");
            httppost.setEntity(myEntity);

            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                InputStreamReader reader = new InputStreamReader(resEntity.getContent(), enCode);
                char[] buff = new char[1024];
                int length = 0;
                while ((length = reader.read(buff)) != -1) {
                    result.append(buff, 0, length);
                }
            }
        } catch (Exception e) {
            // 释放HttpClient连接
            if (httppost != null) {
                httppost.abort();
            }
            if (SocketTimeoutException.class.isInstance(e)) {
                logger.error(uid + ":socket timeout error!", e);
                throw new AppException("SO_TIME_OUT_EXCEPTION", "socket timeout!", e);
            } else if (ConnectTimeoutException.class.isInstance(e) || ConnectException.class.isInstance(e)) {
                logger.error(uid + ":connection timeout error!", e);
                throw new AppException("TIME_OUT_EXCEPTION", "connection timeout!", e);
            } else {
                logger.error("Client requestXml: [{}] \n Server responseXml: [{}] \n HttpResponse: [{}]", new Object[] {
                        msg, result, response });
                AppException re = new AppException("OTHER_EXCEPTION", e.getMessage(), e);
                throw re;
            }

        }
        logger.debug("[HttpClientTemplate:postMsgStream()]: [message={}]: [response={}] send successful!", msg, result);
        return result.toString();
    }

    /**
     * 以流的方式向服务端post字符串
     */
    public String postMsgStream(String url, String msg, String enCode)  {

        return postMsgStream(url, msg, enCode, 0, 0);
    }

    public String postMsgStream(String url, String msg) {

        return postMsgStream(url, msg, null);
    }

    /**
     * 获取HttpClient,不同的业务场景传入不同的连接超时、请求超时参数
     * 
     * @param url 访问的url
     * @param timeOut 连接超时
     * @param soTimeout 请求超时
     * @return HttpClient
     */
    public HttpClient getHttpClient(String url, int timeOut, int soTimeout) {
        if (timeOut == 0) {
            timeOut = CONN_TIME_OUT;
        }
        if (soTimeout == 0) {
            soTimeout = SO_TIME_OUT;
        }

        StringBuilder sb = new StringBuilder(5);
        sb.append(url).append(".").append(timeOut).append(".").append(soTimeout);
        String key = sb.toString();

        DefaultHttpClient httpclient = httpClientMap.get(key);
        if (httpclient == null) {
            httpclient = new DefaultHttpClient(connectionManager);
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
            httpclient.addRequestInterceptor(requestInterceptor);
            httpclient.addResponseInterceptor(responseInterceptor);
            DefaultHttpClient tempClient = httpClientMap.putIfAbsent(key, httpclient);
            if (tempClient != null) {
                httpclient = tempClient;
            }
        }

        return httpclient;
    }

    public void release() {
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }
}
