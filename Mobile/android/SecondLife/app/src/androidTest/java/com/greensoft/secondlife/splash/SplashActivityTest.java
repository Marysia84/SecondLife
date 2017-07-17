package com.greensoft.secondlife.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.mobile_device.registration.MobileDeviceRegisterActivity;
import com.greensoft.secondlife.mobile_device.registration.MobileDeviceRegisterActivityTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.greensoft.secondlife.user.registration.UserRegisterActivityTest.getString;

/**
 * Created by zebul on 7/16/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> activityRule = new ActivityTestRule<>(
            SplashActivity.class, true, false);


    private void setUpDefaults(){

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, SplashActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable(AppConst.KEY_MOBILE_DEVICE_REGISTRAR_BUILDER, new MobileDeviceRegisterActivityTest.RegistarBuilderFake());
        intent.putExtras(bundle);
        activityRule.launchActivity(intent);
    }

    @Test
    public void test1() {

        setUpDefaults();

        onView(withId(R.id.splashProgressBar))
                .check(matches(isDisplayed()));
    }

}
