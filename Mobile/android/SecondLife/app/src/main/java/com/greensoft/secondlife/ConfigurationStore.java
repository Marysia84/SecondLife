package com.greensoft.secondlife;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zebul on 4/28/17.
 */

public class ConfigurationStore {

    private static final String CONFIGURATION_PREF = "CONFIGURATION_PREF";
    private static final String KEY_PEER_NAME = "KEY_PEER_NAME";


    private Context context;
    public ConfigurationStore(Context context){
        this.context = context;
    }

    public Configuration load(){

        SharedPreferences sharedPreferences = createSharedPreferences();
        Configuration configuration = new Configuration();
        configuration.PeerName = sharedPreferences.getString(KEY_PEER_NAME, "");
        return configuration;
    }

    public void save(Configuration configuration){

        SharedPreferences sharedPreferences = createSharedPreferences();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PEER_NAME, configuration.PeerName);
        editor.commit();
    }

    private SharedPreferences createSharedPreferences() {
        return context.getSharedPreferences(CONFIGURATION_PREF, Context.MODE_PRIVATE);
    }
}
