package com.wish.wishlist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import java.net.URISyntaxException; 
import java.net.URI; 
import java.io.File;
import java.io.IOException; 


import com.wish.wishlist.R;

public class Splash extends Activity{

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.splash);
	Handler x = new Handler();
	x.postDelayed(new splashhandler(), 2000);
	//String uri = "http://fare.sinaapp.com/UpImg.php";
}

//	public static String inputStream2String(InputStream is) throws IOException {  
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//		int i = -1;  
//		while ((i = is.read()) != -1) {  
//			baos.write(i);  
//		}  
//		return baos.toString();  
//	}  

//	FileInputStream in = openFileInput("/data/local/tmp/a.png");
//	InputStreamReader inputStreamReader = new InputStreamReader(in);
//	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//	StringBuilder sb = new StringBuilder();
//	String line;
//	while ((line = bufferedReader.readLine()) != null) {
//		sb.append(line);
//	}
	
	class splashhandler implements Runnable{
		public void run() {
			startActivity(new Intent(getApplication(),DashBoard.class));
			startActivity(new Intent(getApplication(), Login.class));
			Splash.this.finish();

			AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... voids) {
					Log.d("Splash", "doInBackground");
					try {
						String uri = "http://fare.sinaapp.com/";
						HttpResponse response = null;
						try {        
							HttpClient client = new DefaultHttpClient();
							HttpGet request = new HttpGet();
							//request.setURI(new URI("https://www.googleapis.com/shopping/search/v1/public/products/?key={my_key}&country=&q=t-shirts&alt=json&rankByrelevancy="));
							request.setURI(new URI(uri));
							response = client.execute(request);
						} catch (URISyntaxException e) {
							Log.d("Splash", "exception");
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						}   

						HttpEntity entity = response.getEntity();
						if (entity == null) {
							Log.d("Splash", "entity is null");
							return "";
						}

						String result = "";
						try {
							result = EntityUtils.toString(entity);
							return result;
						}
						catch (IOException e) {
							e.printStackTrace();
						}

						//	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
						//	entity.writeTo(outstream);
						//	byte [] responseBody = outstream.toByteArray();

						//return response;
					} catch (Exception e) {
						//this.exception = e;
					}
					return "";
				}
				@Override
				protected void onPostExecute(String result) {
					Log.d("Splash", "response is " + result);
					// TODO: check this.exception 
					// TODO: do something with the feed
				}
			};
			//task.execute();

			AsyncTask<Void, Void, String> postTask = new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... voids) {
					Log.d("Splash postTask", "doInBackground");
					try {
						String uri = "http://fare.sinaapp.com/UpImg.php";
						HttpResponse response = null;
						try {        
							HttpClient client = new DefaultHttpClient();
							HttpPost post = new HttpPost(uri);
							MultipartEntity entity = new MultipartEntity();
						//	entity.addPart("action", new StringBody("up"));
						//	entity.addPart("action", new StringBody("up"));
						//	entity.addPart("name", new StringBody("testImage"));
						//	entity.addPart("content", new StringBody("99999"));
							//List<NameValuePair> params = new ArrayList<NameValuePair>();
							//params.add(new BasicNameValuePair("action", "up"));
							//params.add(new BasicNameValuePair("name", "testImage"));
							//params.add(new BasicNameValuePair("content", "1111"));
							//UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
							post.setEntity(entity);
							response = client.execute(post);
						}
						catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						}   

						HttpEntity entity = response.getEntity();
						if (entity == null) {
							Log.d("Splash post", "entity is null");
							return "";
						}

						String result = "";
						try {
							result = EntityUtils.toString(entity);
							return result;
						}
						catch (IOException e) {
							e.printStackTrace();
						}

						//	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
						//	entity.writeTo(outstream);
						//	byte [] responseBody = outstream.toByteArray();

						//return response;
					} catch (Exception e) {
						//this.exception = e;
					}
					return "";
				}
				@Override
				protected void onPostExecute(String result) {
					Log.d("Splash post", "response is " + result);
					// TODO: check this.exception 
					// TODO: do something with the feed
				}
			};
			//postTask.execute();

			AsyncTask<Void, Void, String> stageImageTask = new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... voids) {
					Log.d("Splash stageImageTask", "doInBackground");
					try {
						String uri = "https://graph.facebook.com/100005720562778/staging_resources";
						String token = "BAADJTZCh0pA8BAIGX71M0lnVJfdshs4JAx5ra38C9uVfHMTyTWlezd2dTuExBq1BUIEJcoCEtVTH5WcCzNe9LYlRsldge5JmAbhfehePsDUjcLQI6skPLFyth0cfmbGgxIuXbJ2gGZCSRCzooGAW5aEXWZCJqy8NuehNMiuWFg0KCCTDZBEo4rU8dPx95dEkjbXXJCeMaW1qHUlSbMVoDHmUB7SlqZCxa6ahwtxTNzgZDZD";
						HttpResponse response = null;
						try {        
							HttpClient client = new DefaultHttpClient();
							HttpPost post = new HttpPost(uri);
							MultipartEntity entity = new MultipartEntity();
						//	entity.addPart("action", new StringBody("up"));
						//	entity.addPart("action", new StringBody("up"));
						//	entity.addPart("name", new StringBody("testImage"));
						//	entity.addPart("content", new StringBody("99999"));
							File file = new File("/data/local/tmp/images.jpg");
							Log.d("Splash", "UPLOAD: file length = " + file.length());
							Log.d("Splash", "UPLOAD: file exist = " + file.exists());

							entity.addPart("file", new FileBody(file, "image/jpeg"));
							entity.addPart("access_token", new StringBody(token));
							//List<NameValuePair> params = new ArrayList<NameValuePair>();
							//params.add(new BasicNameValuePair("action", "up"));
							//params.add(new BasicNameValuePair("name", "testImage"));
							//params.add(new BasicNameValuePair("content", "1111"));
							//UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
							post.setEntity(entity);
							response = client.execute(post);
						}
						catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							Log.d("Splash", "exception");
							e.printStackTrace();
						}   

						HttpEntity entity = response.getEntity();
						if (entity == null) {
							Log.d("Splash post", "entity is null");
							return "";
						}

						String result = "";
						try {
							Log.d("stagine", "UPLOAD: respose code: " + response.getStatusLine().toString());
							result = EntityUtils.toString(entity);
							return result;
						}
						catch (IOException e) {
							e.printStackTrace();
						}

						//	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
						//	entity.writeTo(outstream);
						//	byte [] responseBody = outstream.toByteArray();

						//return response;
					} catch (Exception e) {
						//this.exception = e;
					}
					return "";
				}
				@Override
				protected void onPostExecute(String result) {
					Log.d("Splash stage image", "response is " + result);
					// TODO: check this.exception 
					// TODO: do something with the feed
				}
			};
			//stageImageTask.execute();
	}
}
}
