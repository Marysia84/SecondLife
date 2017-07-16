package com.greensoft.secondlife.user.login;

import com.android.volley.VolleyError;
import com.greensoft.secondlife.volley.VolleyException;

import org.json.JSONException;

/**
 * Created by zebul on 6/24/17.
 */

public class UserLoginException extends VolleyException {

    public UserLoginException(JSONException cause_){

        super(cause_);
    }

    public UserLoginException(VolleyError cause_){

        super(cause_);
    }
}
