package com.homefix.tradesman.common;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by samuel on 8/22/2016.
 */

public class HtmlHelper {

    /**
     * Handle deprecated Html.fromHtml call
     * @param text
     *
     * @return
     */
    public static Spanned fromHtml(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);

        //noinspection deprecation
        return Html.fromHtml(text);
    }

}
