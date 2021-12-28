package com.sqd.util;

public class StringUtils {

    public static final String EMPTY_JSON = "{}";

    public static final char BACKSLASH = '\\';

    public static final char DELIM_START = '{';

    public static final String NULL = "null";

    /**
     * is not empty string.
     *
     * @param str source string.
     * @return is not empty.
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * is empty string.
     *
     * @param str source string.
     * @return is empty.
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isBlank(template) || ArrayUtils.isEmpty(params)) {
            return template;
        }
        final int strPatternLength = template.length();

        // 初始化定义好的长度以获得更好的性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        // 记录已经处理到的位置
        int handledPosition = 0;
        // 占位符所在位置
        int delimIndex;
        for (int argIndex = 0; argIndex < params.length; argIndex++) {
            delimIndex = template.indexOf(EMPTY_JSON, handledPosition);
            // 剩余部分无占位符
            if (delimIndex == -1) {
                // 不带占位符的模板直接返回
                if (handledPosition == 0) {
                    return template;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                sbuf.append(template, handledPosition, strPatternLength);
                return sbuf.toString();
            }

            // 转义符
            if (delimIndex > 0 && template.charAt(delimIndex - 1) == BACKSLASH) {
                // 双转义符
                if (delimIndex > 1 && template.charAt(delimIndex - 2) == BACKSLASH) {
                    // 转义符之前还有一个转义符，占位符依旧有效
                    sbuf.append(template, handledPosition, delimIndex - 1);
                    sbuf.append(toJsonStr(params[argIndex]));
                    handledPosition = delimIndex + 2;
                } else {
                    // 占位符被转义
                    argIndex--;
                    sbuf.append(template, handledPosition, delimIndex - 1);
                    sbuf.append(DELIM_START);
                    handledPosition = delimIndex + 1;
                }
            } else {// 正常占位符
                sbuf.append(template, handledPosition, delimIndex);
                sbuf.append(toJsonStr(params[argIndex]));
                handledPosition = delimIndex + 2;
            }
        }

        // append the characters following the last {} pair.
        // 加入最后一个占位符后所有的字符
        sbuf.append(template, handledPosition, template.length());

        return sbuf.toString();
    }

    /**
     * 将对象转为json字符串<br>
     *
     * @param obj 对象
     * @return 字符串
     */
    private static String toJsonStr(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }
}

