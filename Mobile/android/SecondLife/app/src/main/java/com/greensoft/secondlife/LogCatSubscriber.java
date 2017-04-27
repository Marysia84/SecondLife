package com.greensoft.secondlife;

import android.util.Log;

import com.greensoft.log.LogSubscriber;

public class LogCatSubscriber implements LogSubscriber {

	@Override
	public void d(String tag, String msg) {
		Log.d(tag, msg);
	}

	@Override
	public void e(String tag, String msg) {
		Log.e(tag, msg);
	}

	@Override
	public void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	@Override
	public void v(String tag, String msg) {
		Log.v(tag, msg);
	}

	@Override
	public void w(String tag, String msg) {
		Log.w(tag, msg);
	}
}
