package se.sics.trackapi.ski5Cloud;

import java.io.UnsupportedEncodingException;


import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import com.loopj.android.http.AsyncHttpClient;



public class Ski5CloudHttpClient {
	private  static  AsyncHttpClient httpClient;	
	public static int NOT_FOUND_CODE=HttpStatus.SC_NOT_FOUND;//404
	public static int TIMED_OUT_CODE=HttpStatus.SC_REQUEST_TIMEOUT;
	public static int CONNECTION_LOST=0;
	static int defaultTimeOut;
	static int defalutRetry;
	private Ski5Cloud ski5;
	public Ski5CloudHttpClient(Ski5Cloud ski5){
		this.ski5 = ski5;
		Ski5CloudHttpClient.httpClient = new AsyncHttpClient();		
		Ski5CloudHttpClient.defaultTimeOut = httpClient.getTimeout();				

	}	
	public void doGetRequest(String url,ResponseHandler callback){		
		doGetRequest(url, null, callback, defaultTimeOut);
	}
	public void doGetRequest(String url,Header[] header){
		doGetRequest(url, header, new DefaultResoponseHandler(url), defaultTimeOut);
	}
	public void doGetRequest(String url,Header[] headers,ResponseHandler callback,int timeOut){
		LogH.i("Exec REQ : {0}", url);		
		httpClient.setMaxRetriesAndTimeout(0, 2000);		
		httpClient.get(null, url, headers, null, callback);		
	}
	public void doJsonPost(String url,String body,Header[] headers,ResponseHandler callback){
		LogH.i("Exec REQ : {0}", url);			
		StringEntity entity;
		try {
			entity = new StringEntity(body==null?"":body);
			httpClient.post(null, url,headers, entity, "application/json",callback);				
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
	}
	public void doJsonPost(String url,String body,Header[] headers){
		doJsonPost(url,body,headers,new DefaultResoponseHandler(url));		
	}
	public void handleDefaultCloudResponse(byte[] data,final int statusCode,Header[] headers,final String requrl){
		String resp = "";
		if(data!=null && data.length>0)			
			resp=new String(data);
		else
			resp ="Error : "+statusCode+" \n "+requrl;		
		LogH.i("URL = {0} , DATA = {1}", requrl,resp);
		ski5.response(resp,requrl);		
	}
	
	class DefaultResoponseHandler extends ResponseHandlerAdapter{

		DefaultResoponseHandler(String url) {
			super(url);			
		}
		@Override
		void handleCloudResponse(byte[] binaryData, int statusCode,
				Header[] headers, String reqUrl) {
			handleDefaultCloudResponse(binaryData, statusCode, headers, reqUrl);			
		}
	}
	

}