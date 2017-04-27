package com.greensoft.log.subscribers.http;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogItemBundle
{
	private int index;
	private List<LogItem> items;

    public LogItemBundle()
    {
    	
    }
    
    public LogItemBundle(int index_, List<LogItem> items_)
    {
    	index = index_;
    	items = items_;
    }
    
    public int getIndex(){
    	
    	return index;
    }
    
    public void setIndex(int index_){
    	
    	index = index_;
    }

	public boolean isEmpty() {
		
		return items.isEmpty();
	}

	public int getLogItemCount() {

		return items.size();
	}
	
	public List<LogItem> getLogItemList(){
		
		return items;
	}
}
