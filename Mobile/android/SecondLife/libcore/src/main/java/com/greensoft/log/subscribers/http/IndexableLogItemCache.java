package com.greensoft.log.subscribers.http;

import java.util.LinkedList;
import java.util.List;

public class IndexableLogItemCache {

	private List<IndexableLogItem> listOfIndexableLogItems = new LinkedList<IndexableLogItem>();
	private int capacity;
	private int currentIndex = 0;

	public IndexableLogItemCache(int capacity_){
		
		capacity = capacity_;
	}
	
	void add(LogItem logItem_)
    {
        synchronized (this) {
		
        	IndexableLogItem indexableLogItem = new IndexableLogItem(++currentIndex, logItem_); 
        	listOfIndexableLogItems.add(indexableLogItem);
            if (capacity < listOfIndexableLogItems.size())
            {
            	listOfIndexableLogItems.remove(0);
            }
		}
    }

	public int getSize() {
		
		synchronized (this) {
			
			return listOfIndexableLogItems.size();
		}
	}
	
	LogItemBundle getLogItemBundleContainingItemsWithHigherIndexThan(int index_){
		
		int newIndex = -1;
		List<LogItem> bundleLogItemList = new LinkedList<LogItem>();
		synchronized (this) {
			
			int size = listOfIndexableLogItems.size();
			for(int i=0; i<size; i++){
				
				IndexableLogItem indexableLogItem = listOfIndexableLogItems.get(i);
				int logItemIndex = indexableLogItem.getIndex();
				if(index_ < logItemIndex){
					bundleLogItemList.add(indexableLogItem.getLogItem());
					newIndex = logItemIndex; 
				}
			}
		}
		return new LogItemBundle(newIndex, bundleLogItemList);
	}
}
