package com.greensoft.log.subscribers.http.reader;

import com.greensoft.log.subscribers.http.HttpServerLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultHttpFileReader extends HttpFileReader{

	@Override
	public String readHttpFile(String fileName_)throws IOException {
		
		Path [] paths = {
				Paths.get("files", fileName_), 
				Paths.get(new File( "." ).getCanonicalPath(), "files", fileName_)};
		
		String stringifiedPaths = "";
		InputStream inputStream = null;
		for(Path path: paths){
		
			String stringifiedPath = path.toString();
			stringifiedPaths += stringifiedPath;
			inputStream = HttpServerLogger.class.getResourceAsStream(stringifiedPath);
			if(inputStream != null){
				break;
			}
			
			inputStream = new FileInputStream(stringifiedPath);
			if(inputStream != null){
				break;
			}
		}
		
		if(inputStream == null){
			throw new IOException("No file exists in given paths: "+stringifiedPaths);
		}
		
		return inputStreamToLongString(inputStream);
	}

}
