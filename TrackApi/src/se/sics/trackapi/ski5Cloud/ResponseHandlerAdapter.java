package se.sics.trackapi.ski5Cloud;

import org.apache.http.Header;

abstract class ResponseHandlerAdapter extends ResponseHandler {						
	ResponseHandlerAdapter( String url) {
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
    abstract void handleCloudResponse(byte[] binaryData,int statusCode,Header[] headers, String reqUrl);

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {        	
    	logError( statusCode, e);
    	if(isTimeOut(e)){        		
    		statusCode = Ski5CloudHttpClient.TIMED_OUT_CODE;
    	}
    	super.onCompleted();
    	if(statusCode==Ski5CloudHttpClient.NOT_FOUND_CODE){//notFound
    		//respondNotFound();				
			LogH.e("NOT FOUND {0}", reqUrl);
    		return;				
    	}else if(statusCode==Ski5CloudHttpClient.TIMED_OUT_CODE){//timedout
    		LogH.i("Request timed out {0}  ",reqUrl);
    		return;
    	}else if(statusCode==Ski5CloudHttpClient.CONNECTION_LOST){
    		LogH.i("Connection lost  for url {0}",reqUrl);
    		return;
    	}else{
    		LogH.d("Error Responding with code {0}",statusCode);        		
    	}
    }
}