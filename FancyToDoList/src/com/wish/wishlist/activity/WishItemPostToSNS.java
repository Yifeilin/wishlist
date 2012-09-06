package com.wish.wishlist.activity;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class WishItemPostToSNS extends Activity {

	// application id from facebook.com/developers
	//public static final String APP_ID = "198870636822464"; //old app id from bao man
	public static final String APP_ID = "221345307993103";
	// log tag for any log.x statements
	public static final String TAG = "FACEBOOK CONNECT";
	// permissions array
//	private static final String[] PERMS = new String[] { "user_events" };
	// facebook vars
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;

	public static final int LOGIN = Menu.FIRST;
	public static final int GET_EVENTS = Menu.FIRST + 1;
	public static final int GET_ID = Menu.FIRST + 2;

	StringBuilder wishItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		wishItem = new StringBuilder();
		String wish = extras.getString("wishItem");
		wishItem.append(wish);
		mFacebook = new Facebook(APP_ID);
		//mFacebook.authorize(this, new String[] {"publish_stream"}, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {
		//mFacebook.authorize(this, new String[] {"publish_stream"}, new DialogListener() {
		mFacebook.authorize(this, new String[] {"publish_stream", "read_stream", "offline_access"}, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				Log.d("FACEBOOK authorize on complete", "");
				updateStatus("");
			}

			@Override
			public void onFacebookError(FacebookError e) {
			     Log.d("FACEBOOK ERROR","FB ERROR. MSG: "+e.getMessage()+", CAUSE: "+e.getCause());
			}

			@Override
			public void onError(DialogError e) {
			     Log.e("ERROR","AUTH ERROR. MSG: "+e.getMessage()+", CAUSE: "+e.getCause());
			}

			@Override
			public void onCancel() {
			     Log.d("CANCELLED","AUTH CANCELLED");
			}
		});
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	public void updateStatus(String accessToken) {
		new Thread() {
			public void run() {
				try {
					Bundle bundle = new Bundle();
					bundle.putString("message", "test update");
					//bundle.putString(Facebook.TOKEN, accessToken);
					String response = mFacebook.request("me/feed",bundle,"POST");
					Log.d("UPDATE RESPONSE",""+response);
				}
				catch (MalformedURLException e) {
					Log.e("MALFORMED URL",""+e.getMessage());
				}
				catch (IOException e) {
					Log.e("IOEX",""+e.getMessage());
				}
			}
		}.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mFacebook.authorizeCallback(requestCode, resultCode, data);
		finish();
	}

}
//			new Thread() {
//				public void run() {
//	try {
//		Bundle bundle = new Bundle();
//		bundle.putString("message", "test update");
////		bundle.putString(Facebook.TOKEN, accessToken);
//		String response = mFacebook.request("me/feed",bundle,"POST");
//		Log.d("UPDATE RESPONSE",""+response);
//	}
//	catch (MalformedURLException e) {
//		Log.e("MALFORMED URL",""+e.getMessage());
//	}
//	catch (IOException e) {
//		Log.e("IOEX",""+e.getMessage());
//	}

				//Bundle parameters = new Bundle();
				//parameters.putString("method", "status.set");
				//parameters.putString("status", wishItem.toString());
				//try {
				//	String response = mFacebook.request(parameters);
				//	System.out.println(response);
				//} catch (MalformedURLException e) {
				//	e.printStackTrace();
				//} catch (IOException e) {
				//	e.printStackTrace();
				//}
