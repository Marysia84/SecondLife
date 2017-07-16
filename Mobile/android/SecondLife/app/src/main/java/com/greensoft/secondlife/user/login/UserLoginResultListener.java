package com.greensoft.secondlife.user.login;

import com.greensoft.secondlife.user.Credentials;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.registration.UserRegistrationException;

/**
 * Created by zebul on 7/9/17.
 */

public interface UserLoginResultListener {

    void onUserLoginSuccess(Credentials credentials);
    void onUserLoginFailure(UserLoginException exc);
}
