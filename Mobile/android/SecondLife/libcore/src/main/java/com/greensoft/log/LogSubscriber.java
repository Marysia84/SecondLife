package com.greensoft.log;

public interface LogSubscriber {

	void d(String tag, String msg);//debug

	void e(String tag, String msg);//error
	
	void i(String tag, String msg);//info

	void v(String tag, String msg);//verbose

	void w(String tag, String msg);//warning

}
