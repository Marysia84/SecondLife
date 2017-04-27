package com.greensoft.log.subscribers.console;

import com.greensoft.log.LogSubscriber;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger implements LogSubscriber {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	
	@Override
	public void d(String tag, String msg) {
		print("D", tag, msg);
	}

	@Override
	public void e(String tag, String msg) {
		
		print(System.err, "E", tag, msg);
	}

	@Override
	public void i(String tag, String msg) {
		
		print("I", tag, msg);
	}

	@Override
	public void v(String tag, String msg) {
		
		print("V", tag, msg);
	}

	@Override
	public void w(String tag, String msg) {
		
		print("W", tag, msg);
	}
	
	private void print(String severity_, String tag_, String msg_) {
		
		print(System.out, severity_, tag_, msg_);
	}

	private void print(PrintStream printStream_, String severity_, String tag_, String msg_) {
		
		String date = dateFormat.format(new Date());
		printStream_.println(date+" "+severity_+" ["+tag_+"] "+msg_);	
	}

}
