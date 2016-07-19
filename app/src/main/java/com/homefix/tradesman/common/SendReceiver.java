package com.homefix.tradesman.common;

import com.lifeofcoding.cacheutlislibrary.CacheUtils;

/**
 * Created by samuel on 7/13/2016.
 */

public class SendReceiver<O extends Object> {

    private Class<O> clazz;

    public SendReceiver(Class<O> clazz) {
        this.clazz = clazz;
    }

    public String put(String key, O o) {
        CacheUtils.writeObjectFile(key, o);
        return key;
    }

    /**
     * @param o
     * @return the key used to store the object
     */
    public String put(O o) {
        if (o == null) return "";

        String key = "" + o.hashCode();
        CacheUtils.writeObjectFile(key, o);
        return key;
    }

    public O get(String key) {
        return CacheUtils.readObjectFile(key, clazz);
    }

    public O remove(String key) {
        O o = CacheUtils.readObjectFile(key, clazz);

        CacheUtils.writeObjectFile(key, null); // remove from cache

        return o;
    }

    public boolean exists(String key) {
        return CacheUtils.hasCache(key);
    }

}
