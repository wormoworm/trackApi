package se.sics.trackapi.core;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestDataOptions implements Serializable{
	private static final long serialVersionUID = 1L;
	private String requestedTypes;
	
	public RequestDataOptions(){
	}
	
	public RequestDataOptions setRequestedTypes(String requestedTypes){
		this.requestedTypes = requestedTypes;
		return this;
	}
	
	/**
	 * Adds a log file to this track.
	 * @param logFile 	The name of the log file you wish to add.
	 * @return			A copy of the {@link RequestDataOptions} object, with this data type added.
	 */
	public RequestDataOptions addRequestedType(String requestedType) {
		if(requestedTypes==null || requestedTypes.length()==0){		//Set of log files is empty or 0 length, so add this log file straight away
			requestedTypes = requestedType;
		}
		else{
			if(!containsDataType(requestedType)){							//Only proceed if this track does not alredy contain this log file
				requestedTypes+= ','+requestedType;
			}
		}
		return this;
	}
	
	/**
	 * Returns the list of data types associated with this {@link TrackDataOptions} object.
	 * @return		An ArrayList of Strings, where each element is a short code of a data type belonging to this TrackDataOptions object. This may be empty.
	 */
	public ArrayList<String> getRequestedTypes(){
		ArrayList<String> dataTypes = new ArrayList<String>();
		if(requestedTypes!=null){
			String[] typesArray = requestedTypes.split(",");
			int nLogFiles = typesArray.length;
			
			
			for(int i=0; i<nLogFiles; i++){
				dataTypes.add(typesArray[i]);
			}
		}
		return dataTypes;
	}
	
	/**
	 * Returns the list of data types associated with this {@link TrackDataOptions} object.
	 * @return		A comma-separated list of the short codes of each data type this TrackDataOptions object contains. This may be empty.
	 */
	public String getRequestedTypesString(){
		return requestedTypes;
	}
	
	/**
	 * Checks whether this {@link TrackDataOptions} object contains the specified data type.
	 * @param typeCode		The short code of the data type.
	 * @return				True if this TrackDataOptions object contains this data type, false otherwise.
	 */
	public boolean containsDataType(String typeCode) {
		return getRequestedTypes().contains(typeCode);
	}
}
