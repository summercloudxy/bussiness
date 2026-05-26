package com.xy.bussiness.cosme;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CosmeSearchServiceTest {

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
