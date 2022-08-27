package com.xy.bussiness.mercari.constants;

public enum CategoryEnum {
    HUAZHUANGPIN(6,"化妆品/香水/美容"),
    DIZHUANGALL(88,"底妆全部"),
    JICHU(789,"基础"),
    DIZHUANG(788,"底妆"),
    XIANGFEN(792,"香粉"),
    DIZHUANGOTHER(800,"底妆其他"),
    HUAZHUANGPINALL(1387,"化妆品全部"),
    YANYING(793,"眼影"),
    KOUHONG(796,"口红"),
    CHUNCAI(1283,"唇彩"),
    LIANJIA(791,"脸颊"),
    LIANSE(1284,"脸色"),
    YANXIANBI(794,"眼线笔"),
    HUAZHUANGPINOTHER(1390,"化妆品其他"),
    ;



    CategoryEnum(int id, String description) {
        this.id = id;
        this.description = description;
    }

    private int id;
    private String description;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static Integer getIdByName(String name){
        CategoryEnum categoryEnum = CategoryEnum.valueOf(name);
        if (categoryEnum != null){
            return categoryEnum.getId();
        }
        else {
            return null;
        }
    }
}
