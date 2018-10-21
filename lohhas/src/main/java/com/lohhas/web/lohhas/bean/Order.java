package com.lohhas.web.lohhas.bean;

import java.security.Timestamp;

public class Order
{
  private String uuid;
  private String openId;
  private String status;
  private Timestamp createTime;
  private String boxId;
  private Timestamp updateTime;
  private String bankType;
  private String totalFee;
  private String cashFee;
  private String attach;
  private String sign;
  private String orguuid;
  
  public String getOrguuid() {
	return orguuid;
}

public void setOrguuid(String orguuid) {
	this.orguuid = orguuid;
}

public String getSign()
  {
    return this.sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getAttach() {
    return this.attach;
  }

  public void setAttach(String attach) {
    this.attach = attach;
  }

  public String getTotalFee() {
    return this.totalFee;
  }

  public void setTotalFee(String totalFee) {
    this.totalFee = totalFee;
  }

  public String getCashFee() {
    return this.cashFee;
  }

  public void setCashFee(String cashFee) {
    this.cashFee = cashFee;
  }

  public String getOpenId() {
    return this.openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }
  public String getBankType() {
    return this.bankType;
  }

  public void setBankType(String bankType) {
    this.bankType = bankType;
  }
  public Timestamp getUpdateTime() {
    return this.updateTime;
  }

  public void setUpdateTime(Timestamp updateTime) {
    this.updateTime = updateTime;
  }

  public String getBoxId() {
    return this.boxId;
  }

  public void setBoxId(String boxId) {
    this.boxId = boxId;
  }

  public Timestamp getCreateTime() {
    return this.createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getOpenid() {
    return this.openId;
  }

  public void setOpenid(String openid) {
    this.openId = openid;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}