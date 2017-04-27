package com.greensoft.log.subscribers.http.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class HttpFileReader {

	public abstract String readHttpFile(String fileName_)throws IOException;
	
	protected static String inputStreamToLongString(InputStream inputStream_) throws IOException{
		
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream_);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String longString = linesToLongString(bufferedReader);
		bufferedReader.close();
		return longString;
	}
	
	protected static String linesToLongString(BufferedReader bufferedReader_) throws IOException{
		
		StringBuilder stringBuilder = new StringBuilder();
	    String line;
	    while( (line = bufferedReader_.readLine()) != null) {
	       stringBuilder.append(line);
	    }
	    return stringBuilder.toString();
	}
}
