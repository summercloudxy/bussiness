package com.xy.bussiness.cosme;

import com.xy.bussiness.cosme.dto.CosmeCategoryFilter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CosmeSearchServiceTest {

    private static final String LV2_SELECTED_SECTION =
            "<div id=\"nrw-ctg\">"
                    + "<ul>"
                    + "<li class=\"lv1\">"
                    + "<p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/800\" class=\"active\">"
                    + "スキンケア・基礎化粧品<span>(553)</span></a></p>"
                    + "<ul class=\"lv2\">"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/900\">洗顔料<span>(53)</span></a></p>"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/901\">クレンジング<span>(52)</span></a></p>"
                    + "<li><p><span class=\"current\">化粧水<span>(90)</span></span></p>"
                    + "<ul class=\"lv3\">"
                    + "<li><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/1003\">化粧水<span>(81)</span></a></li>"
                    + "</ul>"
                    + "</li>"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/903\">乳液<span>(252)</span></a></p>"
                    + "</ul>"
                    + "</li>"
                    + "<li class=\"lv1\">"
                    + "<p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/801\">日焼け対策<span>(77)</span></a></p>"
                    + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "<div id=\"nrw-efct\"></div>";

    private static final String LV2_FACE_POWDER_SECTION =
            "<div id=\"nrw-ctg\">"
                    + "<ul>"
                    + "<li class=\"lv1\"><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/802\">"
                    + "メイクアップ<span>(478)</span></a></p></li>"
                    + "<li class=\"lv1\">"
                    + "<p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/803\" class=\"active\">"
                    + "ベースメイク<span>(269)</span></a></p>"
                    + "<ul class=\"lv2\">"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/916\">ファンデーション<span>(117)</span></a></p>"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/917\">化粧下地<span>(97)</span></a></p>"
                    + "<li><p><span class=\"current\">フェイスパウダー<span>(73)</span></span></p>"
                    + "<ul class=\"lv3\">"
                    + "<li><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/1014\">ルースパウダー<span>(18)</span></a></li>"
                    + "</ul></li>"
                    + "<li><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/964\">フィックスミスト<span>(4)</span></a></p>"
                    + "</ul></li>"
                    + "<li class=\"lv1\"><p><a href=\"https://cosmeet.cosme.net/product/search/srt/4/fw/dior/itm/804\">"
                    + "香水<span>(287)</span></a></p></li>"
                    + "</ul></div><div id=\"nrw-efct\"></div>";

    @Test
    void parseCategoriesKeepsActiveLv1BeforeLv2Children() throws Exception {
        CosmeSearchService service = new CosmeSearchService();
        Method method = CosmeSearchService.class.getDeclaredMethod("parseCategories", String.class, String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CosmeCategoryFilter> categories = (List<CosmeCategoryFilter>) method.invoke(
                service, LV2_FACE_POWDER_SECTION, "918");

        assertEquals("802", categories.get(1).getId());
        assertEquals(1, categories.get(1).getLevel());
        assertEquals("803", categories.get(2).getId());
        assertEquals("ベースメイク", categories.get(2).getNameJa());
        assertEquals(1, categories.get(2).getLevel());
        assertEquals("916", categories.get(3).getId());
        assertEquals(2, categories.get(3).getLevel());
        assertEquals("フェイスパウダー", categories.get(5).getNameJa());
        assertEquals(2, categories.get(5).getLevel());
        assertTrue(categories.get(5).isSelected());
        assertTrue(categories.get(5).isSelectable());
        assertEquals("918", categories.get(5).getId());
        assertEquals("804", categories.get(7).getId());
        assertEquals(1, categories.get(7).getLevel());
    }

    @Test
    void parseCategoriesKeepsLv1BeforeLv2WhenLv2Selected() throws Exception {
        CosmeSearchService service = new CosmeSearchService();
        Method method = CosmeSearchService.class.getDeclaredMethod("parseCategories", String.class, String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CosmeCategoryFilter> categories = (List<CosmeCategoryFilter>) method.invoke(service, LV2_SELECTED_SECTION, "902");

        assertTrue(categories.size() >= 5);
        assertEquals("", categories.get(0).getId());
        assertEquals("800", categories.get(1).getId());
        assertEquals(1, categories.get(1).getLevel());
        assertEquals("900", categories.get(2).getId());
        assertEquals(2, categories.get(2).getLevel());
        assertEquals("901", categories.get(3).getId());
        assertEquals(2, categories.get(3).getLevel());
        assertEquals("化粧水", categories.get(4).getNameJa());
        assertEquals(2, categories.get(4).getLevel());
        assertTrue(categories.get(4).isSelected());
        assertEquals("902", categories.get(4).getId());
        assertEquals("903", categories.get(5).getId());
        assertEquals(2, categories.get(5).getLevel());
        assertEquals("801", categories.get(6).getId());
        assertEquals(1, categories.get(6).getLevel());
    }

    @Test
    void parseCategoriesFromSavedHtml() throws Exception {
        Path htmlPath = Paths.get("/tmp/cosme.html");
        assumeTrue(Files.exists(htmlPath), "需要先 curl 保存 /tmp/cosme.html");

        byte[] bytes = Files.readAllBytes(htmlPath);
        String html = new String(bytes, Charset.forName("Shift_JIS"));

        CosmeSearchService service = new CosmeSearchService();
        Method method = CosmeSearchService.class.getDeclaredMethod("parseCategories", String.class, String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<?> categories = (List<?>) method.invoke(service, html, null);

        assertTrue(categories.size() > 10, "应解析出多个类别，实际: " + categories.size());
    }
}
