package se.sics.trackapi.ski5Cloud;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

public class Ski5Cloud {
	Ski5CloudHttpClient client;	 
	final Header[] headers;
	final ExampleActivity a;
	public Ski5Cloud(ExampleActivity a){
		headers = Utils.getDefaultHeaders(Api.TOKEN, Api.USER_NAME); // you need to re-implement this, 
																	 //because TOKEN and USER_NAME are not there initially
		client = new Ski5CloudHttpClient(this);
		this.a=a;
	}
	public void response(final String resp, final String requrl) {	
		LogH.i("resp : {0}", resp);
		if(requrl.contains("upload")){
			LogH.i("TICKET RESPONES");
			Map<String,String> map = Utils.toMap(resp);
			LogH.i("MAP TOKEN => {0}", map);
			String folder = map.get("folder");
			String s3Tocken  = map.get("s3token");
			//BasicSessionCredentials c = Utils.getToken(s3Tocken);
		}
		this.a.runOnUiThread(new Runnable (){
			@Override
			public void run() {
				a.asynchResult(resp);
			}			
		});
	}
	
	public void signIn(String userName,String password){
		String url = Api.signIn();
		Map<String,String> map = new HashMap<String, String>();
		map.put("user_name", userName);
		map.put("password",password);
		client.doJsonPost(url, Utils.toJson(map), null);
	}	
	public void getUploadTicket(){
		String url = Api.uploadTicket();
		client.doJsonPost(url, null, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserTech(int userId,long startts,long endts){
		String url = Api.userTech(startts, endts);
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserStats(long startts,long endts){
		String url = Api.userStats(startts, endts);
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserTracks(){
		String url = Api.userTracks();
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
}
