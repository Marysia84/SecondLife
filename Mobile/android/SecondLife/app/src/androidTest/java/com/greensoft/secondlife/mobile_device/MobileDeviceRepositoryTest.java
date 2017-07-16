package com.greensoft.secondlife.mobile_device;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by zebul on 7/12/17.
 */

@RunWith(AndroidJUnit4.class)
public class MobileDeviceRepositoryTest {

    @Test
    public void test_when_mobileDevice_is_saved_then_load_returns_equal_mobileDevice() {

        try {

            Context appContext = InstrumentationRegistry.getTargetContext();
            MobileDeviceRepository mobileDeviceRepository =
                    new MobileDeviceRepository(appContext, "mobile_device_test.bin");

            MobileDevice mobileDeviceSaved = new MobileDevice("id1", "foo", "samsung GT");
            mobileDeviceRepository.save(mobileDeviceSaved);
            MobileDevice mobileDeviceLoaded = mobileDeviceRepository.load();
            assertEquals(mobileDeviceSaved, mobileDeviceLoaded);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
