package com.xy.bussiness.cosme;

import org.apache.commons.lang3.StringUtils;

final class TextSimilarityUtil {

    private TextSimilarityUtil() {
    }

    static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    static String removeSpaces(String text) {
        if (text == null) {
            return "";
        }
        return normalize(text).replace(" ", "");
    }

    /** 去掉 @cosme 产品名中 /品牌 等后缀，便于与关键字比对 */
    static String extractComparableName(String productName) {
        if (StringUtils.isBlank(productName)) {
            return "";
        }
        String name = normalize(productName);
        int slash = name.indexOf('/');
        if (slash > 0) {
            name = name.substring(0, slash).trim();
        }
        return name;
    }

    static double similarity(String left, String right) {
        if (StringUtils.isBlank(left) || StringUtils.isBlank(right)) {
            return 0D;
        }
        double direct = scorePair(left, right);
        double comparable = scorePair(left, extractComparableName(right));
        return Math.max(direct, comparable);
    }

    private static double scorePair(String left, String right) {
        String normalizedLeft = normalize(left);
        String normalizedRight = normalize(right);
        if (normalizedLeft.equalsIgnoreCase(normalizedRight)) {
            return 100D;
        }

        String compactLeft = removeSpaces(normalizedLeft);
        String compactRight = removeSpaces(normalizedRight);
        if (StringUtils.isBlank(compactLeft) || StringUtils.isBlank(compactRight)) {
            return 0D;
        }
        if (compactLeft.equalsIgnoreCase(compactRight)) {
            return 100D;
        }

        String longer = compactLeft.length() >= compactRight.length() ? compactLeft : compactRight;
        String shorter = compactLeft.length() >= compactRight.length() ? compactRight : compactLeft;
        if (longer.contains(shorter) && shorter.length() >= 4) {
            double containRatio = (double) shorter.length() / longer.length();
            if (longer.startsWith(shorter) && containRatio >= 0.88D) {
                return containRatio * 100D;
            }
        }

        return levenshteinRatio(compactLeft, compactRight) * 100D;
    }

    private static double levenshteinRatio(String left, String right) {
        int distance = levenshteinDistance(left, right);
        int maxLen = Math.max(left.length(), right.length());
        if (maxLen == 0) {
            return 1D;
        }
        return 1D - ((double) distance / maxLen);
    }

    private static int levenshteinDistance(String left, String right) {
        int[][] dp = new int[left.length() + 1][right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= right.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= left.length(); i++) {
            for (int j = 1; j <= right.length(); j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[left.length()][right.length()];
    }
}
