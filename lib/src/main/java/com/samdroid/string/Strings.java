package com.samdroid.string;

import android.content.Context;
import android.util.Patterns;

import com.samdroid.math.MathUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strings class with utility functions on Strings
 *
 * @author Sam Koch
 */
public class Strings {

    protected static final String TAG = "Strings";

    public enum HTML_TAG {
        BOLD, ITALIC, UNDERLINE, STRIKE
    }

    private static final Map<HTML_TAG, String> html_tag_map = new HashMap<HTML_TAG, String>() {
        /**
         *
         */
        private static final long serialVersionUID = 1114412377713535678L;

        {
            put(HTML_TAG.BOLD, "b");
            put(HTML_TAG.ITALIC, "i");
            put(HTML_TAG.UNDERLINE, "u");
            put(HTML_TAG.STRIKE, "strike");
        }
    };

    /**
     * Splits a camel case string to make it human readable
     *
     * @param s
     * @return
     */
    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    /**
     * Remove all space and non-visible characters from a string
     *
     * @param s
     * @return
     */
    public static String revertToCamelCase(String s) {
        return s.replaceAll("\\s+", "");
    }

    /**
     * Remove a substring from the end of the string
     *
     * @param s      Main string
     * @param remove substring to remove from end
     * @return string with the end removed, if the original string ended with it
     */
    public static String removeEnd(String s, String remove) {
        if (isEmpty(s) || isEmpty(remove)) {
            return s;
        }
        if (s.endsWith(remove)) {
            return s.substring(0, s.length() - remove.length());
        }
        return s;
    }

    /**
     * Returns if a string is empty (null or zero length)
     *
     * @param s String to check
     * @return if the string is null or has length zero
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.equals("");
    }

    /**
     * @param s
     * @return if a string is empty, after trimming white space before and after its content
     */
    public static boolean isEmptyTrimmed(String s) {
        return s == null || isEmpty(s.trim());
    }

    /**
     * Returns a string safely if it is empty or null
     *
     * @param s String to check
     * @return if the string is null or has length zero
     */
    public static String returnSafely(String s) {
        return (s == null || s.length() == 0) ? "" : s;
    }

