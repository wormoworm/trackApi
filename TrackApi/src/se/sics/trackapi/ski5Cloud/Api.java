package se.sics.trackapi.ski5Cloud;
import static se.sics.trackapi.ski5Cloud.Utils.fmt;

import com.loopj.android.http.RequestParams;
public class Api {
	//----------- ESSENTIAL CONFIGURATIONS-----------
	public static final int ACCOUNT_ID=3; // you can get the account id from the response of the sign in (form multi-account api)
	
	public static final String HOST= "http://ski5-staging.herokuapp.com";
	
	public static final String SITE= fmt("{0}/accounts/{1}",HOST,ACCOUNT_ID);
	
	public static  int USER_ID=5;
	
	public static  String USER_NAME = "magnus@tii.se";
	
	public static  String TOKEN ="457c7ff34e0a90f3b0687677dea566cc";
	
	//------------ APIs-----------
//	public static final String ALL_USERS=fmt("{0}/users.json" ,SITE);;
	private static final String USER_TRACKS= "{0}/users/{1}/userTracks";
	private static final String USER_CLASSIFICATIONS= "{0}/tracks/{1}/classifications";
	
	private static final String USER_STATS= "{0}/users/{1}/userStats";	
	private static final String USER_TECH= "{0}/users/{1}/userTechStats";
	private static final String USER_HR= "{0}/users/{1}/userHeartRateStats";
	
	private static final String SIGN_IN= fmt("{0}/api_users/sign_in.json",HOST);
	private static final String UPLOAD_TICKET = "{0}/uploads";
	
	
	public static String trackClassifications(int trackId){
		return  fmt(USER_CLASSIFICATIONS,SITE,trackId);
	}
	
	public static  String uploadTicket(){				
		return fmt(UPLOAD_TICKET,SITE);
	}
	
	public static String userTracks(){
		return fmt(USER_TRACKS,SITE,USER_ID);
	}
	public static String signIn(){
		return SIGN_IN;
	}
	
	public static String userHR(long startTs,long endTs){
		return stats(USER_HR,startTs,endTs);
	}
	
	public static String userTech(long startTs,long endTs){
		return stats(USER_TECH,startTs,endTs);		
	}

	public static String userStats(long startTs,long endTs){
		return stats(USER_STATS,startTs,endTs);		
	}
	
	private static String stats(String type,long startTS,long endTs){
		RequestParams params = new RequestParams();
		params.add("startTime", startTS+"");
		params.add("endTime", endTs+"");
		String url = fmt(type,SITE,USER_ID);
		String withParams= fmt("{0}?{1}",url,params.toString());
		LogH.i("URL WITH PARAMs ====> {0}", withParams);
		return withParams;		
	}
}
