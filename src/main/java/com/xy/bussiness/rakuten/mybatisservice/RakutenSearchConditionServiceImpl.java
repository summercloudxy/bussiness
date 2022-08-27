package com.xy.bussiness.rakuten.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.rakuten.mapper.RakutenSearchConditionMapper;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import org.springframework.stereotype.Service;

@Service
public class RakutenSearchConditionServiceImpl extends ServiceImpl<RakutenSearchConditionMapper, RakutenSearchCondition> implements RakutenSearchConditionService {
}
