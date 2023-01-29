package com.xy.bussiness.mercari.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.mercari.mapper.MercariItemMapper;
import com.xy.bussiness.mercari.mapper.MercariSellerItemMapper;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.SellerItemRecord;
import org.springframework.stereotype.Service;

@Service
public class MercariSellerItemRecordServiceImpl extends ServiceImpl<MercariSellerItemMapper, SellerItemRecord> implements MercariSellerItemRecordService {
}
