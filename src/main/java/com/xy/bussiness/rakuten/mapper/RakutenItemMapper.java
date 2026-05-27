package com.xy.bussiness.rakuten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RakutenItemMapper extends BaseMapper<RakutenItemRecord> {

    @Select("<script>" +
            "SELECT * FROM (" +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY search_condition_id ORDER BY COALESCE(create_date, update_date) DESC) AS rn " +
            "FROM rakuten_item_record WHERE search_condition_id IN " +
            "<foreach collection='conditionIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            ") ranked WHERE ranked.rn &lt;= #{limit}" +
            "</script>")
    List<RakutenItemRecord> getLatestItemsByConditionIds(@Param("conditionIds") List<Integer> conditionIds,
                                                         @Param("limit") int limit);
}
