package com.itlaborer.apitools.utils;
/**
 * Json格式化输出工具，对于遵守 RFC4627约定的Json串具有很好的兼容性
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.3
 * @since 1.0
 */
public class JsonFormatUtils {
    public static String Format(String jsonStr) {
        int tablevel = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        char pre1 = 0;
        char next = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            if (i > 0) {
                pre1 = jsonStr.charAt(i - 1);
            }
            if (i < jsonStr.length() - 1) {
                next = jsonStr.charAt(i + 1);
            }
            char c = jsonStr.charAt(i);
            if (tablevel > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(GetLevelStr(tablevel));
            }
            switch (c) {
            case '{':
            case '[':
                jsonForMatStr.append(c + "\n");
                tablevel++;
                break;
            case ',':
                // 在这里需要严格判断是否需要换行
                if ((pre1 == ']') || (pre1 == '}') || (pre1 == '"' && next == '"')
                        || (Character.isDigit(pre1) && next == '"')
                        || ((jsonForMatStr.length() > 3)
                                && (jsonForMatStr.substring(jsonForMatStr.length() - 4).equals("null")) && next == '"')
                        || ((jsonForMatStr.length() > 3)
                                && (jsonForMatStr.substring(jsonForMatStr.length() - 4).equals("true")) && next == '"')
                        || ((jsonForMatStr.length() > 3)
                                && (jsonForMatStr.substring(jsonForMatStr.length() - 5).equals("false"))
                                && next == '"')) {
                    jsonForMatStr.append(c + "\n");
                } else {
                    jsonForMatStr.append(c);
                }
                break;
            case '}':
            case ']':
                jsonForMatStr.append("\n");
                tablevel--;
                jsonForMatStr.append(GetLevelStr(tablevel));
                jsonForMatStr.append(c);
                break;
            default:
                jsonForMatStr.append(c);
                break;
            }
        }
        return jsonForMatStr.toString();
    }
    // 缩进填充tab
    private static String GetLevelStr(int tablevel) {
        StringBuffer tablevelStr = new StringBuffer();
        for (int level = 0; level < tablevel; level++) {
            tablevelStr.append("\t");
        }
        return tablevelStr.toString();
    }
}