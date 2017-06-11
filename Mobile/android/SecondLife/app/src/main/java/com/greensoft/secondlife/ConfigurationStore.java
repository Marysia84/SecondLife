package com.greensoft.secondlife;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zebul on 4/28/17.
 */

public class ConfigurationStore {

    private static final String CONFIGURATION_PREF = "CONFIGURATION_PREF";
    private static final String KEY_PEER_NAME = "KEY_PEER_NAME";
    private static final String KEY_SERVER_ADDRESS = "KEY_SERVER_ADDRESS";
    private static final String KEY_MANAGE_CLOCK_VISIBLITY = "KEY_MANAGE_CLOCK_VISIBLITY";


    private Context context;
    public ConfigurationStore(Context context){
        this.context = context;
    }

    public Configuration load(){

        SharedPreferences sharedPreferences = createSharedPreferences();
        Configuration configuration = new Configuration();
        configuration.PeerName = sharedPreferences.getString(KEY_PEER_NAME, "");
        configuration.ServerAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, "http://192.168.1.10:13000/");
        configuration.ManageClockVisiblity = sharedPreferences.getBoolean(KEY_MANAGE_CLOCK_VISIBLITY, true);
        return configuration;
    }

    public void save(Configuration configuration){

        SharedPreferences sharedPreferences = createSharedPreferences();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PEER_NAME, configuration.PeerName);
        editor.putString(KEY_SERVER_ADDRESS, configuration.ServerAddress);
        editor.putBoolean(KEY_MANAGE_CLOCK_VISIBLITY, configuration.ManageClockVisiblity);
        editor.commit();
    }

    private SharedPreferences createSharedPreferences() {
        return context.getSharedPreferences(CONFIGURATION_PREF, Context.MODE_PRIVATE);
    }
}
