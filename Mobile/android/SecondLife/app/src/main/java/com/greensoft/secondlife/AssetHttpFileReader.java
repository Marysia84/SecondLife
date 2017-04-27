package com.greensoft.secondlife;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.greensoft.log.subscribers.http.reader.HttpFileReader;

public class AssetHttpFileReader extends HttpFileReader {

	private Context context;
	public AssetHttpFileReader(Context context_){
		
		context = context_;
	}

	@Override
	public String readHttpFile(String fileName_) throws IOException {
		
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = assetManager.open("http/"+fileName_);
		return inputStreamToLongString(inputStream);
	}

}
