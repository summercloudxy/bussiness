package com.xy.bussiness.mercari.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.mercari.mapper.MercariItemMapper;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import org.springframework.stereotype.Service;

@Service
public class MercariItemRecordServiceImpl extends ServiceImpl<MercariItemMapper, ItemRecord> implements MercariItemRecordService {
}
