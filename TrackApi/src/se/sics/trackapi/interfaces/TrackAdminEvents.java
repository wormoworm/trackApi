package se.sics.trackapi.interfaces;

import java.io.File;

import se.sics.trackapi.core.TrackReference;

/**
 * An interface that can be used for receiving events relating to calls to admin API commands. These events can be simple ACK / ERROR events that
 * denote a command succeeded or failed, or they may contain a set of track data.
 */
public interface TrackAdminEvents extends CommonEvents{

	public void fileUploadProgress(Integer requestCode, long bytesUploaded, long fileSize);
	
	public void trackFileUploaded(TrackReference uploadedTrackReference, File uploadedFile);
	
	public void logFileUploaded(TrackReference trackReference, File uploadedLogFile);
	
	/**
	 * Called when a broadcastTrackDetails request has been issued succesfully.
	 * @param trackReference	A {@link TrackReference} object describing the track that was broadcast.
	 */
	public void broadcastSent(TrackReference trackReference);

	/**
	 * Called when a track has been deleted.
	 * @param deletedTrackReference		A {@link TrackReference} object describing the track that was deleted.
	 */
	public void trackDeleted(TrackReference deletedTrackReference);
}