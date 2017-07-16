package com.greensoft.secondlife.user.login;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greensoft.secondlife.user.Credentials;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.utils.Utils;
import com.greensoft.secondlife.volley.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.greensoft.secondlife.AppConst.VOLEY_SOCKET_TIMEOUT_MS;

/**
 * Created by zebul on 7/16/17.
 */

public class UserLogin {

    private Context context;
    public UserLogin(Context context_){
        context = context_;
    }

    public interface UserLoginBuilder extends Serializable {

        UserLogin build();
    }

    public void login(Credentials credentials, UserLoginResultListener userLoginResultListener) {

        try {
            doLogin(credentials, userLoginResultListener);
        } catch (JSONException e) {
            userLoginResultListener.onUserLoginFailure(new UserLoginException(e));
        }
    }

    private void doLogin(final Credentials credentials, final UserLoginResultListener userLoginResultListener)
            throws JSONException {

        JSONObject userJSON = new JSONObject();
        userJSON.put("email", credentials.Email);
        userJSON.put("password", credentials.Password);

        final String url = Utils.formatAPIUrl("login");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, userJSON, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        userLoginResultListener.onUserLoginSuccess(credentials);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        userLoginResultListener.onUserLoginFailure(new UserLoginException(error));
                    }
                });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                VOLEY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through your singleton class.
        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

}
