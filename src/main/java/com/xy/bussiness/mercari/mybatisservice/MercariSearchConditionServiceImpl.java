package com.xy.bussiness.mercari.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.mercari.mapper.MercariSearchConditionMapper;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.springframework.stereotype.Service;

@Service
public class MercariSearchConditionServiceImpl extends ServiceImpl<MercariSearchConditionMapper, MercariSearchCondition> implements MercariSearchConditionService {
}
