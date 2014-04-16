package se.sics.trackapi.interfaces;

import java.util.ArrayList;

import se.sics.trackapi.classification.ClassificationProfile;
import se.sics.trackapi.core.TrackReference;

public interface ClassificationEvents extends CommonEvents{
	
	public void classificationProfilesListAvailable(ArrayList<ClassificationProfile> profiles);

	public void trackClassificationRequestComplete(TrackReference classifiedTrackReference, String classificationProfileId, boolean asynchronous);
	
	public void classificationProfileCreated(TrackReference sourceTrackReference, String profileId);
}