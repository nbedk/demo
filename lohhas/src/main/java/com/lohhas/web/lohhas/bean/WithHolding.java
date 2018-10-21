package com.lohhas.web.lohhas.bean;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Request")
public class WithHolding
{
  private BigInteger indexId;
  private String uuid;

  @XmlElement
  private String return_code;

  @XmlElement
  private String result_code;

  @XmlElement
  private String return_msg;

  @XmlElement
  private String sign;

  @XmlElement
  private String mch_id;
  private String flag;

  @XmlElement
  private String contract_code;

  @XmlElement
  private String openid;

  @XmlElement
  private String plan_id;

  @XmlElement
  private String change_type;

  @XmlElement
  private String operate_time;

  @XmlElement
  private String contract_id;

  @XmlElement
  private String contract_expired_time;
  private String contract_termination_mode;

  @XmlElement
  private String request_serial;
  private String createTime;
  private String updateTime;
  private String phoneNumber;

  public String getResult_code()
  {
    return this.result_code;
  }

  public void setResult_code(String result_code) {
    this.result_code = result_code;
  }

  public WithHolding(BigInteger indexId, String uuid, String return_code, String return_msg, String sign, String mch_id, String flag, String contract_code, String openid, String plan_id, String change_type, String operate_time, String contract_id, String contract_expired_time, String contract_termination_mode, String request_serial, String createTime, String updateTime, String phoneNumber)
  {
    this.indexId = indexId;
    this.uuid = uuid;
    this.return_code = return_code;
    this.return_msg = return_msg;
    this.sign = sign;
    this.mch_id = mch_id;
    this.flag = flag;
    this.contract_code = contract_code;
    this.openid = openid;
    this.plan_id = plan_id;
    this.change_type = change_type;
    this.operate_time = operate_time;
    this.contract_id = contract_id;
    this.contract_expired_time = contract_expired_time;
    this.contract_termination_mode = contract_termination_mode;
    this.request_serial = request_serial;
    this.createTime = createTime;
    this.updateTime = updateTime;
    this.phoneNumber = phoneNumber;
  }

  public WithHolding()
  {
  }

  public String getPhoneNumber()
  {
    return this.phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getFlag() {
    return this.flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getContract_termination_mode() {
    return this.contract_termination_mode;
  }

  public String getContract_expired_time() {
    return this.contract_expired_time;
  }

  public void setContract_expired_time(String contract_expired_time) {
    this.contract_expired_time = contract_expired_time;
  }

  public String getCreateTime() {
    return this.createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getUpdateTime() {
    return this.updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public void setContract_termination_mode(String contract_termination_mode) {
    this.contract_termination_mode = contract_termination_mode;
  }

  public String getRequest_serial() {
    return this.request_serial;
  }

  public void setRequest_serial(String request_serial) {
    this.request_serial = request_serial;
  }

  public String getReturn_code() {
    return this.return_code;
  }

  public void setReturn_code(String return_code) {
    this.return_code = return_code;
  }

  public String getSign() {
    return this.sign;
  }

  public String toString()
  {
    return "WithHolding [indexId=" + this.indexId + ", uuid=" + this.uuid + ", return_code=" + this.return_code + ", result_code=" + this.result_code + ", return_msg=" + this.return_msg + ", sign=" + this.sign + ", mch_id=" + this.mch_id + ", flag=" + this.flag + ", contract_code=" + this.contract_code + ", openid=" + this.openid + ", plan_id=" + this.plan_id + ", change_type=" + this.change_type + ", operate_time=" + this.operate_time + ", contract_id=" + this.contract_id + ", contract_expired_time=" + this.contract_expired_time + ", contract_termination_mode=" + this.contract_termination_mode + ", request_serial=" + this.request_serial + ", createTime=" + this.createTime + ", updateTime=" + this.updateTime + ", phoneNumber=" + this.phoneNumber + "]";
  }

  public String getReturn_msg()
  {
    return this.return_msg;
  }

  public void setReturn_msg(String return_msg) {
    this.return_msg = return_msg;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getMch_id() {
    return this.mch_id;
  }

  public void setMch_id(String mch_id) {
    this.mch_id = mch_id;
  }

  public String getContract_code() {
    return this.contract_code;
  }

  public void setContract_code(String contract_code) {
    this.contract_code = contract_code;
  }

  public String getOpenid() {
    return this.openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getPlan_id() {
    return this.plan_id;
  }

  public void setPlan_id(String plan_id) {
    this.plan_id = plan_id;
  }

  public String getChange_type() {
    return this.change_type;
  }

  public void setChange_type(String change_type) {
    this.change_type = change_type;
  }

  public String getOperate_time() {
    return this.operate_time;
  }

  public void setOperate_time(String operate_time) {
    this.operate_time = operate_time;
  }

  public String getContract_id() {
    return this.contract_id;
  }

  public void setContract_id(String contract_id) {
    this.contract_id = contract_id;
  }

  public BigInteger getIndexId() {
    return this.indexId;
  }

  public void setIndexId(BigInteger indexId) {
    this.indexId = indexId;
  }
}