package se.sics.trackapi.types;

public enum TrackType{
	NONE (0, "none"),
	TRACK (1, "track"),
	PROFILE_DATA (2, "profile_data"),
	TEST_RESULT(3, "test_result");
	
	public final int intValue;
	public final String textValue;
	
	TrackType(int intValue, String textValue){
		this.intValue = intValue;
		this.textValue = textValue;
	}
	
	public static TrackType fromIntValue(int intValue){
		TrackType returnType = null;
		switch(intValue){
		default:
		case 0:
			returnType = NONE;
			break;
		case 1:
			returnType = TRACK;
			break;
		case 2:
			returnType = PROFILE_DATA;
			break;
		case 3:
			returnType = TEST_RESULT;
			break;
		}
		return returnType;
	}
}