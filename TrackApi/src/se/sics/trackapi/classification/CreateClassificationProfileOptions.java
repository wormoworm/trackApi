package se.sics.trackapi.classification;import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.sics.trackapi.types.SharingMode;
import se.sics.trackapi.types.TechniqueType;

import android.util.Log;

public class CreateClassificationProfileOptions{
	private final String TAG = "CreateClassificationProfileOptions";

	private String profileName;
	private String logFileName;
	private TechniqueType techniqueType;
	private SharingMode sharingMode;
	private ArrayList<CreateClassificationProfileSubTechnique> subTechniques;
	
	public CreateClassificationProfileOptions(){
		subTechniques = new ArrayList<CreateClassificationProfileSubTechnique>();
	}

	public CreateClassificationProfileOptions setName(String profileName){
		this.profileName = profileName;
		return this;
	}
	
	public CreateClassificationProfileOptions setLogFileName(String logFileName){
		this.logFileName = logFileName;
		return this;
	}
	
	public CreateClassificationProfileOptions setTechniqueType(TechniqueType techniqueType){
		this.techniqueType = techniqueType;
		return this;
	}
	
	public CreateClassificationProfileOptions setSharingMode(SharingMode sharingMode){
		this.sharingMode = sharingMode;
		return this;
	}
	
	public CreateClassificationProfileOptions addSubTechnique(CreateClassificationProfileSubTechnique subTechnique){
		subTechniques.add(subTechnique);
		return this;
	}
	
	public String getName(){
		return profileName;
	}
	
	public String getLogFileName(){
		return logFileName;
	}
	
	public TechniqueType getTechniqueType(){
		return techniqueType;
	}
	
	public SharingMode getSharingMode(){
		return sharingMode;
	}
	
	public boolean hasData(){
		return subTechniques.size()>0;
	}
	
	public ArrayList<CreateClassificationProfileSubTechnique> getSubTechniques(){
		return subTechniques;
	}
	
	public JSONArray getSegments(){
		JSONArray segments = null;
		if(subTechniques.size()>0){
			segments = new JSONArray();
			CreateClassificationProfileSubTechnique tempSubTechnique;
			JSONObject tempJson;
			Iterator<CreateClassificationProfileSubTechnique> iterator = subTechniques.iterator();
			while(iterator.hasNext()){
				tempSubTechnique = iterator.next();
				tempJson = new JSONObject();
				try{
					tempJson.put("gear", tempSubTechnique.subType.shortCode);
					tempJson.put("start", tempSubTechnique.startTime);
					tempJson.put("end", tempSubTechnique.endTime);
					segments.put(tempJson);
				}
				catch(JSONException e){
					Log.e(TAG, "Error parsing sub technique to JSON: "+e.toString());
				}
			}
		}
		return segments;
	}
}
