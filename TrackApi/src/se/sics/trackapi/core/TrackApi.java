package se.sics.trackapi.core;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.sics.trackapi.classification.ClassificationProfile;
import se.sics.trackapi.interfaces.ClassificationEvents;
import se.sics.trackapi.interfaces.CommonEvents;
import se.sics.trackapi.interfaces.TrackAdminEvents;
import se.sics.trackapi.interfaces.TrackDataEvents;
import se.sics.trackapi.interfaces.TracksListEvents;
import se.sics.trackapi.types.TechniqueType;
import se.sics.trackapi.types.TrackType;
import uk.tomhomewood.http.Http;
import uk.tomhomewood.http.HttpEvents;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * A class that is used for interacting with the <em>Track</em> API. Since the API uses HTTP for client - server communication,
 * method calls to this class are executed asynchronously. To listen for events, such as the completion of commands, implement
 * the appropriate interface in your parent class, and pass a reference to your parent class in the constructor of your {@link TrackApi} object.
 *
 */
public class TrackApi implements HttpEvents{
	private final static String TAG = "TrackApi";
	
	/**
	 * Represents an <b>upload track file</b> command.
	 */
	public static final int COMMAND_UPLOAD_TRACK_FILE = 1;
	/**
	 * Represents an <b>upload log file</b> command.
	 */
	public static final int COMMAND_UPLOAD_LOG_FILE = 2;
	/**
	 * Represents a <b>delete track</b> command.
	 */
	public static final int COMMAND_DELETE_TRACK = 3;
	
	public static final int COMMAND_DOWNLOAD_TRACK_FILE = 10;
	public static final int COMMAND_DOWNLOAD_LOG_FILE = 11;
	public static final int COMMAND_GET_TRACK_DATA = 12;
	
	public static final int COMMAND_GET_CLASSIFICATION_PROFILES = 20;
	/**
	 * Represents a <b>run classification</b> command.
	 */
	public static final int COMMAND_RUN_CLASSIFICATION = 21;
	public static final int COMMAND_CREATE_CLASSIFICATION_PROFILE = 22;
	
	public static final int COMMAND_BROADCAST_TRACK_DETAILS = 30;
	
	public static final int COMMAND_GET_TRACKS_LIST = 40;
	
	/**
	 * Status flag indicating that an API command encountered an error.
	 */
	public static final int RESPONSE_STATUS_ERROR = 1;
	/**
	 * Status flag indicating that an API command encountered a warning.
	 */
	public static final int RESPONSE_STATUS_WARNING = 2;
	/**
	 * Status flag indicating that an API command was run successfully.
	 */
	public static final int RESPONSE_STATUS_ACK = 3;
	
	protected String apiAddress, userId, apiKey, serviceKey;
	
	private TrackAdminEvents interfaceTrackAdminEvents;
	private TrackDataEvents interfaceTrackDataEvents;
	private ClassificationEvents interfaceClassificationEvents;
	private TracksListEvents interfaceTracksListEvents;

	protected int httpRetries = 3;
	protected int defaultTimeoutSeconds = 30;

	protected Http http;
	
	//Bundle key name
	public final static String KEY_TRACK_REFERENCE = "trackReference";
	public final static String KEY_TRACK_FILE = "trackFile";
	public final static String KEY_LOG_FILE = "logFile";
	public final static String KEY_CLASSIFICATION_PROFILE_ID = "classificationProfileId";
	public final static String KEY_ASYNCHRONOUS = "asynchronous";
	
