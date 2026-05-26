package com.xy.bussiness.controller;

import com.xy.bussiness.brand.mybatisservice.SearchBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CosmePageController {

    @Autowired
    private SearchBrandService searchBrandService;

    @GetMapping("/cosme")
    public String index(Model model,
                        @RequestParam(value = "keyword", required = false, defaultValue = "dior") String keyword) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("brands", searchBrandService.listForCosme());
        return "cosme";
    }
}
