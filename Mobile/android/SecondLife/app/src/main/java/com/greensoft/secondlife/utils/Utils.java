package com.greensoft.secondlife.utils;

import static com.greensoft.secondlife.AppConst.SECOND_LIFE_URL;

/**
 * Created by zebul on 7/8/17.
 */

public class Utils {

    public static String formatAPIUrl(String route){

        return SECOND_LIFE_URL+"/api/"+route;
    }
}
