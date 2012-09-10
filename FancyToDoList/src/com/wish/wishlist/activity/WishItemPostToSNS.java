package com.wish.wishlist.activity;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;
import android.graphics.Bitmap;
import android.content.Context;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.wish.wishlist.model.WishItem;
import com.wish.wishlist.model.WishItemManager;
import com.wish.wishlist.util.ImageManager;


public class WishItemPostToSNS extends Activity {

	// application id from facebook.com/developers
	public static final String APP_ID = "221345307993103";
	public static final String TAG = "FACEBOOK CONNECT";
	// permissions array
	private static final String[] PERMS = new String[] {
		"user_photos", 
		"publish_actions",
	//	"publish_stream",
	//	"read_stream",
	//	"offline_access",
	//	Facebook.FORCE_DIALOG_AUTH, 
	};

	private Facebook _facebook;
	private AsyncFacebookRunner _asyncRunner;
	private long _itemId;
	private Context _ctx;

	public static final int LOGIN = Menu.FIRST;
	public static final int GET_EVENTS = Menu.FIRST + 1;
	public static final int GET_ID = Menu.FIRST + 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_ctx = this;
		Bundle extras = getIntent().getExtras();
		_itemId = extras.getLong("itemId");
		_facebook = new Facebook(APP_ID);
		_asyncRunner = new AsyncFacebookRunner(_facebook);
		_facebook.authorize(this, PERMS, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				Log.d("FACEBOOK authorize on complete", "");
				postWishToWall("");
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
		//_asyncRunner = new AsyncFacebookRunner(_facebook);
	}

	public void postWishToWall(String accessToken) {
		//we need to put this outside the ui thread, otherwise, a runtime execption will occur.
		//new Thread() {
		//	public void run() {
					Log.d("JSON", "run try {");
					WishItem wish_item = WishItemManager.getInstance(_ctx).retrieveItembyId(_itemId);
					String message = wish_item.getShareMessage(true);
					byte[] photoData = wish_item.getPhotoData();
					
					if (photoData == null) {
						postTextWish(message);
					}
					else {
						postTextAndPhotoWish(message, photoData);
					}
			}//end of run
		//}.start();
	//}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		_facebook.authorizeCallback(requestCode, resultCode, data);
		finish();
	}

	private void postTextWish(String message) {
		Log.d(TAG, "postTextWish");
		//String response = null;
		Bundle params = new Bundle();
		//try {
			//params.putString("message", message);
			params.putString("method", "status.set");
			params.putString("status", message);
			//response = _facebook.request("me/feed", params, "POST");
			_asyncRunner.request(params, new simpleRequestListener());
		//	Log.d(TAG, "response update status:\n" + response);
		//}
	}

	private void postTextAndPhotoWish(String message, byte[] photoData) {
		//it appears that there is not easy way to post a message with photo on wall, and make it to
		//appear on friends' feed using facebook's existing api

		//"me/photos" -  Not all uploaded images are displayed on my wall. Instead, there is something
		//like x photos were added to the album xxx. and the post does not appear on friends' feed
		//"status" - This will make the post visible on friends feeds, but it requires the photo to be
		//available on a server with an URL link

		//the workaround here is to add photos and related comments to user's "Wall photos album".
		//it assumes user already has posted some photos to his/her wall sometime in the past.
		//It will fail if there are no wall photos.
		Log.d(TAG, "postTextAndPhotoWish");
		String wallAlbumID = null;
		String response = null;
		try {
			response = _facebook.request("me/albums");
			Log.d(TAG, "response me/albums:\n" + response);
		}
		catch (MalformedURLException e) {
			Log.e("MALFORMED URL",""+e.getMessage());
		}
		catch (IOException e) {
			Log.e("IOEX",""+e.getMessage());
		}

		try {
			Log.d("JSON", "JSON run try {");
			JSONObject json = Util.parseJson(response);
			JSONArray albums = json.getJSONArray("data");
			for (int i = 0; i < albums.length(); i++) {
				Log.d("JSON", "i: " + String.valueOf(i));
				JSONObject album = albums.getJSONObject(i);
				if (album.getString("type").equalsIgnoreCase("wall")) {
					wallAlbumID = album.getString("id");
					Log.d("JSON", "wallAlbumID" + wallAlbumID);
					break;
				}
			}
		}
		catch (JSONException e) {
			Log.d("JSONException","ERROR. MSG: "+e.getMessage()+", CAUSE: "+e.getCause());
		//	e.printStackTrace();
		}

		String requestFlag = "";
		Bundle params = new Bundle();
		if (wallAlbumID != null) {
			Log.d("JSON", "wall album exists");
			params.putString("message", message);
			params.putByteArray("source", photoData);
			requestFlag = wallAlbumID + "/photos";
		}
		else { //there is no wall album for this user, meaning the user has never posted any
			//photo on his/her wall before (a case unlikely), so use "me/photo" request to
			//upload the photo, this will not automatically create a wall album, instead,
			//it will create an album named "Beans Wishlist Photos" and upload the photo
			//to this album. subsequent wish share will upload photos to this album until user 
			//post their first photo to their wall album from outside this app
			//these photos will appear in friends feed as a signle album instead of sepearte 
			//wish post
			Log.d("me/photos", "no wall album");
			params.putString("message", message);
			//bundle.putString("method", "photos.upload");
			params.putByteArray("picture", photoData);
			//bundle.putString(Facebook.TOKEN, accessToken);
			requestFlag = "me/photos";
		}

		//try {
			_asyncRunner.request(requestFlag, params, "POST", new simpleRequestListener(), null);
		//	Log.d(TAG, "response " + requestFlag + "\n" + response);
		//}
	}
	
	private class simpleRequestListener implements RequestListener {
		/**
		* Called when a request completes with the given response.
		*
		* Executed by a background thread: do not update the UI in this method.
		*/
		public void onComplete(String response, Object state) {
			Log.d(TAG, "response "  + "\n" + response);
			if (response.equals("true")) {
				((Activity)_ctx).runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(_ctx, "Success", Toast.LENGTH_SHORT).show();
					}
				});
			}
			else {
				((Activity)_ctx).runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(_ctx, "Fail", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
		/**
		* Called when a request has a network or request error.
		*
		* Executed by a background thread: do not update the UI in this method.
		*/
		public void onIOException(IOException e, Object state) {
		}
		/**
		* Called when a request fails because the requested resource is
		* invalid or does not exist.
		*
		* Executed by a background thread: do not update the UI in this method.
		*/
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
		}
		/**
		* Called if an invalid graph path is provided (which may result in a
		* malformed URL).
		*
		* Executed by a background thread: do not update the UI in this method.
		*/
		public void onMalformedURLException(MalformedURLException e, Object state) {
		}
		/**
		* Called when the server-side Facebook method fails.
		*
		* Executed by a background thread: do not update the UI in this method.
		*/
		public void onFacebookError(FacebookError e, Object state) {
		}
	}
}

