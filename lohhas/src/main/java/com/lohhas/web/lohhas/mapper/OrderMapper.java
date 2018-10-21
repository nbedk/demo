package com.lohhas.web.lohhas.mapper;

import com.lohhas.web.lohhas.bean.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public abstract interface OrderMapper
{
  @Select({"<script> select uuid,createTime,openID,status ,boxId,updateTime,totalFee,attach from orders where openID = #{openId} <if test=\"status != null\">and status=#{status}</if> ORDER BY createTime desc LIMIT 1 </script>"})
  public abstract Order queryOrder(Order paramOrder);

  @Insert({"insert into orders (uuid,openID,status,boxId) values (#{uuid},#{openId},#{status},#{boxId})"})
  public abstract int insertOrder(Order paramOrder);

  @Update({"<script>update orders set status=#{status},bankType=#{bankType},totalFee=#{totalFee},cashFee=#{cashFee}<if test=\"attach != null\">,attach=#{attach}</if> <if test=\"orguuid != null\">, orguuid=#{orguuid}</if> where openID=#{openId} and status=0 order by createTime desc limit 1;</script>"})
  public abstract int updateOrder(Order paramOrder);

  @Select({"select uuid,createTime,openID,status ,boxId,updateTime,totalFee,attach,orguuid from orders where orguuid = #{orguuid} ORDER BY createTime desc LIMIT 1"})
  public abstract Order queryOrguuid(Order paramOrder); 

  @Select({"select uuid,createTime,openID,status ,boxId,updateTime,totalFee,attach,orguuid from orders where uuid = #{uuid} ORDER BY createTime desc LIMIT 1"})
  public abstract Order queryUuid(Order paramOrder);

  @Select({"update orders set status=#{status},bankType=#{bankType},totalFee=#{totalFee},cashFee=#{cashFee} where uuid = #{uuid} and status=0 order by createTime desc limit 1"})
  public abstract Order updateUuid(Order paramOrder);
}