package com.samdroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.samdroid.math.Vector;

/**
 * Class used to get information about the device's screen,
 * such as size and density.
 *
 * @author Sam Koch
 */
public class ScreenUtils {

    /**
     * Screen density enumerator for low, medium, high and extra high
     *
     * @author Sam Koch
     */
    public enum ScreenDensity {
        LOW, MEDIUM, HIGH, EXTRA_HIGH, EXTRA_EXTRA_HIGH
    }

    /**
     * Get the screen density for the device
     *
     * @param activity
     * @return the screen density
     */
    public static float getScreenDensity(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return (((Context) activity).getResources().getDisplayMetrics().density * 160f);
    }

    /**
     * Get the screen dimensions in a vector, x = width, y = height
     *
     * @param activity
     * @return screen dimensions in display pixels (dip), Width index 0, Height index 1
     */
    public static Vector getScreenDimensions(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Vector v = new Vector();
        v.set(outMetrics.widthPixels, outMetrics.heightPixels);
        return v;
    }

    /**
     * Get the screen dimensions in a Point, x = width, y = height
     *
     * @param activity
     * @return screen dimensions in display pixels (dip), Width index 0, Height index 1
     */
    public static Point getScreenSizePixels(Activity activity) {
        if (activity == null) return new Point(0, 0);

        return getScreenSizePixels(activity, activity.getWindowManager());
    }

    /**
     * Get the screen dimensions in a vector, x = width, y = height
     *
     * @param context
     * @param windowManager
     * @return screen dimensions in display pixels (dip), Width index 0, Height index 1
     */
    public static Point getScreenSizePixels(Context context, WindowManager windowManager) {
        if (context == null || windowManager == null) return new Point(0, 0);

        int widthScreen, heightScreen;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            widthScreen = metrics.widthPixels;
            heightScreen = metrics.heightPixels;

        } else {
            Point displaySize = new Point();
            windowManager.getDefaultDisplay().getRealSize(displaySize);
            widthScreen = displaySize.x;
            heightScreen = displaySize.y;
        }

        return new Point(widthScreen, heightScreen);
    }

    /**
     * Get the screen density type
     *
     * @param activity
     * @return the screen density type {low, medium, high, xhigh, xxhigh}
     */
    public static ScreenDensity getScreenDensityType(Activity activity) {
        float density = getScreenDensity(activity);

        if (density < 140)
            return ScreenDensity.LOW;
        else if (density < 180)
            return ScreenDensity.MEDIUM;
        else if (density < 260)
            return ScreenDensity.HIGH;
        else if (density < 340)
            return ScreenDensity.EXTRA_HIGH;
        else return ScreenDensity.EXTRA_EXTRA_HIGH;
    }

    /**
     * Get the screen size type
     *
     * @param context
     * @return the screen size integer
     */
    public static int getScreenSize(Context context) {
        return context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    /**
     * @param activity
     * @return the screen density in dpi
     */
    public static int getScreenDensityDpi(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    /**
     * @param context
     * @param dp
     * @return convert DP to pixels
     */
    public static float dpToPixels(@NonNull Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density + 0.5f;
    }

    /**
     * @param activity
     * @return the size of the screen in pixels
     */
    public static Pair<Integer, Integer> getDeviceSizeInPixels(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }

        return new Pair<>(mWidthPixels, mHeightPixels);
    }

    /**
     * @param activity
     * @return the size of the screen in inches (diagonally)
     */
    public static double getScreenSizeInInches(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        Pair<Integer, Integer> dimens = getDeviceSizeInPixels(activity);
        double x = Math.pow(dimens.first / dm.xdpi, 2);
        double y = Math.pow(dimens.second / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

    /**
     * @param context
     * @return the notification action icon size in pixels
     */
    public static Point getNotificationActionDimens(Context context) {
        Point p;

        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                p = new Point(24, 24);
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                p = new Point(36, 36);
                break;

            case DisplayMetrics.DENSITY_HIGH:
                p = new Point(48, 48);
                break;

            case DisplayMetrics.DENSITY_XHIGH:
                p = new Point(72, 72);
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
                p = new Point(96, 96);
                break;

            default:
                p = new Point(40, 40);
                break;
        }

        return p;
    }

    /**
     * @param context
     * @return if the device is on and being used
     */
    public static boolean isDeviceOnAndBeingUsed(Context context) {
        if (context == null) return false;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /**
     * @param activity
     * @return the height of the status bar
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
