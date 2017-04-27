package com.greensoft.log.subscribers.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogItem {

	private String severity;
	
	public String getSeverity(){
		return severity;
	}
	
	private String tags;
	
	public String getTags(){
		return tags;
	}
	
	private String text;
	
	public String getText(){
		return text;
	}
	
	private String date;
	
	public String getDate(){
		return date;
	}
	
	public LogItem(String severity_, String tags_, String text_, Date date_){
		
		severity = severity_;
        tags = tags_;
        text = text_;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        date = df.format(date_);
	}
	
}
