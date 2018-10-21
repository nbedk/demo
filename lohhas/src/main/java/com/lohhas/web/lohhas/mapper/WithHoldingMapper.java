package com.lohhas.web.lohhas.mapper;

import com.lohhas.web.lohhas.bean.WithHolding;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public abstract interface WithHoldingMapper {
	@Select({
			"select indexId,uuid,openId as openid,phoneNumber,flag,updateTime,mchId ,contractCode ,planId,signs as sign,changeType ,contractId  ,contractExpiredTime ,contractTerminationMode  ,requestSerial  from withholding where openId=#{openid} order by  createTime desc LIMIT 1" })
	@Results({ @org.apache.ibatis.annotations.Result(column = "mchId", property = "mch_id"),
			@org.apache.ibatis.annotations.Result(column = "contractCode", property = "contract_code"),
			@org.apache.ibatis.annotations.Result(column = "planId", property = "plan_id"),
			@org.apache.ibatis.annotations.Result(column = "changeType", property = "change_type"),
			@org.apache.ibatis.annotations.Result(column = "contractId", property = "contract_id"),
			@org.apache.ibatis.annotations.Result(column = "contractExpiredTime", property = "contract_expired_time"),
			@org.apache.ibatis.annotations.Result(column = "contractTerminationMode", property = "contract_termination_mode"),
			@org.apache.ibatis.annotations.Result(column = "requestSerial", property = "request_serial") })
	public abstract WithHolding getWithHolding(WithHolding paramWithHolding);

	@Insert({"insert into withholding (uuid,openId,phoneNumber,flag,updateTime,mchId,contractCode,planId,signs,changeType,contractId,contractExpiredTime,contractTerminationMode) values (#{uuid},#{openid},#{phoneNumber},#{flag},#{updateTime},#{mch_id},#{contract_code},#{plan_id},#{sign},#{change_type},#{contract_id},#{contract_expired_time},#{contract_termination_mode})" })
	@Options(useGeneratedKeys = true, keyProperty = "indexId", keyColumn = "indexId")
	public abstract int insertWithHolding(WithHolding paramWithHolding);

	@Delete({ "delete from withholding where openId=#{openid}" })
	public abstract int deleteWithHolding(String paramString);

	@Update("update withholding set phoneNumber=#{phoneNumber} where openId=#{openid}")
	public void updateOfphoneNumber(Map<String,String> map);

	@Update("<script> update withholding set flag=#{flag} <if test=\"mch_id != null\">,mchId=#{mch_id}</if> <if test=\"contract_code != null\">,contractCode=#{contract_code}</if> <if test=\"plan_id != null\">,planId=#{plan_id}</if> \n" + 
			"<if test=\"sign != null\">,signs=#{sign}</if> <if test=\" change_type!= null\">,changeType=#{change_type}</if> <if test=\"contract_id != null\">,contractId=#{contract_id}</if>  where openId=#{openid}  </script>")
	public void updateWithHolding(WithHolding holding);
}