	/**
	 * Constructor.
	 * @param context			The context in which this {@link TrackApi} object should run.
	 * @param parent			The parent class of this {@link TrackApi} object. If you implement one or more of the provided interfaces in your parent class, 
	 * 							you will be notified when API events occur. This may be null if you do not wish to receive events.
	 * @param apiAddress		The base address for API commands, for example http://server.com/track/
	 * @param userId			The ID of the user under whose name any commands will be performed. May be null, in which case only commands that do
								not require authorisation will be executed, and only details of public tracks will be available.
	 * @param apiKey			The api key of the user under whose name any commands will be performed. May be null, in which case only commands that do
								not require authorisation will be executed, and only details of public tracks will be available.
	 * @param serviceKey		The service key for your application.
	 */
	public TrackApi(Context context, Object parent, String apiAddress, String userId, String apiKey, String serviceKey){
		this.apiAddress = apiAddress;
		this.userId = userId;
		this.apiKey = apiKey;
		this.serviceKey = serviceKey;
		//Attempt to connect to any parent interfaces. If an interface is not available in the parent class, a ClassCastException will be thrown
		if(parent!=null){
			try{
				interfaceTrackAdminEvents = (TrackAdminEvents) parent;
			}
			catch(ClassCastException e){}
			try{
				interfaceTrackDataEvents = (TrackDataEvents) parent;
			} 
			catch(ClassCastException e){}
			try{			
				interfaceClassificationEvents = (ClassificationEvents) parent;
			}
			catch(ClassCastException e){}
			try{			
				interfaceTracksListEvents = (TracksListEvents) parent;
			}
			catch(ClassCastException e){}
		}
		http = new Http(context, this);
		http.setDebuggingEnabled(true);
	}
	
	/**
	 * Call this to request this {@link TrackApi} object to retrieve a list of available tracks from the server.
	 * This list of tracks will include all public tracks, any tracks that are visible to other users, and any
	 * private tracks owned by the userId provided when creating this {@link TrackApi} object.
	 * This command will be asynchronously as it involves network communication. Upon successfull completion,
	 * the event {@link TracksListEvents#tracksListAvailable(ArrayList<Track>)} is called in the parent class. 
	 * @param ownerId		If this is not null, only tracks owned by this userId will be returned.
	 */
	public void getTracksList(String ownerId){
		String address = apiAddress+"tracks.php?userId="+userId+"&apiKey="+apiKey+"&serviceKey="+serviceKey+"&includeMetaInfo=true&includeUserInfo=true&includeLogInfo=true";
		if(ownerId!=null){
			address+= "&ownerId="+ownerId;
		}
		http.executeGetRequest(COMMAND_GET_TRACKS_LIST, address, httpRetries, 10, false, null);
	}
	
	/**
	 * Call this to request this {@link TrackApi} object to retrieve a list of classification profiles available.
	 * This list of profiles will include all public profiles, and any private profiles owned by the userId provided when creating this {@link TrackApi} object.
	 * This command will be asynchronously as it involves network communication. Upon successfull completion,
	 * the event {@link ClassificationEvents#classificationProfilesListAvailable(ArrayList)} is called.
	 * @param type		The type of profiles to request from the API, for example {@link TrackApi#CLASSIFICATION_PROFILE_TYPE_CLASSIC}. If null, all types are requested. 
	 */
	public void getAvailableClassificationProfiles(TechniqueType type, boolean forceRefresh){
		String address = apiAddress+"getAvailableClassificationProfiles.php?userId="+userId+"&apiKey="+apiKey+"&serviceKey="+serviceKey;
		if(type!=null){
			address+= "&type="+type.textValue;
		}
		http.executeGetRequest(COMMAND_GET_CLASSIFICATION_PROFILES, address, httpRetries, 10, !forceRefresh, null);
	}
	
	public boolean parseErrors(String apiResponse){
		boolean returnCode = false;
		try {
			JSONObject responseJSON = new JSONObject(apiResponse);
			if(responseJSON.has("ack") || responseJSON.has("data")){
				returnCode = true;
			}
		}
		catch (JSONException e) {}
		return returnCode;
	}
	
	public TrackReference obtainTrackReference(Track track){
		return new TrackReference(track, this);
	}

