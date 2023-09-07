package com.example.justpost.domain.utils;


import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    public static int getIndex(String[] strings, String string) {
        for (int i = 0; i < strings.length; i++) {
            if (StringUtils.equals(strings[i], string)) {
                return i;
            }
        }

        return -1;
    }
}
