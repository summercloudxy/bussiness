package com.xy.bussiness.yahoo.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.yahoo.mapper.YahooSearchConditionMapper;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import org.springframework.stereotype.Service;

@Service
public class YahooSearchConditionServiceImpl extends ServiceImpl<YahooSearchConditionMapper, YahooSearchCondition> implements YahooSearchConditionService {
}
