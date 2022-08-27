package com.xy.bussiness.mercari.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MercariItemMapper extends BaseMapper<ItemRecord> {

}