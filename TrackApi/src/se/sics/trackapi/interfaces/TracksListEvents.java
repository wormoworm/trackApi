package se.sics.trackapi.interfaces;

import java.util.ArrayList;

import se.sics.trackapi.core.Track;

public interface TracksListEvents extends CommonEvents{
	
	public void tracksListAvailable(ArrayList<Track> tracks);
}