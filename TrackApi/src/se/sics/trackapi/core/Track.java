package se.sics.trackapi.core;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import se.sics.trackapi.types.TrackType;

public class Track implements Serializable{
	//private final String TAG = "Track";

	private static final long serialVersionUID = 1L;		//Java being pedantic

	/** Track's technique state is unknown. Included for completeness */
	public static final int TECHNIQUE_STATE_UNKNOWN = -1;
	/** Track contains no accelerometer data */
	public static final int TECHNIQUE_STATE_NOT_AVAILABLE = 0;
	/** Track contains accelerometer data but it has not yet been uploaded */
	public static final int TECHNIQUE_STATE_DATA_AVAILABLE = 1;
	/** Track is being analysed */
	public static final int TECHNIQUE_STATE_WAITING = 2;
	/** Track results have been delivered and at least one cycle was found */
	public static final int TECHNIQUE_STATE_RESULTS_AVAILABLE = 3;
	/** Track results have been delivered but no cycles were found */
	public static final int TECHNIQUE_STATE_RESULTS_NOT_AVAILABLE = 4;

	public static final int SERVER_STATE_NOT_UPLOADED = 0;
	public static final int SERVER_STATE_UPLOADED_LOCAL_DEVICE = 1;
	public static final int SERVER_STATE_UPLOADED_REMOTE_DEVICE = 2;

	public static final String TRACK_FILE_NAME = "track.json";
	public static final String FILE_TECHNIQUE_SUMMARY = "technique_summary.json";


	//Basic parameters, used for behind-the-scenes stuff
	protected String id = null;
	protected long timestamp;
	protected String path = null;

	//Track meta-data
	protected String name;
	protected String description;
	protected long duration = 0;
	protected int distance = 0;
	protected int averageHeartRate = 0;
	protected int cycles = 0;
	protected int points = 0;
	protected int techniqueStatus;
	protected String dataTypesString;
	protected String logFilesString;
	protected String ownerId;
	protected TrackType trackType;
	protected int serverState;
	protected boolean favourite;

	/**	Constructor. Use when the Track being created exists on the file system.
	 * @param id		The id of the track
	 * @param timestamp	The timestamp of this track (the time at which it was created), expressed in milliseconds since Jan 1st 1970
	 * @param path		The path to the root directory where this track's files are stored
	 */
	public Track(String id, long timestamp, String path){
		this.id = id;
		this.timestamp = timestamp;
		if(path!=null && !path.endsWith("/")){		//Make sure the path ends with a '/' character
			path = path+'/';
		}
		this.path = path;
		techniqueStatus = TECHNIQUE_STATE_NOT_AVAILABLE;
		trackType = TrackType.TRACK;
		name = id;
		serverState = SERVER_STATE_NOT_UPLOADED;
		favourite = false;
	}

	/**	Constructor. Use when the Track being created does not exist on the file system (e.g. if the Track has come from a list pulled from a server).
	 * @param id		The id of the track
	 * @param timestamp	The timestamp of this track (the time at which it was created), expressed in milliseconds since Jan 1st 1970
	 */
	public Track(String id, long timestamp){
		this(id, timestamp, null);
	}

	/**	Sets this track's name.
	 * @param name The name to set.
	 */
	public Track setName(String name) {
		this.name = name;
		return this;
	}

	/**	Sets this track's path.
	 * @param name The path to the root directory where this track's files are stored.
	 */
	public Track setPath(String path){
		if(path!=null && !path.endsWith("/")){		//Make sure the path ends with a '/' character
			path = path+'/';
		}
		this.path = path;
		return this;
	}

	/**	Sets this track's description.
	 * @param name The description to set.
	 */
	public Track setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Sets this track's duration.
	 * @param duration The duration to set, in seconds.
	 */
	public Track setDuration(Long duration) {
		this.duration = duration;
		return this;
	}

	/**
	 * Sets this track's type.
	 * @param trackType 	The new track type.
	 */
	public Track setTrackType(TrackType trackType) {
		if(trackType!=null){
			this.trackType = trackType;
		}
		return this;
	}

