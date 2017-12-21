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
package com.kxd.framework.utils;

import com.kxd.framework.lang.AppException;
import com.kxd.framework.lang.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

public abstract class HttpClient {

    private static final int                         SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;

    public static final String                       CONTENT_TYPE_SERIALIZED_OBJECT                = "application/x-java-serialized-object";

    private static final String                      HTTP_METHOD_POST                              = "POST";

    private static final String                      HTTP_HEADER_CONTENT_TYPE                      = "Content-Type";

    private static final String                      HTTP_HEADER_CONTENT_LENGTH                    = "Content-Length";

    private static final String                      HTTP_HEADER_CONTENT_ENCODING                  = "Content-Encoding";

    private static final String                      ENCODING_GZIP                                 = "gzip";

    private static Logger                            logger                                        = LoggerFactory
                                                                                                           .getLogger(HttpClient.class);
    
    private static Object httpsLock = new Object();
    
    private static final String HTTPS = "https";
    
	private static final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		public void checkClientTrusted(X509Certificate[] certs, String authType) {}
		public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	} };
	
	private static   SSLContext sc = null;
	
	private static final HostnameVerifier allHostsValid = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	//为保证http访问不受https配置影响,需提前将http的相关参数拿出来
	private static final SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY_NO_HTTPS = HttpsURLConnection.getDefaultSSLSocketFactory();
	private static final HostnameVerifier DEFAULT_SSL_HOST_NAME_VERIFIER_NO_HTTPS = HttpsURLConnection.getDefaultHostnameVerifier();
    
	static{
		try {
			sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e) {

		} catch (KeyManagementException e) {
			
		}
	}



    private static ConcurrentHashMap<String, Object> HTTPS_INIT_KEYSTORES                          = new ConcurrentHashMap<String, Object>();

    public static Object sendByPost(List<String> urlList, Object data, int timeout) throws Exception {
        return sendByPost(urlList, data, timeout, 1, 0, null);
    }

    public static Object sendByPost(List<String> urlList, Object data, int timeout, int retry, int interval)
            throws Exception {
        return sendByPost(urlList, data, timeout, retry, interval, null);
    }

    public static Object sendByPost(List<String> urlList, Object data, int timeout, int retry) throws Exception {
        return sendByPost(urlList, data, timeout, retry, 0, null);
    }
    
    public static  String sendByPostOfStringHttpsTrustAll(String httpsUrl, String data, HashMap<String, String> headers, int timeout,
            String charset) throws Exception{
        InputStream responseBody = null;
        try {
        	synchronized(httpsLock){
    	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        	}

            ByteArrayOutputStream baos = getByteArrayOutputStream(data);
            URLConnection   connection = new URL(httpsUrl).openConnection(Proxy.NO_PROXY);
            if (!(connection instanceof HttpsURLConnection)) {
                logger.error("Service URL [" + httpsUrl + "] is not an HTTPS URL");
                return null;
            }
            HttpsURLConnection con = (HttpsURLConnection) connection;
            prepareConnection(con, baos.size(), timeout, headers);
            writeStringRequestBody(con, data, charset);
            validateResponse(con);
            responseBody = readResponseBody(con);
            if (con.getResponseCode() != 200) {
                logger.error("The server return code isn't 200. so return false; Return code is "
                        + con.getResponseCode());

            }
            return readResultOfString(responseBody, charset, con.getContentLength(),false);
            // Get result

        } catch(SocketTimeoutException ex){
        	String errorMessage="An Http time out exception happends in HttpClient.sendByPostSingle(),url is "+httpsUrl+" and exception is: " + ex.getMessage();
        	String errorCode=ErrorCode.ERROR_NET_TIMEOUT;
        	logger.error(errorMessage);
        	throw new AppException(errorCode, errorMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("error happens in HttpClient.sendByPostSingle(),url is "+httpsUrl+" and exception is: " + ex.getMessage());
            throw ex;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }
    
    public static  String sendByPostOfStringForProxy(String url, String data, HashMap<String, String> headers, int timeout,
            String charset, String proxyAddr, String proxyPort) throws IOException {
        InputStream responseBody = null;
        HttpURLConnection con = null;
        boolean isHttps = false;
        try {
        	
        	synchronized(httpsLock){
                if (url.startsWith(HTTPS)) {
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    isHttps = true;
                }
        	}
            
/*            else{
    	        HttpsURLConnection.setDefaultSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY_NO_HTTPS);
    	        HttpsURLConnection.setDefaultHostnameVerifier(DEFAULT_SSL_HOST_NAME_VERIFIER_NO_HTTPS);
            }*/
            
            ByteArrayOutputStream baos = getByteArrayOutputStream(data);
            
            Proxy proxy = Proxy.NO_PROXY;
            
            if (StringUtils.isNotBlank(proxyAddr) && StringUtils.isNotBlank(proxyPort))
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddr, Integer.parseInt(proxyPort)));
            
            URLConnection connection = new URL(url).openConnection(proxy);
            if (!(connection instanceof HttpURLConnection)) {
                logger.error("Service URL [" + url + "] is not an HTTP URL");
                return null;
            }
            if(isHttps){
                con = (HttpsURLConnection) connection;
            }
            else{
                con = (HttpURLConnection) connection;
            }
            prepareConnection(con, baos.size(), timeout, headers);
            writeStringRequestBody(con, data, charset);
        } catch (Exception e) {
            logger.error("error happens in HttpClient.sendByPostSingle(),url is "+url+" and e Message is: " + e.getMessage());
            throw e;
        }
        
        try {
            validateResponse(con);
            responseBody = readResponseBody(con);
            if (con.getResponseCode() != 200) {
                logger.error("The server return code isn't 200. so return false,url is "+url+" ; Return code is "
                        + con.getResponseCode());

            }
            return readResultOfString(responseBody, charset, con.getContentLength(),false);
            // Get result

        } catch (Exception ex) {
            logger.warn("error happens in HttpClient.sendByPostSingle(),url is "+url+" and ex Message is: " + ex.getMessage());
            throw ex;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }
    
    public static  String sendByPostOfStringForProxyByLine(String url, String data, HashMap<String, String> headers, int timeout,
            String charset, String proxyAddr, String proxyPort) throws IOException {
        InputStream responseBody = null;
        HttpURLConnection con = null;
        boolean isHttps = false;
        try {
            
            synchronized(httpsLock){
                if (url.startsWith(HTTPS)) {
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    isHttps = true;
                }
            }
            
/*            else{
                HttpsURLConnection.setDefaultSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY_NO_HTTPS);
                HttpsURLConnection.setDefaultHostnameVerifier(DEFAULT_SSL_HOST_NAME_VERIFIER_NO_HTTPS);
            }*/
            
            ByteArrayOutputStream baos = getByteArrayOutputStream(data);
            
            Proxy proxy = Proxy.NO_PROXY;
            
            if (StringUtils.isNotBlank(proxyAddr) && StringUtils.isNotBlank(proxyPort))
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddr, Integer.parseInt(proxyPort)));
            
            URLConnection connection = new URL(url).openConnection(proxy);
            if (!(connection instanceof HttpURLConnection)) {
                logger.error("Service URL [" + url + "] is not an HTTP URL");
                return null;
            }
            if(isHttps){
                con = (HttpsURLConnection) connection;
            }
            else{
                con = (HttpURLConnection) connection;
            }
            prepareConnection(con, baos.size(), timeout, headers);
            writeStringRequestBody(con, data, charset);
        } catch (Exception e) {
            logger.error("error happens in HttpClient.sendByPostSingle(),url is "+url+" and e Message is: " + e.getMessage());
            throw e;
        }
        
        try {
            validateResponse(con);
            responseBody = readResponseBody(con);
            if (con.getResponseCode() != 200) {
                logger.error("The server return code isn't 200. so return false; Return code is "
                        + con.getResponseCode());

            }
            return readResultOfString(responseBody, charset, con.getContentLength(),true);
            // Get result

        } catch (Exception ex) {
            logger.warn("error happens in HttpClient.sendByPostSingle(),url is "+url+" and ex Message is: " + ex.getMessage());
            return null;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }

    /**
     * since 2.0.2
     * @param url
     * @param data
     * @param headers
     * @param timeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static  String sendByPostOfString(String url, String data, HashMap<String, String> headers, int timeout,
            String charset) throws Exception {
        InputStream responseBody = null;
        try {
/*	        HttpsURLConnection.setDefaultSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY_NO_HTTPS);
	        HttpsURLConnection.setDefaultHostnameVerifier(DEFAULT_SSL_HOST_NAME_VERIFIER_NO_HTTPS);*/
        	
        	//判断是否https链接,如果是https链接,则直接进入https调用方法
        	if(url.startsWith(HTTPS)){
        		return sendByPostOfStringHttpsTrustAll(url,data,headers,timeout,charset);
        	}
        	
            ByteArrayOutputStream baos = getByteArrayOutputStream(data);
            URLConnection connection = new URL(url).openConnection(Proxy.NO_PROXY);
            if (!(connection instanceof HttpURLConnection)) {
                logger.error("Service URL [" + url + "] is not an HTTP URL");
                return null;
            }
            HttpURLConnection con = (HttpURLConnection) connection;
            prepareConnection(con, baos.size(), timeout, headers);
            writeStringRequestBody(con, data, charset);
            validateResponse(con);
            responseBody = readResponseBody(con);
            if (con.getResponseCode() != 200) {
                logger.error("The server return code isn't 200. so return false; Return code is "
                        + con.getResponseCode());

            }
            return readResultOfString(responseBody, charset, con.getContentLength(),false);
            // Get result

        } catch(SocketTimeoutException ex){
        	String errorMessage="An Http time out exception happends in HttpClient.sendByPostSingle(),url is "+url+" and exception is: " + ex.getMessage();
        	String errorCode=ErrorCode.ERROR_NET_TIMEOUT;
        	logger.error(errorMessage);
        	throw new AppException(errorCode, errorMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("error happens in HttpClient.sendByPostSingle(),url is "+url+" and exception is: " + ex.getMessage());
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
        return null;
    }

    public static Object sendByPost(List<String> urlList, Object data, int timeout, int retry, int interval,
            RetryCallBack callback) throws Exception {
        Collections.shuffle(urlList);
        Exception exception = null;
        for (int i = 0; i < retry; i++) {
            InputStream responseBody = null;
            try {
                Object newData = data;
                if (i != 0 && callback != null) {
                    newData = callback.onRetry(data);
                }
                String url = urlList.get(i % urlList.size());
                ByteArrayOutputStream baos = getByteArrayOutputStream(newData);
                // Not use proxy to prevent: Software caused connection abort:
                // socket write error
                URLConnection connection = new URL(url).openConnection(Proxy.NO_PROXY);
                if (!(connection instanceof HttpURLConnection)) {
                    exception = new Exception("Service URL [" + url + "] is not an HTTP URL");
                    break;
                }
                HttpURLConnection con = (HttpURLConnection) connection;
                prepareConnection(con, baos.size(), timeout);
                writeRequestBody(con, baos);
                validateResponse(con);
                responseBody = readResponseBody(con);
                return readResult(responseBody);
            } catch (Exception ex) {
                exception = ex;
                // If IOException, retry
                if (!(ex instanceof IOException))
                    break;
                if (interval > 0) {
                    Thread.sleep(interval);
                }
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        }
        throw exception;
    }

    public interface RetryCallBack {

        // can modify data on each retry.
        public Object onRetry(Object data);

    }

    private static ByteArrayOutputStream getByteArrayOutputStream(Object data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(data);
        } finally {
            oos.close();
        }
        return baos;
    }

    private static void prepareConnection(HttpURLConnection connection, int contentLength, int timeout)
            throws IOException {
        if (timeout >= 0) {
            connection.setConnectTimeout(timeout);
        }
        if (timeout >= 0) {
            connection.setReadTimeout(timeout);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod(HTTP_METHOD_POST);
        connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_SERIALIZED_OBJECT);
        connection.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
    }

    private static void prepareConnection(HttpURLConnection connection, int contentLength, int timeout,
            HashMap<String, String> headers) throws IOException {
        if (timeout >= 0) {
            connection.setConnectTimeout(timeout);
        }
        if (timeout >= 0) {
            connection.setReadTimeout(timeout);
        }
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod(HTTP_METHOD_POST);
        connection.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

    }

    private static void writeRequestBody(HttpURLConnection con, ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(con.getOutputStream());
    }

    private static void writeStringRequestBody(HttpURLConnection con, String requestBody, String charset)
            throws IOException {
        con.getOutputStream().write(requestBody.getBytes(charset));
        con.getOutputStream().flush();
        con.getOutputStream().close();
    }

    private static void validateResponse(HttpURLConnection con) throws IOException {

    	if(con.getResponseCode() != 200){
        	logger.warn("Did not receive successful HTTP response: status code = " + con.getResponseCode()
                    + ", status message = [" + con.getResponseMessage() + "]");
    	}
    	
        if (con.getResponseCode() >= 300) {
        	logger.error("Did not receive successful HTTP response: status code = " + con.getResponseCode()
                    + ", status message = [" + con.getResponseMessage() + "]");
        	
            throw new IOException("Did not receive successful HTTP response: status code = " + con.getResponseCode()
                    + ", status message = [" + con.getResponseMessage() + "]");
        }
    }

    private static InputStream readResponseBody(HttpURLConnection con) throws IOException {
        if (isGzipResponse(con)) {
            // GZIP response found - need to unzip.
            return new GZIPInputStream(con.getInputStream());
        } else {
            // Plain response found.
            return con.getInputStream();
        }
    }

    private static boolean isGzipResponse(HttpURLConnection con) {
        String encodingHeader = con.getHeaderField(HTTP_HEADER_CONTENT_ENCODING);
        return (encodingHeader != null && encodingHeader.toLowerCase().contains(ENCODING_GZIP));
    }

    private static Object readResult(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }

    private static String readResultOfString(InputStream is, String charset, int length,boolean byLine) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, charset));
        String line = bufferedReader.readLine();
        StringBuffer resultBuffer = new StringBuffer("");
        while (line != null) {
            resultBuffer.append(line);
            if (byLine){
                resultBuffer.append("\r\n");
            }
            line = bufferedReader.readLine();
        }

        String result = resultBuffer.toString();
        int actualLength = result.getBytes(charset).length;

        if (actualLength != length) {
            logger.info("Actually read content length(" + actualLength + ") not equal to [content-length] header("
                    + length + ")");
        }
        return result;
    }

    /**
     * 获得KeyStore.
     * @param keyStorePath 密钥库路径
     * @param password 密码
     * @return 密钥库
     * @throws Exception
     */
    public static KeyStore getKeyStore(String password, String keyStorePath) throws Exception {
        // 实例化密钥库
        KeyStore ks = KeyStore.getInstance("JKS");
        // 获得密钥库文件流
        FileInputStream is = new FileInputStream(keyStorePath);
        // 加载密钥库
        ks.load(is, password.toCharArray());
        // 关闭密钥库文件流
        is.close();
        return ks;
    }

    /**
     * 获得SSLSocketFactory.
     * @param password 密码
     * @param keyStorePath 密钥库路径
     * @param trustStorePath 信任库路径
     * @return SSLSocketFactory
     * @throws Exception
     */
    public static SSLContext getSSLContext(String password, String keyStorePath, String trustStorePath)
            throws Exception {
        // 实例化密钥库
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // 获得密钥库
        KeyStore keyStore = getKeyStore(password, keyStorePath);
        // 初始化密钥工厂
        keyManagerFactory.init(keyStore, password.toCharArray());

        // 实例化信任库
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                .getDefaultAlgorithm());
        // 获得信任库
        KeyStore trustStore = getKeyStore(password, trustStorePath);
        // 初始化信任库
        trustManagerFactory.init(trustStore);
        // 实例化SSL上下文
        SSLContext ctx = SSLContext.getInstance("TLS");
        // 初始化SSL上下文
        ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        // 获得SSLSocketFactory
        return ctx;
    }

    /**
     * 初始化HttpsURLConnection.
     * @param password 密码
     * @param keyStorePath 密钥库路径
     * @param trustStorePath 信任库路径
     * @throws Exception
     */
    public static void initHttpsURLConnection(String password, String keyStorePath, String trustStorePath)
            throws Exception {

        StringBuffer mapKeyBuffer = new StringBuffer();

        mapKeyBuffer.append(password).append(keyStorePath).append(trustStorePath);

        if (null != HTTPS_INIT_KEYSTORES.get(mapKeyBuffer.toString())) {
            return;
        }
        
        // 声明SSL上下文
        SSLContext sslContext = null;
        // 实例化主机名验证接口
        HostnameVerifier hnv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                // if("localhost".equals(hostname)){
                // return true;
                // } else {
                // return false;
                // }
                return true;
            }
        };
        try {
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);

        HTTPS_INIT_KEYSTORES.putIfAbsent(mapKeyBuffer.toString(), mapKeyBuffer.toString());
    }

    /**
     * 发送请求.
     * @param httpsUrl 请求的地址
     * @param xmlStr 请求的数据
     */
    public static void post(String httpsUrl, String xmlStr) {
        HttpsURLConnection urlCon = null;
        try {
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length", String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            // 设置为gbk可以解决服务器接收时读取的数据中文乱码问题
            urlCon.getOutputStream().write(xmlStr.getBytes("utf-8"));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 基于代理的http访问
     * @param url
     * @param data
     * @param headers
     * @param timeout
     * @param charset
     * @param proxyAddr
     * @param proxyPort
     * @return
     * @throws IOException
     */
  /*  public static String sendByPostOfStringWithProxy(String url, String data, HashMap<String, String> headers, int timeout,
            String charset, String proxyAddr, String proxyPort) throws IOException {
        InputStream responseBody = null;
        HttpURLConnection con = null;
        try {
            ByteArrayOutputStream baos = getByteArrayOutputStream(data);
            
            Proxy proxy = Proxy.NO_PROXY;
            
            if (StringUtils.isNotBlank(proxyAddr) && StringUtils.isNotBlank(proxyPort))
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddr, Integer.parseInt(proxyPort)));
            
            URLConnection connection = new URL(url).openConnection(proxy);
            if (!(connection instanceof HttpURLConnection)) {
                logger.error("Service URL [" + url + "] is not an HTTP URL");
                return null;
            }
            con = (HttpURLConnection) connection;
            prepareConnection(con, baos.size(), timeout, headers);
            writeStringRequestBody(con, data, charset);
        } catch (Exception e) {
            logger.error("error happens in HttpClient.sendByPostSingle(),and e Message is: " + e.getMessage());
            throw e;
        }
        
        try {
            validateResponse(con);
            responseBody = readResponseBody(con);
            if (con.getResponseCode() != 200) {
                logger.error("The server return code isn't 200. so return false; Return code is "
                        + con.getResponseCode());

            }
            return readResultOfString(responseBody, charset, con.getContentLength());
            // Get result

        } catch (Exception ex) {
            logger.warn("error happens in HttpClient.sendByPostSingle(),and ex Message is: " + ex.getMessage());
            return null;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }*/


}
