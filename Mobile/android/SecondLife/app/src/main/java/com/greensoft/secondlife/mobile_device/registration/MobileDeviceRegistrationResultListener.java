package com.greensoft.secondlife.mobile_device.registration;

import com.greensoft.secondlife.mobile_device.MobileDevice;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.registration.UserRegistrationException;

/**
 * Created by zebul on 7/9/17.
 */

public interface MobileDeviceRegistrationResultListener {

    void onMobileDeviceRegistrationSuccess(MobileDevice mobileDevice);
    void onMobileDeviceRegistrationFailure(MobileDeviceRegistrationException exc);
}
