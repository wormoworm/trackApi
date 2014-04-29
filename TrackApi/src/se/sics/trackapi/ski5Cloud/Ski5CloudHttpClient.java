package se.sics.trackapi.ski5Cloud;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;


public class Ski5CloudHttpClient {
	private  static  AsyncHttpClient httpClient;	
	static int NOT_FOUND_CODE=HttpStatus.SC_NOT_FOUND;//404
	static int TIMED_OUT_CODE=HttpStatus.SC_REQUEST_TIMEOUT;
	static int CONNECTION_LOST=0;
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
		doGetRequest(url, header, new DefaultResponseHandler(url), defaultTimeOut);
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
			entity = new StringEntity(body);
			httpClient.post(null, url,headers, entity, "application/json",callback);				
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
	}
	public void doJsonPost(String url,String body,Header[] headers){
		LogH.i("Exec REQ : {0}", url);			
		StringEntity entity;
		try {
			entity = new StringEntity(body==null?"":body);
			httpClient.post(null, url,headers, entity, "application/json",new DefaultResponseHandler(url));				
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
	}
	public void handleCloudResponse(byte[] data,final int statusCode,Header[] headers,final String requrl){
		String resp = "";
		if(data!=null && data.length>0)			
			resp=new String(data);
		else
			resp ="Error : "+statusCode+" \n "+requrl;		
		LogH.i("DATA = {0}", resp);
		ski5.response(resp,requrl);		
	}

	abstract class ResponseHandler extends BinaryHttpResponseHandler {		
		String reqUrl;		
		ResponseHandler(String reqUrl) {			
			this.reqUrl=reqUrl;
		}		
		public void onCompleted() {
//			removePendingRequest(reqUrl);
		}

		public boolean isTimeOut(Throwable e){
			return  e instanceof SocketTimeoutException;
		}
		void logError(int statusCode, Throwable e){
			
        	LogH.e("SC-Failure code {0}-{1}, throwable = {2}",statusCode,statusCode,e != null ? LogH.throwableToString(e):"");
		}
		void logSuccess(int statusCode){			
        	LogH.i("SC-Success code {0}-{1}",statusCode,statusCode);
		}
		@Override
        public void onStart() {         
        }

        @Override
        public String[] getAllowedContentTypes() {            
            return new String[]{".*"};
        }
	}	

	
	class DefaultResponseHandler extends ResponseHandler {						
		DefaultResponseHandler( String url) {
			super(url);			
		}	
		@Override
		public void onCompleted() {
			super.onCompleted();			
		}
        public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {        	
        	logSuccess( statusCode);
        	super.onCompleted();        	
        	handleCloudResponse(binaryData,statusCode,headers,reqUrl);        	
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {        	
        	logError( statusCode, e);
        	if(isTimeOut(e)){        		
        		statusCode = TIMED_OUT_CODE;
        	}
        	super.onCompleted();
        	if(statusCode==NOT_FOUND_CODE){//notFound
        		//respondNotFound();				
				LogH.e("NOT FOUND {0}", reqUrl);
        		return;				
        	}else if(statusCode==TIMED_OUT_CODE){//timedout
        		LogH.i("Request timed out {0}  ",reqUrl);
        		return;
        	}else if(statusCode==CONNECTION_LOST){
        		LogH.i("Connection lost  for url {0}",reqUrl);
        		return;
        	}else{
        		LogH.d("Error Responding with code {0}",statusCode);        		
        	}
        }
	}
		
//	public  synchronized void  cancelPendingRequests(){
//		LogH.i("Cancelling all pending requests L = {0} urls:{1}", pendingRequests.size(),pendingRequests.keySet());
//		for(String url:pendingRequests.keySet()){
//			if(!pendingRequests.get(url).isFinished()){
//				pendingRequests.get(url).cancel(true);
//			}
//		}
//		pendingRequests.clear();
//	}
////	public void removePendingRequest(String url){
//		LogH.d("PendingReq L ={0} ", pendingRequests.size());
//		if(pendingRequests.containsKey(url)){
//			if(pendingRequests.get(url).isFinished()){
//				LogH.d("PR L ={0} Removing pending request {1} ",pendingRequests.size(), url);
//				pendingRequests.remove(url);
//			}else{
//				LogH.w("Removing un-finished request {0}", url);
//				pendingRequests.remove(url).cancel(true);
//			}
//		}else{
//			LogH.w("The request is assumed to be pending {0}", url);
//		}
//	}
//	private void addPendingRequest(String url,RequestHandle futureResp){
//		LogH.d("PendingReq L ={0} ", pendingRequests.size());
//		if(pendingRequests.containsKey(url)){
//			LogH.w("a pending request is already in progress for url {0}", url);
//			return;
//		}else{
//			LogH.d("PR L ={0} Adding pending request {1} ",pendingRequests.size(), url);
//			pendingRequests.put(url, futureResp);
//		}
//	}
//	public void stop() {
//		cancelPendingRequests();		
//	}
//	public synchronized void nullify(){
//		stop();
//		httpClient =null;				
//		pendingRequests = null;
//	}
}