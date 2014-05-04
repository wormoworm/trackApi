package se.sics.trackapi.ski5Cloud;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import se.sics.trackapi.R;
import android.os.AsyncTask;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class Ski5Cloud {
	Ski5CloudHttpClient client;	 
	
	final Header[] headers;
	final ExampleActivity a;
	public Ski5Cloud(ExampleActivity a){
		headers = Utils.getDefaultHeaders(Api.TOKEN, Api.USER_NAME); // you need to re-implement this, 
																	 //because TOKEN and USER_NAME are not there initially
		client = new Ski5CloudHttpClient(this);
		this.a=a;
	}
	public void response(final String resp, final String requrl) {	
		LogH.i("resp : {0}", resp);
		this.a.runOnUiThread(new Runnable (){
			@Override
			public void run() {
				a.asynchResult(resp);
			}			
		});
	}
	
	public void signIn(String userName,String password){
		String url = Api.signIn();
		Map<String,String> map = new HashMap<String, String>();
		map.put("user_name", userName);
		map.put("password",password);
		client.doJsonPost(url, Utils.toJson(map), null);
	}	
	public void upload(){
		String url = Api.uploadTicket();
		client.doJsonPost(url, null, headers,new UploadHandler(url));
		LogH.i("URL -----> {0}",url);
	}
	public void getUserTech(int userId,long startts,long endts){
		String url = Api.userTech(startts, endts);
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserStats(long startts,long endts){
		String url = Api.userStats(startts, endts);
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserTracks(){
		String url = Api.userTracks();
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getTrackLogFiles(String trackId){
		String url = Api.trackLogFiles(trackId);
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void getUserStyles(){
		String url = Api.userStyles();
		client.doGetRequest(url, headers);
		LogH.i("URL -----> {0}",url);
	}
	public void classify(String trackId,String styleId,String logFileId ){
		String url = Api.trackClassifications(trackId);
		Map<String,String> classification = new HashMap<String, String>();
		Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
		classification.put("log_file_id", logFileId);
		classification.put("style_id", styleId);
		data.put("classification", classification);
		client.doJsonPost(url, Utils.toJson(data), headers);
		LogH.i("URL -----> {0}",url);
	}
	public void submitTrack(String uploadId,long recordedAt,String s3path, int duration,int distance, int avgHeartRate){
		Map<String, Object> track = new HashMap<String, Object>();
		track.put("recorded_at", recordedAt+"");
		track.put("duration", duration+"");
		track.put("distance", distance+"");
		track.put("avg_heart_rate", avgHeartRate+"");
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("track", track);
		data.put("s3path", s3path);
		String url = Api.submitTrack(uploadId);
		client.doJsonPost(url, Utils.toJson(data), headers);		
	}
	//----------------- S3 upload ----------
	private class SubmissiontTask extends AsyncTask<String, Void, S3TaskResult> {
		protected S3TaskResult doInBackground(String... resp) {
			LogH.i("TICKET RESPONES");
			Map<String,String> map = Utils.toMap(resp[0]);
			LogH.i("MAP TOKEN => {0}", map);
			String folder = map.get("folder");
			String s3Tocken  = map.get("s3token");
			String uploadId = map.get("id");
			LogH.i("FOLDER => {0}", folder);
			LogH.i("S3Token", s3Tocken);
			BasicSessionCredentials c = Utils.getToken(s3Tocken);
			AmazonS3Client s3Client = new AmazonS3Client(c);
			S3TaskResult result = new S3TaskResult();
			result.setUri(folder);			
			try {				
				ObjectMetadata om = new ObjectMetadata();
				om.setContentLength(7149);
				PutObjectRequest por = new PutObjectRequest(Constants.BUCKET, folder+"/all.zip", a.getResources().openRawResource(R.raw.track_1369), om);						
				s3Client.putObject(por);
				LogH.i("DONE UPLOADING TRACK TO {0}", folder);
				submitTrack(uploadId, 1366715885, folder+"/all.zip", 123, 124, 125);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {
			LogH.i("DONE => {0}",result );
		}
	}
	private class S3TaskResult {
		String errorMessage = null;
		String uri = null;
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		@Override
		public String toString() {
			
			return Utils.fmt("Uri : {0} , Err: {1}",uri,errorMessage);
		}
	}
	
	class UploadHandler extends ResponseHandlerAdapter{

		public UploadHandler( String url) {
			super(url);
		}
		@Override
		void handleCloudResponse(byte[] binaryData, int statusCode,
				Header[] headers, String reqUrl) {			
			new SubmissiontTask().execute(new String(binaryData));
		}
	}
}
