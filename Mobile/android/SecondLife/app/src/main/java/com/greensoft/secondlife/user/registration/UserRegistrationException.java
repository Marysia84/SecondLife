package com.greensoft.secondlife.user.registration;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.greensoft.secondlife.volley.VolleyException;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zebul on 6/24/17.
 */

public class UserRegistrationException extends VolleyException {

    public UserRegistrationException(JSONException cause_){

        super(cause_);
    }

    public UserRegistrationException(VolleyError cause_){

        super(cause_);
    }

    public boolean userAlreadyExists(){

        return resourceConflict();
    }
}
