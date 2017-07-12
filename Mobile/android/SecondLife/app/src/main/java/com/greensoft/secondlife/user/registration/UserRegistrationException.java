package com.greensoft.secondlife.user.registration;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zebul on 6/24/17.
 */

public class UserRegistrationException extends Exception{

    public UserRegistrationException(JSONException cause_){

        super(cause_);
    }

    public UserRegistrationException(VolleyError cause_){

        super(cause_);
    }

    private NetworkResponse getNetworkResponse(){

        final Throwable cause = getCause();
        if(cause == null){
            return null;
        }

        if(!VolleyError.class.isInstance(cause)){
            return null;
        }

        VolleyError volleyError = (VolleyError)cause;
        return volleyError.networkResponse;
    }

    public boolean hasHTTPResponseCode(){

        NetworkResponse networkResponse = getNetworkResponse();
        if(networkResponse == null){
            return false;
        }
        return true;
    }

    public boolean userAlreadyExists(){

        NetworkResponse networkResponse = getNetworkResponse();
        if(networkResponse == null){
            return false;
        }
        return networkResponse.statusCode == HttpsURLConnection.HTTP_CONFLICT;//  409;
    }
}
