package com.greensoft.secondlife.user.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.mobile_device.registration.MobileDeviceRegisterActivity;
import com.greensoft.secondlife.user.User;
import com.greensoft.secondlife.user.UserRepository;
import com.greensoft.secondlife.user.UserValidator;

import java.io.IOException;

public class UserRegisterActivity extends AppCompatActivity implements UserRegistrationResultListener {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private UserRegistrar userRegistrar;

    class UserCreationException extends Exception{

        private EditText editText;
        public UserCreationException(EditText editText, String errorMessage) {
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
        setContentView(R.layout.activity_user_register);

        setUpControls();

        userRegistrar = buildRegistrar();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpControls() {

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private User createUser() throws UserCreationException{

        String firstName = firstNameEditText.getText().toString();
        if(!UserValidator.isFirstNameValid(firstName)){
            throw new UserCreationException(firstNameEditText, UserValidator.formatFirstNameErrorText(this));
        }
        String lastName = lastNameEditText.getText().toString();
        if(!UserValidator.isLastNameValid(lastName)){
            throw new UserCreationException(lastNameEditText, UserValidator.formatLastNameErrorText(this));
        }
        String email = emailEditText.getText().toString();
        if(!UserValidator.isEmailValid(email)){
            throw new UserCreationException(emailEditText, UserValidator.formatEmailErrorText(this));
        }

        String password = passwordEditText.getText().toString();
        if(!UserValidator.isPasswordValid(password)){
            throw new UserCreationException(passwordEditText, UserValidator.formatPasswordErrorText(this));
        }

        String passwordConfirmation = confirmPasswordEditText.getText().toString();
        if(!UserValidator.arePasswordsEqual(password, passwordConfirmation)){
            throw new UserCreationException(confirmPasswordEditText, UserValidator.formatPasswordsEqualErrorText(this));
        }

        String id = ""; //id is created by server side
        return new User(id, firstName, lastName, email, password);
    }

    private void register() {

        try {
            final User user = createUser();
            userRegistrar.register(user, this);
        } catch (UserCreationException exc) {
            exc.showError();
        }
    }

    @Override
    public void onUserRegistrationSuccess(User user) {

        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(UserRegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });*/
        try {
            UserRepository userRepository = new UserRepository(this);
            userRepository.save(user);
            Intent intent = new Intent(this, MobileDeviceRegisterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConst.KEY_USER, user);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } catch (final IOException exc) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(UserRegisterActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onUserRegistrationFailure(final UserRegistrationException exc) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(exc.userAlreadyExists()){
                    Toast.makeText(UserRegisterActivity.this, "user already exists", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(UserRegisterActivity.this, exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private UserRegistrar buildRegistrar() {

        final Intent intent = getIntent();
        if(intent == null){
            return createDefaultRegistar();
        }
        final Bundle extras = intent.getExtras();
        if(extras == null){
            return createDefaultRegistar();
        }
        final UserRegistrar.RegistarBuilder registarBuilder =
                (UserRegistrar.RegistarBuilder)extras.getSerializable(AppConst.KEY_USER_REGISTRAR_BUILDER);
        if(registarBuilder == null){
            return createDefaultRegistar();
        }
        return registarBuilder.build();
    }

    private UserRegistrar createDefaultRegistar(){

        return new UserRegistrar(this);
    }
}
