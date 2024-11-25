package org.example.backend.web.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class T {

    // HOUR SECOND
    public static final long HOUR = 3600L * 1000L;

    // DAY SECOND
    public static final long DAY = 24L * HOUR;

    private final static Pattern IPV6_PATTERN = Pattern.compile("(^((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4}){1}|:))"
            + "|(([0-9A-Fa-f]{1,4}:){6}((:[0-9A-Fa-f]{1,4}){1}|"
            + "((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
            + "(([0-9A-Fa-f]{1,4}:){5}((:[0-9A-Fa-f]{1,4}){1,2}|"
            + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
            + "(([0-9A-Fa-f]{1,4}:){4}((:[0-9A-Fa-f]{1,4}){1,3}"
            + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){3}((:[0-9A-Fa-f]{1,4}){1,4}|"
            + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
            + "(([0-9A-Fa-f]{1,4}:){2}((:[0-9A-Fa-f]{1,4}){1,5}|"
            + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))"
            + "|(([0-9A-Fa-f]{1,4}:){1}((:[0-9A-Fa-f]{1,4}){1,6}"
            + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
            + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
            + "(:((:[0-9A-Fa-f]{1,4}){1,7}|(:[fF]{4}){0,1}:((22[0-3]|2[0-1][0-9]|"
            + "[0-1][0-9][0-9]|([0-9]){1,2})"
            + "([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:)))$)");

    protected T() {
    }

    /**
     * 判断给定的值，在不在后续给的数据里
     *
     * @param value
     * @param list
     * @return
     */
    public static boolean valueInList(Object value, Object... list) {
        for (Object item : list) {
            if (Objects.equals(value, item)) {
                return true;
            }
        }
        return false;
    }

    public static String getExceptionDetail(Throwable e) {

        return getExceptionDetail(e, Integer.MAX_VALUE);
    }

    public static String getExceptionDetail(Throwable e, int length) {
        try {
            StringBuffer msg = new StringBuffer("null");
            if (e != null) {
                msg = new StringBuffer("");
                String message = e.toString();
                length = Math.min(e.getStackTrace().length, length);
                if (length > 0) {
                    msg.append(message + "\n");
                    for (int i = 0; i < length; i++) {
                        msg.append("\t" + e.getStackTrace()[i] + "\n");
                    }
                } else {
                    msg.append(message);
                }
            }
            return msg.toString();
        } catch (Exception e1) {
            return "获取异常详细信息出错";
        }
    }

    /**
     * 下划线转驼峰
     *
     * @param underlineName
     * @return
     */
    public static String underlineToHump(String underlineName) {
        if (T.isBlank(underlineName)) {
            return underlineName;
        }
        //截取下划线分成数组
        char[] charArray = underlineName.toCharArray();
        //判断上次循环的字符是否是"_"
        boolean underlineBefore = false;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, l = charArray.length; i < l; i++) {
            //判断当前字符是否是"_",如果跳出本次循环
            char ch = charArray[i];
            if (ch == '_') {
                underlineBefore = true;
            } else if (underlineBefore) {
                if (ch >= 'a' && ch <= 'z')
                    ch -= 32;
                buffer.append(ch);
                underlineBefore = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param humpName
     * @return
     */
    public static String humpToUnderline(String humpName) {
        if (T.isBlank(humpName)) {
            return humpName;
        }
        //截取下划线分成数组，
        char[] charArray = humpName.toCharArray();
        StringBuffer buffer = new StringBuffer();
        //处理字符串
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <E> boolean isEmpty(E[] arr) {
        return (arr == null || arr.length == 0);
    }

    public static <E> boolean isNotEmpty(E[] arr) {
        return arr != null && arr.length > 0;
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        int offset = 0;
        if (str.startsWith("-") || str.startsWith("+")) {
            if (str.length() > 1) {
                offset = 1;
            } else {
                return false;
            }
        }
        for (int i = offset; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getMinute(Date date) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        return cld.get(Calendar.MINUTE);
    }

    public static Date date(String v, String fm, Date def) {
        if (v == null || v.length() == 0)
            return def;
        try {
            return new SimpleDateFormat(fm).parse(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static Date dateValue(String v, Date def) {
        return date(v, "yyyy-MM-dd", def);
    }

    public static Date dateTimeValue(String v, Date def) {
        return date(v, "yyyy-MM-dd HH:mm:ss", def);
    }

    public static Date getNow() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getTodayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getTheDayEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date subDate(int dayNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(T.getTodayStart().getTime() - DAY * dayNum);
        return cal.getTime();
    }

    public static Date addHour(Date date, int hourNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime() + HOUR * (long) hourNum);
        return cal.getTime();
    }

    public static Date addDate(Date date, int dayNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime() + DAY * (long) dayNum);
        return cal.getTime();
    }

    public static Date addMonth(Date date, int monthNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + monthNum);
        return cal.getTime();
    }

    public static Date addYear(Date date, int yearNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + yearNum);
        return cal.getTime();
    }

    public static Date addMinute(Date date, int minNum) {
        Calendar cal = Calendar.getInstance();
        long min = 1000 * 60;
        cal.setTimeInMillis(date.getTime() + min * (long) minNum);
        return cal.getTime();
    }

    public static Date addSecond(Date date, long secondNum) {
        Calendar cal = Calendar.getInstance();
        long min = 1000;
        cal.setTimeInMillis(date.getTime() + min * secondNum);
        return cal.getTime();
    }

    public static Date addMilSecond(Date date, long milSecondNum) {
        Calendar cal = Calendar.getInstance();
        long min = 1;
        cal.setTimeInMillis(date.getTime() + min * milSecondNum);
        return cal.getTime();
    }

    public static Date getYesterdayStart() {
        Date today = T.getNow();
        long t = today.getTime();
        Date date = new Date(t - 24 * 60 * 60 * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {

        }
        return date;
    }

    public static Date getTheDayStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int getYear(Date date) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        return cld.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        return cld.get(Calendar.MONTH);
    }

    public static int getDate(Date date) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        return cld.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 断日期是否为同一天
     *
     * @param dateA
     * @param dateB
     * @return
     */
    public static boolean areSameDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour(Date date) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        return cld.get(Calendar.HOUR_OF_DAY);
    }

    public static String format(Date date, String fmt) {
        if (date == null)
            return "";
        DateFormat formatter = new SimpleDateFormat(fmt);
        return formatter.format(date);
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static boolean isIp(String ip) {//判断是否是一个IP
        boolean b = false;
        if (isIpV4(ip)) {
            b = true;
        } else if (isIpV6(ip)) {
            b = true;
        }
        return b;
    }

    public static boolean isIpV4(String ip) {//判断是否是一个IPV4

        return ip.matches("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
    }

    public static boolean isIpV6(String ip) {//判断是否是一个IPV6

        return ip.matches("((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)") || ip.matches("(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}");
    }

    public static String formatDate() {
        return format(new Date(System.currentTimeMillis()), "yyyy-MM-dd");
    }

    public static Date getThisWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(cal.get(Calendar.DAY_OF_WEEK) - 1));
        return getTheDayStart(cal.getTime());
    }

    public static Date getLastWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(cal.get(Calendar.DAY_OF_WEEK) - 1));
        cal.add(Calendar.DATE, -7);
        return getTheDayStart(cal.getTime());
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getThisMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getTheDayStart(cal.getTime());
    }

    public static Date getThisMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getMonthStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getTheDayStart(cal.getTime());

    }

    public static Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, -1);
        return getTheDayEnd(cal.getTime());
    }

    public static Date getTheMonthStart(int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisMonthStart());
        cal.add(Calendar.MONTH, amount);
        return cal.getTime();
    }

    public static int getDays(Date date1, Date date2) {
        int days = 0;
        days = (int) (Math.abs((date2.getTime() - date1.getTime())) / (24 * 60 * 60 * 1000));
        return days;
    }

    public static int getHourDif(Date date1, Date date2) {
        long DAY = 24 * 60 * 60 * 1000;
        long between = Math.abs((date2.getTime() - date1.getTime()));
        long day = between / DAY;
        long hour = between / (60 * 60 * 1000);
        return (int) hour;
    }

    public static List<String> splitString(String str, String split) {
        if (T.isBlank(str) || T.isBlank(split)) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(split)).stream()
                .filter(Objects::nonNull).filter(T::isNotBlank).collect(Collectors.toList());
    }

    public static List<String> string2List(String str, String split) {
        List<String> list = null;
        String temp[] = string2Array(str, split);
        if (temp == null)
            return list;
        list = new ArrayList<>(Arrays.asList(temp));
        return list;
    }

    public static List<String> string2ListNoBlank(String str, String split) {

        List<String> list = null;
        String temp[] = string2Array(str, split);
        if (temp == null)
            return list;
        list = Arrays.asList(temp).stream().filter(T::isNotBlank).collect(Collectors.toList());
        return list;
    }

    public static Set<String> string2SetNoBlank(String str, String split) {

        Set<String> set = null;
        String temp[] = string2Array(str, split);
        if (temp == null)
            return set;
        set = Arrays.asList(temp).stream().filter(T::isNotBlank).collect(Collectors.toSet());
        return set;
    }

    public static Set<String> string2Set(String str, String split) {
        List<String> list = string2List(str, split);
        Set<String> set = null;
        if (list != null && list.size() > 0) {
            set = new HashSet<>(list);
        }
        return set;
    }

    static public String[] string2Array(String str, String split) {
        if (str == null || str.equals("")) {
            return null;
        }
        return str.split(split);
    }

    public static String array2String(String str[], String split) {

        return objectArray2String(str, split);
    }

    public static <E> String objectArray2String(E[] arr, String split) {

        if (arr == null || arr.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                result.append(split);
            }
            result.append(arr[i]);
        }

        return result.toString();
    }

    public static String array2String(int str[], String split) {
        if (str == null || str.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            if (i > 0) {
                result.append(split);
            }
            result.append(str[i]);
        }

        return result.toString();
    }

    public static String queue2String(Queue<String> list, String split) {
        return collection2String(list, split);
    }

    public static String set2String(Set set, String split) {

        return collection2String(set, split);
    }

    public static String list2String(List list, String split) {

        return collection2String(list, split);
    }

    public static String collection2String(Collection collection, String split) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Iterator it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                result.append(split);
            }
            result.append(it.next());
            i++;
        }

        return result.toString();
    }

    public static <F, E> String collection2String(Collection<F> collection, Function<F, E> function, String split) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Iterator<F> it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                result.append(split);
            }
            E e = function.apply(it.next());
            result.append(e);
            i++;
        }
        return result.toString();
    }

    public static <F, E> String list2String(List<F> list, BiFunction<F, Integer, E> function, String split) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i > 0) {
                result.append(split);
            }
            E e = function.apply(list.get(i), i);
            result.append(e);
        }

        return result.toString();
    }

    public static <F, E> String list2String(List<F> list, Function<F, E> function, String split) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i > 0) {
                result.append(split);
            }
            E e = function.apply(list.get(i));
            result.append(e);
        }

        return result.toString();
    }

    public static <F> String list2StringPredicate(List<F> list, Predicate<F> predicate, String split) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i > 0) {
                result.append(split);
            }

            F f = list.get(i);
            if (predicate.test(f)) {
                result.append(f);
            }
        }

        return result.toString();
    }

    public static <F, E> String set2String(Set<F> set, Function<F, E> function, String split) {
        return collection2String(set, function, split);
    }

    /**
     * long转ip
     * 低位存储左边数值
     *
     * @param longIP
     * @return
     */
    public static String long2Ip(long longIP) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(longIP & 0x000000FF));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //直接右移24位
        sb.append(String.valueOf(longIP >>> 24));
        return sb.toString();
    }

    public static String encodeURL(String s) {
        if (s == null)
            return null;
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return s;
    }

    public static String decodeURL(String s) {
        if (s == null)
            return null;

        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        return s;
    }

    public static String getLocalHostIp() {
        InetAddress addr = null;
        String address = "";
        try {
            addr = InetAddress.getLocalHost();
            address = addr.getHostAddress().toString();

            if (address.indexOf("127.0.0.1") != -1) {
                Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
                InetAddress ip = null;
                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                    System.out.println(ni.getName());
                    ip = ni.getInetAddresses().nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        address = ip.getHostAddress();
                        break;
                    } else {
                        ip = null;
                    }
                }
            }
        } catch (UnknownHostException uhe) {
        } catch (SocketException se) {
        }
        return address;
    }

    public static Date getAfterNow(int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000);
    }

    public static Date getWeekStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return getTheDayStart(cal.getTime());
    }

    public static Date getWeekEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        return getTheDayStart(cal.getTime());
    }

    public static Object jsonToObject(String json, Class obj) {
        return JSON.parseObject(json, obj);
    }

    public static <V> V jsonToObject(String json, Type obj) {
        return JSON.parseObject(json, obj);
    }

    public static <V> V jsonToObjectV(String json, Class<V> obj) {
        return JSON.parseObject(json, obj);
    }

    public static String getMd5(String plainText) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();// 32位的加密
            // System.out.println("result: " + buf.toString().substring(8,
            // 24));// 16位的加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String md5StrByFile(File file) throws IOException, NoSuchAlgorithmException {

        try (FileInputStream fis = new FileInputStream(file)) {
            return md5StrByIO(fis);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String md5StrByIO(InputStream io) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = new byte[8192];
        int len = 0;
        MessageDigest md = MessageDigest.getInstance("MD5");
        while ((len = io.read(buffer)) != -1) {
            md.update(buffer, 0, len);
        }
        byte[] digest = md.digest();
        String md5 = DatatypeConverter.printHexBinary(digest).toLowerCase();
        return md5;
    }

    public static String md5StrByByteArray(byte[] data) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] digest = md.digest();
        String md5 = DatatypeConverter.printHexBinary(digest).toLowerCase();
        return md5;
    }

    public static boolean isURL(String str) {
        // 转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)" + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }

    public static String subString(String content, int length) {
        if (T.isBlank(content)) {
            return "";
        }
        if (content.length() >= length) {
            return content.substring(0, length - 1) + "...";
        }
        return content;
    }

    public static List<String> array2List(String[] strs) {

        if (strs != null && strs.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String str : strs) {
                list.add(str);
            }
            return list;
        }

        return null;
    }

    /**
     * 把list分割成 每组大小为pageSize 的份
     *
     * @param list
     * @param pageSize
     * @return
     */
    public static <T> List<List<T>> split2List(List<T> list, int pageSize) {

        int listSize = list.size();
        int page = (listSize + (pageSize - 1)) / pageSize;

        List<List<T>> listArray = new ArrayList<List<T>>();
        int fromIndex = 0;
        int toIndex = 0;
        for (int i = 1; i < page; i++) {
            toIndex = i * pageSize;
            List<T> subList = subList(list, fromIndex, toIndex);
            fromIndex = toIndex;
            listArray.add(subList);
        }
        List<T> last = subList(list, fromIndex, listSize);
        if (last != null && last.size() > 0) {
            listArray.add(last);
        }

        return listArray;
    }

    private static <T> List<T> subList(List<T> list, int fromIndex, int toIndex) {
        List<T> subList = new ArrayList<T>();
        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(list.get(i));
        }
        return subList;
    }

    /**
     * 判断是否为手机号码 通用判断
     *
     * @param mobile 手机号码
     * @return
     */
    public static boolean isMobilePhoneNum(String mobile) {
        if (T.isBlank(mobile)) {
            return false;
        }
        String regex = "^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 返回异常堆信息
     *
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(buf, true));
        String expMessage = buf.toString();
        try {
            buf.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return expMessage;
    }

    public static boolean checkIsIps(String ips) {
        if (T.isBlank(ips)) {
            return false;
        }

        String[] _ips = ips.split(",");
        if (_ips != null && _ips.length > 0) {
            for (String ip : _ips) {
                if (!T.isIp(ip)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * sha1加密
     *
     * @param decript
     * @return
     */
    public static String getSHA1Encode(String decript) {
        try {
            return getSHA1Encode(decript.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * sha1加密
     *
     * @param bytes
     * @return
     */
    public static String getSHA1Encode(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    //的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static String trim(String str) {
        if (str == null)
            return null;
        return str.trim();
    }

    public static String trim(String str, String def) {
        if (str == null)
            return def;

        return str.trim();
    }

    public static double doubleTrim(double d) {
        return ((double) Math.round(d * 100)) / 100;
    }

    public static double doubleTrim(double d, int precision) {
        double pow = Math.pow(10, precision);
        return ((double) Math.round(d * pow)) / pow;
    }

    public static Double doubleTrim(Double d) {
        if (Objects.isNull(d))
            return null;
        return doubleTrim(d.doubleValue());
    }

    public static String doubleFormat2(Double d) {
        if (d == null)
            return null;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    public static String doubleFormat3(Double d) {
        if (d == null)
            return null;
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(d);
    }

    public static String doubleFormat1(Double d) {
        if (d == null)
            return null;
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(d);
    }

    public static void requireGT0(Integer val, String key) {
        Objects.requireNonNull(val, key + "为空");
        requireTrue(val > 0, key + "小于等于0");
    }

    public static void requireGT0(Long val, String key) {
        Objects.requireNonNull(val, key + "为空");
        requireTrue(val > 0, key + "小于等于0");
    }

    public static void requireTrue(boolean val, String message) {
        if (!val)
            throw new RuntimeException(message);
    }

    public static void requireTrue(boolean val, Supplier<String> messageSupplier) {
        if (!val)
            throw new RuntimeException(messageSupplier.get());
    }

    public static void requireFalse(boolean val, String message) {
        if (val)
            throw new RuntimeException(message);
    }

    public static String getDayRange(Date date1, Date date2) {
        long DAY = 24 * 60 * 60 * 1000;
        long between = Math.abs((date2.getTime() - date1.getTime()));
        long day = between / DAY;
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//		if(day>0){
//			return (day+1)+"d";
//		}
//		if(hour>0){
//			return (hour+1)+"h";
//		}
//		if(min>0){
//
//			return (min+1)+"m";
//		}
//
        return "5m";
    }

    /**
     * 编码
     *
     * @param bstr
     * @return String
     */
    public static String encodeBase64Opentsdb(byte[] bstr) {
        String s = new BASE64Encoder().encode(bstr);
        try {
            if (s.contains("+")) {
                s = s.replaceAll("\\+", ".");
            }
            if (s.contains("=")) {
                s = s.replaceAll("=", "_");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 解码
     *
     * @param str
     * @return string
     */
    public static byte[] decodeBase64Opentsdb(String str) {
        byte[] bt = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            str = str.replaceAll("\\.", "+");
            str = str.replaceAll("_", "=");
            bt = decoder.decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bt;
    }

    //3普通，2：重要，1：重要且紧急
    public static String getAlarmLevelCn(int alarmLevel) {
        if (alarmLevel == 3) {
            return "普通";
        } else if (alarmLevel == 2) {
            return "重要";
        } else if (alarmLevel == 1) {
            return "重要且紧急";
        }
        return "未知(" + alarmLevel + ")";
    }

    public static double defaultValue(Double v, double def) {
        if (v != null)
            return v;
        return def;
    }

    public static int defaultValue(Integer v, int def) {
        if (v != null)
            return v;
        return def;
    }

    public static int defaultValue(Double v, int def) {
        if (v != null)
            return v.intValue();
        return def;
    }

    public static String defaultNotBlank(String... vals) {

        if (vals == null) {
            return null;
        }

        for (String e : vals)
            if (T.isNotBlank(e))
                return e;
        return null;
    }

    public static String defaultNotBlank(String v, Supplier<String>... suppliers) {

        if (T.isNotBlank(v)) {
            return v;
        }
        for (Supplier<String> supply : suppliers) {
            String a = supply.get();
            if (T.isNotBlank(a)) {
                return a;
            }
        }
        return null;
    }

    public static <E> E defaultValue(E... arr) {
        for (E e : arr) {
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    public static <E> E defaultValue(E v, Supplier<E>... suppliers) {
        if (v != null) {
            return v;
        }
        for (Supplier<E> supply : suppliers) {
            E a = supply.get();
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    public static <E> E defaultValue(E v, Predicate<E> predicate, Supplier<E>... suppliers) {
        if (v != null) {
            return v;
        }
        for (Supplier<E> supply : suppliers) {
            E a = supply.get();
            if (predicate.test(a)) {
                return a;
            }
        }
        return null;
    }

    public static Integer searchLevel(Long n, Integer[] level) {
        if (level[0] >= n) {
            return level[0];
        } else if (level[level.length - 1] <= n) {
            return level[level.length - 1];
        } else {
            int low = 1;
            int high = level.length - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                Integer midVal = level[mid];

                if (midVal < n) {
                    low = mid + 1;
                } else if (midVal > n) {
                    high = mid - 1;
                } else {
                    return midVal;
                }
            }
            return level[low];
        }
    }

    public static Integer searchLevel(Integer n, Integer[] level) {
        if (level[0] >= n) {
            return level[0];
        } else if (level[level.length - 1] <= n) {
            return level[level.length - 1];
        } else {
            int low = 1;
            int high = level.length - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                Integer midVal = level[mid];

                if (midVal < n) {
                    low = mid + 1;
                } else if (midVal > n) {
                    high = mid - 1;
                } else {
                    return midVal;
                }
            }
            return level[low];
        }
    }

    public static Integer searchLevel(Double n, Integer[] level) {
        if (level[0] >= n) {
            return level[0];
        } else if (level[level.length - 1] <= n) {
            return level[level.length - 1];
        } else {
            int low = 1;
            int high = level.length - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                Integer midVal = level[mid];

                if (midVal < n) {
                    low = mid + 1;
                } else if (midVal > n) {
                    high = mid - 1;
                } else {
                    return midVal;
                }
            }
            return level[low];
        }
    }

    public static String encodeBase64(byte[] bstr) {
        return new BASE64Encoder().encode(bstr);
    }

    public static byte[] decodeBase64(String bstr) {
        try {
            return new BASE64Decoder().decodeBuffer(bstr);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("decodeBase64 error bstr={}", bstr, e);
        }
        return null;
    }

    public static boolean GT(Float v, int val) {
        return v != null && v > val;
    }

    public static boolean GT(Double v, int val) {
        return v != null && v > val;
    }

    public static boolean GT(Integer v, int val) {
        return v != null && v > val;
    }

    public static boolean GT(Long v, long val) {
        return v != null && v > val;
    }

    public static boolean noGT(Long v, long val) {
        return !T.GT(v, val);
    }

    public static boolean noGT(Integer v, int val) {

        return !T.GT(v, val);
    }

    public static boolean noGT(Double v, int val) {
        return !T.GT(v, val);
    }

    public static boolean noGT(Float v, int val) {
        return !T.GT(v, val);
    }

    public static boolean GTEQ(Float v, int val) {
        return v != null && v >= val;
    }

    public static boolean GTEQ(Double v, int val) {
        return v != null && v >= val;
    }

    public static boolean GTEQ(Integer v, int val) {
        return v != null && v >= val;
    }

    public static boolean GTEQ(Long v, long val) {
        return v != null && v >= val;
    }

    public static boolean GTEQ(BigDecimal v, long val) {
        return v != null && v.compareTo(new BigDecimal(val)) >= 0;
    }

    public static boolean GTEQ(BigDecimal v, BigDecimal val) {
        return v != null && v.compareTo(val) >= 0;
    }

    public static boolean noGTEQ(BigDecimal v, long val) {
        return !T.GTEQ(v, val);
    }

    public static boolean noGTEQ(BigDecimal v, BigDecimal val) {
        return !T.GTEQ(v, val);
    }

    public static boolean noGTEQ(Long v, long val) {
        return !T.GTEQ(v, val);
    }

    public static boolean noGTEQ(Integer v, int val) {
        return !T.GTEQ(v, val);
    }

    public static boolean noGTEQ(Float v, int val) {
        return !T.GTEQ(v, val);
    }

    public static boolean noGTEQ(Double v, int val) {
        return !T.GTEQ(v, val);
    }

    public static boolean LT(Float v, int val) {
        return v != null && v < val;
    }

    public static boolean LT(Double v, int val) {
        return v != null && v < val;
    }

    public static boolean LT(Integer v, int val) {
        return v != null && v < val;
    }

    public static boolean LT(Long v, long val) {
        return v != null && v < val;
    }

    public static boolean noLT(Long v, long val) {
        return !T.LT(v, val);
    }

    public static boolean noLT(Integer v, int val) {
        return !T.LT(v, val);
    }

    public static boolean noLT(Double v, int val) {
        return !T.LT(v, val);
    }

    public static boolean noLT(Float v, int val) {
        return !T.LT(v, val);
    }

    public static boolean LTEQ(Float v, int val) {
        return v != null && v <= val;
    }

    public static boolean LTEQ(Double v, int val) {
        return v != null && v <= val;
    }

    public static boolean LTEQ(Integer v, int val) {
        return v != null && v <= val;
    }

    public static boolean LTEQ(Long v, long val) {
        return v != null && v <= val;
    }

    public static boolean noLTEQ(Long v, long val) {
        return !T.LTEQ(v, val);
    }

    public static boolean noLTEQ(Integer v, int val) {
        return !T.LTEQ(v, val);
    }

    public static boolean noLTEQ(Double v, int val) {
        return !T.LTEQ(v, val);
    }

    public static boolean noLTEQ(Float v, int val) {
        return !T.LTEQ(v, val);
    }

    public static boolean EQ(Float v, int val) {
        return v != null && v == val;
    }

    public static boolean EQ(Double v, int val) {
        return v != null && v == val;
    }

    public static boolean EQ(Integer v, int val) {
        return v != null && v == val;
    }

    public static boolean EQ(Long v, long val) {
        return v != null && v == val;
    }

    public static boolean noEQ(Long v, long val) {
        return !T.EQ(v, val);
    }

    public static boolean noEQ(Integer v, int val) {
        return !T.EQ(v, val);
    }

    public static boolean noEQ(Double v, int val) {
        return !T.EQ(v, val);
    }

    public static boolean noEQ(Float v, int val) {
        return !T.EQ(v, val);
    }

    public static boolean isEquals(Object o, Object n) {
        return o != null ? o.equals(n) : n == null;
    }

    /**
     * Collection
     */

    public static boolean isEmpty(Collection list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(Collection list) {
        return list != null && !list.isEmpty();
    }

    /**
     * map
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return map != null && !map.isEmpty();
    }

    /**
     * toString
     */
    public static String toString(Object o) {
        return o != null ? o.toString() : null;
    }

    public static String toString(Object o, String def) {
        return o != null ? o.toString() : def;
    }

    /**
     * 对象转Byte数组
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("objectToByteArray failed, " + e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    log.error("objectToByteArray failed, " + e);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    log.error("objectToByteArray failed, " + e);
                }
            }
        }
        return bytes;
    }

    /**
     * Byte数组转对象
     */
    public static Object byteArrayToObj(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            log.error("byteArrayToObject failed, " + e);
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    log.error("byteArrayToObject failed, " + e);
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    log.error("byteArrayToObject failed, " + e);
                }
            }
        }
        return obj;
    }

    public static List<Long> int2Long(List<Integer> list) {
        return list.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public static List<Integer> long2Int(List<Long> list) {
        return list.stream().map(Long::intValue).collect(Collectors.toList());
    }

    public static Long int2Long(Integer integer) {
        return longValue(integer, null);
    }

    public static Integer long2Int(Long aLong) {
        return integerValue(aLong, null);
    }

    public static Integer integerValue(Object o, Integer def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        return ((Number) o).intValue();
    }

    public static int intValue(Object o, int def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        return ((Number) o).intValue();
    }

    public static Double doubleValue(Object o) {
        return doubleValue(o, null);
    }

    public static Double doubleValue(Object o, Double def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }
        if (o instanceof Double) {
            return (Double) o;
        }
        return ((Number) o).doubleValue();
    }

    public static double doubleValue(Object object, double def) {
        if (object == null) {
            return def;
        }
        if (!(object instanceof Number)) {
            return T.parse(object.toString(), def);
        }

        if (object instanceof Double) {
            return (Double) object;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return def;
    }

    public static Long longValue(Object o) {
        return longValue(o, null);
    }

    public static Long longValue(Object o, Long def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }
        if (o instanceof Long) {
            return (Long) o;
        }
        return ((Number) o).longValue();
    }

    public static long longValue(Object o, long def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }
        if (o instanceof Long) {
            return (Long) o;
        }
        return ((Number) o).longValue();
    }

    public static String stringValue(Object o, String def) {

        if (o == null) {
            return def;
        }

        if (o instanceof String) {
            String str = (String) o;
            if (str.length() == 0) {
                return def;
            }
            return str.trim();
        }
        return o.toString();
    }

    public static BigDecimal decimalValue(Object o) {
        return decimalValue(o, null);
    }

    public static BigDecimal decimalValue(Object o, BigDecimal def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }

        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        }
        return new BigDecimal(o.toString());
    }

    public static BigDecimal parse(String v, BigDecimal def) {
        try {
            return new BigDecimal(v.toString());
        } catch (Exception ex) {
        }
        return def;
    }

    public static Double parse(String v, Double def) {
        try {
            return Double.valueOf(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static Integer parse(String v, Integer def) {
        try {
            return Integer.valueOf(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static Long parse(String v, Long def) {
        try {
            return Long.valueOf(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static double parse(String v, double def) {
        try {
            return Double.parseDouble(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static int parse(String v, int def) {
        try {
            return Integer.parseInt(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static byte parse(String v, byte def) {
        try {
            return Byte.parseByte(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static float parse(String v, float def) {
        try {
            return Float.parseFloat(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static char parse(String v, char def) {
        try {
            if (v != null && v.length() == 1) {
                return v.charAt(0);
            }
        } catch (Exception ex) {
        }
        return def;
    }

    public static long parse(String v, long def) {
        try {
            return Long.parseLong(v);
        } catch (Exception ex) {
        }
        return def;
    }

    public static Boolean booleanValue(Object o) {
        return booleanValue(o, null);
    }

    public static Boolean booleanValue(Object o, Boolean def) {
        if (o == null) {
            return def;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        if ("true".equals(o.toString())) {
            return true;
        }
        if ("false".equals(o.toString())) {
            return false;
        }
        return def;
    }

    public static boolean booleanValue(Object o, boolean def) {
        if (o == null) {
            return def;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        if ("true".equalsIgnoreCase(o.toString())) {
            return true;
        }
        if ("false".equalsIgnoreCase(o.toString())) {
            return false;
        }
        return def;
    }

    public static String[] stringArrayValue(String[] v, String[] def) {
        if (v == null || v.length == 0) {
            return def;
        }
        return v;
    }

    public static byte byteValue(Object o, byte def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }

        if (o instanceof Byte) {
            return (Byte) o;
        }
        return ((Number) o).byteValue();
    }

    public static char charValue(Object o, char def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Character)) {
            return T.parse(o.toString(), def);
        }
        return (Character) o;
    }

    public static Integer integerValue(Object v) {
        return integerValue(v, null);
    }

    public static Float floatValue(Object o) {
        return floatValue(o, 0.0f);
    }

    public static float floatValue(Object o, float def) {
        if (o == null) {
            return def;
        }
        if (!(o instanceof Number)) {
            return T.parse(o.toString(), def);
        }

        if (o instanceof Float) {
            return (Float) o;
        }
        return ((Number) o).floatValue();
    }

    public static float floatValue(Object v, int remain, float def) {
        if (v == null) {
            return def;
        }
        try {
            BigDecimal bd = new BigDecimal(v.toString());
            bd = bd.setScale(remain, BigDecimal.ROUND_HALF_UP);
            return bd.floatValue();
        } catch (Exception e) {
            return def;
        }
    }

    public static String parseToRegex(String str) {
        char[] regexs = {'*', '.', '?', '+', '$', '^', '[', ']', '(', ')', '{', '}', '|', '\\', '/'};
        Arrays.sort(regexs);
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            int index = Arrays.binarySearch(regexs, c);
            if (index >= 0) {
                builder.append("\\");
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static Long divide(long sum, long div, Long def) {
        if (div == 0) {
            return def;
        }
        return sum / div;
    }

    public static Long add(Long sum, Long data) {
        if (Objects.isNull(sum)) {
            sum = 0L;
        }
        if (Objects.nonNull(data)) {
            sum = sum + data;
        }
        return sum;
    }

    public static Integer add(Integer sum, Integer data) {
        if (Objects.isNull(sum)) {
            sum = 0;
        }
        if (Objects.nonNull(data)) {
            sum = sum + data;
        }
        return sum;
    }

    public static List<Integer> add(List<Integer> sum, List<Integer> data) {
        int size = Math.max(Optional.ofNullable(sum).map(List::size).orElse(0),
                Optional.ofNullable(data).map(List::size).orElse(0));
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int index = i;
            Integer s = Optional.ofNullable(sum).map(v -> v.get(index)).orElse(null);
            Integer d = Optional.ofNullable(data).map(v -> v.get(index)).orElse(null);
            Integer t = add(s, d);
            list.add(t);
        }
        return list;
    }

    public static <F> List<F> obj2List(Object o, Function<Object, F> function) {
        if (o == null) {
            return null;
        }
        if (o instanceof Collection) {
            return toList((Collection) o, function);
        }
        if (o.getClass().isArray()) {
            return array2List(o, function);
        }
        return new ArrayList<>(Arrays.asList(function.apply(o)));
    }

    public static <F> List<F> array2List(Object o, Function<Object, F> function) {
        List<F> list = new ArrayList<>(Array.getLength(o));
        for (int i = 0, len = Array.getLength(o); i < len; i++) {
            list.add(function.apply(Array.get(o, i)));
        }
        return list;
    }

    public static <F> List<F> toList(Collection<F> list) {
        return list.stream().collect(Collectors.toList());
    }

    public static <E, F> List<E> toListNonNull(Collection<F> list, Function<F, E> func) {
        return list.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <E> List<E> toListPredicate(Collection<E> list, Predicate<E> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <E, F> List<E> toList(Collection<F> list, Function<F, E> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }

    public static <E> List<E> toListPredicate(E[] arr, Predicate<E> predicate) {
        return toListPredicate(Arrays.asList(arr), predicate);
    }

    public static <E, F> List<E> toListNonNull(F[] arr, Function<F, E> func) {
        return toListNonNull(Arrays.asList(arr), func);
    }

    public static <E, F> List<E> toList(F[] arr, Function<F, E> function) {
        return toList(Arrays.asList(arr), function);
    }

    public static <E, F> List<E> toListValues(Collection<F> list, Function<F, List<E>> function) {
        return list.stream().map(function).flatMap(List::stream).collect(Collectors.toList());
    }

    public static <E, F> List<E> toListDistinct(Collection<F> list, Function<F, E> function) {
        return list.stream().map(function).distinct().collect(Collectors.toList());
    }

    public static <F> List<F> toListDistinct(Collection<F> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    public static <E, F> List<E> toListDistinct(Collection<F> list, Function<F, E> function, Predicate<E> predicate) {
        return list.stream().map(function).filter(predicate).distinct().collect(Collectors.toList());
    }

    public static <E, F> List<E> toList(Collection<F> list, Function<F, E> function, Predicate<E> predicate) {
        return list.stream().map(function).filter(predicate).collect(Collectors.toList());
    }

    public static <E, F> List<E> toListPredicate(Collection<F> list, Predicate<F> predicate, Function<F, E> function) {
        return list.stream().filter(predicate).map(function).collect(Collectors.toList());
    }

    public static <E, F> List<E> toListIndex(Collection<F> list, BiFunction<F, Integer, E> function) {

        Iterator<F> it = list.iterator();
        int i = 0;
        List<E> eList = new ArrayList<>(list.size());
        while (it.hasNext()) {
            F f = it.next();
            E e = function.apply(f, i);
            eList.add(e);
            i++;
        }
        return eList;
    }

    public static <E, F> Set<E> toSetIndex(Collection<F> list, BiFunction<F, Integer, E> function) {

        Iterator<F> it = list.iterator();
        int i = 0;
        Set<E> eList = new HashSet<>(list.size());
        while (it.hasNext()) {
            F f = it.next();
            E e = function.apply(f, i);
            eList.add(e);
            i++;
        }
        return eList;
    }

    public static <E> Set<E> toSet(Collection<E> list) {
        return list.stream().collect(Collectors.toSet());
    }

    public static <E, F> Set<E> toSet(Collection<F> list, Function<F, E> function) {
        return list.stream().map(function).collect(Collectors.toSet());
    }

    public static <E, F> Set<E> toSet(Collection<F> list, Function<F, E> function, Predicate<E> predicate) {
        return list.stream().map(function).filter(predicate).collect(Collectors.toSet());
    }

    public static <E, F> Set<E> toSet(Collection<F> list, Predicate<F> predicate, Function<F, E> function) {
        return list.stream().filter(predicate).map(function).collect(Collectors.toSet());
    }

    public static <E> Set<E> toSetPredicate(Collection<E> list, Predicate<E> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toSet());
    }

    public static List<Integer> string2Int(String str, String split) {
        List<Integer> list = Collections.EMPTY_LIST;
        String temp[] = string2Array(str, split);
        if (temp == null) {
            return list;
        }
        list = new ArrayList<>(temp.length);
        for (String s : temp) {
            list.add(Integer.valueOf(s));
        }
        return list;
    }

    public static List<Long> string2Long(String str, String split) {
        List<Long> list = Collections.EMPTY_LIST;
        String temp[] = string2Array(str, split);
        if (temp == null) {
            return list;
        }
        list = new ArrayList<>(temp.length);
        for (String s : temp) {
            list.add(Long.valueOf(s));
        }
        return list;
    }

    public static <E, F> Map<E, List<F>> toMapValueList(Collection<F> list, Function<F, E> keyFunc) {
        Map<E, List<F>> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            List<F> fList = map.get(e);
            if (fList == null) {
                fList = new LinkedList<>();
                map.put(e, fList);
            }
            fList.add(f);
        }
        return map;
    }

    public static <E, F> Map<E, List<F>> toMapValueList(Stream<F> list, Function<F, E> keyFunc) {
        Map<E, List<F>> map = new HashMap<>();
        list.sequential().forEach((F f) -> {
            E e = keyFunc.apply(f);
            List<F> fList = map.get(e);
            if (fList == null) {
                fList = new LinkedList<>();
                map.put(e, fList);
            }
            fList.add(f);
        });
        return map;
    }

    public static <E, F, G> Map<E, List<G>> toMapValueList(Collection<F> list, Function<F, E> keyFunc, Function<F, G> valueFunc) {
        Map<E, List<G>> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            List<G> fList = map.get(e);
            if (fList == null) {
                fList = new LinkedList<>();
                map.put(e, fList);
            }
            G g = valueFunc.apply(f);
            fList.add(g);
        }
        return map;
    }

    public static <E, F, G> Map<E, Set<G>> toMapValueSet(Collection<F> list, Function<F, E> keyFunc, Function<F, G> valueFunc) {
        Map<E, Set<G>> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            Set<G> fList = map.get(e);
            if (fList == null) {
                fList = new HashSet<>();
                map.put(e, fList);
            }
            G g = valueFunc.apply(f);
            fList.add(g);
        }
        return map;
    }

    public static <E, F, G> Map<E, Map<G, F>> toMapValueMap(Collection<F> list, Function<F, E> keyFunc, Function<F, G> subKeyFunc) {
        Map<E, Map<G, F>> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            Map<G, F> fgMap = map.get(e);
            if (fgMap == null) {
                fgMap = new HashMap<>();
                map.put(e, fgMap);
            }
            G g = subKeyFunc.apply(f);
            fgMap.put(g, f);
        }
        return map;
    }

    public static <E, F> Map<E, F> toMap(Collection<F> list, Function<F, E> keyFunc) {
        Map<E, F> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            map.put(e, f);

        }
        return map;
    }

    public static <E, F> Map<E, F> toMap(Collection<F> list, Function<F, E> keyFunc, BiFunction<F, F, F> valuesSelectFunc) {
        Map<E, F> map = new HashMap<>();
        for (F f : list) {
            E e = keyFunc.apply(f);
            F old = map.get(e);
            if (old == null) {
                map.put(e, f);
            } else {
                map.put(e, valuesSelectFunc.apply(old, f));
            }
        }
        return map;
    }

    public static <K, V, G> Map<K, V> toMap(Collection<G> list, Function<G, K> keyFunc, Function<G, V> valueFunc) {
        Map<K, V> map = new HashMap<>();
        for (G g : list) {
            K k = keyFunc.apply(g);
            V v = valueFunc.apply(g);
            map.put(k, v);

        }
        return map;
    }

    public static <K, V, G> Map<K, V> toMap(Stream<G> stream, Function<G, K> keyFunc, Function<G, V> valueFunc) {
        Map<K, V> map = new HashMap<>();
        stream.sequential().forEach(g -> {
            K k = keyFunc.apply(g);
            V v = valueFunc.apply(g);
            map.put(k, v);

        });
        return map;
    }

    public static <E, F> Map<E, F> toMapKeys(Collection<F> list, Function<F, Collection<E>> keysFunc) {
        Map<E, F> map = new HashMap<>();
        for (F f : list) {
            Collection<E> es = keysFunc.apply(f);
            if (!T.isEmpty(es)) {
                for (E e : es) {
                    map.put(e, f);
                }
            }

        }
        return map;
    }

    public static <E, F, G> Map<E, List<G>> toMapKeysValueList(Collection<F> list, Function<F, Set<E>> keysFunc, Function<F, G> valueFunc) {
        Map<E, List<G>> map = new HashMap<>();
        for (F f : list) {
            Set<E> es = keysFunc.apply(f);
            if (!T.isEmpty(es)) {
                for (E e : es) {
                    List<G> fList = map.get(e);
                    if (fList == null) {
                        fList = new LinkedList<>();
                        map.put(e, fList);
                    }
                    G g = valueFunc.apply(f);
                    fList.add(g);
                }
            }
        }
        return map;
    }

    public static boolean isBlankOrNullStr(String str) {
        return str == null || (str = str.trim()).length() == 0 || "null".equalsIgnoreCase(str);
    }

    public static boolean isWindowEnv() {
        String osName = System.getProperty("os.name");
        boolean flag = osName.toLowerCase().contains("window");
        return flag;
    }

    public static boolean isLinuxEnv() {
        String osName = System.getProperty("os.name");
        boolean flag = osName.toLowerCase().contains("linux");
        return flag;
    }

    public static <E> E findFirst(List<E> list) {
        if (list == null) {
            return null;
        }
        return list.stream().findFirst().orElse(null);
    }

    public static String UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public static String endsWith(String str, String suffix) {
        if (!str.endsWith(suffix)) {
            str = str + suffix;
        }
        return str;
    }

    public static String endsWithOrBlank(String str, String suffix) {
        if (T.isBlank(str)) {
            return "";
        }
        if (!str.endsWith(suffix)) {
            str = str + suffix;
        }
        return str;
    }

    public static boolean isNull(Object val) {
        return val == null;
    }

    public static boolean nonNull(Object val) {
        return val != null;
    }

    public static boolean isNotNull(Object val) {
        return val != null;
    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new NullPointerException(messageSupplier.get());
        }
        return obj;
    }

    public static <E, F> E getArrayElement(E[] arr, F val, Function<E, F> function) {
        if (val == null) {
            return null;
        }
        for (E a : arr) {
            if (val.equals(function.apply(a))) {
                return a;
            }
        }
        return null;
    }

    public static <E> E getArrayElement(E[] arr, E val) {
        if (val == null) {
            return null;
        }
        for (E a : arr) {
            if (val.equals(a)) {
                return a;
            }
        }
        return null;
    }

    public static <E> E catchException(SupplierThrow<E> supplier, String msg) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            log.warn(msg, e);
        }
        return null;
    }

    public static void catchException(Done done, String msg) {
        try {
            done.doing();
        } catch (Throwable e) {
            log.warn(msg, e);
        }
    }

    public static void catchException(Done done, Function<Throwable, String> function) {
        try {
            done.doing();
        } catch (Throwable e) {
            log.warn(function.apply(e), e);
        }
    }

    public static String findFirst(String data) {

        return findFirst(data, ",");
    }

    public static String findFirst(String data, String split) {
        if (T.isBlank(data)) {
            return null;
        }
        for (String s : data.split(split)) {
            if (T.isNotBlank(s)) {
                return s;
            }
        }
        return null;
    }

    public static List<Integer> integerList(List<String> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    public static List<Long> longList(List<String> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public static <E> boolean checkChangeField(E oldE, E newE, Class<E> clas) {
        if (oldE == null) {
            return true;
        }
        Field[] fields = clas.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldE);
                Object newValue = field.get(newE);

                if (!T.isEquals(oldValue, newValue)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                log.warn("比较异常", e);
                return false;
            }
        }
        return false;
    }

    public static <K, V> V get(Map<K, V> map, K k) {
        return map == null ? null : map.get(k);
    }

    public static <K, V> V remove(Map<K, V> map, K k) {
        return map == null ? null : map.remove(k);
    }

    public static <K, V> boolean containsKey(Map<K, V> map, K k) {
        return map == null ? false : map.containsKey(k);
    }

    public static <K, V> boolean containsValue(Map<K, V> map, K k) {
        return map == null ? false : map.containsValue(k);
    }

    public static <K> boolean contains(Set<K> set, K k) {
        return set == null ? false : set.contains(k);
    }

    public static <K> boolean contains(String str, String v) {
        return str == null ? false : str.contains(v);
    }

    public static boolean findInSet(String str, String strings) {
        if (T.isBlank(str)) {
            return false;
        }

        Set<String> set = string2Set(strings, ",");
        if (isEmpty(set)) {
            return false;
        }
        return set.contains(str);
    }

    public static <E> void copy(E oldE, E newE, Class<E> clas) {
        if (oldE == null) {
            return;
        }
        BeanUtils.copyProperties(oldE, newE);
    }

    public static Map<String, Object> beanField2Map(Object obj) {
        return beanField2Map(obj, false);
    }

    public static Map<String, Object> beanField2Map(Object obj, boolean containsNullKey) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        try {
            Map<String, Object> result = new HashMap();
            Class<?> type = obj.getClass();
            SetAccessibleAction setAccessibleAction = new SetAccessibleAction();
            while (type != null && !type.getName().equals(Object.class.getName())) {

                Field[] fields = type.getDeclaredFields();
                for (int i = 0; i < fields.length; ++i) {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();
                    if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                        setAccessibleAction.setField(field);
                        AccessController.doPrivileged(setAccessibleAction);
                        Object fieldValue = field.get(obj);
                        String propertyKey = field.getName();
                        if ((fieldValue != null || containsNullKey) && !result.containsKey(propertyKey)) {
                            result.put(propertyKey, fieldValue);
                        }
                    }
                }
                type = type.getSuperclass();
            }
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    public static BigDecimal divide(String v, int rate, int scale) {
        return new BigDecimal(v).divide(new BigDecimal(rate), scale, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean isEmail(String s) {

        return s.matches("[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})");
    }

    public static boolean isNullOrEmptyStr(Object obj) {
        return obj == null || (obj instanceof String ? ((String) obj).length() == 0 : false);
    }

    public static boolean isNullOrEmptyStrOrEmptyCollection(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).size() == 0;
        }
        return false;
    }

    public static boolean isArrayOrCollection(Object value) {
        return value != null && (value instanceof Collection || value.getClass().isArray());
    }

    public static List<Integer> string2Int(List<String> list) {
        return list.stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    public static List<Long> string2Long(List<String> list) {
        return list.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public static boolean startWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }

    public static boolean endWith(String str, String suffix) {
        return str != null && str.endsWith(suffix);
    }

    public static boolean macthes(String str, String regex) {
        return str != null && str.matches(regex);
    }

    public static String getMessage(Throwable e) {
        return T.defaultValue(e.getMessage(), () -> T.getExceptionDetail(e, 10));
    }

    public static Long multi(Long value, int mul) {
        if (value == null) {
            return value;
        }
        return value * mul;
    }

    public static Long divide(Long value, long div) {
        if (value == null) {
            return value;
        }
        return value / div;
    }

    public static Double divide(Double value, long div) {
        if (value == null) {
            return value;
        }
        return value / div;
    }

    public static JSON toJson(String json) {
        if (T.isBlank(json)) {
            return null;
        }
        if (JSON.isValidArray(json)) {
            return JSON.parseArray(json);
        }
        if (JSON.isValidObject(json)) {
            return JSON.parseObject(json);
        }
        return null;
    }

    public static Object toNumber(Double d) {
        String s = d.toString();
        if (s.matches(".*\\.0+")) {
            if (d.longValue() == d.intValue()) {
                return d.intValue();
            }
            return d.longValue();
        }

        return d;
    }

    public static String tryEncode(byte[] data, Charset charset) {
        try {
            String content = new String(data, charset);
            byte[] decode = content.getBytes(charset);
            if (Arrays.equals(data, decode)) {
                return content;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String retry(SupplierThrow<String> supplier, int size) {
        return retry(supplier, T::isNotBlank, size, 5 * 1000);
    }

    public static <E> E retry(SupplierThrow<E> supplier, Predicate<E> successPredicate, int size, long sleepMilsecond) {
        E res = null;
        for (int i = 0; i < size; i++) {
            try {
                res = supplier.get();
                if (successPredicate.test(res)) {
                    break;
                }
            } catch (Exception ex) {
                log.warn("retry请求异常,第" + i + "次, res:" + T.subString(T.toString(res), 200), ex);
            }

            if (sleepMilsecond > 0) {
                try {
                    Thread.sleep(sleepMilsecond);
                } catch (Exception ex) {
                    log.warn("查询操作结果休眠异常" + i, ex);
                }
            }
        }
        return res;
    }

    public static Map<String, Object> objectToMap(Object args) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(args.getClass()))
                .filter(pd -> !"class".equals(pd.getName()))
                .collect(HashMap::new,
                        (map, pd) -> map.put(pd.getName(), ReflectionUtils.invokeMethod(pd.getReadMethod(), args)),
                        HashMap::putAll);
    }

    /**
     * Object to POJO
     *
     * @param object
     * @param toValueType
     * @param <T>
     * @return
     */
    public static <T> T convertObject(Object object, Class<T> toValueType) {
        final ObjectMapper mapper = new ObjectMapper();
        T t = mapper.convertValue(object, toValueType);
        return t;
    }

    /**
     * 去掉特殊颜色缩小图片
     *
     * @param inputStream
     * @param imgType
     * @param defaultColors
     * @return
     * @throws IOException
     */
    public static byte[] scaleImage(InputStream inputStream, String imgType, Set<Integer> defaultColors,
                                    int spanX, int spanY, int minWidth, int minHeight) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        int x0 = 0;
        {
            xLabel:
            for (int i = 0, len = bufferedImage.getWidth(); i < len; i++) {
                for (int j = 0, h = bufferedImage.getHeight(); j < h; j++) {
                    int color = bufferedImage.getRGB(i, j);
                    if (!defaultColors.contains(color)) {
                        x0 = i;
                        break xLabel;
                    }
                }
            }
        }

        int x1 = bufferedImage.getWidth();
        {
            xLabel:
            for (int i = bufferedImage.getWidth() - 1; i >= 0; i--) {
                for (int j = 0, h = bufferedImage.getHeight(); j < h; j++) {
                    int color = bufferedImage.getRGB(i, j);
                    if (!defaultColors.contains(color)) {
                        x1 = i;
                        break xLabel;
                    }
                }
            }
        }

        int y0 = 0;
        {
            yLabel:
            for (int i = 0, len = bufferedImage.getHeight(); i < len; i++) {
                for (int j = 0, h = bufferedImage.getWidth(); j < h; j++) {
                    int color = bufferedImage.getRGB(j, i);
                    if (!defaultColors.contains(color)) {
                        y0 = i;
                        break yLabel;
                    }
                }
            }
        }

        int y1 = bufferedImage.getHeight();
        {
            yLabel:
            for (int i = bufferedImage.getHeight() - 1; i >= 0; i--) {
                for (int j = 0, h = bufferedImage.getWidth(); j < h; j++) {
                    int color = bufferedImage.getRGB(j, i);
                    if (!defaultColors.contains(color)) {
                        y1 = i;
                        break yLabel;
                    }
                }
            }
        }

        bufferedImage = bufferedImage.getSubimage(x0, y0, Math.min(bufferedImage.getWidth(), x1 - x0 + 1), Math.min(bufferedImage.getHeight(), y1 - y0 + 1));

        final int bHeight = bufferedImage.getHeight();
        final int bWidth = bufferedImage.getWidth();
        final int maxWidth = Math.max(minWidth, bWidth);
        final int maxHeight = Math.max(minHeight, bHeight);

        int w = maxWidth - spanX * 2;
        int h = maxHeight - spanY * 2;
        int x = spanX;
        int y = spanY;

        if (w * 1.0 / bWidth <= h * 1.0 / bHeight) {
            h = (int) (bHeight * 1.0 * w / bWidth);
            y = (maxHeight - h) / 2;
        } else {
            w = (int) (bWidth * 1.0 * h / bHeight);
            x = (maxWidth - w) / 2;
        }
        Image image = bufferedImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);

        BufferedImage newImage = new BufferedImage(maxWidth, maxHeight, bufferedImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(image, x, y, w, h, null);
        g.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(newImage, imgType, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 获取Ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 判断ip是否符合ipv6格式
     *
     * @param IP
     * @return
     */
    public static boolean isIpv6(String IP) {
        if (T.isBlank(IP)) {
            return false;
        }
        return IPV6_PATTERN.matcher(IP).matches();
    }

    public static Long getTimeInMillis(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.getTime();
    }

    /**
     * 计算分批次数
     *
     * @param total
     * @param pageSize
     * @return
     */
    public static int computePages(int total, int pageSize) {
        if (total == 0) {
            return total;
        }
        int pages = 1;
        if (total % pageSize > 0) {
            pages = (total / pageSize) + 1;
        } else if (total % pageSize == 0) {
            pages = total / pageSize;
        }
        return pages;
    }


    public static interface SupplierThrow<E> {
        E get() throws Exception;
    }

    public static interface Done {
        void doing() throws Throwable;
    }

    private static class SetAccessibleAction implements PrivilegedAction<Object> {
        @Nullable
        private Field field;

        public SetAccessibleAction() {
        }

        @Override
        public Object run() {
            this.field.setAccessible(true);
            return null;
        }

        public void setField(@Nullable Field field) {
            this.field = field;
        }
    }


    public static Collection subtractList(Collection a, Collection b) {
        if (a == null) {
            a = new ArrayList();
        }
        if (b == null) {
            b = new ArrayList();
        }
        return CollectionUtils.subtract(a, b);
    }

    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    public static String toJsonString(Object obj, String def) {
        if (obj == null) {
            return def;
        }
        return JSON.toJSONString(obj);
    }

    public static <E> List<E> pageList(Supplier<Long> queryCountSupp,
                                       BiFunction<Integer, Integer, List<E>> queryListFunc) {
        return pageList(1000, queryCountSupp, queryListFunc);
    }

    public static <E> List<E> pageList(final int pageSize, Supplier<Long> queryCountSupp,
                                       BiFunction<Integer, Integer, List<E>> queryListFunc) {
        long totalCount = queryCountSupp.get();
        int pageTotal = T.computePages((int) totalCount, pageSize);
        if (pageTotal <= 0) {
            return Collections.emptyList();
        }
        List<E> dataList = Lists.newArrayList();
        for (int page = 1; page <= pageTotal; page++) {
            int offset = (page - 1) * pageSize;
            List<E> list = queryListFunc.apply(pageSize, offset);
            dataList.addAll(list);
        }
        return dataList;
    }

    public static <E> List<E> parallelPageList(Supplier<Long> queryCountSupp,
                                               BiFunction<Integer, Integer, List<E>> queryListFunc) throws ExecutionException, InterruptedException {
        return parallelPageList(1000, queryCountSupp, queryListFunc);
    }

    public static <E> List<E> parallelPageList(final int pageSize, Supplier<Long> queryCountSupp,
                                               BiFunction<Integer, Integer, List<E>> queryListFunc) throws ExecutionException, InterruptedException {
        long totalCount = queryCountSupp.get();
        int pageTotal = T.computePages((int) totalCount, pageSize);
        if (pageTotal <= 0) {
            return Collections.emptyList();
        }
        ExecutorService executor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setDaemon(false).setNameFormat("parallel-page-list-pool-%d").build());
        List<CompletableFuture<List<E>>> futureList = new ArrayList<>();
        for (int page = 1; page <= pageTotal; page++) {
            int finalPage = page;
            CompletableFuture<List<E>> future = CompletableFuture.supplyAsync(() -> {
                int offset = (finalPage - 1) * pageSize;
                List<E> list = queryListFunc.apply(pageSize, offset);
                return list;
            }, executor);
            futureList.add(future);
        }
        List<E> dataList = Lists.newArrayList();
        Object lock = new Object();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).whenComplete((v, t) -> {
            futureList.forEach(future -> {
                synchronized (lock) {
                    dataList.addAll(future.getNow(null));
                }
            });
        }).get();
        return dataList;
    }

    public static String getFileType(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx < 0) {
            return "";
        }
        return fileName.substring(idx);
    }

}