package com.greensoft.secondlife.user;

import android.content.Context;

import com.greensoft.secondlife.BinaryPersister;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zebul on 7/10/17.
 */

public class UserRepository {

    private Context context;
    private String fileName = "user.bin";
    public UserRepository(Context context) {
        this.context = context;
    }

    public UserRepository(Context context, String fileName) {
        this(context);
        this.fileName = fileName;
    }

    private static final String ID = "id";
    private static final String VERSION = "version";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";

    public void save(User user) throws IOException{

        try {
            JSONObject json = new JSONObject();
            json.put(VERSION, 1);
            json.put(ID, user.Id);
            json.put(FIRST_NAME, user.FirstName);
            json.put(LAST_NAME, user.LastName);
            json.put(EMAIL, user.Email);
            json.put(PASSWORD, user.Password);
            String jsonText = json.toString();
            BinaryPersister binaryPersister = new BinaryPersister(context);
            binaryPersister.save(fileName, jsonText.getBytes());
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public User load() throws IOException{

        try {
            BinaryPersister binaryPersister = new BinaryPersister(context);
            final byte[] bytes = binaryPersister.load(fileName);
            String jsonText = new String(bytes);
            JSONObject json = new JSONObject(jsonText);
            int version = json.getInt(VERSION);
            String id = json.getString(ID);
            String firstName = json.getString(FIRST_NAME);
            String lastName = json.getString(LAST_NAME);
            String email = json.getString(EMAIL);
            String password = json.getString(PASSWORD);
            return new User(id, firstName, lastName, email, password);
        }
        catch(FileNotFoundException e){
            return null;
        }
        catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
