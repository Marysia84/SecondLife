package com.greensoft.secondlife.user.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.user.registration.UserRegisterActivityTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by zebul on 7/10/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserLoginActivityTest {

    @Rule
    public ActivityTestRule<UserLoginActivity> activityRule = new ActivityTestRule<>(
            UserLoginActivity.class, true, false);

    private void setUpDefaults(){

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, UserLoginActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable(AppConst.KEY_USER_REGISTRAR_BUILDER, new RegistarBuilderFake());
        intent.putExtras(bundle);
        activityRule.launchActivity(intent);
    }

    @Test
    public void test_when1() {

        setUpDefaults();

        //when
        onView(withId(R.id.loginButton)).perform(click());

        //then
        onView(withId(R.id.emailEditText))
                .check(matches(hasErrorText(UserRegisterActivityTest.getString(R.string.error_userFirstName))));
    }
}
