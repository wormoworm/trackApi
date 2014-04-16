package se.sics.trackapi.types;

public enum TechniqueSubType{
	DIAGONAL ("dia", "Diagonal"),
	DP ("dp", "Double poling"),
	DP_KICK ("dpk", "Kick double poling");
	
	public final String shortCode, fullName;
	
	TechniqueSubType(String shortCode, String fullName){
		this.shortCode = shortCode;
		this.fullName = fullName;
	}
	
	public static TechniqueSubType getTypeFromShortCode(String shortCode){
		TechniqueSubType returnType = null;
		if(shortCode!=null){
			if(shortCode.equals(DIAGONAL.shortCode)){
				returnType = DIAGONAL;
			}
			else if(shortCode.equals(DP.shortCode)){
				returnType = DP;
			}
			else if(shortCode.equals(DP_KICK.shortCode)){
				returnType = DP_KICK;
			}
		}
		return returnType;
	}
}