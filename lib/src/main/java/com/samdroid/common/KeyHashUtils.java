package com.samdroid.common;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by samuel on 9/24/2015.
 */
public class KeyHashUtils {

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            MyLog.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                MyLog.e("Key Hash: ", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            MyLog.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            MyLog.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            MyLog.e("Exception", e.toString());
        }

        return key;
    }

}
