package se.sics.trackapi.interfaces;

import android.os.Bundle;

/**
 * All interfaces extend this base interface.
 */
public interface CommonEvents{
	public void apiError(int requestCode, String errorText, Bundle extras);
	
	public void apiWarning(int requestCode, String warningText, Bundle extras);
}