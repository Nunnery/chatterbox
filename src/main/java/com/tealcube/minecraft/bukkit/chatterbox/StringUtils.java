package com.tealcube.minecraft.bukkit.chatterbox;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {

    private StringUtils() {
        // do nothing
    }

    @SafeVarargs
    public static List<String> concat(List<String>... strings) {
        List<String> ret = new ArrayList<>();
        for (List<String> list : strings) {
            ret.addAll(list);
        }
        return ret;
    }


    public static String[] concat(String string, String[] strings) {
        int size = strings.length + 1;
        String[] ret = new String[size];
        ret[0] = string;
        System.arraycopy(strings, 0, ret, 1, strings.length);
        return ret;
    }

    public static String[] concat(String[] strings, String string) {
        int size = strings.length + 1;
        String[] ret = new String[size];
        System.arraycopy(strings, 0, ret, 0, strings.length);
        ret[strings.length] = string;
        return ret;
    }

    public static String[] concat(String[]... strings) {
        int size = 0;
        for (String[] array : strings) {
            size += array.length;
        }
        String[] ret = new String[size];
        int counter = 0;
        for (String[] array : strings) {
            for (String string : array) {
                ret[counter++] = string;
            }
        }
        return ret;
    }

}