    /**
     * Add double inverted commas to a string
     *
     * @param s String to edit
     * @return String with double inverted commas at start and end
     */
    public static String addInvertedCommas(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Trim a string by a certain number of characters from both sides
     *
     * @param s             String to trim
     * @param charsEachSide number of characters to trim from the start and end
     * @return s with the number of characters removed from both sides
     */
    public static String trimStringBothSides(String s, int charsEachSide) {
        if (s == null || s.length() < (charsEachSide * 2)) {
            return "";
        }

        return s.substring(charsEachSide, s.length() - charsEachSide);
    }

    /**
     * Split and trim a string with a comma delimiter
     *
     * @param s             String to trim and split
     * @param charsEachSide number of characters to trim from start and end
     * @return String split and trimmed
     */
    public static String[] splitAndTrimString(String s, int charsEachSide) {
        String[] ids = s.split(",");

        int len = ids.length;
        for (int i = 0; i < len; i++) {
            ids[i] = trimStringBothSides(ids[i], charsEachSide);
        }

        return ids;
    }

    /**
     * Capitalise the first character of a string if not capitalised already
     *
     * @param s String to capitalised
     * @return first character capitalised in the string
     */
    public static String capitalise(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Get the longest length string from a list
     *
     * @param strings list of strings
     * @return length of the longest
     */
    public static int longestStringSize(String... strings) {
        int maxLength = 0;
        for (String s : strings) {
            if (s.length() > maxLength) maxLength = s.length();
        }
        return maxLength;
    }

    /**
     * Pad a string (s) with (n) white space characters right
     *
     * @param s String to pad
     * @param n number of characters
     * @return string padded with the white space characters right
     */
    public static String padRight(String s, int n) {
        //		return String.format("%1$-" + n + "s", s);
        for (int i = 0; i < n; i++) {
            s = s + "&#160;";
        }
        return s;
    }

    /**
     * Pad a string (s) with (n) white space characters left
     *
     * @param s String to pad
     * @param n number of characters
     * @return string padded with the white space characters left
     */
    public static String padLeft(String s, int n) {
        //		return String.format("%1$" + n + "s", s);
        for (int i = 0; i < n; i++) {
            s = "&#160;" + s;
        }
        return s;
    }

    //	&#160;

    /**
     * Pad a string with even number of white space either side of the string
     *
     * @param s       String to pad
     * @param padding number of characters to be split each side
     * @return string padded with white space each side
     */
    public static String addPadding(String s, int padding) {
        // get the rounded down half of the number of characters
        int numLeft = (int) padding / 2;

        // get the rounded up half of the number of characters
        int numRight = Math.round((padding * 1f) / 2f);

        // if there are characters to pad left
        if (numLeft > 0) {
            // pad the string left
            s = padLeft(s, numLeft);
        }

        // if there are characters to pad left
        if (numRight > 0) {
            // pad the string right
            s = padRight(s, numRight);
        }

        // return the padded string
        return s;
    }

    /**
     * Remove all new lines from a string
     *
     * @param s string with new lines
     * @return s without new line characters
     */
    public static String removeNewLines(String s) {
        return s.replaceAll("[\n\r]", "");
    }

    /**
     * Set the colour of a string when used in a display
     *
     * @param s        String to set the colour of
     * @param hexValue the hex value (without the hashtag #)
     * @return the HTML to set the string to have the colour
     */
    public static String setStringColour(String s, String hexValue) {
        return "<font color=\"#" + hexValue + "\">" + s + "</font>";
    }

    /**
     * Apply a list of HTML tags to a string
     *
     * @param s    String to apply tags to
     * @param tags comma list of HTML_TAGs
     * @return the tags applied to s
     */
    public static String setStringTags(String s, HTML_TAG... tags) {
        String t;

        // for each tag
        for (HTML_TAG tag : tags) {
            // get the HTML tag
            t = html_tag_map.get(tag);

            // apply it to the string
            s = "<" + t + ">" + s + "</" + t + ">";
        }

        // return the tagged string
        return s;
    }

    /**
     * Get the number of occurrences of a sub string in a string
     *
     * @param s   String to search sub string in
     * @param sub sub string to search for
     * @return number of occurrences of sub in s
     */
    public static int getSubStringCount(String s, String sub) {
        // put the strings to lower case
        s = s.toLowerCase(Locale.getDefault());
        sub = sub.toLowerCase(Locale.getDefault());

        // check for every instance of the sub string
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {

            // check for the sub string from the last index checked
            lastIndex = s.indexOf(sub, lastIndex);

            // if the sub string is found
            if (lastIndex != -1) {
                // increment the count
                count++;

                // update the last index searched
                lastIndex += sub.length();
            }
        }

        return count;
    }

    /**
     * Set all sub string matches in a string to black colour and bold in HTML tags
     *
     * @param s   String to search for sub strings
     * @param sub sub string to replace
     * @return all occurrences of the sub string put in black and bold HTML tags
     */
    public static String setSubStringMatchesBold(String s, String sub) {
        // get all indices of matching sub string
        ArrayList<Integer> subIndices = Strings.getAllSubStringStartIndices(s, sub);

        // if there are no matches, return the original string
        if (subIndices.size() == 0) {
            return s;
        }

        // create list of string parts
        ArrayList<StringPart> stringParts = new ArrayList<StringPart>();

        // store the last index started from
        int subLength = sub.length(), lastIndex = 0, nextIndex;

        int numSubIndices = subIndices.size();

        // for each sub string index
        for (int i = 0; i < numSubIndices; i++) {
            // get the next index
            nextIndex = subIndices.get(i);

            // add the previous string (not a sub string)
            stringParts.add(new StringPart(s.substring(lastIndex, nextIndex), false));

            // get the new last index
            lastIndex = nextIndex + subLength;

            // add the matching substring
            stringParts.add(new StringPart(s.substring(nextIndex, lastIndex), true));

            // if this is the last index
            if (i == subIndices.size() - 1) {
                // add the remaining text as a string part
                stringParts.add(new StringPart(s.substring(lastIndex), false));
            }
        }

        // make a string with all string parts making the sub string parts in black html tags
        String matchedString = "";
        StringPart nextPart;
        int partsSize = stringParts.size();

        for (int i = 0; i < partsSize; i++) {
            nextPart = stringParts.get(i);

            // if the next part is a sub string
            if (nextPart.isSubString) {
                // add it with bold and black HTML tags
                matchedString += Strings.setStringColour(setStringTags(nextPart.content, HTML_TAG.BOLD), "000000");
            } else {
                // else just add the part as is
                matchedString += nextPart.content;
            }
        }

        return matchedString;
    }

    /**
     * Used to store parts of a string that match a sub string
     *
     * @author Sam Koch
     */
    static class StringPart {
        String content;
        boolean isSubString;

        public StringPart(String content, boolean isSubString) {
            this.content = content;
            this.isSubString = isSubString;
        }
    }

    /**
     * @param a
     * @param b
     * @return if the strings are different
     */
    public static boolean areDifferent(String a, String b) {
        return returnSafely(a).equals(Strings.returnSafely(b));
    }

    /**
     * Get all substring starting indicies
     *
     * @param s
     * @param sub
     * @return
     */
    public static ArrayList<Integer> getAllSubStringStartIndices(String s, String sub) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        // check for null strings
        if (s == null || sub == null) {
            return list;
        }

        // put the strings to lower case
        s = s.toLowerCase(Locale.getDefault());
        sub = sub.toLowerCase(Locale.getDefault());

        // check for every instance of the sub string
        int lastIndex = 0;
        while (lastIndex != -1) {

            // check for the sub string from the last index checked
            lastIndex = s.indexOf(sub, lastIndex);

            // if the sub string is found
            if (lastIndex != -1) {
                // add the index where the sub string was found
                list.add(lastIndex);

                // update the last index searched
                lastIndex += sub.length();
            }
        }

        return list;
    }

    /**
     * Combine multiple strings from the string resources
     *
     * @param context
     * @param stringIds
     * @return the string resources combined
     */
    public static String getResourceStringsCombined(Context context, int... stringIds) {
        String s = "";

        // for every string id
        for (int id : stringIds) {
            s += context.getResources().getString(id);
        }

        return s;
    }

    /**
     * Combine multiple strings from the string resources with a space between each
     *
     * @param context
     * @param stringIds
     * @return the string resources combined
     */
    public static String getResourceStringsCombinedWithSpaces(Context context, int... stringIds) {
        String s = "";

        // for every string id
        for (int id = 0; id < stringIds.length; id++) {
            s += context.getResources().getString(id);

            // add a space to all but the last string
            if (id < stringIds.length - 1) {
                s += " ";
            }
        }

        return s;
    }

    /**
     * @return the string resources combined
     */
    public static String combineStrings(String delimiter, String... strings) {
        String s = "";

        delimiter = returnSafely(delimiter);

        // for every string id
        for (int i = 0; i < strings.length; i++) {
            String next = returnSafely(strings[i]);

            if (Strings.isEmpty(next)) continue;

            if (i > 0) s += delimiter;

            s += next;
        }

        return s;
    }

    /**
     * Add a resource string to the end of a string
     *
     * @param context
     * @param s
     * @param stringId
     * @return
     */
    public static String addResourceStrings(Context context, String s, int stringId) {
        s += context.getResources().getString(stringId);

        return s;
    }

    /**
     * Add resource strings to the end of a string with spaces
     *
     * @param context
     * @param s
     * @param stringIds
     * @return
     */
    public static String addResourceStrings(Context context, String s, int... stringIds) {
        // for every string id
        for (int id = 0; id < stringIds.length; id++) {
            s += context.getResources().getString(stringIds[id]);

            // add a space to all but the last string
            if (id < stringIds.length - 1) {
                s += " ";
            }
        }

        return s;
    }

    /**
     * Condense a name, for example:
     * <p/>
     * George Clooney -> G. Clooney
     * brad pitt -> B. Pitt
     *
     * @param s
     * @return
     */
    public static String condenseName(String s) {
        // split the words in s by spaces
        String[] names = s.split(" ");

        // if there are none or one names, return s
        if (names.length <= 1) {
            return s;
        }

        // else take first and last names
        String first = names[0];
        String last = names[names.length - 1];

        // take the first letter of the first name and capitalise
        char initial = first.charAt(0);
        initial = Character.toUpperCase(initial);

        // return the capitalised initial and last name
        return initial + ". " + capitalise(last);
    }

    /**
     * Get the string between two other strings
     *
     * @param s
     * @param before
     * @param after
     * @return get the string between two other strings
     */
    public static String getStringBetween(String s, String before, String after) {
        String regex = before + "(.+?)" + after;

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    /**
     * Format an enum name to remove any under scores
     *
     * @param name
     * @return an enum name to replacing any under scores with spaces
     */
    public static CharSequence formatEnumName(String name) {
        return name.replaceAll("_", " ");
    }

    /**
     * Get the start and end position of a sub string in a string
     *
     * @param s
     * @param substring
     * @return array with result[0] = start index and result[1] = end index
     */
    public static int[] getStartEndIndices(String s, String substring) {
        int start = 0, end = 0;

        try {
            start = s.indexOf(substring);
            end = start + substring.length();

            if (start == -1 || end == -1) {
                start = 0;
                end = 0;
            }
        } catch (NullPointerException e) {
            return new int[]{0, 0};
        }

        return new int[]{start, end};
    }

    public static String removeWhiteSpacesBeforeAndAfter(String s) {
        return s.replaceAll("^\\s+|\\s+$", "");
    }

    public static String removeAllWhiteSpaceAndNonVisibleChars(String s) {
        return s.replaceAll("\\s+", "");
    }

    /**
     * Format a number to show in a profile.
     * <p/>
     * 0 -         999 = $ X
     * 1000 -     999,999 = $ (X/100) k
     * 1,000,000 - 100,000,000 = $ (X/1,000,000) m
     *
     * @param num
     * @return
     */
    public static String formatNumber(float num) {
        // make sure number is a float
        num = 1f * num;

        String s = "$";

        DecimalFormat myFormatter;

        // if the number is less than 1000, return the number
        if (num < 1000) {
            s += (int) num;

        } else if (num < 1000000) {
            // else if between 1000 and 1 million

            num /= 1000f;

            myFormatter = new DecimalFormat("###.#");
            s += myFormatter.format(num) + "k";
        } else {
            // else if between 1 million and 100 million

            num /= 1000000f;

            myFormatter = new DecimalFormat("###.#");
            s += myFormatter.format(num) + "m";
        }

        return s;
    }

    /**
     * Set the country in the location to bold
     *
     * @param location
     * @return
     */
    public static String setLocationHTML(String location) {
        if (Strings.isEmpty(location)) return "";

        // split on the comma
        String[] words = location.split(",");

        if (words.length <= 1) return location;

        // get the city and country parts
        String city = words[0];
        String country = words[1];

        return city + "," + Strings.setStringTags(country, HTML_TAG.BOLD);
    }

    /**
     * @param s username to check
     * @return if the username contains a character that is not a letter or digit or _
     */
    public static boolean containsIllegalUsernameCharacter(String s) {
        char c;

        // for each character
        for (int i = 0; i < s.length(); i++) {
            // get the next character
            c = s.charAt(i);

            // if the character is not a letter or digit AND not an underscore, the character is illegal
            if (!Character.isLetterOrDigit(c) && c != '_') return true;
        }

        // return false because each character is okay
        return false;
    }

    /**
     * Safely combine a first and last name.
     *
     * @param first
     * @param last
     * @return
     */
    public static String combineNames(String first, String last) {
        // combine the city and country with a comma between the two
        String s = Strings.returnSafely(first);
        if (s.length() > 0) s += " ";
        s += Strings.returnSafely(last);
        return s;
    }

    /**
     * @param originalMessage
     * @param webLink
     * @return if the original message and the web link combined is too long
     */
    public static boolean isTwitterMessageTooLong(String originalMessage, String webLink) {
        int webLinkLen = webLink.length();

        String resultingMessage = Strings.returnSafely(originalMessage);
        int contentMessageLen = resultingMessage.length();

        return contentMessageLen + webLinkLen > 140;
    }

    /**
     * @param originalMessage
     * @param webLink
     * @return the message with the web link to send to Twitter
     */
    public static String getTwitterMessage(String originalMessage, String webLink) {
        // format the users message and the link to cut the users content message
        // so the combination is at most 140 characters with the web link
        int webLinkLen = webLink.length();

        // check the content message is valid
        String resultingMessage = Strings.returnSafely(originalMessage);
        int contentMessageLen = resultingMessage.length();

        // get the message cut off
        int cutOff = contentMessageLen;
        if (cutOff > (140 - webLinkLen)) cutOff = contentMessageLen - webLinkLen;

        // make the resulting string
        resultingMessage = resultingMessage.substring(0, cutOff);
        resultingMessage += webLink;

        return resultingMessage;
    }

    /**
     * @param d
     * @return the double value as a string to 2 decimal places
     */
    public static String to2DecimalPlacesStr(Double d) {
        if (d == null || d == Double.NaN) return "";

        return to2DecimalPlacesStr(d.doubleValue());
    }

    /**
     * @param d
     * @return the double value as a string to 2 decimal places
     */
    public static String to2DecimalPlacesStr(double d) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d);
    }

