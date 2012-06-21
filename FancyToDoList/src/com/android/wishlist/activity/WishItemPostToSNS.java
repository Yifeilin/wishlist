package com.android.wishlist.activity;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class WishItemPostToSNS extends Activity {

	// application id from facebook.com/developers
	public static final String APP_ID = "198870636822464";
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
		// this.setContentView(R.layout.facebook);//my layout xml
		Bundle extras = getIntent().getExtras();

		wishItem = new StringBuilder();

		String wish = extras.getString("wishItem");

		wishItem.append(wish);

		mFacebook = new Facebook(APP_ID);
		// replace APP_API_ID with your own
		mFacebook.authorize(this, new String[] { "publish_stream",
				"read_stream", "offline_access" }, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {

				Bundle parameters = new Bundle();
				parameters.putString("method", "status.set");
				parameters.putString("status", wishItem.toString());
				try {
					String response = mFacebook.request(parameters);
					System.out.println(response);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFacebookError(FacebookError error) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onCancel() {
			}
		});
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mFacebook.authorizeCallback(requestCode, resultCode, data);
		finish();
	}

}
