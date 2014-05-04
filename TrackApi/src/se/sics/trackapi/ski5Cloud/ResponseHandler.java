package se.sics.trackapi.ski5Cloud;

import java.net.SocketTimeoutException;

import com.loopj.android.http.BinaryHttpResponseHandler;

abstract class ResponseHandler extends BinaryHttpResponseHandler {		
	String reqUrl;		
	ResponseHandler(String reqUrl) {			
		this.reqUrl=reqUrl;
	}		
	public void onCompleted() {
//		removePendingRequest(reqUrl);
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