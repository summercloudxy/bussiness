package com.xy.bussiness.yahoo.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.yahoo.mapper.YahooItemMapper;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import org.springframework.stereotype.Service;

@Service
public class YahooItemRecordServiceImpl extends ServiceImpl<YahooItemMapper, YahooItemRecord> implements YahooItemRecordService {
}
