package com.homefix.tradesman.common;

import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 10/10/2016.
 */

public class CacheUtilsHelper {

    public static String getStringSafely(String name) {
        if (Strings.isEmpty(name)) return "";

        String value = CacheUtils.readFile(name);
        return Strings.returnSafely(value);
    }

}
