package se.sics.trackapi.types;

public enum SharingMode{
	PRIVATE ("private"),
	USERS ("users"),
	PUBLIC ("public");
	
	public final String textValue;
	
	SharingMode(String textValue){
		this.textValue = textValue;
	}
	
	public static SharingMode getTypeFromValue(String value){
		SharingMode returnType = null;
		if(value!=null){
			if(value.equals(PRIVATE.textValue)){
				returnType = PRIVATE;
			}
			else if(value.equals(USERS.textValue)){
				returnType = USERS;
			}
			else if(value.equals(PUBLIC.textValue)){
				returnType = PUBLIC;
			}
		}
		return returnType;
	}
}