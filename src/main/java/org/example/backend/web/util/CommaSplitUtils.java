package org.example.backend.web.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
public class CommaSplitUtils {

    public static final String COMMA = ",";

    /**
     * 把数组变成以逗号分隔的字段串
     *
     * @param list
     * @return 如：1,2,3
     */
    public static String toString(Collection list) {
        if (list.size() == 0) {
            return "";
        }
        return StringUtils.join(list, COMMA);
    }

    /**
     * 把数组变成以逗号分隔的字段串,左右都有分隔符
     *
     * @param list
     * @return 如： ,1,2,3,
     */
    public static String toStringWithBothSides(Collection list) {
        if (list.size() == 0) {
            return "";
        }
        return COMMA + StringUtils.join(list, COMMA) + COMMA;
    }

    /**
     * 把数组变成以逗号分隔的字段串,
     *
     * @param array
     * @return 如： 1,2,3
     */
    public static String toString(String... array) {
        return StringUtils.join(array, COMMA);
    }

    /**
     * 把数组变成以逗号分隔的字段串,
     *
     * @param array
     * @return 如： 1,2,3
     */
    public static String toStringByNotBlank(String... array) {
        List<String> list = new ArrayList<>(array.length);
        Collections.addAll(list, array);
        list = list.stream().filter(T::isNotBlank).collect(Collectors.toList());
        return toString(list);
    }

    /**
     * 把数组变成以逗号分隔的字段串,
     *
     * @param array
     * @return 如： 1,2,3
     */
    public static String toStringWithComma(String comma, String... array) {
        return StringUtils.join(array, comma);
    }

    /**
     * 把以逗号分隔的字符串变成list
     *
     * @param str
     * @return
     */
    public static List<String> toList(String str) {
        return toList(str, COMMA);
    }

    /**
     * 把以逗号分隔的字符串变成list
     *
     * @param str
     * @return
     */
    public static List<Integer> toIntegerList(String str) {
        return toList(str, COMMA).stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    /**
     * 把以逗号分隔的字符串变成list
     *
     * @param str
     * @return
     */
    public static Set<Integer> toIntegerSet(String str) {
        return toList(str, COMMA).stream().map(Integer::parseInt).collect(Collectors.toSet());
    }

    public static List<String> toList(String str, String comma) {
        if (str == null) return new ArrayList<>();
        String[] array = str.split(comma);
        List<String> result = new ArrayList<>(array.length);
        for (String item : array) {
            if (StringUtils.isNotBlank(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static Set<String> toSet(String... strings) {
        Set<String> set = new HashSet<>();
        for (String str : strings) {
            set.addAll(toList(str));
        }
        return set;
    }

    /**
     * 添加新元素
     *
     * @param str
     * @param newItem
     * @return
     */
    public static String addAndFormatWithBothSides(String str, String newItem) {
        List<String> list = CommaSplitUtils.toList(str);
        if (!list.contains(newItem)) {
            list.add(newItem);
        }
        return CommaSplitUtils.toStringWithBothSides(list);
    }

    /**
     * 转成前后都有分隔符
     *
     * @param str
     * @return
     */
    public static String formatWithBothSides(String str) {
        return toStringWithBothSides(toList(str));
    }

    /**
     * 前后都加分隔符
     *
     * @param str
     * @return
     */
    public static String addBothSides(String str) {
        return COMMA + str + COMMA;
    }

    /**
     * 判断以逗号分隔的字符串里有没有子项
     *
     * @param str  以逗号分隔的字符串
     * @param part 需要查找的字符串
     * @return
     */
    public static boolean contains(String str, String part) {
        List<String> list = toList(str);
        return list.contains(part);
    }


    /**
     * 把换行分隔的字符串改成逗号分隔
     *
     * @param str
     * @return
     */
    public static String formatWithNewline(String str) {
        if (str != null) {
            List<String> ipList = toList(str.replaceAll("\n", ",")).stream().filter(T::isNotBlank).map(String::trim).collect(Collectors.toList());
            return toString(ipList);
        }
        return null;
    }


    public static void main(String[] args) {

        String str = toStringByNotBlank("a", null, "", "b");
        System.out.println(str);

    }


}
