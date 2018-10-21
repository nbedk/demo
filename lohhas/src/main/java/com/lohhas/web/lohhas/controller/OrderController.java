package com.lohhas.web.lohhas.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lohhas.web.lohhas.bean.Order;
import com.lohhas.web.lohhas.bean.WithHolding;
import com.lohhas.web.lohhas.mapper.OrderMapper;
import com.lohhas.web.lohhas.mapper.WithHoldingMapper;
import com.lohhas.web.lohhas.utils.ClientCustomSSL;
import com.lohhas.web.lohhas.utils.HttpsUtils;
import com.lohhas.web.lohhas.utils.WXPayUtil;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RestController
@ComponentScan({ "com.lohhas.web.lohhas.controller" })
@MapperScan({ "com.lohhas.web.lohhas.mapper" })
@RequestMapping({ "/order" })
public class OrderController {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	WithHoldingMapper withHoldingMapper;

	@Value("${weixin.pay.url}")
	private String payUrl;
	@Value("${lohhas.result.url}")
	private String lohhasResultUrl;
	@Value("${weixin.appid}")
	private String appid;

	@Value("${weixin.pay.key}")
	private String key;
	@Value("${weixin.mchId}")
	private String mchId;
	@Value("${weixin.pay.notifyUrl}")
	private String notifyUrl;
	@Value("${weixin.refund.url}")
	private String refundUrl;
	@Value("${order.myorders.url}")
	private String myOrdersUrl;

	@ResponseBody
	@RequestMapping(value = { "/query" }, method = { RequestMethod.POST }, produces = {
			"application/json;charset=UTF-8" })
	public JSONObject queryOrder(@RequestBody JSONObject jsonParam) {
		this.logger.info("/order/query请求参数" + jsonParam);
		String openId = jsonParam.getString("openId");
		Order order = new Order();
		order.setOpenid(openId);
		order = this.orderMapper.queryOrder(order);
		JSONObject json = new JSONObject();
		String status = "";
		if (order != null) {
			status = order.getStatus();
		}
		if ("1".equals(status)) {
			String goodsDetails = order.getAttach();
			json = JSONObject.parseObject(goodsDetails);
			json.put("totalFee", order.getTotalFee());
		}
		json.put("status", status);
		this.logger.info("/order/query返回参数" + jsonParam);
		return json;
	}

