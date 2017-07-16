package com.greensoft.secondlife.mobile_device.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.mobile_device.MobileDevice;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.registration.UserRegisterActivity;
import com.greensoft.secondlife.user.registration.UserRegistrar;
import com.greensoft.secondlife.user.registration.UserRegistrationResultListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.greensoft.secondlife.user.registration.UserRegisterActivityTest.getString;

/**
 * Created by zebul on 6/24/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MobileDeviceRegisterActivityTest {


    @Rule
    public ActivityTestRule<MobileDeviceRegisterActivity> activityRule = new ActivityTestRule<>(
            MobileDeviceRegisterActivity.class, true, false);


    public static class MobileDeviceRegistrarFake extends MobileDeviceRegistrar {

        public MobileDeviceRegistrarFake(){

            super(null);
        }

        @Override
        public void register(
                MobileDevice mobileDevice,
                MobileDeviceRegistrationResultListener mobileDeviceRegistrationResultListener){

        }
    }

    private void setUpDefaults(){

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, MobileDeviceRegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConst.KEY_MOBILE_DEVICE_REGISTRAR_BUILDER, new RegistarBuilderFake());
        intent.putExtras(bundle);
        activityRule.launchActivity(intent);
    }

    public static class RegistarBuilderFake implements MobileDeviceRegistrar.RegistarBuilder{

        @Override
        public MobileDeviceRegistrar build() {
            return new MobileDeviceRegistrarFake();
        }
    }

    @Test
    public void test_when_firstNameEditText_is_empty_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.nameEditText))
                .check(matches(hasErrorText(getString(R.string.error_mobileDeviceName))));
    }


}
