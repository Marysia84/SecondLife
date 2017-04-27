package com.greensoft.log.subscribers.http;

import java.util.List;

public class LogBundleXmlSerializer {

	public static String serializeToXmlText(LogItemBundle logBundle_) throws LogBundleXmlSerializeException{
		
		String indent = "   ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<logItemBundle>\n");
			stringBuilder.append(indent+"<index>"+logBundle_.getIndex()+"</index>\n");
			stringBuilder.append(indent+"<items>\n");
			
				List<LogItem> logItems = logBundle_.getLogItemList();
				for(LogItem logItem: logItems){
					
					stringBuilder.append(indent+indent+"<item>\n");
					
					stringBuilder.append(indent+indent+"<severity>"+logItem.getSeverity()+"</severity>\n");
					stringBuilder.append(indent+indent+"<tags>"+logItem.getTags()+"</tags>\n");
					stringBuilder.append(indent+indent+"<text>"+logItem.getText()+"</text>\n");
					stringBuilder.append(indent+indent+"<date>"+logItem.getDate()+"</date>\n");
					
					stringBuilder.append(indent+indent+"</item>\n");
				}
			
			stringBuilder.append(indent+"</items>\n");
		stringBuilder.append("</logItemBundle>");
		
		return stringBuilder.toString();
	}
}
