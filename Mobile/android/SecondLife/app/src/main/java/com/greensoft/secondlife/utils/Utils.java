package com.greensoft.secondlife.utils;

import android.os.Build;

import static com.greensoft.secondlife.AppConst.SECOND_LIFE_URL;

/**
 * Created by zebul on 7/8/17.
 */

public class Utils {

    public static String formatAPIUrl(String route){

        return SECOND_LIFE_URL+"/api/"+route;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
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

}
