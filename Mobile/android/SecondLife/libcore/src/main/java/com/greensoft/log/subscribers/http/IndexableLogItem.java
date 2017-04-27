package com.greensoft.log.subscribers.http;

public class IndexableLogItem {

	private int index; 
	private LogItem logItem;
	
	public IndexableLogItem(int index_, LogItem logItem_){
		
		index = index_; 
		logItem = logItem_;
	}

	public int getIndex() {
		return index;
	}

	public LogItem getLogItem() {
		return logItem;
	}	
}
