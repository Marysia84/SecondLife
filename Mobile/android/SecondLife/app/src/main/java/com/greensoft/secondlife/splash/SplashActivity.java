package com.greensoft.secondlife.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.greensoft.secondlife.R;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.UserRepository;
import com.greensoft.secondlife.user.login.UserLoginActivity;
import com.greensoft.secondlife.user.registration.UserRegisterActivity;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity
implements Runnable{

    private Handler switchActivityHandler = new Handler();
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = loadUser();
        switchActivityHandler.postDelayed(this, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        switchActivityHandler.removeCallbacks(this);
    }

    private User loadUser() {
        UserRepository userRepository = new UserRepository(this);
        try {
            return userRepository.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        switchActivity();
    }

    private void switchActivity() {

        if(user == null){

            Intent intent = new Intent(this, UserRegisterActivity.class);
            startActivity(intent);
        }
        else{

            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
