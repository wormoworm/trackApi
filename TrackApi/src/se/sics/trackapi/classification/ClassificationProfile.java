package se.sics.trackapi.classification;import java.io.Serializable;

import se.sics.trackapi.core.TrackApi;
import se.sics.trackapi.types.TechniqueType;

/**
 * A class that represents a classification profile. These profiles are downloaded from the server using the command
 * {@link TrackApi#getAvailableClassificationProfiles()}.
 *
 */
public class ClassificationProfile implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int PROFILE_TYPE_CLASSIC = 1;
	public static final int PROFILE_TYPE_SKATE = 2;
	
	private String id;
	private TechniqueType type;
	private String name;
	private String description;
	
	public ClassificationProfile(String id, TechniqueType type, String name, String description){
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
	}
	public String getId() {
		return id;
	}

	public TechniqueType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}