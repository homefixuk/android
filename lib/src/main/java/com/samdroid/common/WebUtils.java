package com.samdroid.common;

import com.samdroid.string.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by samuel on 3/7/2016.
 */
public class WebUtils {

    public static String getWebPageContents(String url) {
        String s = "";

        URLConnection con = null;
        BufferedReader in = null;
        try {
            URL requestUrl = new URL(url);
            con = requestUrl.openConnection();
            s = Strings.getInputReaderInput(new InputStreamReader(con.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return s;
    }

}