	/**
	 * Sets this track's distance.
	 * @param distance The distance to set.
	 */
	public Track setDistance(Integer distance) {
		if(distance!=null){
			this.distance = distance;
		}
		return this;
	}

	/**
	 * Sets this track's average heart rate.
	 * @param averageHeartRate the averageHeartRate to set.
	 */
	public Track setAverageHeartRate(Integer averageHeartRate) {
		if(averageHeartRate!=null){
			this.averageHeartRate = averageHeartRate;
		}
		return this;
	}

	/**
	 * Sets this track's cycles.
	 * @param cycles The cycles to set.
	 */
	public Track setCycles(Integer cycles) {
		if(cycles!=null){
			this.cycles = cycles;
		}
		return this;
	}

	/**
	 * Sets this track's activity points.
	 * @param points 	The points to set.
	 */
	public Track setPoints(Integer points) {
		if(points!=null){
			this.points = points;
		}
		return this;
	}

	/**
	 * Sets this track's technique status.
	 * @param points 	The new technique status. Must be one of: {@link Track#TECHNIQUE_STATE_NOT_AVAILABLE}, {@link Track#TECHNIQUE_STATE_WAITING} or {@link Track#TECHNIQUE_STATE_AVAILABLE}.
	 */
	public Track setTechniqueStatus(Integer status) {
		if(status!=null){
			techniqueStatus = status;
		}
		return this;
	}

	/**
	 * Adds a log file to this track.
	 * @param logFile 	The name of the log file you wish to add.
	 */
	public Track addLogFile(String logFile) {
		if(logFilesString==null || logFilesString.length()==0){		//Set of log files is empty or 0 length, so add this log file straight away
			logFilesString = logFile;
		}
		else{
			if(!containsLogFile(logFile)){							//Only proceed if this track does not alredy contain this log file
				logFilesString+= ','+logFile;
			}
		}
		return this;
	}

	public Track setServerState(int serverState){
		this.serverState = serverState;
		return this;
	}

	public Track setFavourite(boolean favourite){
		this.favourite = favourite;
		return this;
	}

	/**
	 * Adds a data type to this track.
	 * @param dataType 	The {@link SensorDataType} you wish to add.
	 */
	public Track addDataType(String dataTypeCode) {
		if(dataTypesString==null || dataTypesString.length()==0){		//Set of data types is empty or 0 length, so add this type straight away
			dataTypesString = dataTypeCode;
		}
		else{
			if(!containsDataType(dataTypeCode)){				//Only proceed if this track does not alredy contain this type
				dataTypesString+= ','+dataTypeCode;
			}
		}
		return this;
	}

	public Track setDataTypes(String dataTypes){
		dataTypesString = dataTypes;
		return this;
	}

	public Track setLogFiles(String logFiles){
		logFilesString = logFiles;
		return this;
	}

	/**
	 * Sets the track's owner (who created it).
	 * @param ownerId		The user ID of the track's creator.
	 */
	public Track setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	/** Returns this track's id 
	 * @return The track's id
	 */
	public String getId() {
		return id;
	}

	/**	Returns this track's timestamp (the time at which it was created)
	 * @return The timestamp, represented as milliseconds since Jan 1st 1970
	 */
	public long getTimestamp() {
		if(timestampIsMilliseconds(timestamp)){
			return timestamp / 1000;
		}
		else{
			return timestamp;
		}
	}

	private boolean timestampIsMilliseconds(long timestamp) {
		return timestamp > 947414504000L;
	}

	/** Returns the path to the root directory where this track's files are stored
	 * @return The path, relative to the system root
	 */
	public String getPath() {
		return path;
	}

	/** Return's this track's name.
	 * @return The name, or the track's ID if the name has not been set using {@link Track#setName(String)}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns this track's description.
	 * @return	The description, or null if there is not one available.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns this track's duration.
	 * @return The duration in milliseconds, which may be 0.
	 */
	public Long getDuration() {
		return duration;
	}

	/** Returns the technique status of this track. This value will be one of the TECHNIQUE_STATUS_XX constants defined in this class.
	 * @return The track's technique status.
	 */
	public int getTechniqueStatus() {
		return techniqueStatus;
	}

