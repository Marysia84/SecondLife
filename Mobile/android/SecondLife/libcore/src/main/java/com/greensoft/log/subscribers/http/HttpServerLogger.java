package com.greensoft.log.subscribers.http;

import com.greensoft.log.LogSubscriber;
import com.greensoft.log.Severity;
import com.greensoft.log.subscribers.http.reader.HttpFileReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class HttpServerLogger extends NanoHTTPD
implements LogSubscriber {
	
	public static final String MIME_CSS = "text/css";
	public static final String MIME_JS = "text/javascript";
	public static final String MIME_XML = "text/xml";
	
	public final static HashMap<String, String> HTTP_FILE_MIMES = new HashMap<String, String>();
	static {
		HTTP_FILE_MIMES.put("html", MIME_HTML);
		HTTP_FILE_MIMES.put("css", MIME_CSS);
		HTTP_FILE_MIMES.put("js", MIME_JS);
		HTTP_FILE_MIMES.put("xml", MIME_XML);
	}
	
	private IndexableLogItemCache logItemCache = null;
	private HttpFileReader httpFileReader;
	public HttpServerLogger(int port_, HttpFileReader httpFileReader_) {
		
		this(port_, httpFileReader_, 1000);
	}
	
	public HttpServerLogger(int port_, HttpFileReader httpFileReader_, int logCapacity_) {
		
		super(port_);
		httpFileReader = httpFileReader_;
		logItemCache = new IndexableLogItemCache(logCapacity_);
	}
	
	@Override
    public void start() throws IOException {
		
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}
	
	@Override
    public Response serve(IHTTPSession session) {
        
		/*
		String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
        */
		
		if(isHttpFileRequest(session)){
	
			return createHttpFileResponse(session);
		}
		else if(isLogXmlRequest(session)){
			
			return createLogXmlResponse(session);
		}
		else{
		
			return newFixedLengthResponse("<html><body>Enter http://ip:port/index.html</body></html>");
		}
	}

	public static String normalizeFileName(String fileName_){
		
		String normalizedFileName = fileName_.replaceAll("/", "");
		normalizedFileName = normalizedFileName.replaceAll("\\\\", "");
		return normalizedFileName;
	}
	private Response createHttpFileResponse(IHTTPSession session) {
		
		try {
			String fileName = normalizeFileName(session.getUri());
			String mime = findMime(fileName);
			String httpFileContent = httpFileReader.readHttpFile(fileName);
			return newFixedLengthResponse(Response.Status.OK, mime, httpFileContent);
		} catch (Exception exc_) {
			StringWriter errors = new StringWriter();
			exc_.printStackTrace(new PrintWriter(errors));
			return newFixedLengthResponse("<p>"+errors.toString()+"</p>");
		}
		
	}
	
	private String findMime(String uri) {
		
		for(Entry<String, String> entry: HTTP_FILE_MIMES.entrySet()){
			
			String fileExtension = entry.getKey();
			if(uri.contains(fileExtension)){
				
				return entry.getValue();
			}
		}
		
		return MIME_HTML;
	}

	private static final boolean isHttpFileRequest(IHTTPSession session_){
		
		String uri = session_.getUri();
		for(String ext: HTTP_FILE_MIMES.keySet()){
			
			if(uri.contains(ext)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isLogXmlRequest(IHTTPSession session) {
		
		String uri = session.getUri();
		if(uri == null){
			return false;
		}
		return uri.contains("ajax");
	}
	
	private Response createLogXmlResponse(IHTTPSession session) {
		
		String queryParameterString = session.getQueryParameterString();
		int index = findIndex(queryParameterString);
		
		LogItemBundle logBundle = logItemCache.getLogItemBundleContainingItemsWithHigherIndexThan(index);
		if(logBundle.isEmpty()){
			return newFixedLengthResponse("");
		}
		
		try {
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					LogBundleXmlSerializer.serializeToXmlText(logBundle);
			return newFixedLengthResponse(Response.Status.OK, MIME_XML, xml);
		} catch (LogBundleXmlSerializeException e) {
			return newFixedLengthResponse("");
		}
	}
	
	static int findIndex(String queryParameter_) {
		
		if(queryParameter_ == null){
			return 0;
		}
		String [] args = queryParameter_.split("&");
		for(String arg: args){
			
			String [] keyValue = arg.split("=");
			if(keyValue.length == 2){
				String key = keyValue[0]; 
				String value = keyValue[1];
				if(key.contains("next_index")){
					try{
					
						return Integer.parseInt(value);
					}
					catch(NumberFormatException exc_){
						
						return 0;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public void d(String tag, String msg) {
		
		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void e(String tag, String msg) {
		
		addToLogItemCache(Severity.Error, tag, msg);
	}

	@Override
	public void i(String tag, String msg) {
		
		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void v(String tag, String msg) {
		
		addToLogItemCache(Severity.Info, tag, msg);
	}

	@Override
	public void w(String tag, String msg) {
		
		addToLogItemCache(Severity.Warning, tag, msg);
	}
	
	private void addToLogItemCache(Severity severity_, String tag_, String msg_){
		
		logItemCache.add(new LogItem(severity_.toString(), tag_, msg_, new Date()));
	}
}
