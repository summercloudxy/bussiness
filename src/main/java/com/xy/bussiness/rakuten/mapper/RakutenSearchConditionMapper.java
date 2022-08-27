package com.xy.bussiness.rakuten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RakutenSearchConditionMapper extends BaseMapper<RakutenSearchCondition> {
}
