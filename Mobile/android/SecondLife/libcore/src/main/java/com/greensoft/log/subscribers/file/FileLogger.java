package com.greensoft.log.subscribers.file;

import com.greensoft.log.LogSubscriber;
import com.greensoft.log.Severity;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;


public class FileLogger implements LogSubscriber {

	private PrintWriter writer;
	
	public FileLogger(String filePath_) {
		
		try {
			writer = new PrintWriter(filePath_, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException exc_) {
			exc_.printStackTrace();
		}
	}
	@Override
	public void d(String tag, String msg) {
		
		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void e(String tag, String msg) {

		addToLogItemCache(Severity.Error, tag, msg);		
	}

	@Override
	public void i(String tag, String msg) {
		
		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void v(String tag, String msg) {

		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void w(String tag, String msg) {
		
		addToLogItemCache(Severity.Warning, tag, msg);
	}

	private void addToLogItemCache(Severity severity_, String tag_, String msg_){
		
		if(writer == null){
			return;
		}
		synchronized (this) {
			
			String logEntry = String.format("[%s] %s [tag: %s] %s\n", 
					severity_.toString(), new Date().toString(), tag_, msg_);
			writer.write(logEntry);
			writer.flush();
		}
	}
}
