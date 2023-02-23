package util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
    /**
     * 按splitRegex分割字符串，返回List
     * @param str
     * @param splitRegex
     * @return
     */
    public static List<String> split(String str, String splitRegex) {
        return Arrays.asList(str.split(splitRegex));
    }

    /**
     * 按ch分割字符串，返回List
     * @param str
     * @param ch
     * @return
     */
    public static List<String> split(String str, Character ch) {
        return split(str, String.format("[%c]", ch));
    }
    /**
     * 移除字符串str的后缀suffix，返回移除后的字符串
     * @param str
     * @param suffix
     * @return
     */
    public static String stripEnd(String str, String suffix) {
        if (str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }

        return str;
    }

    /**
     * 获取字符串分割后的最后一个
     * @param str
     * @param ch
     * @return
     */
    public static String lastAfterSplit(String str, Character ch) {
        return lastAfterSplit(str, String.format("[%c]", ch));
    }

    /**
     * 获取字符串分割后的最后一个
     * @param str
     * @param splitRegex
     * @return
     */
    public static String lastAfterSplit(String str, String splitRegex) {
        List<String> splited = split(str, splitRegex);
        if (!splited.isEmpty()) {
            return splited.get(splited.size() - 1);
        }

        return str;
    }

}
