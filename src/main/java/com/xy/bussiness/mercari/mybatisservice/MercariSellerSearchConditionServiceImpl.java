package com.xy.bussiness.mercari.mybatisservice;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.mercari.mapper.MercariSearchConditionMapper;
import com.xy.bussiness.mercari.mapper.MercariSellerSearchConditionMapper;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import org.springframework.stereotype.Service;

@Service
public class MercariSellerSearchConditionServiceImpl extends ServiceImpl<MercariSellerSearchConditionMapper, MercariSellerSearchCondition> implements MercariSellerSearchConditionService {
}
