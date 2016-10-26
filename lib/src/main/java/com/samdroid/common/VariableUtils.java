package com.samdroid.common;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.samdroid.string.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * Class to provide utility functions for various variables
 *
 * @author Sam Koch
 */
public class VariableUtils {

    public static final String TAG = "VariableUtils";

    /**
     * Swap the values of two variables
     *
     * @param a
     * @param b
     */
    public static void swapValues(Object a, Object b) {
        Object temp = a;
        a = b;
        b = temp;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;

        try {
            byte[] bytes = new byte[buffer_size];

            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }

        } catch (Exception ex) {
        }
    }

    /**
     * @param s
     * @param strings
     * @return the number of times o is in objects
     */
    public static int occurrencesInList(String s, List<String> strings) {
        // if the list is null
        if (strings == null) return 0;
        if (strings.size() == 0) return 0;

        int count = 0;

        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals(s)) count++;
        }

        return count;
    }

    /**
     * @param strings
     * @param s
     * @return if the list of strings contains the string s
     */
    public static boolean containsString(List<String> strings, String s) {
        if (strings == null || strings.size() == 0) return false;

        for (String next : strings)
            if (next.equals(s)) return true;

        return false;
    }

    /**
     * @param packageInfos
     * @param pckge
     * @return if the list of PackageInfos contains the a Package
     */
    public static boolean containsPackage(List<PackageInfo> packageInfos, PackageInfo pckge) {
        if (pckge == null
                || Strings.isEmpty(pckge.packageName)
                || packageInfos == null
                || packageInfos.size() == 0) return false;

        for (PackageInfo next : packageInfos) {
            if (next == null || Strings.isEmpty(next.packageName)) continue;

            if (next.packageName.equals(pckge.packageName)) return true;
        }

        return false;
    }

    /**
     * @param subset
     * @param superset
     * @return if the subset is a subset of the superset
     */
    public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Handles if the value is being stored as an Integer, String, Float or Double from a hash map
     *
     * @param map
     * @param key
     * @return safely the double value from the key in the hashmap
     */
    public static Double getDoubleSafely(HashMap<String, Object> map, String key) {
        if (map == null) return Double.NaN;
        if (key == null) return Double.NaN;
        if (!map.containsKey(key)) return Double.NaN;
        if (map.get(key) == null) return Double.NaN;

        return getDoubleSafely(map.get(key));
    }

    /**
     * Handles if the value is being stored as an Integer, String, Float or Double
     *
     * @param object
     * @return safely the double value from object
     */
    public static Double getDoubleSafely(Object object) {
        if (object == null) return null;

        if (object instanceof Integer) {
            return (Double) (1.0 * ((Integer) object));

        } else if (object instanceof String) {
            return Double.valueOf((String) object);

        } else if (object instanceof Double) {
            return (Double) object;
        }

        return null;
    }

    /**
     * Handles if the value is being stored as an Integer, String, Float or Double
     *
     * @param object
     * @return safely the double value from object
     */
    public static Integer getIntegerSafely(Object object) {
        if (object == null) return null;

        if (object instanceof Integer) {
            return (Integer) object;

        } else if (object instanceof String) {
            return Integer.valueOf((String) object);

        } else if (object instanceof Double) {
            return (Integer) object;
        }

        return null;
    }

    /**
     * @param list
     * @return return a copy of the list
     */
    public static List<Object> copyList(List<Object> list) {
        if (list == null) return null;

        List<Object> returnList = new ArrayList<Object>();

        for (int i = 0; i < list.size(); i++) {
            returnList.add(list.get(i));
        }

        return list;
    }

    public static <T> void printStack(Stack<T> stack) {
        if (stack == null) {
            MyLog.e(TAG, "Stack is NULL");
            return;
        }

        printArray(stack.toArray());
    }

    public static <T> void printList(List<T> list) {
        if (list == null) {
            MyLog.e("VariableUtils", "List is NULL");
            return;
        }

        MyLog.e("VariableUtils", "List");
        for (int i = 0; i < list.size(); i++) {
            MyLog.e("VariableUtils", "i => " + list.get(i));
        }
    }

    public static <T> String listToString(List<T> list) {
        return "[" + listToString(list, ", ") + "]";
    }

    public static <T> String listToString(List<T> list, String delimiter) {
        if (list == null) return "";

        delimiter = Strings.returnSafely(delimiter);

        String s = "";
        for (int i = 0; i < list.size(); i++) {
            Object next = list.get(i);
            if (next == null) continue;

            if (i > 0) s += delimiter;
            s += "" + next;
        }

        return s;
    }

    public static <T> void printArray(T[] array) {
        if (array == null) {
            MyLog.e("VariableUtils", "Array is NULL");
            return;
        }

        MyLog.e("VariableUtils", "array");
        for (int i = 0; i < array.length; i++) {
            MyLog.e("VariableUtils", "i => " + array[i]);
        }
    }

    public static void printHashMap(HashMap<String, Object> example) {
        if (example == null) {
            MyLog.e(TAG, "Map is NULL");
            return;
        }

        for (String name : example.keySet()) {
            String value = example.get(name).toString();
            MyLog.e("HashMap", name + " " + value);
        }
    }

    public static void printMap(Map<String, Object> example) {
        if (example == null) {
            MyLog.e(TAG, "Map is NULL");
            return;
        }

        for (String name : example.keySet()) {
            String value = example.get(name).toString();
            MyLog.e("HashMap", name + " " + value);
        }
    }

    public static void printStringToObjectMap(Map<String, Object> example) {
        if (example == null) {
            MyLog.e(TAG, "Map is NULL");
            return;
        }

        for (String name : example.keySet()) {
            String key = name.toString();
            String value = example.get(name).toString();
            MyLog.e("Map", key + " " + value);
        }
    }

    public static void printStringToStringMap(Map<String, String> example) {
        if (example == null) {
            MyLog.e(TAG, "Map is NULL");
            return;
        }

        for (String name : example.keySet()) {
            String key = name.toString();
            String value = example.get(name).toString();
            MyLog.e("Map", key + " " + value);
        }
    }

    public static void printMapEntryIntegersList(List<Map.Entry<String, Integer>> list) {
        if (list == null) {
            MyLog.e("VariableUtils", "List is NULL");
            return;
        }

        MyLog.e("VariableUtils", "List");
        for (int i = 0; i < list.size(); i++) {
            MyLog.e("VariableUtils", "(" + i + ") " + list.get(i).getKey() + " => " + list.get(i).getValue());
        }
    }

    public static void printIntent(Intent i) {
        if (i == null) {
            MyLog.e(TAG, "Intent is NULL");
            return;
        }

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            MyLog.e(TAG, "Intent start");
            while (it.hasNext()) {
                String key = it.next();
                MyLog.e(TAG, "[" + key + "=" + bundle.get(key) + "]");
            }
            MyLog.e(TAG, "Intent end");
        }
    }

    public static void printPackageInfos(List<PackageInfo> list) {
        if (list == null) {
            MyLog.e("VariableUtils", "List is NULL");
            return;
        }

        MyLog.e("VariableUtils", "List");
        for (int i = 0; i < list.size(); i++) {
            MyLog.e(
                    "VariableUtils",
                    "(" + i + ") " + list.get(i).packageName + " => " + list.get(i).versionName + "v");
        }
    }

    public static void printBundle(Bundle b) {
        if (b == null) {
            MyLog.e("VariableUtils", "Bundle is NULL");
            return;
        }

        MyLog.e("VariableUtils", "Bundle");
        for (String key : b.keySet()) {
            MyLog.e("VariableUtils", key + " => " + b.get(key) + "");
        }
    }

    public static Intent extractAllNestedData(Intent intent) {
        Intent i = new Intent();

        if (intent == null) return i;

        // add all extras from the push intent to the new intent
        Bundle b = intent.getExtras();
        for (String key : b.keySet()) {
            MyLog.e(TAG, "key: " + key);
            // get the extra value
            String value = Strings.returnSafely(b.getString(key));
            MyLog.e(TAG, "value: " + value);

            try {
                // parse the JSON and put all its values into the main intent
                JSONObject json = new JSONObject(value);

                // iterate over its keys
                Iterator<?> keys = json.keys();
                while (keys.hasNext()) {
                    String jsonKey = (String) keys.next();
                    Object jsonValue = json.get(jsonKey);
                    MyLog.e(TAG, "JSON -> " + jsonKey + ": " + jsonValue);

                    // if the value is a string
                    if (jsonValue instanceof String) {
                        // if the string was a JSONObject and we got all the values from it,
                        // add them to the intent extras, else just add the value as a string
                        Bundle b2;
                        if ((b2 = getAllNestedJSONValues(new JSONObject((String) jsonValue))) != null || b.isEmpty())
                            i.putExtras(b2);
                        else
                            i.putExtra(jsonKey, (String) jsonValue);
                    } else if (jsonValue instanceof Integer)
                        i.putExtra(jsonKey, (Integer) jsonValue);
                    else if (jsonValue instanceof Double) i.putExtra(jsonKey, (Double) jsonValue);
                    else if (jsonValue instanceof Boolean) i.putExtra(jsonKey, (Boolean) jsonValue);
                }

            } catch (Exception e) {
                MyLog.e(TAG, "JSON ERRROR");
                e.printStackTrace();
            }
        }

        return i;
    }

    public static Bundle getAllNestedJSONValues(JSONObject json) {
        Bundle b = new Bundle();

        try {
            // iterate over its keys
            Iterator<?> keys = json.keys();
            while (keys.hasNext()) {
                String jsonKey = (String) keys.next();
                Object jsonValue = json.get(jsonKey);
                MyLog.e(TAG, "JSON -> " + jsonKey + ": " + jsonValue);

                if (jsonValue instanceof String) {
                    try {
                        Bundle b2 = getAllNestedJSONValues(new JSONObject((String) jsonValue));
                        b.putAll(b2);

                    } catch (Exception e) {
                        b.putString(jsonKey, (String) jsonValue);
                    }
                } else if (jsonValue instanceof Integer) b.putInt(jsonKey, (Integer) jsonValue);
                else if (jsonValue instanceof Double) b.putDouble(jsonKey, (Double) jsonValue);
                else if (jsonValue instanceof Boolean) b.putBoolean(jsonKey, (Boolean) jsonValue);
            }

        } catch (JSONException e) {
            return null;
        }

        return b;
    }

    public static Bundle jsonToBundle(JSONObject json) {
        Bundle b = new Bundle();

        try {
            // iterate over its keys
            Iterator<?> keys = json.keys();
            while (keys.hasNext()) {
                String jsonKey = (String) keys.next();
                Object jsonValue = json.get(jsonKey);
                MyLog.e(TAG, "JSON to Bundle -> " + jsonKey + ": " + jsonValue);

                if (jsonValue instanceof String) b.putString(jsonKey, (String) jsonValue);
                else if (jsonValue instanceof Integer) b.putInt(jsonKey, (Integer) jsonValue);
                else if (jsonValue instanceof Double) b.putDouble(jsonKey, (Double) jsonValue);
                else if (jsonValue instanceof Boolean) b.putBoolean(jsonKey, (Boolean) jsonValue);
            }

        } catch (Exception e) {
        }

        return b;
    }

    /**
     * @param list
     * @return a list safely, making an empty list if the list is null
     */
    public static List returnListSafely(List list) {
        return list != null ? list : new ArrayList();
    }

    /**
     * @param a
     * @param b
     * @return get the difference between 2 lists
     */
    public static List getListDifferences(List a, List b) {
        Set ad = new HashSet(a != null ? a : new ArrayList());
        Set bd = new HashSet(b != null ? b : new ArrayList());
        ad.removeAll(bd);
        return new ArrayList(ad);
    }

    /**
     * @param a
     * @param b
     * @return if 2 lists are different (contain any different objects)
     */
    public static boolean areListsDifferent(List a, List b) {
        return getListDifferences(a, b).size() > 0;
    }

    /**
     * @param a
     * @param b
     * @return if two dates are different
     */
    public static boolean areDatesDifferent(Date a, Date b) {
        if (a == null && b == null) return false;
        if (a == null && b != null) return true;
        if (a != null && b == null) return true;
        return !a.equals(b);
    }

    /**
     * @param json
     * @param field
     * @return the string value from the JSON object safely
     */
    public static String getStringSafley(JSONObject json, String field) {
        if (json == null || Strings.isEmpty(field) || !json.has(field)) return "";

        try {
            return json.getString(field);
        } catch (JSONException e) {
        }

        return "";
    }

    /**
     * Convert Bundle into a Map object
     *
     * @param b a bundle to convert
     * @return Map a map version of the bundle
     */
    public static HashMap convertBundleToMap(Bundle b) {
        HashMap map = new HashMap();

        if (b == null) return map;

        Set<String> keys = b.keySet();
        for (String key : keys) {
            map.put(key, b.get(key));
        }

        return map;
    }

    public static boolean areMapsDifferent(HashMap<String, Object> a, HashMap<String, Object> b) {
        if (a == null && b == null) return false;
        if (a == null && b != null) return true;
        if (a != null && b == null) return true;

        Set<String> aKeys = a.keySet();
        Set<String> bKeys = b.keySet();
        for (String aKey : aKeys) {
            for (String bKey : bKeys) {
                if (a.get(aKey) != null && !a.get(aKey).equals(b.get(bKey))) return false;
            }
        }

        return true;
    }

    public static List<Integer> jsonArrayToIntegerList(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return new ArrayList<>();

        List<Integer> list = new ArrayList<>();
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            String o = jsonArray.optString(i);
            if (o == null) continue;

            try {
                list.add(Integer.valueOf(o));
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static ArrayList<String> jsonStringsArrayToList(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            String o = jsonArray.optString(i);

            if (Strings.isEmpty(o)) continue;

            list.add(o);
        }

        return list;
    }

    public static void printStrings(@NonNull String... strings) {
        MyLog.e(TAG, "Printing strings");
        for (int i = 0; i < strings.length; i++) MyLog.e(TAG, i + ": " + strings[i]);
    }


    /**
     * @param c
     * @return get the last element from a collection
     */
    public static Long getBiggestElement(@NonNull final Collection<Long> c) {
        final Iterator<Long> itr = c.iterator();
        Long lastElement = itr.next();
        while (itr.hasNext()) {
            Long next = itr.next();
            if (next > lastElement) lastElement = next;
        }
        return lastElement;
    }

    public static class LastElement<O> {

        public LastElement() {
        }

        /**
         * @param c
         * @return get the last element from a collection
         */
        public O getLastElement(@NonNull final Collection<O> c) {
            final Iterator<O> itr = c.iterator();
            O lastElement = itr.next();
            while (itr.hasNext()) lastElement = itr.next();
            return lastElement;
        }

    }

}
