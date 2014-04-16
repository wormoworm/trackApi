package se.sics.trackapi.interfaces;

import java.io.File;

import org.json.JSONArray;

import se.sics.trackapi.core.TrackReference;

/**
 * An interface that can be used for receiving events relating to calls to data API commands. For example, events are
 * fired when a track or log file has finished downloading.
 */
public interface TrackDataEvents extends CommonEvents{
	
	public void trackDataAvailable(TrackReference trackReference, JSONArray trackData);
	
	public void trackFileAvailable(TrackReference trackReference, File trackFile);
	
	public void logFileAvailable(TrackReference trackReference, File logFile);
	
	public void trackFileDownloadFailed(TrackReference trackReference);
	
	public void logFileDownloadFailed(TrackReference trackReference);
}