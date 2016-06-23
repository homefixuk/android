package com.samdroid.common;

import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by samuel on 12/29/2015.
 */
public class TextContentUtils {

    /**
     * @param text
     * @param limit the maximum number of results to return (<=0 for no limit)
     * @return a list of the most frequent words in a block of text and its number of occurrences
     */
    public static List<Map.Entry<String, Integer>> getWordsInMostFrequentOrder(String text, int limit) {
        if (Strings.isEmpty(text)) return new ArrayList<>();

        // make sure the text is lower case
        text = text.toLowerCase(Locale.getDefault());

        // split the text into words
        String[] words = text.split(" ");

        // get the count for each word
        Map<String, Integer> map = new HashMap<>();
        for (String w : words) {
            Integer n = map.get(w);
            n = (n == null) ? 1 : ++n;
            map.put(w, n);
        }

        // put each entry into a list
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) sortedList.add(entry);

        // sort the list in word frequency order
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                if (lhs.getValue() > rhs.getValue()) return -1;
                if (lhs.getValue() < rhs.getValue()) return 1;
                return 0;
            }

        });

        if (limit > 0) {
            // only return the limited number wanted
            sortedList = sortedList.subList(0, Math.min(sortedList.size(), limit));
        }

        return sortedList;
    }

}
