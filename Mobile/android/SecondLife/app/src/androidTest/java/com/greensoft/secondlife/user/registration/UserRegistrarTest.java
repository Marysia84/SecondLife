package com.greensoft.secondlife.user.registration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.user.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by zebul on 7/8/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserRegistrarTest {

    @Test
    public void test1() {

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();

        final AtomicBoolean success = new AtomicBoolean(false);
        final AtomicReference error = new AtomicReference();
        UserRegistrationResultListener userRegistrationResultListener = new UserRegistrationResultListener() {
            @Override
            public void onUserRegistrationSuccess(User user) {
                success.set(true);
                notifyAll();
            }

            @Override
            public void onUserRegistrationFailure(UserRegistrationException exc) {
                fail(exc.getMessage());
                notifyAll();
            }
        };

        UserRegistrar userRegistrar = new UserRegistrar(targetContext);
        User user = new User("foo", "bar", "foo@bar12.com", "foofoo");
        userRegistrar.register(user, userRegistrationResultListener);

        synchronized (userRegistrationResultListener){

            try {
                userRegistrationResultListener.wait();
                assertTrue(success.get());
            } catch (InterruptedException exc) {
                fail(exc.getMessage());
            }
        }
    }
}