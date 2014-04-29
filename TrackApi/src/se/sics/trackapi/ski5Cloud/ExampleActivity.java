package se.sics.trackapi.ski5Cloud;

import se.sics.trackapi.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ExampleActivity extends Activity {
	Ski5CloudTester ct;
	public static Ski5Cloud ski5;
	TextView out;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		//client  = new Ski5CloudHttpClient();
		ski5 = new Ski5Cloud(this);
		ct = new Ski5CloudTester(ski5);
		ct.execute();
		out= (TextView) findViewById(R.id.output);
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
		//ski5.signIn("magnus@tii.se", "password");
		//ski5.getUserTech( 0, new Date().getTime()/1000);
		//ski5.getUserStats( 0, new Date().getTime()/1000);		
		//ski5.getUserTracks();
		ski5.getUploadTicket();
		return null;
	}
	@Override
	protected void onPostExecute(Void d) {		
		super.onPostExecute(d);		
	}
}

