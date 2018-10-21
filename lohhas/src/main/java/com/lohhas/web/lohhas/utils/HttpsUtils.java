package com.lohhas.web.lohhas.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HttpsUtils {
	static Logger logger = LoggerFactory.getLogger(HttpsUtils.class);
	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static String line;
	@Value("${httpClient.connectTimeout}")
	private static int connectTimeout;
	@Value("${httpClient.sockTimeout}")
	private static int sockTimeout;
	private static final int MAX_TIMEOUT = 7000;
	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
		// Validate connections after 1 sec of inactivity
		connMgr.setValidateAfterInactivity(1000);
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(connectTimeout);
		// 设置读取超时
		configBuilder.setSocketTimeout(sockTimeout);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		requestConfig = configBuilder.build();
	}

	/**
	 * 发送 GET 请求（HTTP），不带输入数据
	 * 
	 * @param url
	 * @return
	 */
	public static JSONObject doGet(String url) {
		return doGet(url, new HashMap<String, Object>());
	}

	/**
	 * 发送 GET 请求（HTTP），K-V形式
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject doGet(String url, Map<String, Object> params) {
		String apiUrl = url;
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
		String result = null;
		HttpClient httpClient = null;
		if (apiUrl.startsWith("https")) {
			httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
					.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		try {
			HttpGet httpGet = new HttpGet(apiUrl);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = IOUtils.toString(instream, "UTF-8");
			}
		} catch (IOException e) {
		}
		return JSON.parseObject(result);
	}

	/**
	 * 发送 POST 请求（HTTP），不带输入数据
	 * 
	 * @param apiUrl
	 * @return
	 * @throws IOException 
	 */
	public static JSONObject doPost(String apiUrl) throws IOException {
		return doPost(apiUrl, new HashMap<String, Object>());
	}

	/**
	 * 发送 POST 请求，K-V形式
	 * 
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 * @throws IOException 
	 */
	public static JSONObject doPost(String apiUrl, Map<String, Object> params) throws IOException {
		CloseableHttpClient httpClient = null;
		if (apiUrl.startsWith("https")) {
			httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		String httpStr = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;

		try {
			httpPost.setConfig(requestConfig);
			List<NameValuePair> pairList = new ArrayList<>(params.size());
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			httpStr = EntityUtils.toString(entity, "UTF-8");
			logger.info("/order/refund请求参数" + httpStr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return JSON.parseObject(httpStr);
	}

	/**
	 * 发送 POST 请求，JSON形式
	 * 
	 * @param apiUrl
	 * @param json
	 *            json对象
	 * @return
	 * @throws IOException 
	 */
	public static JSONObject doPost(String apiUrl, JSONObject json) throws IOException {
		CloseableHttpClient httpClient = null;
		if (apiUrl.startsWith("https")) {
			httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
					.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		String httpStr = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;

		try {
			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			httpStr = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return JSON.parseObject(httpStr);
	}
	/**
	 * 发送 POST 请求，JSON形式
	 * 
	 * @param apiUrl
	 * @param json
	 *            json对象
	 * @return
	 * @throws IOException 
	 */
	public static String  sendXMLDataByPost(String apiUrl,  String xmlData) throws IOException {
		CloseableHttpClient httpClient = null;
		if (apiUrl.startsWith("https")) {
			httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
					.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		String result = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;
		try {
			httpPost.setConfig(requestConfig);
			List<BasicNameValuePair> parameters = new ArrayList<>();
			parameters.add(new BasicNameValuePair("xml", xmlData));
			httpPost.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
			response = httpClient.execute(httpPost);
			System.out.println(response.toString());
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
			
		} catch (IOException e) {
		} finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return result;
	}

	/**
	 * 创建SSL安全连接
	 * 
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
		} catch (GeneralSecurityException e) {
		}
		return sslsf;
	}
	public static String sendContent(String appid,String mchId,String nonceStr,String sign,String body,String detail,String attach,
			String outTradeNo,String totalFee,String feeType,String spbill_create_ip,String goods_tag,String notify_url,String trade_type,String contract_id,String clientip,String deviceid,String mobile,String email,String qq,String openid,String creid,String outerid,String timestamp)
	{
		StringBuffer sb =new StringBuffer();
		sb.append("<xml>").append(line);
		sb.append("<appid>").append(appid).append("</appid>").append(line);
		sb.append("<mch_id>").append(mchId).append("</mch_id>").append(line);
		sb.append("<nonce_str>").append(nonceStr).append("</nonce_str>").append(line);
		sb.append("<sign>").append(sign).append("</sign>").append(line);
		sb.append("<body>").append(body).append("</body>").append(line);
		sb.append("<detail>").append(detail).append("</detail>").append(line);
		sb.append("<attach>").append(attach).append("</attach>").append(line);
		sb.append("<out_trade_no>").append(outTradeNo).append("</out_trade_no>").append(line);
		sb.append("<total_fee>").append(totalFee).append("</total_fee>").append(line);
		sb.append("<fee_type>").append(feeType).append("</fee_type>").append(line);
		sb.append("<spbill_create_ip>").append(spbill_create_ip).append("</spbill_create_ip>").append(line);
		sb.append("<goods_tag>").append(goods_tag).append("</goods_tag>").append(line);
		sb.append("<notify_url>").append(notify_url).append("</notify_url>").append(line);
		sb.append("<trade_type>").append(trade_type).append("</trade_type>").append(line);
		sb.append("<contract_id>").append(contract_id).append("</contract_id>").append(line);
		sb.append("<clientip>").append(clientip).append("</clientip>").append(line);
		sb.append("<deviceid>").append(deviceid).append("</deviceid>").append(line);
		sb.append("<mobile>").append(mobile).append("</mobile>").append(line);
		sb.append("<email>").append(email).append("</email>").append(line);
		sb.append("<qq>").append(qq).append("</qq>").append(line);
		sb.append("<openid>").append(openid).append("</openid>").append(line);
		sb.append("<creid>").append(creid).append("</creid>").append(line);
		sb.append("<outerid>").append(outerid).append("</outerid>").append(line);
		sb.append("<timestamp>").append(timestamp).append("</timestamp>").append(line);
		sb.append("</xml>");
		return sb.toString();
	}
	static { 
		line=System.getProperty("line.separator");
	}
}
