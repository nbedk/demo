package com.lohhas.web.lohhas.controller;

import com.alibaba.fastjson.JSONObject;
import com.lohhas.web.lohhas.bean.Order;
import com.lohhas.web.lohhas.mapper.OrderMapper;
import com.lohhas.web.lohhas.utils.HttpsUtils;
import java.util.UUID;
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
@RequestMapping({"/door"})
@ComponentScan({"com.lohhas.web.lohhas.controller"})
@MapperScan({"com.lohhas.web.lohhas.mapper"})
public class DoorController
{
  Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${door.open.url}")
  private String openUrl;

  @Autowired
  OrderMapper orderMapper;

  @ResponseBody
  @RequestMapping(value={"/open"}, method={RequestMethod.POST}, produces={"application/json;charset=UTF-8"})
  public JSONObject queryOpenId(@RequestBody JSONObject jsonParam) 
  { this.logger.info("/door/open请求参数" + jsonParam);
    String boxId = jsonParam.getString("boxId");
    String openDoorSource = jsonParam.getString("openDoorSource");
    String distribution = jsonParam.getString("distribution");
    String customerMobile = jsonParam.getString("customerMobile");
    String partnerCustomerId = jsonParam.getString("partnerCustomerId");
    String url = this.openUrl + "?boxId=" + boxId + "&openDoorSource=" + openDoorSource + "&distribution=" + distribution + "&customerMobile=" + customerMobile + "&partnerCustomerId=" + partnerCustomerId;
    System.out.println(url);
    JSONObject resulr = HttpsUtils.doGet(url);
    Order order = new Order();
    String uuid = UUID.randomUUID().toString();
    uuid = uuid.replace("-", "");
    order.setUuid(uuid);
    order.setOpenid(partnerCustomerId);
    order.setBoxId(boxId);
    order.setStatus("0");
    this.orderMapper.insertOrder(order);
    this.logger.info("/door/openf返回参数" + resulr);
    return resulr;
  }
}