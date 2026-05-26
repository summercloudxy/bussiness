package com.xy.bussiness.cosme;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class CosmeCategoryLabels {

    private static final Map<String, String> JA_TO_ZH;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("スキンケア・基礎化粧品", "护肤·基础化妆品");
        map.put("日焼け対策・UVケア", "防晒·UV护理");
        map.put("メイクアップ", "彩妆");
        map.put("ベースメイク", "底妆");
        map.put("香水・フレグランス", "香水·香氛");
        map.put("ヘアケア・スタイリング", "护发·造型");
        map.put("ボディケア・オーラルケア", "身体护理·口腔护理");
        map.put("コフレ・キット・セット", "套盒·套装");
        map.put("美容グッズ・美容家電", "美容工具·美容电器");
        map.put("サプリメント・フード", "supplement·食品");
        map.put("日用品", "日用品");
        map.put("保健機能食品", "保健功能食品");
        map.put("ハーブ・アロマ", "草本·芳疗");
        map.put("ダイエットサポート", "减肥辅助");
        map.put("ファッション", "时尚");
        map.put("医薬品", "医药品");
        map.put("医療機器", "医疗器械");
        map.put("食品・ドリンク", "食品·饮料");
        map.put("その他", "其他");

        // 二级分类
        map.put("化粧水", "化妆水");
        map.put("乳液・美容液・フェイスクリームなど", "乳液·精华·面霜等");
        map.put("クレンジング", "卸妆");
        map.put("洗顔料", "洁面");
        map.put("パック・フェイスマスク", "面膜·面贴膜");
        map.put("目元・口元ケア", "眼周·唇部护理");
        map.put("その他スキンケア", "其他护肤");
        map.put("化粧下地・コンシーラー", "妆前·遮瑕");
        map.put("ファンデーション", "粉底");
        map.put("フェイスパウダー", "粉饼·散粉");
        map.put("フィックスミスト", "定妆喷雾");
        map.put("アイシャドウ", "眼影");
        map.put("アイライナー", "眼线");
        map.put("マスカラ", "睫毛膏");
        map.put("アイブロウ", "眉妆");
        map.put("口紅・グロス・リップライナー", "口红·唇彩·唇线笔");
        map.put("チーク", "腮红");
        map.put("ハイライト", "高光");
        map.put("シェーディング", "修容");
        map.put("ネイル・ネイルケア", "美甲·指甲护理");
        map.put("香水・フレグランス(レディース・ウィメンズ)", "香水·香氛（女士）");
        map.put("香水・フレグランス(メンズ)", "香水·香氛（男士）");
        map.put("香水・フレグランス(その他)", "香水·香氛（其他）");

        JA_TO_ZH = Collections.unmodifiableMap(map);
    }

    private CosmeCategoryLabels() {
    }

    static String toChinese(String nameJa) {
        if (StringUtils.isBlank(nameJa)) {
            return "";
        }
        String trimmed = nameJa.trim();
        return JA_TO_ZH.getOrDefault(trimmed, trimmed);
    }

    static String toLabel(String nameJa) {
        if (StringUtils.isBlank(nameJa)) {
            return "";
        }
        String zh = toChinese(nameJa);
        return zh + "（" + nameJa.trim() + "）";
    }
}
