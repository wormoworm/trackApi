package se.sics.trackapi.classification;

import se.sics.trackapi.types.TechniqueSubType;

public class CreateClassificationProfileSubTechnique{
	
	public final TechniqueSubType subType;
	public final long startTime, endTime;
	
	public CreateClassificationProfileSubTechnique(TechniqueSubType subType, long startTime, long endTime){
		this.subType = subType;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}