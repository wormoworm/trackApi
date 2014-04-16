package se.sics.trackapi.core;import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;

import se.sics.trackapi.classification.CreateClassificationProfileOptions;
import se.sics.trackapi.interfaces.TrackAdminEvents;
import se.sics.trackapi.types.SharingMode;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * A class that can be used to hold a reference to a track for use with the <em>Track</em> API. Each TrackReference is built around a {@link Track} object.
 * To obtain a TrackReference for a {@link Track}, create a {@link TrackApi} object, and call {@link TrackApi#obtainTrackReference(Track)}. This will
 * return a TrackReference which can then be used to perform API commands on this track, for example {@link TrackReference#delete()}.
 * This class is {@link Serializable}, meaning that TrackReferences may be stored in {@link Handler} messages or {@link Bundle}s.
 *
 */
public class TrackReference implements Serializable{
	private final String TAG = "TrackReference";
	private static final long serialVersionUID = 1L;

	public Track track;
	private TrackApi trackApi;

	public TrackReference(Track track, TrackApi parentApi){
		this.track = track;
		trackApi = parentApi;
	}
	
	public void uploadTrackFile(SharingMode sharingMode, String deviceId){
		File trackFile = track.getTrackFile();
		if(trackFile!=null){
			String trackName = "Track";
			try {
				trackName = URLEncoder.encode(track.getName(), "utf-8");
			}
			catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Error encoding track name: "+e.toString());
			}
			
			String address = trackApi.apiAddress+"addTrackFile.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&sharingMode="+sharingMode.textValue+"&providedTypes="+track.getDataTypesString()+"&name="+trackName+"&type="+track.trackType.intValue;
			address+= "&created="+(track.getTimestamp());
			address+= "&deviceId="+deviceId;
			address+= "&description="+track.getDescription();
			address+= "&duration="+(track.getDuration() / 1000);
			address+= "&distance="+track.getDistance();
			address+= "&avgSpeed="+track.getAverageSpeed();
			address+= "&avgHeartRate="+track.getAverageHeartRate();
			
			Bundle extras = new Bundle();
			extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
			extras.putSerializable(TrackApi.KEY_TRACK_FILE, trackFile);
			trackApi.http.executePostRequest(TrackApi.COMMAND_UPLOAD_TRACK_FILE, address, trackFile, trackApi.httpRetries, false, extras);
		}
	}
	
	public void uploadLogFile(String logFileName){
		String address = trackApi.apiAddress+"addLogFile.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&fileName="+logFileName;
		File logFile = track.getLogFile(logFileName);
		if(logFile!=null){
			Bundle extras = new Bundle();
			extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
			extras.putSerializable(TrackApi.KEY_LOG_FILE, logFile);
			trackApi.http.executePostRequest(TrackApi.COMMAND_UPLOAD_LOG_FILE, address, logFile, trackApi.httpRetries, false, extras);
		}
	}
	
	/**
	 * Instructs the API to broadcast details of this track to any other devices that the current user is using.
	 * This uses the API command 'broadcastTrackDetails'. If this command is successfully run on the server, the
	 * interface callback {@link TrackAdminEvents#broadcastSent(TrackReference)} will be called. Otherwise, either
	 * {@link TrackAdminEvents#apiError(int, String, Bundle) or {@link TrackAdminEvents#apiWarning(int, String, Bundle)} will be called.
	 */
	public boolean broadcastTrackDetails(String deviceId, String gcmId){
		if(deviceId!=null && gcmId!=null){												//True if this device has a unique ID and a GCM ID, meaning it can send GCM messages
			String address = trackApi.apiAddress+"broadcastTrackDetails.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&senderDeviceId="+deviceId;
			Bundle extras = new Bundle();
			extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
			trackApi.http.executeGetRequest(TrackApi.COMMAND_BROADCAST_TRACK_DETAILS, address, trackApi.defaultTimeoutSeconds, trackApi.httpRetries, false, extras);
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean createClassificationProfile(CreateClassificationProfileOptions profileOptions){
		boolean returnCode = false;
		if(profileOptions!=null){
			JSONArray segmentsJson = profileOptions.getSegments();
			if(segmentsJson!=null){							//Valid segment data, so can can continue
				String address = trackApi.apiAddress+"createClassificationProfile.php";
				String parameters = "userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId();
				try {
					parameters+= "&logFile="+profileOptions.getLogFileName();
					parameters+= "&name="+URLEncoder.encode(profileOptions.getName(), "utf-8");
					parameters+= "&techniqueType="+profileOptions.getTechniqueType().textValue;
					parameters+= "&visibility="+profileOptions.getSharingMode().textValue;
					parameters+= "&segments="+URLEncoder.encode(segmentsJson.toString(), "utf-8");					
				}
				catch (UnsupportedEncodingException e) {
					Log.e(TAG, "Error encoding parameter: "+e.toString());
				}
				
				Bundle extras = new Bundle();
				extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
				trackApi.http.executePostRequest(TrackApi.COMMAND_CREATE_CLASSIFICATION_PROFILE, address, parameters, trackApi.httpRetries, false, extras);
			}
		}
		return returnCode;
	}
	
	public void delete(){
		String address = trackApi.apiAddress+"deleteTrack.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId();
		Bundle extras = new Bundle();
		extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
		trackApi.http.executeGetRequest(TrackApi.COMMAND_DELETE_TRACK, address, trackApi.httpRetries, trackApi.defaultTimeoutSeconds, false, extras);
	}
	
	public void getData(RequestDataOptions options){
		if(options!=null){
			String address = trackApi.apiAddress+"trackDataNew.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&types="+options.getRequestedTypesString();
			Bundle extras = new Bundle();
			extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
			trackApi.http.executeGetRequest(TrackApi.COMMAND_GET_TRACK_DATA, address, trackApi.httpRetries, trackApi.defaultTimeoutSeconds, false, extras);
		}
	}
	
	public void downloadTrackFile(){
		String address = trackApi.apiAddress+"downloadTrackFile.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId();
		Bundle extras = new Bundle();
		extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
		trackApi.http.downloadFile(TrackApi.COMMAND_DOWNLOAD_TRACK_FILE, address, track.getPath(), Track.TRACK_FILE_NAME, trackApi.httpRetries, trackApi.defaultTimeoutSeconds, extras);
	}
	
	public void downloadLogFile(String logFileName){
		String address = trackApi.apiAddress+"downloadLogFile.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&fileName="+logFileName;
		Bundle extras = new Bundle();
		extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
		extras.putString(TrackApi.KEY_LOG_FILE, logFileName);
		trackApi.http.downloadFile(TrackApi.COMMAND_DOWNLOAD_LOG_FILE, address, track.getPath(), logFileName, trackApi.httpRetries, trackApi.defaultTimeoutSeconds, extras);
	}
	
	public void runClassification(String classificationProfileId, String logFileName, String deviceId, boolean runAsynchronously){
		if(logFileName!=null && deviceId!=null){
			String address = trackApi.apiAddress+"runClassification.php?userId="+trackApi.userId+"&apiKey="+trackApi.apiKey+"&serviceKey="+trackApi.serviceKey+"&trackId="+track.getId()+"&logFilename="+logFileName+"&referenceId="+classificationProfileId+"&deviceId="+deviceId+"&uniqueId="+track.getId();
			if(runAsynchronously){
				address+= "&asynchronous=true";
			}
			Bundle extras = new Bundle();
			extras.putSerializable(TrackApi.KEY_TRACK_REFERENCE, this);
			extras.putString(TrackApi.KEY_CLASSIFICATION_PROFILE_ID, classificationProfileId);
			extras.putBoolean(TrackApi.KEY_ASYNCHRONOUS, runAsynchronously);
			trackApi.http.executeGetRequest(TrackApi.COMMAND_RUN_CLASSIFICATION, address, trackApi.httpRetries, trackApi.defaultTimeoutSeconds, false, extras);
		}
	}
}