	@Override
	public void httpRequestComplete(int requestCode, String responseText, Bundle extras) {
		ApiResponse apiResponse = new ApiResponse(responseText);
		int apiState = apiResponse.getState();
		if(apiState==ApiResponse.STATE_ACK || apiState==ApiResponse.STATE_DATA){
			switch(requestCode){
			case COMMAND_UPLOAD_TRACK_FILE:
				handleTrackFileUploaded(apiResponse, extras);
				break;
			case COMMAND_UPLOAD_LOG_FILE:
				handleLogFileUploaded(apiResponse, extras);
				break;
			case COMMAND_GET_CLASSIFICATION_PROFILES:
				handleClassificationProfilesAvailable(apiResponse);
				break;
			case COMMAND_DELETE_TRACK:
				handleDeleteTrack(extras);
				break;
			case COMMAND_DOWNLOAD_TRACK_FILE:
				handleTrackFileDownloaded(extras);
				break;
			case COMMAND_DOWNLOAD_LOG_FILE:
				handleLogFileDownloaded(extras);
				break;
			case COMMAND_GET_TRACK_DATA:
				handleGetTrackData(apiResponse, extras);
				break;
			case COMMAND_RUN_CLASSIFICATION:
				handleRunClassification(apiResponse, extras);
				break;
			case COMMAND_CREATE_CLASSIFICATION_PROFILE:
				handleProfileCreationComplete(apiResponse, extras);
				break;
			case COMMAND_BROADCAST_TRACK_DETAILS:
				handleBroadcastSent(apiResponse, extras);
				break;
			case COMMAND_GET_TRACKS_LIST:
				handleTracksListAvailable(apiResponse, extras);
				break;
			default:
				break;
			}
		}
		else if(apiState==ApiResponse.STATE_WARNING){
			CommonEvents warningInterface = null;
			switch(requestCode){
			case COMMAND_RUN_CLASSIFICATION:
			case COMMAND_GET_CLASSIFICATION_PROFILES:
				warningInterface = interfaceClassificationEvents;
				break;
			case COMMAND_DELETE_TRACK:
			case COMMAND_UPLOAD_TRACK_FILE:
			case COMMAND_UPLOAD_LOG_FILE:
			case COMMAND_BROADCAST_TRACK_DETAILS:
				warningInterface = interfaceTrackAdminEvents;
				break;
			case COMMAND_DOWNLOAD_TRACK_FILE:
			case COMMAND_DOWNLOAD_LOG_FILE:
				warningInterface = interfaceTrackDataEvents;
				break;
			case COMMAND_GET_TRACKS_LIST:
				warningInterface = interfaceTracksListEvents;
			default:
				break;
			}
			if(warningInterface!=null){
				warningInterface.apiWarning(requestCode, apiResponse.getWarning(), extras);
			}
		}
		else if(apiState==ApiResponse.STATE_ERROR){
			CommonEvents errorInterface = null;
			switch(requestCode){
			case COMMAND_RUN_CLASSIFICATION:
			case COMMAND_GET_CLASSIFICATION_PROFILES:
				errorInterface = interfaceClassificationEvents;
				break;
			case COMMAND_DELETE_TRACK:
			case COMMAND_UPLOAD_TRACK_FILE:
			case COMMAND_UPLOAD_LOG_FILE:
			case COMMAND_BROADCAST_TRACK_DETAILS:
				errorInterface = interfaceTrackAdminEvents;
				break;
			case COMMAND_DOWNLOAD_TRACK_FILE:
			case COMMAND_DOWNLOAD_LOG_FILE:
				errorInterface = interfaceTrackDataEvents;
				break;
			case COMMAND_GET_TRACKS_LIST:
				errorInterface = interfaceTracksListEvents;
			default:
				break;
			}
			if(errorInterface!=null){
				errorInterface.apiError(requestCode, apiResponse.getError(), extras);
			}
		}
	}

