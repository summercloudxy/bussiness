package com.xy.bussiness.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * @Description Post方式发送请求
 * @File HttpsPostMap.java
 */
public class HttpPostMap {
	/**
	 * 向URL发送POST请求，返回值采用UTF-8格式处理
	 * map封装了键值对
	 * @param URL  server url
	 * @param params  post varieties
	 * @return
	 */

	@SuppressWarnings("unused")
	private static final int BUFFERSIZE = 1048576;
	private static final int TIMEOUT = 12000;
	private static final String CODE_FORMAT = "UTF-8";

	private static int httpTimeOut = TIMEOUT;
	private static String codeFormat = CODE_FORMAT;


	public static int getHttpTimeOut() {
		return httpTimeOut;
	}

	public static void setHttpTimeOut(int httpTimeOut) {
		HttpPostMap.httpTimeOut = httpTimeOut;
	}

	public static String getCodeFormat() {
		return codeFormat;
	}

	public static void setCodeFormat(String codeFormat) {
		HttpPostMap.codeFormat = codeFormat;
	}

	/**
	 * post请求方法
	 * @param url:请求地址
	 * @param params：请求参数
	 */
	public static String sendPost(String url,Map<String, Object> params) throws NoSuchAlgorithmException, KeyManagementException {

		/**
		 * 读取param中的数据，写入NameValuePair
		 */
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if(params!=null){	
			Iterator iter = params.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();

				nvps.add(new BasicNameValuePair(key, value));
			}
		}
		StringBuffer result = new StringBuffer();
		CloseableHttpClient  httpClient =null;
		try{

			/**
			 *setConnectionRequestTimeout 设置连接请求最长时间 
			 *setSocketTimeout 数据传输的最长时间 
			 *setStaleConnectionCheckEnabled 提交请求前测试连接是否可用 
			 */
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(httpTimeOut).setConnectTimeout(httpTimeOut).setSocketTimeout(httpTimeOut).build();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps,"utf-8");
			httpPost.setEntity(formEntity);

			if(url.startsWith("https")){
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				};
				ctx.init(null, new TrustManager[] { tm }, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("https", 443, ssf));
				ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
				httpClient = new DefaultHttpClient(mgr);  
			}else{
				httpClient =  HttpClients.createDefault();
			}
			HttpResponse response = httpClient.execute(httpPost);

			/**
			 * 获取服务器响应          
			 */
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line="";
			while((line=bufferedReader.readLine()) != null){
				result.append(line);
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();  
		} catch (ClientProtocolException e) {
			e.printStackTrace();  
		} catch (IOException e) {
			e.printStackTrace();  
		}
		return result.toString();
	}

	/**
	 * json请求方法
	 * @param url:请求地址
	 * @param params：请求参数
	 * @return 返回json格式字符串
	 */
	public static String sendPost(String url,String params) throws NoSuchAlgorithmException, KeyManagementException {
		StringBuffer result = new StringBuffer();
		CloseableHttpClient  httpClient =null;
		try{

			/**
			 *setConnectionRequestTimeout 设置连接请求最长时间 
			 *setSocketTimeout 数据传输的最长时间 
			 *setStaleConnectionCheckEnabled 提交请求前测试连接是否可用 
			 */
			HttpPost httpPost = new HttpPost(url);
			if(url.startsWith("https")){
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				};
				ctx.init(null, new TrustManager[] { tm }, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("https", 443, ssf));
				ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
				httpClient = new DefaultHttpClient(mgr);  
			}else{
				httpClient =  HttpClients.createDefault();
			}
			httpPost.setEntity(new StringEntity(params,"UTF-8"));
			httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
			HttpResponse response = httpClient.execute(httpPost);

			/**
			 * 获取服务器响应        
			 */
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line="";
			while((line=bufferedReader.readLine()) != null){
				result.append(line);
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();  
		} catch (ClientProtocolException e) {
			e.printStackTrace();  
		} catch (IOException e) {
			e.printStackTrace();  
		}
		return result.toString();
	}

}
