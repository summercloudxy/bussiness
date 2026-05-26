package com.xy.bussiness.brand.mybatisservice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.bussiness.brand.mybean.SearchBrand;

import java.util.List;

public interface SearchBrandService extends IService<SearchBrand> {

    List<SearchBrand> listForCosme();

    int syncFromMercariConditions();

    int syncFromMercariApi();

    String suggestBrandSlug(String cosmeBrand, String searchKeyword);
}