	private void handleTrackFileDownloaded(Bundle extras) {
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		if(interfaceTrackDataEvents!=null && trackReference!=null){
			File trackFile = trackReference.track.getTrackFile();
			if(trackFile!=null){
				interfaceTrackDataEvents.trackFileAvailable(trackReference, trackFile);
			}
		}
	}

	private void handleLogFileDownloaded(Bundle extras) {
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		String logFileName = extras.getString(KEY_LOG_FILE);
		if(interfaceTrackDataEvents!=null && trackReference!=null && logFileName!=null){
			File logFile = trackReference.track.getLogFile(logFileName);
			if(logFile!=null){
				interfaceTrackDataEvents.logFileAvailable(trackReference, logFile);
			}
			else{
				interfaceTrackDataEvents.logFileDownloadFailed(trackReference);
			}
		}
	}

	private void handleProfileCreationComplete(ApiResponse apiResponse, Bundle extras) {
		String profileId = apiResponse.getAck();
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		if(profileId!=null && interfaceClassificationEvents!=null && trackReference!=null){
			interfaceClassificationEvents.classificationProfileCreated(trackReference, profileId);
		}
	}

	@Override 
	public void httpError(int requestCode, int errorCode, Bundle extras) {
		CommonEvents errorInterface = null;
		switch(requestCode){
		case COMMAND_GET_CLASSIFICATION_PROFILES:
			errorInterface = interfaceClassificationEvents;
			break;
		case COMMAND_DELETE_TRACK:
		case COMMAND_RUN_CLASSIFICATION:
		case COMMAND_UPLOAD_TRACK_FILE:
		case COMMAND_UPLOAD_LOG_FILE:
			errorInterface = interfaceTrackAdminEvents;
			break;
		case COMMAND_GET_TRACKS_LIST:
			errorInterface = interfaceTracksListEvents;
		default:
			break;
		}
		if(errorInterface!=null){
			errorInterface.apiError(requestCode, "HTTP error: "+errorCode, extras);
		}
	}

	@Override
	public void fileUploadProgress(Integer requestCode, long bytesUploaded, long fileSize, Bundle extras) {
		if(interfaceTrackAdminEvents!=null){
			interfaceTrackAdminEvents.fileUploadProgress(requestCode, bytesUploaded, fileSize);
		}
	}

	private void handleGetTrackData(ApiResponse apiResponse, Bundle extras) {
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		if(interfaceTrackDataEvents!=null && trackReference!=null){
			interfaceTrackDataEvents.trackDataAvailable(trackReference, apiResponse.getData());
		}
	}

	private void handleTrackFileUploaded(ApiResponse apiResponse, Bundle extras) {
		TrackReference uploadedTrackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		File uploadedTrackFile = (File) extras.getSerializable(KEY_TRACK_FILE);
		if(interfaceTrackAdminEvents!=null && uploadedTrackReference!=null && uploadedTrackFile!=null){
			String apiId = apiResponse.getTrackId();
			if(apiId!=null){
				interfaceTrackAdminEvents.trackFileUploaded(uploadedTrackReference, uploadedTrackFile);
			}
		}
	}
	
	private void handleLogFileUploaded(ApiResponse apiResponse, Bundle extras) {
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		File uploadedLogFile = (File) extras.getSerializable(KEY_LOG_FILE);
		if(interfaceTrackAdminEvents!=null && trackReference!=null && uploadedLogFile!=null){
			interfaceTrackAdminEvents.logFileUploaded(trackReference, uploadedLogFile);
		}
	}
	
	private void handleBroadcastSent(ApiResponse apiResponse, Bundle extras) {
		TrackReference trackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		if(interfaceTrackAdminEvents!=null && trackReference!=null){
			interfaceTrackAdminEvents.broadcastSent(trackReference);
		}
	}

