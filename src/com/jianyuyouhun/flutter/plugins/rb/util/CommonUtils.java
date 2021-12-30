package com.jianyuyouhun.flutter.plugins.rb.util;

import org.apache.http.util.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 下划线转小驼峰
     *
     * @param source
     * @return
     */
    public static String lineToCamel(String source) {
        source = source.toLowerCase();
        Matcher matcher = linePattern.matcher(source);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 下划线转大驼峰
     *
     * @param source
     * @return
     */
    public static String lineToBigCamel(String source) {
        String smallCamel = lineToCamel(source);
        if (TextUtils.isEmpty(smallCamel)) {
            return smallCamel;
        }
        String firstChar = String.valueOf(smallCamel.charAt(0));
        return smallCamel.replaceFirst(firstChar, firstChar.toUpperCase());
    }


    /**
     * 驼峰转下划线
     */
    public static String camelToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.startsWith("_")) {
            return result.substring(1);
        }
        return result;
    }

}
