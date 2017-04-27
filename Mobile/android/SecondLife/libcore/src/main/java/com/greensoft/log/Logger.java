package com.greensoft.log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

	private static LoggerImpl loggerImpl = new LoggerImpl();
	
	public static void addLogSubscriber(LogSubscriber logSubscriber_){
		loggerImpl.addLogSubscriber(logSubscriber_);
	}
	
	public static void removeLogSubscriber(LogSubscriber logSubscriber_){
		loggerImpl.removeLogSubscriber(logSubscriber_);
	}
	
	public static void d(String tag, String msg){
		loggerImpl.d(tag, msg);
	}
	
	public static void e(String tag, String msg){
		loggerImpl.e(tag, msg);
	}
	
	public static void e(String tag, Exception exc){
		StringWriter errors = new StringWriter();
		exc.printStackTrace(new PrintWriter(errors));
		loggerImpl.e(tag, errors.toString());
	}
	
	
	
	public static void i(String tag, String msg){
		loggerImpl.i(tag, msg);
	}
	
	public static void v(String tag, String msg){
		loggerImpl.v(tag, msg);
	}
	
	public static void w(String tag, String msg){
		loggerImpl.w(tag, msg);
	}
}