    /**
     * @param d
     * @return the double value rounded to no decimal places in a string
     */
    public static String toRoundNumberStr(Double d) {
        if (d == null || d == Double.NaN) return "";

        return toRoundNumberStr(d.doubleValue());
    }

    /**
     * @param d
     * @return the double value rounded to no decimal places in a string
     */
    public static String toRoundNumberStr(double d) {
        DecimalFormat df = new DecimalFormat("#");
        return df.format(d);
    }

    /**
     * @param d
     * @return the double value as a string, removing the decimal places if the number is a whole integer
     */
    public static String formatRaised(Double d) {
        if (d == null || d == Double.NaN) return "";

        return formatRaised(d.doubleValue());
    }

    /**
     * @param d
     * @return the double value as a string to 2 decimal places
     */
    public static String formatRaised(double d) {
        if (MathUtils.isWholeNumber(d)) {
            return toRoundNumberStr(d);

        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        }
    }

    public static String priceWithDecimal(Double price) {
        if (price == null) return "0.00";

        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    public static String priceWithoutDecimal(Double price) {
        if (price == null) return "0";

        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String priceToString(Double price) {
        if (price == null) return "0";

        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(".") > 0) {
            return priceWithDecimal(price);
        } else {
            return priceWithoutDecimal(price);
        }
    }

    public static double parseDouble(String s) {
        if (Strings.isEmpty(s)) return 0d;

        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return 0d;
        }
    }

