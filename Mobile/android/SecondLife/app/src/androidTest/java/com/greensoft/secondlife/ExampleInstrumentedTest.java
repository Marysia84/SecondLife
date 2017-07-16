package com.greensoft.secondlife;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.greensoft.secondlife", appContext.getPackageName());
    }


    @Test
    public void test1() {

        try {
            JSONObject jsonObject2 = new JSONObject("{\"Name\":\"mkyong.com\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":100}");
            final Object name = jsonObject2.get("Name");

            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("version", "1");
            jsonObject0.put("firstName", "john");
            jsonObject0.put("lastName", "smith");
            jsonObject0.put("Email", "john.smith@gmail.com");
            final String stringifiedJSONObject = jsonObject0.toString();

            JSONObject jsonObject1 = new JSONObject(stringifiedJSONObject);
            final Object firstName = jsonObject1.get("firstName");
            assertEquals("john", firstName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
