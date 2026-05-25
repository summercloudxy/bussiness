package com.xy.bussiness.mercari;

import com.xy.bussiness.mercari.apibean.ItemsItem;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = {
        "mercari.enable=false",
        "network.proxy.enabled=true",
        "network.proxy.host=127.0.0.1",
        "network.proxy.port=7897"
})
public class StandardDpopMercariCrawlerTest {

    @Autowired
    private MercariCrawler mercariCrawler;

    @Test
    public void searchWithStandardDpop() throws Exception {
        MercariSearchCondition condition = new MercariSearchCondition();
        condition.setKeyword("chanel");

        List<ItemsItem> items = mercariCrawler.getMercariItemsByCondition(condition);
        System.out.println("mercari item count: " + (items == null ? 0 : items.size()));
        if (items != null && !items.isEmpty()) {
            ItemsItem first = items.get(0);
            System.out.println("first item id: " + first.getId());
            System.out.println("first item name: " + first.getName());
        }
    }
}
