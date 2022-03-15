package org.falconframework.logging.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class TraceIdGenerator {
    /**
     * 长度为32的字典集合，每个元素为1位字符
     */
    private static List<String> dict32 = Arrays.asList("a", "b", "4", "c", "d", "e", "5", "f", "g", "h", "6", "i", "j", "k", "l", "m", "n", "7", "o", "p", "q", "r", "8", "s", "t", "u", "v", "w", "x", "9", "y", "z");

    /**
     * 长度为1024的字典集合，每个元素为2位字符
     */
    private static List<String> dict1024 = new ArrayList<>();

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

    static {
        init();
    }

    private static void init() {
        for (int i = 0; i < dict32.size(); i++) {
            for (int j = 0; j < dict32.size(); j++) {
                dict1024.add(dict32.get(i) + dict32.get(j));
            }
        }
    }

    public static String generate() {
        // 毫秒级别的当前时间，10位字符
        String shortTime = getShortTime();
        // 6位随机字符
        String random = getRandom(6);
        return shortTime + random;
    }

    /**
     * 获取毫秒级别的当前时间，10位字符
     */
    private static String getShortTime() {
        // 分段大小，每段按3位分，所以每段的最大值不会超过999
        int block = 3;
        // 当前毫秒级别时间的15位整数（必须是block的倍数）
        String timeNumber = dateFormat.format(new Date());
        // 压缩后的时间字符
        StringBuffer shortTime = new StringBuffer();

        for (int i = 0; i < (timeNumber.length() / block); i++) {
            int start = i * block;
            int end = start + block;
            // 因为dict1024长度为1024，所以blockNumber的最大值只能为1023，这里按3位截取，所以blockNumber的最大值只会是999
            int blockNumber = Integer.parseInt(timeNumber.substring(start, end));
            // 3位数字压缩为2位字符
            shortTime.append(dict1024.get(blockNumber));
        }
        return shortTime.toString();
    }

    /**
     * 获取随机字符
     * @param length 要获取的个数
     * @return
     */
    private static String getRandom(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(dict32.size());
            sb.append(dict32.get(index));
        }
        return sb.toString();
    }

}
