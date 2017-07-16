package com.greensoft.secondlife.user;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by zebul on 7/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {

    @Test
    public void test_when_user_is_saved_then_load_returns_equal_user() {

        try {

            Context appContext = InstrumentationRegistry.getTargetContext();
            UserRepository userRepository = new UserRepository(appContext, "user_test.bin");

            User userSaved = new User("foo", "bar", "foo@bar.com", "foofoo");
            userRepository.save(userSaved);
            User userLoaded = userRepository.load();
            assertEquals(userSaved, userLoaded);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }


}
