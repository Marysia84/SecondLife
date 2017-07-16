package com.greensoft.secondlife.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zebul on 7/12/17.
 */

public abstract class VolleyException extends Exception{

    public VolleyException(JSONException cause_){

        super(cause_);
    }

    public VolleyException(VolleyError cause_){

        super(cause_);
    }

    protected NetworkResponse getNetworkResponse(){

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

    protected boolean resourceConflict(){

        NetworkResponse networkResponse = getNetworkResponse();
        if(networkResponse == null){
            return false;
        }
        return networkResponse.statusCode == HttpsURLConnection.HTTP_CONFLICT;//  409;
    }
}
