package com.homefix.tradesman.base.activity.pdf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.samdroid.common.IntentHelper;
import com.samdroid.string.Strings;

import java.io.File;

/**
 * Created by samuel on 9/6/2016.
 */

public class PdfViewActivity extends BaseCloseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String uri = IntentHelper.getStringSafely(i, "uri");
        String filePath = IntentHelper.getStringSafely(i, "filePath");

        File file = null;
        if (!Strings.isEmpty(filePath)) file = new File(filePath);

        baseFragment = PdfViewFragment.getInstance(uri, file, null);
        replaceFragment(baseFragment);
    }
}
