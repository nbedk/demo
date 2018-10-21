package com.lohhas.web.lohhas.controller;

import com.alibaba.fastjson.JSONObject;
import com.lohhas.web.lohhas.utils.HttpsUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ComponentScan({"com.lohhas.web.lohhas.controller"})
@MapperScan({"com.lohhas.web.lohhas.mapper"})
@RequestMapping({"/openId"})
public class OpenIdController
{
  Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${weixin.appid}")
  private String appid;

  @Value("${weixin.secret}")
  private String secret;

  @Autowired
  WithHoldingController withHoldingController;

  @ResponseBody
  @RequestMapping(value={"/queryOpenId"}, method={RequestMethod.POST}, produces={"application/json;charset=UTF-8"})
  public JSONObject queryOpenId(@RequestBody JSONObject jsonParam) { this.logger.info("/openId/queryOpenId请求参数" + jsonParam);

    String code = jsonParam.getString("code");
    String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + this.appid + "&secret=" + this.secret + "&js_code=" + code + "&grant_type=authorization_code";
    JSONObject result = HttpsUtils.doGet(url);
    this.logger.info("/openId/queryOpenId返回参数" + result);

    String openId = result.getString("openid");
    JSONObject json = this.withHoldingController.getJson(openId);
    result.putAll(json);
    return result;
  }
}