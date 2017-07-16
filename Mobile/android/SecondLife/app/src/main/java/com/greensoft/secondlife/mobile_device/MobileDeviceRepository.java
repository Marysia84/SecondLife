package com.greensoft.secondlife.mobile_device;

import android.content.Context;

import com.greensoft.secondlife.BinaryPersister;
import com.greensoft.secondlife.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zebul on 7/12/17.
 */

public class MobileDeviceRepository {

    private Context context;
    private String fileName = "mobileDevice.bin";
    public MobileDeviceRepository(Context context) {
        this.context = context;
    }

    public MobileDeviceRepository(Context context, String fileName) {
        this(context);
        this.fileName = fileName;
    }

    private static final String VERSION = "version";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String MODEL_NAME = "modelName";

    public void save(MobileDevice mobileDevice) throws IOException {

        try {
            JSONObject json = new JSONObject();
            json.put(VERSION, 1);
            json.put(ID, mobileDevice.Id);
            json.put(NAME, mobileDevice.Name);
            json.put(MODEL_NAME, mobileDevice.ModelName);
            String jsonText = json.toString();
            BinaryPersister binaryPersister = new BinaryPersister(context);
            binaryPersister.save(fileName, jsonText.getBytes());
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public MobileDevice load() throws IOException{

        try {
            BinaryPersister binaryPersister = new BinaryPersister(context);
            final byte[] bytes = binaryPersister.load(fileName);
            String jsonText = new String(bytes);
            JSONObject json = new JSONObject(jsonText);
            int version = json.getInt(VERSION);
            String id = json.getString(ID);
            String name = json.getString(NAME);
            String modelName = json.getString(MODEL_NAME);
            return new MobileDevice(id, name, modelName);
        }
        catch(FileNotFoundException e){
            return null;
        }
        catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
