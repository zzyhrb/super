package com.newgrand.superviseanalysis.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName PinYin4JUtil.java
 * @Description TODO
 * @createTime 2020年04月07日 11:23:00
 */
public class PinYin4JUtil {
    public static String toPinyin(String str) {
        String convert = "";
        for (int j =0, len = str.length();j<len;j++){
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }
    }
