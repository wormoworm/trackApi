package se.sics.trackapi.ski5Cloud;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import se.sics.trackapi.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ExampleActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "761665968034";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;

	Ski5CloudTester ct;
	public static Ski5Cloud ski5;
	TextView out;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		//client  = new Ski5CloudHttpClient();
		// Check device for Play Services APK.
		out= (TextView) findViewById(R.id.output);
		context = getApplicationContext();
	    if (checkPlayServices()) {
	    	gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            
            if (regid.isEmpty()) {
                registerInBackground();
            }else{
            	startSki5();
            	LogH.i("REQISTRATION ID : {0}",regid);
            }
	    }
		
		
	    

	}
	private void startSki5() {
		Api.gcmId = regid;
    	ski5 = new Ski5Cloud(this);
		ct = new Ski5CloudTester(ski5);
		ct.execute();		
	}
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        LogH.i( "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        LogH.i( "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(ExampleActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	private void registerInBackground() {
	    new AsyncTask<Void,Void,String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                Api.gcmId=regid;
	                msg = "Device registered, registration ID=" + regid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	out.setText(msg + "\n");
	        	startSki5();
	        }
	    }.execute(null, null, null);
	    //...
	}
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	}
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    LogH.i( "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}


	private boolean  checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            LogH.i("This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void asynchResult(String result){		
		out.setText(result);
	}
	
	
}
//----------- Async---
class Ski5CloudTester extends AsyncTask<Void, Void, Void>{	
		
	
	Ski5Cloud ski5;
	public Ski5CloudTester(Ski5Cloud ski5) {
		this.ski5 = ski5;
	}

	@Override
	protected Void doInBackground(Void... params) {
		System.out.println("signing in");
		ski5.signIn("magnus@tii.se", "password");
		//ski5.getUserTech( 0, new Date().getTime()/1000);
		//ski5.getUserStats( 0, new Date().getTime()/1000);		
		//ski5.getUserTracks();
		//ski5.upload();
		//ski5.getTrackLogFiles("622");
		//ski5.getUserStyles();
		//ski5.classify("622", "1", "390");
		
		return null;
	}
	@Override
	protected void onPostExecute(Void d) {		
		super.onPostExecute(d);		
	}
}