	@ResponseBody
	@RequestMapping(value = { "/myOrders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public JSONObject querymyOrders(String openId) {
		this.logger.info("/order/myOrders请求参数" + openId);
		String url = this.myOrdersUrl + "?openId=" + openId;
		System.out.println(url);
		JSONObject result = HttpsUtils.doGet(url);
		return result;
	}

	@ResponseBody
	@RequestMapping(value = { "/result" }, method = { RequestMethod.POST }, produces = {
			"application/json;charset=UTF-8" })
	public JSONObject resultOrder(@RequestBody JSONObject jsonParam) {
		this.logger.info("/order/result请求参数" + jsonParam);
		String returnCode = "000000";
		String returnMessage = "操作成功";
		String goodsDetails = "";
		String goodsDescription = jsonParam.getString("goodsDescription");
		JSONArray array = jsonParam.getJSONArray("goodsDetails");
		String totalFee = jsonParam.getString("totalFee");
		String feeType = jsonParam.getString("feeType");
		String orguuid = jsonParam.getString("orgID");
		String partnerCustomerId = jsonParam.getString("partnerCustomerId");
		WithHolding holding = new WithHolding();
		holding.setOpenid(partnerCustomerId);
		holding = this.withHoldingMapper.getWithHolding(holding);
		Order order = new Order();
		order.setOpenid(partnerCustomerId);
		order.setStatus("0");
		order = this.orderMapper.queryOrder(order);
		if(order==null) {
			 order = new Order();
			 String uuid = UUID.randomUUID().toString();
			    uuid = uuid.replace("-", "");
			    order.setUuid(uuid);
			    order.setOpenid(partnerCustomerId);
			    order.setBoxId("0");
			    order.setStatus("0");
			    this.orderMapper.insertOrder(order);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("goodsDetails", array);
		order.setAttach(jsonObject.toJSONString());
		order.setOrguuid(orguuid);
		if (holding == null) {
			returnCode = "999999";
			returnMessage = "第三方唯一标识错误";
			order.setStatus("2");
		} else {
			try {
				String result = wxDeductMoney(mchId, goodsDescription,  order, totalFee,
						holding.getContract_id(), feeType);
				Map resultMap = WXPayUtil.xmlToMap(result);
				if (("FAIL".equals(resultMap.get("return_code"))) || ("FAIL".equals(resultMap.get("result_code")))) {
					returnCode = "999999";
					returnMessage = (String) resultMap.get("err_code_des");
					order.setStatus("2");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		orderMapper.updateOrder(order);
		JSONObject json = new JSONObject();
		json.put("returnCode", returnCode);
		json.put("returnMessage", returnMessage);
		this.logger.info("/order/result请求参数" + json);
		return json;
	}

	@ResponseBody
	@RequestMapping(value = { "/callBack" }, method = { RequestMethod.POST }, produces = {
			"application/xml;charset=UTF-8" })
	public String resultOrder(@RequestBody String result) {
		String returnCode = "SUCCESS";
		String returnMsg = "OK";
		this.logger.info("/order/callBack+请求参数" + result);
		Order order = new Order();
		try {
			Map resultMap = WXPayUtil.xmlToMap(result);
			String tradeState = (String) resultMap.get("trade_state");
			String return_code = (String) resultMap.get("return_code");
			String openid = (String) resultMap.get("openid");
			String bankType = (String) resultMap.get("bank_type");
			String totalFee = (String) resultMap.get("total_fee");
			String uuid = (String) resultMap.get("attach");
			String cashFee = (String) resultMap.get("cash_fee");
			String err_code_des = (String) resultMap.get("err_code_des");
			order.setOpenid(openid);
			order.setBankType(bankType);
			order.setTotalFee(totalFee);
			order.setCashFee(cashFee);
			order.setUuid(uuid);
			if (("SUCCESS".equals(tradeState)) && ("SUCCESS".equals(return_code)))
				order.setStatus("1");
			else {
				order.setStatus("2");
			}
			this.orderMapper.updateUuid(order);
			order = this.orderMapper.queryUuid(order);
			String orguuid = order.getOrguuid();
			JSONObject json = new JSONObject();
			json.put("orderId", orguuid);
			json.put("payState", "支付失败:" + err_code_des);
			json.put("payAmount", changeF2Y(totalFee));
			this.logger.info("通知请求" + json.toString());
			try {
				JSONObject httpResult = HttpsUtils.doPost(this.lohhasResultUrl, json);
				this.logger.info("通知结果" + httpResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<return_msg>");
		sb.append(returnMsg);
		sb.append("</return_msg>");
		sb.append("<return_code>");
		sb.append(returnCode);
		sb.append("</return_code>");
		sb.append("</xml>");
		this.logger.info("/withholding/callBack返回参数" + sb.toString());
		return sb.toString();
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	/** 金额为分的格式 */
	public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

	public static String changeF2Y(String amount) throws Exception {
		if (!amount.matches(CURRENCY_FEN_REGEX)) {
			throw new Exception("金额格式有误");
		}
		return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)).toString();
	}

	public String wxDeductMoney(String mch_id, String body, Order order, String total_fee,
			String contract_id, String fee_type) {
		Map<String, String> map = new HashMap<String, String>();
		String nonce_str = WXPayUtil.generateNonceStr();
		if (StringUtils.isBlank(fee_type)) {
			fee_type = "CNY";
		}
		String spbill_create_ip = "192.168.1.1";
		String goods_tag = "WXG";
		String notify_url = this.notifyUrl;
		String trade_type = "PAP";
		map.put("appid", appid);
		map.put("mch_id", mch_id);
		map.put("nonce_str", nonce_str);
		map.put("body", body);
		map.put("detail", order.getAttach());
		map.put("attach", order.getUuid());
		map.put("out_trade_no", order.getUuid());
		map.put("total_fee", total_fee);
		map.put("fee_type", fee_type);
		map.put("spbill_create_ip", spbill_create_ip);
		map.put("goods_tag", goods_tag);
		map.put("notify_url", notify_url);
		map.put("trade_type", trade_type);
		map.put("contract_id", contract_id);
		String result = "";
		try {
			String sign = WXPayUtil.generateSignature(map, this.key);
			map.put("sign", sign);
			order.setSign(sign);
			String xmlParams = mapToXml(map);
			this.logger.info("/order/xmlParams" + xmlParams);
			HttpPost post = new HttpPost(this.payUrl);
			post.setHeader("Content-type", "text/xml; charset=utf-8");
			post.setHeader("User-Agent", "Firefox/6.0.2");
			HttpClient client = HttpClients.createDefault();
			post.setEntity(new StringEntity(xmlParams, "UTF-8"));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		this.logger.info("/order/result" + result);
		return result;
	}

	public static String mapToXml(Map<String, String> map) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element element = document.createElement("xml");
		document.appendChild(element);
		for (String key : map.keySet()) {
			String value = (String) map.get(key);
			if (value == null) {
				value = "";
			}
			value = value.trim();
			Element filed = document.createElement(key);
			filed.appendChild(document.createTextNode(value));
			element.appendChild(filed);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty("encoding", "utf-8");
		transformer.setOutputProperty("indent", "yes");
		StringWriter write = new StringWriter();
		StreamResult result = new StreamResult(write);
		transformer.transform(source, result);
		String output = write.getBuffer().toString();
		try {
			write.close();
		} catch (Exception localException) {
		}
		return output;
	}

	@ResponseBody
	@RequestMapping(value = { "/refund" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST }, produces = {
					"application/json;charset=UTF-8" })
	public JSONObject refundOrders(@RequestBody JSONObject jsonParam) {
		this.logger.info("/order/refund请求参数" + jsonParam);
		String orguuid = jsonParam.getString("orgID");
		String refund_fee = jsonParam.getString("refund_fee");
		String refund_fee_type = jsonParam.getString("refund_fee_type");
		String refund_desc = jsonParam.getString("refund_desc");
		JSONObject json = new JSONObject();
		Order order = new Order();
		order.setOrguuid(orguuid);
		order = this.orderMapper.queryOrguuid(order);
		if(order!=null) {
		String out_refund_no = WXPayUtil.generateNonceStr();
		String result = refundMoney(order.getUuid(), out_refund_no, order.getTotalFee(), refund_fee, refund_fee_type,
				refund_desc);
		try {
			String s = new ClientCustomSSL().doRefund(mchId, refundUrl, result);
			logger.info("post返回报文" + s);
			Map resultMap = WXPayUtil.xmlToMap(s);
			json = JSONObject.parseObject(JSON.toJSONString(resultMap));
		} catch (Exception e) {
			json.put("result_code", "FAIL");
			json.put("err_code_des", "退款失败");
			e.printStackTrace();
		}
		}else {
			json.put("result_code", "FAIL");
			json.put("err_code_des", "订单主键不存在");
		}
		
		return json;
	}

	public String refundMoney(String out_trade_no, String out_refund_no, String total_fee, String refund_fee,
			String refund_fee_type, String refund_desc) {
		Map<String, String> map = new HashMap<String, String>();
		String nonce_str = WXPayUtil.generateNonceStr();
		if (StringUtils.isBlank(refund_fee_type)) {
			refund_fee_type = "CNY";
		}
		map.put("appid", this.appid);
		map.put("mch_id", this.mchId);
		map.put("nonce_str", nonce_str);
		map.put("sign_type", "MD5");
		map.put("out_trade_no", out_trade_no);
		map.put("out_refund_no", out_refund_no);
		map.put("out_trade_no", out_trade_no);
		map.put("total_fee", total_fee);
		map.put("refund_fee", refund_fee);
		map.put("refund_fee_type", refund_fee_type);
		map.put("refund_desc", refund_desc);
		String xmlParams = "";
		try {
			String sign = WXPayUtil.generateSignature(map, this.key);
			map.put("sign", sign);
			xmlParams = mapToXml(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlParams;
	}
}