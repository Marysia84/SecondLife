package com.greensoft.secondlife.user.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.user.Credentials;
import com.greensoft.secondlife.user.UserValidator;
import com.greensoft.secondlife.user.registration.UserRegistrar;


public class UserLoginActivity extends AppCompatActivity
implements UserLoginResultListener{

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private UserLogin userLogin;

    class CredentialsCreationException extends Exception{

        private EditText editText;
        public CredentialsCreationException(EditText editText, String errorMessage) {
            super(errorMessage);
            this.editText = editText;
        }

        public void showError(){

            editText.setError(getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        setUpControls();
        userLogin = buildUserLogin();
    }

    private void setUpControls() {

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {

        try {
            final Credentials credentials = createCredentials();
            userLogin.login(credentials, this);
        } catch (CredentialsCreationException exc) {
            exc.showError();
        }
    }

    private Credentials createCredentials() throws CredentialsCreationException {
        String email = emailEditText.getText().toString();
        if(!UserValidator.isEmailValid(email)){
            throw new CredentialsCreationException(emailEditText, UserValidator.formatEmailErrorText(this));
        }

        String password = passwordEditText.getText().toString();
        if(!UserValidator.isPasswordValid(password)){
            throw new CredentialsCreationException(passwordEditText, UserValidator.formatPasswordErrorText(this));
        }
        return new Credentials(email, password);
    }

    @Override
    public void onUserLoginSuccess(Credentials credentials) {


    }

    @Override
    public void onUserLoginFailure(final UserLoginException exc) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(UserLoginActivity.this, exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private UserLogin buildUserLogin() {

        final Intent intent = getIntent();
        if(intent == null){
            return createDefaultUserLogin();
        }
        final Bundle extras = intent.getExtras();
        if(extras == null){
            return createDefaultUserLogin();
        }
        final UserLogin.UserLoginBuilder userLoginBuilder =
                (UserLogin.UserLoginBuilder)extras.getSerializable(AppConst.KEY_USER_REGISTRAR_BUILDER);
        if(userLoginBuilder == null){
            return createDefaultUserLogin();
        }
        return userLoginBuilder.build();
    }

    private UserLogin createDefaultUserLogin(){

        return new UserLogin(this);
    }
}
