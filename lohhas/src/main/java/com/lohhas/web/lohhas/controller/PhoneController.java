package com.lohhas.web.lohhas.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lohhas.web.lohhas.mapper.WithHoldingMapper;
import com.lohhas.web.lohhas.utils.AES;

import java.util.HashMap;
import java.util.Map;

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
@ComponentScan({"com.lohhas.web.lohhas.controller"})
@RequestMapping({"/phone"})
public class PhoneController
{
  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  WithHoldingMapper withHoldingMapper;

  @ResponseBody
  @RequestMapping(value={"/query"}, method={RequestMethod.POST}, produces={"application/json;charset=UTF-8"})
  public JSONObject queryOrder(@RequestBody JSONObject jsonParam) { this.logger.info("/phone/query请求参数" + jsonParam);
    String encrypted = jsonParam.getString("encrypted");
    String session_key = jsonParam.getString("session_key");
    String iv = jsonParam.getString("iv");
    String json = AES.wxDecrypt(encrypted, session_key, iv);
    String openid = jsonParam.getString("openid");
    JSONObject jsonPhone = JSONObject.parseObject(json);
    Map<String,String> map  = new HashMap<String,String>();
    map.put("phoneNumber", jsonPhone.getString("phoneNumber"));
    map.put("openid", openid);
    this.withHoldingMapper.updateOfphoneNumber(map);
    this.logger.info("/phone/query返回参数" + jsonParam);
    return JSON.parseObject(json);
  }
}