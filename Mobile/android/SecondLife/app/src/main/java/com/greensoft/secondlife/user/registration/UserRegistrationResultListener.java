package com.greensoft.secondlife.user.registration;

import com.greensoft.secondlife.user.User;

/**
 * Created by zebul on 7/9/17.
 */

public interface UserRegistrationResultListener {

    void onUserRegistrationSuccess(User user);
    void onUserRegistrationFailure(UserRegistrationException exc);
}
