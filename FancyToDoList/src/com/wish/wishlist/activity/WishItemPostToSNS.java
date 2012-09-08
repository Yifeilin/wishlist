package com.wish.wishlist.activity;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;
import android.graphics.Bitmap;
import android.content.Context;

import com.facebook.android.AsyncFacebookRunner;
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
	//public static final String APP_ID = "198870636822464"; //old app id from bao man
	public static final String APP_ID = "221345307993103";
	// log tag for any log.x statements
	public static final String TAG = "FACEBOOK CONNECT";
	// permissions array
//	private static final String[] PERMS = new String[] { "user_events" };
	// facebook vars
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
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
		//String wish = extras.getString("wishItem");
		_itemId = extras.getLong("itemId");
		mFacebook = new Facebook(APP_ID);
		//mFacebook.authorize(this, new String[] {"publish_stream"}, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {
		//mFacebook.authorize(this, new String[] {"publish_stream"}, new DialogListener() {
		mFacebook.authorize(this, new String[] {"user_photos", "publish_stream", "read_stream", "offline_access"}, new DialogListener() {
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
					WishItem wish_item = WishItemManager.getInstance(_ctx).retrieveItembyId(_itemId);
					String message = wish_item.getShareMessage();
					String fullsize_picture_str = wish_item.getFullsizePicPath();
					//Display display = getWindowManager().getDefaultDisplay(); 
					//int width = display.getWidth();  // deprecated
					//int height = display.getHeight();  // deprecated
					int width = 250;  // deprecated
					int height = 250;  // deprecated
					Bitmap bitmap = null;
					byte[] data = null;
					if (fullsize_picture_str != null) {
						bitmap = ImageManager.getInstance().decodeSampledBitmapFromFile(fullsize_picture_str, width, height);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						data = baos.toByteArray();
					}
					
					//it appears that there is not easy way to post a message with photo on wall using facebook's
					//existing api

					//"me/photos" -  Not all uploaded images are displayed on my wall. Instead, there is something
					//like x photos were added to the album xxx. and the post is not visible to your friends
					//"me/feed" - This will make the post visible to friends, but it requires the photo to be
					//available on server with an URL link

					//the workaround here is to add photos and related comments to user's "Wall photos album".
					//it assumes user already has posted some photos to her wall sometime in the past.
					//It will fail if there are no wall photos.
					String wallAlbumID = null;
					String response = mFacebook.request("me/albums");
					Log.d("JSON", "facebook.request");
					try {
						Log.d("JSON", "try { ");
						Log.d("JSON", "response:" + response);
						JSONObject json = Util.parseJson(response);
						JSONArray albums = json.getJSONArray("data");
						for (int i =0; i < albums.length(); i++) {
							Log.d("JSON", "i: " + String.valueOf(i));
							JSONObject album = albums.getJSONObject(i);
							if (album.getString("type").equalsIgnoreCase("wall")) {
								wallAlbumID = album.getString("id");
								Log.d("JSON", "wallAlbumID" + wallAlbumID);
								break;
							}
						}

						if (wallAlbumID != null) {
							Bundle params = new Bundle();
							params.putString("message", message);
							params.putByteArray("source", data);
							//asyncRunner.request(wallAlbumID + "/photos", params, "POST", new PostPhotoRequestListener(), null);
							response = mFacebook.request(wallAlbumID + "/photos", params, "POST");
						}
					}
					catch(JSONException e) {
						e.printStackTrace();
					}
					catch(FacebookError e) {
						e.printStackTrace();
					}
						
				//	Bundle bundle = new Bundle();
				//	bundle.putString("message", message);
					//bundle.putString("method", "photos.upload");
				//	bundle.putByteArray("picture", data);
					//bundle.putString(Facebook.TOKEN, accessToken);
			//		String response = mFacebook.request("me/feed",bundle,"POST");
					//String response = mFacebook.request("me/photos",bundle,"POST");
			//		Log.d("UPDATE RESPONSE",""+response);
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
