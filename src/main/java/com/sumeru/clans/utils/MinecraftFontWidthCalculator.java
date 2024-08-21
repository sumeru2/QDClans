package com.sumeru.clans.utils;

import java.util.HashMap;
import java.util.Map;

public class MinecraftFontWidthCalculator {
    private static final int DEFAULT_WIDTH = 5;
    private static final Map<Character, Integer> SPECIAL_WIDTHS = new HashMap();

    public static int getStringWidth(String s) {
        if (s == null) {
            return 0;
        }
        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == '\247') {
                j++;
                continue;
            }
            i += SPECIAL_WIDTHS.getOrDefault(s.charAt(j), DEFAULT_WIDTH);
            if (j + 1 < s.length()) {
                i++;
            }
        }

        return i;
    }

    static {
        SPECIAL_WIDTHS.put(' ', 3);
        SPECIAL_WIDTHS.put('!', 1);
        SPECIAL_WIDTHS.put('"', 3);
        SPECIAL_WIDTHS.put('\'', 1);
        SPECIAL_WIDTHS.put('(', 3);
        SPECIAL_WIDTHS.put(')', 3);
        SPECIAL_WIDTHS.put('*', 3);
        SPECIAL_WIDTHS.put(',', 1);
        SPECIAL_WIDTHS.put('.', 1);
        SPECIAL_WIDTHS.put(':', 1);
        SPECIAL_WIDTHS.put(';', 1);
        SPECIAL_WIDTHS.put('<', 4);
        SPECIAL_WIDTHS.put('>', 4);
        SPECIAL_WIDTHS.put('@', 6);
        SPECIAL_WIDTHS.put('I', 3);
        SPECIAL_WIDTHS.put('[', 3);
        SPECIAL_WIDTHS.put(']', 3);
        SPECIAL_WIDTHS.put('`', 2);
        SPECIAL_WIDTHS.put('f', 4);
        SPECIAL_WIDTHS.put('i', 1);
        SPECIAL_WIDTHS.put('k', 4);
        SPECIAL_WIDTHS.put('l', 2);
        SPECIAL_WIDTHS.put('t', 3);
        SPECIAL_WIDTHS.put('{', 3);
        SPECIAL_WIDTHS.put('|', 1);
        SPECIAL_WIDTHS.put('}', 3);
        SPECIAL_WIDTHS.put('~', 6);
    }
}