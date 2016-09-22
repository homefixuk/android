package com.samdroid.string;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 9/22/2016.
 */

public class ToCommaParameters {

    public interface ToCommaParametersCallback {

        void onToCommaParametersCalled(String... s);

    }

    public static void run(@NonNull ToCommaParametersCallback callback, List<String> workAreas) {
        if (workAreas == null) workAreas = new ArrayList<>();

        switch (workAreas.size()) {

            case 1:
                callback.onToCommaParametersCalled(workAreas.get(0));
                break;

            case 2:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1));
                break;

            case 3:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2));
                break;

            case 4:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3));
                break;

            case 5:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4));
                break;

            case 6:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5));
                break;

            case 7:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6));
                break;

            case 8:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7));
                break;

            case 9:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8));
                break;

            case 10:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9));
                break;

            case 11:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10));
                break;

            case 12:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11));
                break;

            case 13:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12));
                break;

            case 14:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12), workAreas.get(13));
                break;

            case 15:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12), workAreas.get(13), workAreas.get(14));
                break;

            case 16:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12), workAreas.get(13), workAreas.get(14), workAreas.get(15));
                break;

            case 17:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12), workAreas.get(13), workAreas.get(14), workAreas.get(15), workAreas.get(16));
                break;

            case 18:
                callback.onToCommaParametersCalled(workAreas.get(0), workAreas.get(1), workAreas.get(2), workAreas.get(3), workAreas.get(4), workAreas.get(5), workAreas.get(6), workAreas.get(7), workAreas.get(8), workAreas.get(9), workAreas.get(10), workAreas.get(11), workAreas.get(12), workAreas.get(13), workAreas.get(14), workAreas.get(15), workAreas.get(16), workAreas.get(17));
                break;

            default:
            case 0:
                callback.onToCommaParametersCalled();
                break;
        }

    }

}
