package com.greensoft.secondlife.mobile_device.registration;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.greensoft.secondlife.volley.VolleyException;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zebul on 6/24/17.
 */

public class MobileDeviceRegistrationException extends VolleyException {

    public MobileDeviceRegistrationException(JSONException cause_){

        super(cause_);
    }

    public MobileDeviceRegistrationException(VolleyError cause_){

        super(cause_);
    }


    public boolean mobileDeviceAlreadyExists(){

        return resourceConflict();
    }
}
