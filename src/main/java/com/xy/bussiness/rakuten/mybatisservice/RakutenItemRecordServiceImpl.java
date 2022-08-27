package com.xy.bussiness.rakuten.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import org.springframework.stereotype.Service;
import com.xy.bussiness.rakuten.mapper.RakutenItemMapper;

@Service
public class RakutenItemRecordServiceImpl extends ServiceImpl<RakutenItemMapper, RakutenItemRecord> implements RakutenItemRecordService {
}
