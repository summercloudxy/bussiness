package com.xy.bussiness.mercari.constants;

//https://api.mercari.jp/services/master/v1/itemConditions

public enum ConditionEnum {
    QUANXIN(1,"**全新"),JINQUANXIN(2,"*接近全新"),MEIYOUMINGXIAN(3,"没有明显的划痕或污垢"),YOUYIXIE(4,"有一些划痕和污垢"),
    HUAHEN(5,"划痕和污垢"),BUJIA(6,"整体状况不佳");

    ConditionEnum(int id, String description) {
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

    public static String getDescriptionById(Integer id){
        for (ConditionEnum conditionEnum: values()){
            if (conditionEnum.getId() == id){
                return conditionEnum.getDescription();
            }
        }
        return "";
    }


    public static Integer getIdByName(String name){
        ConditionEnum conditionEnum = ConditionEnum.valueOf(name);
        if (conditionEnum != null){
            return conditionEnum.getId();
        }
        else {
            return null;
        }
    }
}
