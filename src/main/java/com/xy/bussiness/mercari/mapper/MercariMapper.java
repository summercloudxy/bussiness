package com.xy.bussiness.mercari.mapper;

import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MercariMapper {
    @Select("select * from mercari_search_condition")
    List<MercariSearchCondition> getAllSearchCondition();

    @Select("select * from mercari_item_record where search_condition_id = #{searchConditionId}")
    List<ItemRecord> getItemRecordsByCondition(@Param("searchConditionId") Integer searchConditionId);


    @Select("select * from mercari_item_record where seller_id = #{sellerId}")
    List<ItemRecord> getItemRecordsBySeller(@Param("sellerId") String sellerId);


    @Select("select * from mercari_item_record where mercari_item_id = #{mercariItemId} limit 0,1")
    ItemRecord getItemRecordByMercariId(@Param("mercariItemId")String mercariItemId);


    @Select("delete from mercari_item_record where search_condition_id = #{searchConditionId}")
    void delItemRecords(@Param("searchConditionId") Integer searchConditionId);

    @Insert("<script>" +
            "insert into mercari_item_record (search_condition_id,mercari_item_id,mercari_item_title,interest,origin_price,current_price) values" +
            "<foreach collection='list' item='item' open='(' close=')' separator='),('>"  +
            "#{item.searchConditionId},#{item.mercariItemId},#{item.mercariItemTitle},#{item.interest},#{item.originPrice},#{item.currentPrice}" +
            "</foreach>" +
            "</script>")
    void insertItemRecords(List<ItemRecord> records);

    @Insert("<script>" +
            "<foreach collection='list' item='item' separator=';'>" +
            "update mercari_item_record set current_price =#{item.currentPrice} where mercari_item_id = #{item.mercariItemId} " +
            "</foreach>" +
            "</script>")
    void updateItemRecords(List<ItemRecord> records);

    @Update("update mercari_item_record set interest = #{interest} where  mercari_item_id = #{itemId}")
    void setInterest(@Param("itemId")String itemId,@Param("interest")Integer interest);


    @Update("update mercari_seller_item_record set interest = #{interest} where  mercari_item_id = #{itemId}")
    void setSellerInterest(@Param("itemId")String itemId,@Param("interest")Integer interest);
}
