package com.greensoft.secondlife.user;

import android.content.Context;
import android.text.TextUtils;

import com.greensoft.secondlife.R;

/**
 * Created by zebul on 7/9/17.
 */

public class UserValidator {

    public static boolean isFirstNameValid(String firstName){

        return !TextUtils.isEmpty(firstName);
    }

    public static String formatFirstNameErrorText(Context context) {

        return context.getString(R.string.error_userFirstName);
    }

    public static boolean isLastNameValid(String lastName) {

        return !TextUtils.isEmpty(lastName);
    }
    
    public static String formatLastNameErrorText(Context context) {

        return context.getString(R.string.error_userLastName);
    }

    public static boolean isEmailValid(String email) {

        if(TextUtils.isEmpty(email)){
            return false;
        }
        return true;
    }

    public static String formatEmailErrorText(Context context) {

        return context.getString(R.string.error_userEmail);
    }

    public static boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password);
    }

    public static String formatPasswordErrorText(Context context) {

        return context.getString(R.string.error_userPassword);
    }

    public static boolean arePasswordsEqual(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public static String formatPasswordsEqualErrorText(Context context) {
        return context.getString(R.string.error_userPasswordConfirmation);
    }
}
