package se.sics.trackapi.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponse{
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	
	private int state;
	public static final int STATE_UNKNOWN = -1;
	public static final int STATE_ERROR = 1;
	public static final int STATE_WARNING = 2;
	public static final int STATE_ACK = 3;
	public static final int STATE_DATA = 4;
	
	public ApiResponse(String responseString){
		state = STATE_UNKNOWN;
		try {
			jsonObject = new JSONObject(responseString);
			if(jsonObject.has("data")){
				state = STATE_DATA;
				jsonArray = jsonObject.getJSONArray("data");
			}
			else if(jsonObject.has("trackId")){		//Track ID takes priority over warning and error
				state = STATE_DATA;
			}
			else if(jsonObject.has("error")){
				state = STATE_ERROR;
			} 
			else if(jsonObject.has("ack")){
				state = STATE_ACK;
			}
			else if(jsonObject.has("warning")){
				state = STATE_WARNING;
			}
		}
		catch (JSONException e) {
			try {
				jsonArray = new JSONArray(responseString);
				state = STATE_DATA;
			}
			catch (JSONException e1) {
				state = STATE_UNKNOWN;
			}
		}
	}
	
	public int getState(){
		return state;
	}
	
	public String getAck(){
		return jsonObject.optString("ack", null);
	}
	
	public String getError(){
		return jsonObject.optString("error", null);
	}
	
	public String getWarning(){
		return jsonObject.optString("warning", null);
	}
	
	public JSONArray getData(){
		return jsonArray;
	}
	
	public String getTrackId(){
		return jsonObject.optString("trackId", null);
	}
}