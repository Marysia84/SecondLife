package com.greensoft.log;

import java.util.LinkedList;
import java.util.List;

class LoggerImpl {

	private List<LogSubscriber> subscribers = new LinkedList<LogSubscriber>();
	
	public void addLogSubscriber(LogSubscriber logSubscriber_) {

		synchronized (subscribers) {
		
			subscribers.add(logSubscriber_);
		}
	}

	public void removeLogSubscriber(LogSubscriber logSubscriber_) {
		
		synchronized (subscribers) {
		
			subscribers.remove(logSubscriber_);
		}
	}

	public void d(String tag_, String msg_) {
		
		synchronized (subscribers) {
			
			for(LogSubscriber subscriber: subscribers){
				subscriber.d(tag_, msg_);
			}
		}
	}

	public void e(String tag_, String msg_) {
		
		synchronized (subscribers) {
			
			for(LogSubscriber subscriber: subscribers){
				subscriber.e(tag_, msg_);
			}
		}
	}
	
	public void i(String tag_, String msg_) {
		
		synchronized (subscribers) {
			
			for(LogSubscriber subscriber: subscribers){
				subscriber.i(tag_, msg_);
			}
		}
	}

	public void v(String tag_, String msg_) {
		
		synchronized (subscribers) {
			
			for(LogSubscriber subscriber: subscribers){
				subscriber.v(tag_, msg_);
			}
		}
	}

	public void w(String tag_, String msg_) {
		
		synchronized (subscribers) {
			
			for(LogSubscriber subscriber: subscribers){
				subscriber.w(tag_, msg_);
			}
		}
	}

}
