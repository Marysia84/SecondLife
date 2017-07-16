package com.greensoft.secondlife.mobile_device.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greensoft.secondlife.AppConst;
import com.greensoft.secondlife.R;
import com.greensoft.secondlife.mobile_device.MobileDevice;
import com.greensoft.secondlife.utils.Utils;

public class MobileDeviceRegisterActivity extends AppCompatActivity
implements MobileDeviceRegistrationResultListener{

    private EditText nameEditText;
    private EditText modelNameEditText;
    private MobileDeviceRegistrar mobileDeviceRegistrar;

    class MobileDeviceCreationException extends Exception{

        private EditText editText;
        public MobileDeviceCreationException(EditText editText, String errorMessage) {
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
        setContentView(R.layout.activity_mobile_device_register);

        setUpControls();
        mobileDeviceRegistrar = buildRegistar();
    }

    private void setUpControls() {

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        modelNameEditText = (EditText)findViewById(R.id.modelNameEditText);

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {

        try {
            final MobileDevice mobileDevice = createMobileDevice();
            mobileDeviceRegistrar.register(mobileDevice, this);
        } catch (MobileDeviceRegisterActivity.MobileDeviceCreationException exc) {
            exc.showError();
        }
    }

    private MobileDevice createMobileDevice() throws MobileDeviceCreationException {

        final String name = nameEditText.getText().toString();
        if(TextUtils.isEmpty(name)){
            throw new MobileDeviceCreationException(nameEditText, getString(R.string.error_mobileDeviceName));
        }

        final String id = "foo";
        final String deviceName = Utils.getDeviceName();

        return new MobileDevice(id, name, deviceName);
    }

    @Override
    public void onMobileDeviceRegistrationSuccess(MobileDevice moblieDevice) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(MobileDeviceRegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMobileDeviceRegistrationFailure(final MobileDeviceRegistrationException exc) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(exc.mobileDeviceAlreadyExists()){
                    Toast.makeText(MobileDeviceRegisterActivity.this, "device already exists", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MobileDeviceRegisterActivity.this, exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private MobileDeviceRegistrar buildRegistar() {

        final Intent intent = getIntent();
        if(intent == null){
            return createDefaultRegistar();
        }
        final Bundle extras = intent.getExtras();
        if(extras == null){
            return createDefaultRegistar();
        }
        final MobileDeviceRegistrar.RegistarBuilder registarBuilder =
                (MobileDeviceRegistrar.RegistarBuilder)extras.getSerializable(AppConst.KEY_MOBILE_DEVICE_REGISTRAR_BUILDER);
        if(registarBuilder == null){
            return createDefaultRegistar();
        }
        return registarBuilder.build();
    }

    private MobileDeviceRegistrar createDefaultRegistar(){

        return new MobileDeviceRegistrar(this);
    }
}
