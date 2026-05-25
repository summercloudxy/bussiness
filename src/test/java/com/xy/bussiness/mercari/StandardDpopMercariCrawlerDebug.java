package com.xy.bussiness.mercari;

import com.xy.bussiness.BussinessApplication;
import com.xy.bussiness.mercari.apibean.ItemsItem;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class StandardDpopMercariCrawlerDebug {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(BussinessApplication.class)
                .web(WebApplicationType.NONE)
                .properties(
                        "mercari.enable=false",
                        "network.proxy.enabled=true",
                        "network.proxy.host=127.0.0.1",
                        "network.proxy.port=7897"
                )
                .run(args);
        try {
            MercariCrawler mercariCrawler = context.getBean(MercariCrawler.class);
            MercariSearchCondition condition = new MercariSearchCondition();
            condition.setKeyword("chanel");
            List<ItemsItem> items = mercariCrawler.getMercariItemsByCondition(condition);
            System.out.println("mercari item count: " + (items == null ? 0 : items.size()));
            if (items != null && !items.isEmpty()) {
                ItemsItem first = items.get(0);
                System.out.println("first item id: " + first.getId());
                System.out.println("first item name: " + first.getName());
            }
        } finally {
            context.close();
        }
    }
}
