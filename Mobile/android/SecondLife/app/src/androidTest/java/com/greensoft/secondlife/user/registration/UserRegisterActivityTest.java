package com.greensoft.secondlife.user.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.user.User;

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

/**
 * Created by zebul on 6/24/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserRegisterActivityTest {

    public static String getString(int resourceId){

        return InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getString(resourceId);
    }

    @Rule
    public ActivityTestRule<UserRegisterActivity> activityRule = new ActivityTestRule<>(
            UserRegisterActivity.class, true, false);


    public static class UserRegistrarFake extends UserRegistrar {

        public UserRegistrarFake(){

            super(null);
        }

        @Override
        public void register(User user, UserRegistrationResultListener userRegistrationResultListener){

        }
    }

    private void setUpDefaults(){

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, UserRegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConst.KEY_USER_REGISTRAR_BUILDER, new RegistarBuilderFake());
        intent.putExtras(bundle);
        activityRule.launchActivity(intent);
    }

    public static class RegistarBuilderFake implements UserRegistrar.RegistarBuilder{

        @Override
        public UserRegistrar build() {
            return new UserRegistrarFake();
        }
    }

    @Test
    public void test_when_firstNameEditText_is_empty_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.firstNameEditText))
                .check(matches(hasErrorText(getString(R.string.error_userFirstName))));
    }

    @Test
    public void test_when_lastNameEditText_is_empty_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        onView(withId(R.id.firstNameEditText))
                .perform(typeText("foo"), closeSoftKeyboard());

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.lastNameEditText))
                .check(matches(hasErrorText(getString(R.string.error_userLastName))));
    }

    @Test
    public void test_when_emailEditText_is_empty_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        onView(withId(R.id.firstNameEditText))
                .perform(typeText("foo"), closeSoftKeyboard());

        onView(withId(R.id.lastNameEditText))
                .perform(typeText("bar"), closeSoftKeyboard());

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.emailEditText))
                .check(matches(hasErrorText(getString(R.string.error_userEmail))));
    }

    @Test
    public void test_when_passwordEditText_has_not_required_format_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        onView(withId(R.id.firstNameEditText))
                .perform(typeText("foo"), closeSoftKeyboard());

        onView(withId(R.id.lastNameEditText))
                .perform(typeText("bar"), closeSoftKeyboard());

        onView(withId(R.id.emailEditText))
                .perform(typeText("foo@bar.com"), closeSoftKeyboard());

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.passwordEditText))
                .check(matches(hasErrorText(getString(R.string.error_userPassword))));
    }

    @Test
    public void test_when_confirmPasswordEditText_differs_from_PasswordEditText_then_after_registerButton_click_it_has_expected_error() {

        setUpDefaults();

        onView(withId(R.id.firstNameEditText))
                .perform(typeText("foo"), closeSoftKeyboard());

        onView(withId(R.id.lastNameEditText))
                .perform(typeText("bar"), closeSoftKeyboard());

        onView(withId(R.id.emailEditText))
                .perform(typeText("foo@bar.com"), closeSoftKeyboard());

        onView(withId(R.id.passwordEditText))
                .perform(typeText("FooBar123"), closeSoftKeyboard());

        //when
        onView(withId(R.id.registerButton)).perform(click());

        //then
        onView(withId(R.id.confirmPasswordEditText))
                .check(matches(hasErrorText(getString(R.string.error_userPasswordConfirmation))));
    }
}
