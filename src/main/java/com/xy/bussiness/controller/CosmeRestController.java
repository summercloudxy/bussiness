package com.xy.bussiness.controller;

import com.xy.bussiness.brand.mybatisservice.SearchBrandService;
import com.xy.bussiness.brand.mybean.SearchBrand;
import com.xy.bussiness.cosme.CosmeSearchService;
import com.xy.bussiness.cosme.SearchConditionAddService;
import com.xy.bussiness.cosme.dto.AddSearchConditionRequest;
import com.xy.bussiness.cosme.dto.AddSearchConditionResult;
import com.xy.bussiness.cosme.dto.CosmeSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CosmeRestController {

    @Autowired
    private CosmeSearchService cosmeSearchService;
    @Autowired
    private SearchConditionAddService searchConditionAddService;
    @Autowired
    private SearchBrandService searchBrandService;

    @GetMapping("/cosme/search")
    public CosmeSearchResult search(@RequestParam String keyword,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(required = false) String categoryId) {
        return cosmeSearchService.search(keyword, page, categoryId);
    }

    @GetMapping("/cosme/brands")
    public List<SearchBrand> brands() {
        return searchBrandService.listForCosme();
    }

    @GetMapping("/cosme/brands/sync")
    public Map<String, Object> syncBrands() {
        int fromConditions = searchBrandService.syncFromMercariConditions();
        int fromApi = searchBrandService.syncFromMercariApi();
        Map<String, Object> result = new HashMap<>();
        result.put("addedFromMercari", fromConditions);
        result.put("updatedFromApi", fromApi);
        result.put("brands", searchBrandService.listForCosme());
        return result;
    }

    @GetMapping("/cosme/suggestBrand")
    public Map<String, String> suggestBrand(@RequestParam(required = false) String cosmeBrand,
                                            @RequestParam(required = false) String searchKeyword) {
        Map<String, String> result = new HashMap<>();
        result.put("brand", searchConditionAddService.suggestBrand(cosmeBrand, searchKeyword));
        return result;
    }

    @PostMapping("/cosme/addCondition")
    public AddSearchConditionResult addCondition(@RequestBody AddSearchConditionRequest request) {
        return searchConditionAddService.add(request);
    }
}
