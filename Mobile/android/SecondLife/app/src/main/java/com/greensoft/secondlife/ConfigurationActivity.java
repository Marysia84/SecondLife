package com.greensoft.secondlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigurationActivity extends AppCompatActivity {

    private EditText peerNameEditText;
    private EditText serverAddressEditText;
    private Button saveConfigurationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        peerNameEditText = (EditText) findViewById(R.id.peerNameEditText);
        serverAddressEditText = (EditText)findViewById(R.id.serverAddressEditText);
        saveConfigurationButton = (Button) findViewById(R.id.saveConfigurationButton);
        saveConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConfigurationStore configurationStore = createConfigurationStore();
                Configuration configuration = configurationStore.load();
                UI2Configuration(configuration);
                configurationStore.save(configuration);
                sendResult(configuration);
            }
        });

        ConfigurationStore configurationStore = createConfigurationStore();
        Configuration configuration = configurationStore.load();
        configuration2UI(configuration);
    }

    private ConfigurationStore createConfigurationStore() {

        return new ConfigurationStore(ConfigurationActivity.this);
    }

    private void sendResult(Configuration configuration) {

        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Configuration.KEY, configuration);
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void configuration2UI(Configuration configuration) {

        peerNameEditText.setText(configuration.PeerName);
        serverAddressEditText.setText(configuration.ServerAddress);
    }

    private void UI2Configuration(Configuration configuration) {

        configuration.PeerName = peerNameEditText.getText().toString();
        configuration.ServerAddress = serverAddressEditText.getText().toString();
    }
}