    public static int parseInteger(String s) {
        if (Strings.isEmpty(s)) return 0;

        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param raised
     * @param cost
     * @return the fraction correctly formatted
     */
    public static String formatFraction(double raised, double cost) {
        return Strings.formatRaised(raised) + "/" + Strings.toRoundNumberStr(cost);
    }

    /**
     * @param text
     * @return the URLs contained in the text
     */
    public static ArrayList<String> getLinksInText(String text) {
        ArrayList<String> links = new ArrayList<>();

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            //char[] stringArray1 = urlStr.toCharArray();

            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                char[] stringArray = urlStr.toCharArray();
                char[] newArray = new char[stringArray.length - 2];
                System.arraycopy(stringArray, 1, newArray, 0, stringArray.length - 2);
                urlStr = new String(newArray);
            }

            links.add(urlStr);
        }

        return links;
    }

    /**
     * @param bis
     * @return convert a BufferedInputStream to a string
     */
    public static String readBufferedInputStream(InputStream bis) {
        if (bis == null) return "";

        String strFileContents = "";

        try {
            byte[] contents = new byte[2048];

            int bytesRead = 0;
            while ((bytesRead = bis.read(contents)) != -1) {
                strFileContents += new String(contents, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return strFileContents;
    }

    /**
     * @param url
     * @return make sure the URL has http:// in front
     */
    public static String checkUrl(String url) {
        if (Strings.isEmpty(url)) return "";

        if (!url.startsWith("https://") && !url.startsWith("http://")) url = "http://" + url;

        return url;
    }

    /**
     * @param url
     * @return make sure the URL has http:// in front
     */
    public static String checkUrlSecure(String url) {
        if (Strings.isEmpty(url)) return "";

        if (!url.startsWith("https://")) url = "https://" + url;

        return url;
    }

    /**
     * @param s
     * @return the extract href link
     */
    public static String extractHref(String s) {
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group(1); // this variable should contain the link URL
        }

        return "";
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String coolFormat(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return coolFormat(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + coolFormat(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    /**
     * @param s
     * @return if the text contains letters
     */
    public static boolean containsLetters(String s) {
        return isEmpty(s) ? false : s.contains("[a-zA-Z]+");
    }

    /**
     * @param s
     * @return if text contains any numbers
     */
    public static boolean containsNumbers(String s) {
        return isEmpty(s) ? false : s.contains("[0-9]+");
    }

    /**
     * @param is
     * @return get the string from an input stream
     */
    public static String getInputReaderInput(InputStreamReader is) {
        if (is == null) return "";

        BufferedReader in = new BufferedReader(is);
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = in.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (Exception e1) {
        }

        return sb.toString();
    }

    /**
     * @param email
     * @return if the email is of a valid format
     */
    public static boolean isEmailValid(String email) {
        return !isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String flattenList(List<String> list, String delimiter) {
        if (list == null || list.size() == 0) return "";

        delimiter = Strings.returnSafely(delimiter);
        String s = "";
        for (int i = 0; i < list.size(); i++) {
            if (Strings.isEmpty(list.get(i))) continue;

            if (!Strings.isEmpty(s)) s += delimiter;

            s += list.get(i);
        }

        if (s.endsWith(delimiter)) s = s.substring(0, s.length() - delimiter.length());
        return s;
    }

}
