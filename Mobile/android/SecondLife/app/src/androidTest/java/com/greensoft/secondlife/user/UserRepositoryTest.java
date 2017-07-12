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
    public void test12332() {

        try {

            Context appContext = InstrumentationRegistry.getTargetContext();
            UserRepository userRepository = new UserRepository(appContext);

            User userSaved = new User("foo", "bar", "foo@bar.com", "foofoo");
            userRepository.save(userSaved);
            User userLoaded = userRepository.load();
            assertEquals(userSaved, userLoaded);
            /*
            JSONObject jsonObject2 = new JSONObject("{\"name\":\"mkyong.com\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":100}");
            final Object name = jsonObject2.get("name");

            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("version", "1");
            jsonObject0.put("firstName", "john");
            jsonObject0.put("lastName", "smith");
            jsonObject0.put("email", "john.smith@gmail.com");
            final String stringifiedJSONObject = jsonObject0.toString();

            JSONObject jsonObject1 = new JSONObject(stringifiedJSONObject);
            final Object firstName = jsonObject1.get("firstName");
            assertEquals("john", firstName);
            */
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }


}
