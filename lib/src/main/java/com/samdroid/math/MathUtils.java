package com.samdroid.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class MathUtils {

    /**
     * Get the progress value to use when setting the progress bar progress
     *
     * @param current        the current value for the progress, aiming to reach the 'target'
     * @param target         the amount aiming to raise
     * @param progressBarMax the max value of the progress bar view
     * @return the progress value to use when setting the progress bar progress
     */
    public static int getProgress(float current, float target, int progressBarMax) {
        return (int) (((current * 1f) / (target * 1f)) * (progressBarMax * 1f));
    }

    /**
     * Get a percentage from a fraction
     *
     * @param value
     * @param max
     * @return
     */
    public static int getPercentage(float value, float max) {
        float percent = value / max;
        percent *= 100;

        return (int) (percent);
    }

    /**
     * Get a percentage from a fraction
     *
     * @param value
     * @param max
     * @return
     */
    public static int getPercentage(double value, double max) {
        double percent = value / max;
        percent *= 100;

        return (int) (percent);
    }

    /**
     * @param n
     * @return if a double is a whole number
     */
    public static boolean isWholeNumber(double n) {
        return n % 1 == 0;
    }

    /**
     * @param num
     * @param x
     * @return the number to x decimal places
     */
    public static double roundToXDecimals(double num, int x) {
        String s = "#";

        if (x > 0) {
            s += ".";
            for (int i = 0, len = x; i < len; i++) s += "#";
        }
        DecimalFormat xDForm = new DecimalFormat(s);
        return Double.valueOf(xDForm.format(num));
    }

    /**
     * @param amount
     * @param locale
     * @return the value of a currency from a string when you know the Locale
     * @throws ParseException
     */
    public static BigDecimal parseCurrency(final String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
    }

    public static int sumIndexes(List l) {
        if (l == null) return 0;

        int count = 0;
        for (int i = 0; i < l.size(); i++) {
            count += i;
        }

        return count;
    }

}
