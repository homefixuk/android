package com.homefix.tradesman.common;

import com.homefix.tradesman.model.Charge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by samuel on 10/4/2016.
 */
public class ListConverter<T> {

    Map<String, T> charges;

    public ListConverter(Map<String, T> charges) {
        this.charges = charges;
    }

    public List<T> toList() {
        if (charges == null || charges.isEmpty()) return new ArrayList<>();

        List<T> list = new ArrayList<>();
        Set<String> keys = charges.keySet();
        for (String key : keys) {
            if (charges.get(key) == null) continue;

            list.add(charges.get(key));
        }

        return list;
    }

}
