package com.greensoft.secondlife.mobile_device.registration;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greensoft.secondlife.mobile_device.MobileDevice;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.utils.Utils;
import com.greensoft.secondlife.volley.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.greensoft.secondlife.AppConst.VOLEY_SOCKET_TIMEOUT_MS;

/**
 * Created by zebul on 7/12/17.
 */

public class MobileDeviceRegistrar{

    private Context context;

    public interface RegistarBuilder extends Serializable {

        MobileDeviceRegistrar build();
    }

    public MobileDeviceRegistrar(Context context) {
        this.context = context;
    }

    public void register(
            User user,
            MobileDevice mobileDevice,
            MobileDeviceRegistrationResultListener mobileDeviceRegistrationResultListener) {

        try {
            doRegister(user, mobileDevice, mobileDeviceRegistrationResultListener);
        } catch (JSONException e) {
            mobileDeviceRegistrationResultListener.onMobileDeviceRegistrationFailure(new MobileDeviceRegistrationException(e));
        }
    }

    private void doRegister(
            final User user,
            final MobileDevice mobileDevice,
            final MobileDeviceRegistrationResultListener mobileDeviceRegistrationResultListener)
            throws JSONException {

        JSONObject userJSON = new JSONObject();
        userJSON.put("id", mobileDevice.Id);
        userJSON.put("name", mobileDevice.Name);
        userJSON.put("modelName", mobileDevice.ModelName);

        final String url = Utils.formatAPIUrl("registerdevice", user.Id);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, userJSON, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mobileDeviceRegistrationResultListener.
                                onMobileDeviceRegistrationSuccess(mobileDevice);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MobileDeviceRegistrationException exc = new MobileDeviceRegistrationException(error);
                        mobileDeviceRegistrationResultListener.
                                onMobileDeviceRegistrationFailure(exc);
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