	private void handleRunClassification(ApiResponse apiResponse, Bundle extras) {
		if(interfaceClassificationEvents!=null){
			TrackReference classifiedTrackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
			String classificationProfileId = extras.getString(KEY_CLASSIFICATION_PROFILE_ID);
			boolean asynchronous = extras.getBoolean(KEY_ASYNCHRONOUS);
			if(classifiedTrackReference!=null && classificationProfileId!=null){
				interfaceClassificationEvents.trackClassificationRequestComplete(classifiedTrackReference, classificationProfileId, asynchronous);
			}
		}
	}

	private void handleDeleteTrack(Bundle extras) {
		TrackReference deletedTrackReference = (TrackReference) extras.getSerializable(KEY_TRACK_REFERENCE);
		if(interfaceTrackAdminEvents!=null){
			interfaceTrackAdminEvents.trackDeleted(deletedTrackReference);
		}
	}

	private void handleClassificationProfilesAvailable(ApiResponse apiResponse){
		if(apiResponse.getState()==ApiResponse.STATE_DATA){
			JSONArray profiles = apiResponse.getData();
			int nProfiles = profiles.length();
			JSONObject temp;
			final ArrayList<ClassificationProfile> profilesList = new ArrayList<ClassificationProfile>();
			for(int i=0; i<nProfiles; i++){
				try{
					temp = profiles.getJSONObject(i);
					String type = temp.getString("type");
					TechniqueType techniqueType = TechniqueType.getTypeFromValue(type);
					if(techniqueType!=null){
						String id = temp.getString("id");
						String name = temp.getString("name");
						String description = temp.optString("description", null);
						profilesList.add(new ClassificationProfile(id, techniqueType, name, description));
					}
				}
				catch(JSONException e){
					Log.e(TAG, "Error parsing classification profile: "+e.toString());
				}
			}
			if(interfaceClassificationEvents!=null){
				interfaceClassificationEvents.classificationProfilesListAvailable(profilesList);
			}
		}		
	}

	private void handleTracksListAvailable(ApiResponse apiResponse, Bundle extras) {
		if(apiResponse.getState()==ApiResponse.STATE_DATA && interfaceTracksListEvents!=null){
			JSONArray tracksJson = apiResponse.getData();
			if(tracksJson!=null){
				int nTracks = tracksJson.length();
				ArrayList<Track> tracksList = new ArrayList<Track>();
				for(int i=0; i<nTracks; i++){
					try {
						Track track = getTrackFromJsonObject(tracksJson.getJSONObject(i));
						if(track!=null){
							tracksList.add(track);
						}
					}
					catch (JSONException e) {
						Log.e(TAG, "Error parsing track: "+e.toString());
					}
				}
				if(!tracksList.isEmpty()){
					interfaceTracksListEvents.tracksListAvailable(tracksList);
				}
				else{
					interfaceTracksListEvents.apiWarning(COMMAND_GET_TRACKS_LIST, "No tracks found", extras);
				}
			}
			else{
				interfaceTracksListEvents.apiError(COMMAND_GET_TRACKS_LIST, "No valid data returned", extras);
			}
		}
	}
	
	private Track getTrackFromJsonObject(JSONObject jsonObject){
		Track track = null;
		try{
			track = new Track(jsonObject.getString("tid"), Long.parseLong(jsonObject.getString("ctd")));
			track.setName(jsonObject.getString("tnm"));
			track.setTrackType(TrackType.fromIntValue(Integer.parseInt(jsonObject.getString("typ"))));
			track.setDataTypes(jsonObject.getString("dat"));
			track.setLogFiles(jsonObject.getString("log"));
			if(jsonObject.has("tim")){
				track.setDuration(jsonObject.optLong("tim"));
			}
			if(jsonObject.has("dis")){
				track.setDistance(jsonObject.optInt("dis"));
			}
			track.setServerState(Track.SERVER_STATE_UPLOADED_REMOTE_DEVICE);
		}
		catch(JSONException e){
			//Log.e(TAG, "Error parsing track: "+e.toString());
			track = null;
		}
		catch(NumberFormatException e){
			//Log.e(TAG, "Error parsing track: "+e.toString());
			track = null;
		}
		return track;
	}
}