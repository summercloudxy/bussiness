package com.xy.bussiness.mercari.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.SellerItemRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MercariSellerItemMapper extends BaseMapper<SellerItemRecord> {

}