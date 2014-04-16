package se.sics.trackapi.types;

public enum TechniqueType{
	/**Identifies the "Classic" technique type*/
	CLASSIC ("classic"),
	/**Identifies the "Skate" technique type*/
	SKATE ("skate");
	
	public final String textValue;
	
	TechniqueType(String textValue){
		this.textValue = textValue;
	}
	
	public static TechniqueType getTypeFromValue(String value){
		TechniqueType returnType = null;
		if(value!=null){
			if(value.equals(CLASSIC.textValue)){
				returnType = CLASSIC;
			}
			else if(value.equals(SKATE.textValue)){
				returnType = SKATE;
			}
		}
		return returnType;
	}
}