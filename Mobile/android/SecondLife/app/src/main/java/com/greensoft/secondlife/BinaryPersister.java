package com.greensoft.secondlife;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zebul on 7/10/17.
 */

public class BinaryPersister {

    private Context context;

    public BinaryPersister(Context context) {
        this.context = context;
    }

    public void save(String fileName, byte [] data) throws IOException {

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(data);
        fos.close();
    }

    public byte[] load(String fileName) throws IOException {

        FileInputStream fis = context.openFileInput(fileName);
        int fileSize = fis.available();
        byte [] data = new byte[fileSize];
        fis.read(data);
        fis.close();
        return data;
    }

}
