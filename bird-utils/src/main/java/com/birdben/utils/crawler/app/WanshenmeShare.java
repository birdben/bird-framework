package com.birdben.utils.crawler.app;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class WanshenmeShare {

    public static Set<String> set = new HashSet<String>();

    public static synchronized void add(String str) {
        set.add(str);
    }

    public static synchronized void clearSet() {
        set.clear();
    }

    public static Set<String> getSet() {
        Set<String> s = new HashSet<String>();
        s.addAll(set);
        return s;
    }

    public static AtomicInteger count = new AtomicInteger(0);

    public static String site = "今天玩什么";
    public static int list_sleep_millis = 200;
    public static int detail_sleep_millis = 100;

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static String HandleMessyCode(String str) {
        char[] ch = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            if (!Character.isLetterOrDigit(ch[i])) {
                if (!isChinese(ch[i])) {
                    ch[i] = ' ';
                }
            }
            sb.append(ch[i]);
        }
        return sb.toString();
    }
}
