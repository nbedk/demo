package com.lohhas.web.lohhas.controller;

import com.alibaba.fastjson.JSONObject;
import com.lohhas.web.lohhas.bean.WithHolding;
import com.lohhas.web.lohhas.mapper.WithHoldingMapper;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan({ "com.lohhas.web.lohhas.controller" })
@MapperScan({ "com.lohhas.web.lohhas.mapper" })
@RequestMapping({ "/withholding" })
public class WithHoldingController {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	WithHoldingMapper withHoldingMapper;

	@ResponseBody
	@RequestMapping(value = { "/query" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST }, produces = {
					"application/json;charset=UTF-8" })
	public JSONObject queryWithHolding(@RequestBody JSONObject jsonParam) {
		this.logger.info("/withholding/query请求参数" + jsonParam);
		String openId = jsonParam.getString("openId");
		JSONObject json = getJson(openId);
		this.logger.info("/withholding/contractFlag返回参数" + json);
		return json;
	}

	public JSONObject getJson(String openId) {
		WithHolding holding = new WithHolding();
		holding.setOpenid(openId);
		holding = this.withHoldingMapper.getWithHolding(holding);
		boolean flag = false;
		JSONObject jsonContract = new JSONObject();
		JSONObject jsonPhone = new JSONObject();
		JSONObject json = new JSONObject();
		json.put("contract", jsonContract);
		String phoneNumber = "";
		boolean phoneFlag = false;
		if (holding == null) {
			holding = new WithHolding();
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replace("-", "");
			holding.setUuid(uuid);
			holding.setOpenid(openId);
			this.withHoldingMapper.insertWithHolding(holding);
			jsonContract.put("uuid", holding.getUuid());
			jsonContract.put("indexId", holding.getIndexId());

		} else {
			if ("1".equals(holding.getFlag())) {
				flag = true;
			} else {
				jsonContract.put("uuid", holding.getUuid());
				jsonContract.put("indexId", holding.getIndexId());
			}
			phoneNumber = holding.getPhoneNumber();
			if (StringUtils.isNotBlank(phoneNumber)) {
				phoneFlag = true;
			}
		}

		jsonPhone.put("phoneNumber", phoneNumber);
		jsonPhone.put("phoneFlag", phoneFlag);
		jsonContract.put("contractFlag", flag);
		json.put("phone", jsonPhone);
		return json;
	}

	@ResponseBody
	@RequestMapping(value = { "/result" }, method = {RequestMethod.POST }, produces = {"application/xml;charset=UTF-8" })
	public String resultWithHolding(@RequestBody WithHolding result) {
		this.logger.info("/withholding/result请求参数" + result.toString());
		String returnCode = "ERROR"; 
		String returnMsg = "签约失败";
		WithHolding holding = new WithHolding();
		if (("SUCCESS".equals(result.getReturn_code())) && ("OK".equals(result.getReturn_msg()))) {
			String mchId = result.getMch_id();
			String contractCode = result.getContract_code();
			String planId = result.getPlan_id();
			String openId = result.getOpenid();
			String sign = result.getSign();
			String changeType = result.getChange_type();
//			String operateTime = result.getOperate_time();
			String contractId = result.getContract_id();
			String contractExpiredTime = result.getContract_expired_time();
			String contractTerminationMode = result.getContract_termination_mode();
			String requestSerial = result.getRequest_serial();
			holding.setUuid(UUID.randomUUID().toString());
			holding.setMch_id(mchId);
			holding.setContract_code(contractCode);
			holding.setPlan_id(planId);
			holding.setOpenid(openId);
			holding.setSign(sign);
			holding.setChange_type(changeType);
			holding.setContract_id(contractId);
			holding.setContract_expired_time(contractExpiredTime);
			holding.setContract_termination_mode(contractTerminationMode);
			holding.setRequest_serial(requestSerial);
			WithHolding holdingResult = new WithHolding();
			holdingResult = withHoldingMapper.getWithHolding(holding);
			holding.setFlag("1");
			if (holdingResult == null) {
				withHoldingMapper.insertWithHolding(holding);
			}else {
				withHoldingMapper.updateWithHolding(holding);
			}
			returnCode = "SUCCESS";
			returnMsg = "OK";
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
		this.logger.info("/withholding/result返回参数" + sb.toString());
		return sb.toString();
	}

	@ResponseBody
	@RequestMapping(value = { "/cancle" }, method = {RequestMethod.POST }, produces = {"application/xml;charset=UTF-8" })
	public String cancleWithHolding(@RequestBody WithHolding result) {
		this.logger.info("/withholding/result请求参数" + result.toString());
		String returnCode = "ERROR";
		String returnMsg = "签约失败";
		WithHolding holding = new WithHolding();
		if (("SUCCESS".equals(result.getReturn_code())) && ("OK".equals(result.getReturn_msg()))) {
			String mchId = result.getMch_id();
			String contractCode = result.getContract_code();
			String planId = result.getPlan_id();
			String openId = result.getOpenid();
			String sign = result.getSign();
			String changeType = result.getChange_type();
//			String operateTime = result.getOperate_time();
			String contractId = result.getContract_id();
			String contractExpiredTime = result.getContract_expired_time();

			String contractTerminationMode = result.getContract_termination_mode();
			String requestSerial = result.getRequest_serial();
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replace("-", "");
			holding.setUuid(uuid);
			holding.setMch_id(mchId);
			holding.setContract_code(contractCode);
			holding.setPlan_id(planId);
			holding.setOpenid(openId);
			holding.setSign(sign);
			holding.setChange_type(changeType);
			holding.setContract_id(contractId);
			holding.setContract_expired_time(contractExpiredTime);
			holding.setContract_termination_mode(contractTerminationMode);
			holding.setRequest_serial(requestSerial);

			this.withHoldingMapper.deleteWithHolding(openId);
			returnCode = "SUCCESS";
			returnMsg = "OK";
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
		this.logger.info("/withholding/result返回参数" + sb.toString());
		return sb.toString();
	}
}