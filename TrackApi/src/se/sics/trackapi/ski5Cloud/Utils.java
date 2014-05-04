package se.sics.trackapi.ski5Cloud;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.BasicSessionCredentials;

public class Utils {
	public static String fmt(String string, Object... p){
    	String ret = null;
    	if(p==null || p.length==0){
    		ret =  string;
    	}else{
    		ret = MessageFormat.format(string, p); 
    	}    	
    	return ret;
    }	
	public static Header[] getDefaultHeaders(String accessToken,String userName){
		Header token = new BasicHeader("X-API-TOKEN", accessToken);
		Header user = new BasicHeader("X-API-USERNAME", userName);				
		return new Header[]{token,user};
	}
	
	public static String toJson(Map<String,? extends Object> map){		
		
		JSONObject jsonParams = toJsonObject(map);
		String res =  jsonParams.toString();
		LogH.i("Parameters : {0}", res);
		return res;
		
	}
	public static JSONObject toJsonObject(Map<String,? extends Object> map){		
		try {
			JSONObject jsonParams = new JSONObject();
			for(String k : map.keySet()){
				Object v= map.get(k);
				if(v instanceof Map)
					jsonParams.put(k, toJsonObject((Map<String,? extends Object>) v));
				else
					jsonParams.put(k,v);
			}			
			return jsonParams;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Map<String,String> toMap(String json){
		try {
		    JSONObject jsonObject = new JSONObject(json);
		    Iterator keys = jsonObject.keys();
		    Map<String, String> map = new HashMap<String, String>();
		    while (keys.hasNext()) {
		        String key = (String) keys.next();
		        map.put(key, jsonObject.getString(key));
		    }
		    return map;
		} catch (JSONException e) {
		    e.printStackTrace();
		    return null;
		}
	}
	public static BasicSessionCredentials getToken(String s3Tocken){
		LogH.i("S3TOCKEN====>{0}", s3Tocken);
		Map<String,String> map = toMap(s3Tocken);
		LogH.i("CRED====> {0}", map);
		 BasicSessionCredentials c = new BasicSessionCredentials(
				  map.get("access_key_id"), 
			      map.get("secret_access_key"), 
			      map.get("session_token"));
		 return c;
	}
}