	/**
	 * Returns the distance of this track.
	 * @return The distance in metres, which may be 0.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Returns's the average speed of this track.
	 * @return The average speed in metres per second, which may be 0.
	 */
	public float getAverageSpeed(){
		if(duration!=0 && distance!=0){
			return (float) distance / (float) duration;
		}
		else return 0;
	}

	/**
	 * Returns this track's average heart rate.
	 * @return The average heart rate, which may be 0.
	 */
	public Integer getAverageHeartRate() {
		return averageHeartRate;
	}

	/**
	 * Returns this track's total cycles.
	 * @return The cycles, which may be 0.
	 */
	public Integer getCycles() {
		return cycles;
	}

	/**
	 * Returns this track's activity points.
	 * @return The points, which may be 0.
	 */
	public Integer getPoints() {
		return points;
	}

	public int getServerState(){
		return serverState;
	}

	public boolean isFavourite(){
		return favourite;
	}

	/**
	 * Checks whether this track contains the data type.
	 * @param typeCode		The short code of the data type.
	 * @return				True if this track contains this data type, false otherwise.
	 */
	private boolean containsDataType(String typeCode) {
		return getDataTypes().contains(typeCode);
	}

	/**
	 * Checks whether this track contains the specified log file.
	 * @param logFile		The name of the log file.
	 * @return				True if this track contains this log file, false otherwise.
	 */
	public boolean containsLogFile(String logFile) {
		return getLogFileNames().contains(logFile);
	}

	/**
	 * Returns the list of data types associated with this track.
	 * @return		An ArrayList of Strings, where each element is a short code of a data type belonging to this track. This may be empty.
	 */
	public ArrayList<String> getDataTypes(){
		ArrayList<String> dataTypes = new ArrayList<String>();

		if(dataTypesString!=null){
			String[] typesArray = dataTypesString.split(",");
			int nLogFiles = typesArray.length;


			for(int i=0; i<nLogFiles; i++){
				dataTypes.add(typesArray[i]);
			}
		}
		return dataTypes;
	}

	/**
	 * Returns the list of data types associated with this track.
	 * @return		A comma-separated list of the short codes of each data type this track contains. This may be empty.
	 */
	public String getDataTypesString(){
		return dataTypesString;
	}

	/**
	 * Returns a list of names of all log files associated with this track.
	 * @return		An ArrayList of {@link Strings}, where each element is the name of a log file belonging to this track. This may be empty.
	 */
	public ArrayList<String> getLogFileNames(){
		ArrayList<String> logFiles = new ArrayList<String>();

		if(logFilesString!=null){
			String[] logFilesArray = logFilesString.split(",");
			int nLogFiles = logFilesArray.length;

			for(int i=0; i<nLogFiles; i++){
				logFiles.add(logFilesArray[i]);
			}
		}
		return logFiles;
	}

	/**
	 * Returns a list of names of all log files associated with this track.
	 * @return		A comma-separated list of the short codes of each data type this track contains.
	 */
	public String getLogFileNamesString(){
		return logFilesString;
	}

	/**
	 * Returns the track's owner (who created it).
	 * @return		The user ID of the track's creator, or null if not available.
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * Returns a {@link File} object that represents this tracks's main track file.
	 * @return		The main track file. If the file does not exist, this will be null.
	 */
	public File getTrackFile() {
		File trackFile = new File(path+TRACK_FILE_NAME);
		if(trackFile.exists()){
			return trackFile;
		}
		else{
			return null;
		}
	}

	/**
	 * Returns a {@link File} object that represents the requested log file.
	 * @return		The log file, or null if this particular log file does not exist.
	 */
	public File getLogFile(String logFileName) {
		File logFile = new File(path+logFileName);
		if(logFile.exists()){
			return logFile;
		}
		else{
			return null;
		}
	}

	/**
	 * Returns the type of this track.
	 * @return		The type.
	 */
	public TrackType getTrackType() {
		return trackType;
	}

	/**
	 * Compares this track to the provided track. If the tracks are the same (have the same ID), then true is returned.
	 * @param track		The Track to which you are comparing to.
	 * @return			True if the Tracks match (have the same ID), false otherwise.
	 */
	public boolean equals(Track track){		
		return getId().equals(track.getId());
	}
}