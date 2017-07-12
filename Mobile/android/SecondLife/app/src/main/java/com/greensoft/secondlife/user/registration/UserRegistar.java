package com.greensoft.secondlife.user.registration;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.utils.Utils;
import com.greensoft.secondlife.voley.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zebul on 6/24/17.
 */

public class UserRegistar {

    private Context context;
    public UserRegistar(Context context_){
        context = context_;
    }

    public interface RegistarBuilder extends Serializable{

        UserRegistar build();
    }

    public void start(){

    }

    public void stop(){

    }

    public void register(User user, UserRegistrationResultListener userRegistrationResultListener){

        try {
            doRegister(user, userRegistrationResultListener);
        } catch (JSONException e) {
            userRegistrationResultListener.onUserRegistrationFailure(new UserRegistrationException(e));
        }
    }

    private void doRegister(final User user, final UserRegistrationResultListener userRegistrationResultListener)
            throws JSONException {

        JSONObject userJSON = new JSONObject();
        userJSON.put("firstName", user.FirstName);
        userJSON.put("lastName", user.LastName);
        userJSON.put("email", user.Email);
        userJSON.put("password", user.Password);

        final String url = Utils.formatAPIUrl("register");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, userJSON, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        userRegistrationResultListener.onUserRegistrationSuccess(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        userRegistrationResultListener.onUserRegistrationFailure(new UserRegistrationException(error));
                    }
                });

        final int SOCKET_TIMEOUT_MS = 10000;
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through your singleton class.
        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }


}
