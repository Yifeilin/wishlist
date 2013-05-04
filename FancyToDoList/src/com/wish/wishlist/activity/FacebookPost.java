package com.wish.wishlist.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.facebook.widget.ProfilePictureView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import android.view.Menu;
import android.content.Context;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.net.URISyntaxException; 
import java.net.URI; 
import java.io.File;
import java.io.IOException; 
import com.wish.wishlist.R;
import com.wish.wishlist.model.WishItem;
import com.wish.wishlist.model.WishItemManager;
import com.wish.wishlist.util.ImageManager;


public class FacebookPost extends Activity {
    /**
     * Interface representing the Wish Open Graph object.
     */
	private interface WishGraphObject extends GraphObject {
		// A URL
		public String getUrl();
		public void setUrl(String url);
		// An ID
		public String getId();
		public void setId(String id);
	}

    /**
     * Interface representing the Make action.
     */
	private interface MakeAction extends OpenGraphAction {
		// The wish object
		public WishGraphObject getWish();
		public void setWish(WishGraphObject wish);
	}

    /**
     * Used to inspect the response from posting an action
     */
    private interface PostResponse extends GraphObject {
        String getId();
    }

	private static final String TAG = WishList.LOG_TAG;

	private static final String wishUrl = "http://samples.ogp.me/320819528045680";
	private static final String POST_ACTION_PATH = "me/beans_wishlist:make";
	private static final String POST_OBJECT_PATH = "me/objects/beans_wishlist:wish";
	private static final String UPLOAD_PICTURE_PATH =  "me/staging_resources";
	private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private ProgressDialog progressDialog;

	private boolean pendingAnnounce;
	private long _itemId;
	private Context _ctx;

    private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);
			}
	};

    /**
     * Notifies that the session token has been updated.
     */
	private void tokenUpdated() {
		Log.d(TAG, "tokenUpdated");
		Log.d(TAG, "pendingAnnounce is " + Boolean.valueOf(pendingAnnounce));
		if (pendingAnnounce) {
			handleAnnounce();
		}
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.d(TAG, "onSessionStateChange");
		if (session != null && state.isOpened()) {
			Log.i(TAG, "onSessionStateChange: Logged in...");
			String token = session.getAccessToken();
			Log.i(TAG, "onSessionStateChange: access token:" + token);
			stageImage(token);
		}
		else if (state.isClosed()) {
			Log.i(TAG, "onSessionStateChange: Logged out...");
		}

		//if (session != null && session.isOpened()) {
			//handleAnnounce();
			//postWish();
			//stageImage();
	//		Log.d(TAG, "onSessionStateChange: session != null &&  session is opened");
	//		if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	//			Log.d(TAG, "onSessionStateChange: state equals opened token updated");
	//			tokenUpdated();
	//		} else {
	//			Log.d(TAG, "onSessionStateChange: makeMeRequest");
	//			makeMeRequest(session);
	//		}
		//}
	}

	private void makeMeRequest(final Session session) {
		Log.d(TAG, "makeMeRequest: start");
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					Log.d(TAG, "makeMeRequest: session == Session.getActiveSession");
					if (user != null) {
						Log.d(TAG, "makeMeRequest: user != null");
						Log.d(TAG, "user id:" + user.getId());
						Log.d(TAG, "user name:" + user.getName());
						//profilePictureView.setProfileId(user.getId());
						//userNameView.setText(user.getName());
					}
					else {
						Log.d(TAG, "makeMeRequest: user is null");
					}
				}
				if (response.getError() != null) {
					handleError(response.getError());
				}
			}
		});
		request.executeAsync();
	}

	/**
	 * Resets the view to the initial defaults.
	 */
	private void init(Bundle savedInstanceState) {
		Log.d(TAG, "init");
		if (savedInstanceState != null) {
			pendingAnnounce = savedInstanceState.getBoolean(PENDING_ANNOUNCE_KEY, false);
		}

		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			Log.d(TAG, "init: session != null &&  session is opened");
			makeMeRequest(session);
		}
		else {
			Log.d(TAG, "init: session == null ||  session is not opened");
		}
	}

	private void postWish() {
		Log.d(TAG, "postWish");
		pendingAnnounce = false;
		Session session = Session.getActiveSession();

		if (session == null || !session.isOpened()) {
			Log.d(TAG, "handleAnnounce: session is null or session is not opened");
			return;
		}

		List<String> permissions = session.getPermissions();
		if (!permissions.containsAll(PERMISSIONS)) {
			Log.d(TAG, "handleAnnounce: session not contain all permission");
			pendingAnnounce = true;
			requestPublishPermissions(session);
			return;
		}

		// Show a progress dialog because sometimes the requests can take a while.
		progressDialog = ProgressDialog.show(this, "", this.getResources().getString(R.string.progress_dialog_text), true);

		// Run this in a background thread since some of the populate methods may take
		// a non-trivial amount of time.
		AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {
			@Override
				protected Response doInBackground(Void... voids) {
					Log.d(TAG, "doInBackground");
					Bundle postParams = new Bundle();
					postParams.putString("privacy", "{'value':'ALL_FRIENDS'}");
					postParams.putString("object",
					"{\"title\":\"ipod\"," +  
					  "\"description\":\"a great map3 pod\"}");
					Request request = new Request(Session.getActiveSession(), POST_OBJECT_PATH, postParams, HttpMethod.POST);
					return request.executeAndWait();
				}

			@Override
				protected void onPostExecute(Response response) {
					Log.d(TAG, "onPostExecute");
					onPostActionResponse(response);
				}
		};
		task.execute();
	}

	private void handleAnnounce() {
		Log.d(TAG, "handleAnnounce");
		pendingAnnounce = false;
		Session session = Session.getActiveSession();

		if (session == null || !session.isOpened()) {
			Log.d(TAG, "handleAnnounce: session is null or session is not opened");
			return;
		}

		List<String> permissions = session.getPermissions();
		if (!permissions.containsAll(PERMISSIONS)) {
			Log.d(TAG, "handleAnnounce: session not contain all permission");
			pendingAnnounce = true;
			requestPublishPermissions(session);
			return;
		}

		// Show a progress dialog because sometimes the requests can take a while.
		progressDialog = ProgressDialog.show(this, "", this.getResources().getString(R.string.progress_dialog_text), true);

		// Run this in a background thread since some of the populate methods may take
		// a non-trivial amount of time.
		AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {
			@Override
				protected Response doInBackground(Void... voids) {
					Log.d(TAG, "doInBackground");
					MakeAction makeAction = GraphObject.Factory.create(MakeAction.class);
					//for (BaseListElement element : listElements) {
					//   element.populateOGAction(eatAction);
					//}

					//associate the wish object to make action
					if (wishUrl != null) {
					//	MakeAction makeAction = action.cast(MakeAction.class);
						WishGraphObject wish = GraphObject.Factory.create(WishGraphObject.class);
						wish.setUrl(wishUrl);
						makeAction.setWish(wish);
					}

					Request request = new Request(Session.getActiveSession(), POST_ACTION_PATH, null, HttpMethod.POST);

					request.setGraphObject(makeAction);
					return request.executeAndWait();
				}

			@Override
				protected void onPostExecute(Response response) {
					Log.d(TAG, "onPostExecute");
					onPostActionResponse(response);
				}
		};
		task.execute();
	}

	private void requestPublishPermissions(Session session) {
		Log.d(TAG, "requestPublishPermissions");
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
				// demonstrate how to set an audience for the publish permissions,
				// if none are set, this defaults to FRIENDS
				.setDefaultAudience(SessionDefaultAudience.FRIENDS)
				.setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	private void onPostActionResponse(Response response) {
		Log.d(TAG, "onPostActionResponse");
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (this == null) {
			// if the user removes the app from the website, then a request will
			// have caused the session to close (since the token is no longer valid),
			// which means the splash fragment will be shown rather than this one,
			// causing activity to be null. If the activity is null, then we cannot
			// show any dialogs, so we return.
			return;
		}

		PostResponse postResponse = response.getGraphObjectAs(PostResponse.class);

		if (postResponse != null && postResponse.getId() != null) {
			String dialogBody = String.format(getString(R.string.result_dialog_text), postResponse.getId());
			new AlertDialog.Builder(this)
				.setPositiveButton(R.string.result_dialog_button_text, null)
				.setTitle(R.string.result_dialog_title)
				.setMessage(dialogBody)
				.show();
			init(null);
		} else {
			handleError(response.getError());
		}
	}

	private void handleError(FacebookRequestError error) {
		Log.d(TAG, "handleError: start");
		DialogInterface.OnClickListener listener = null;
		String dialogBody = null;

		if (error == null) {
			dialogBody = getString(R.string.error_dialog_default_text);
		} else {
			switch (error.getCategory()) {
				case AUTHENTICATION_RETRY:
					// tell the user what happened by getting the message id, and
					// retry the operation later
					String userAction = (error.shouldNotifyUser()) ? "" :
						getString(error.getUserActionMessageId());
					dialogBody = getString(R.string.error_authentication_retry, userAction);
					listener = new DialogInterface.OnClickListener() {
						@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
								startActivity(intent);
							}
					};
					break;

				case AUTHENTICATION_REOPEN_SESSION:
					// close the session and reopen it.
					dialogBody = getString(R.string.error_authentication_reopen);
					listener = new DialogInterface.OnClickListener() {
						@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Session session = Session.getActiveSession();
								if (session != null && !session.isClosed()) {
									session.closeAndClearTokenInformation();
								}
							}
					};
					break;

				case PERMISSION:
					// request the publish permission
					dialogBody = getString(R.string.error_permission);
					listener = new DialogInterface.OnClickListener() {
						@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								pendingAnnounce = true;
								requestPublishPermissions(Session.getActiveSession());
							}
					};
					break;

				case SERVER:
				case THROTTLING:
					// this is usually temporary, don't clear the fields, and
					// ask the user to try again
					dialogBody = getString(R.string.error_server);
					break;

				case BAD_REQUEST:
					// this is likely a coding error, ask the user to file a bug
					dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
					break;

				case OTHER:
				case CLIENT:
				default:
					// an unknown issue occurred, this could be a code error, or
					// a server side issue, log the issue, and either ask the
					// user to retry, or file a bug
					dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
					break;
			}
		}

		new AlertDialog.Builder(this)
			.setPositiveButton(R.string.error_dialog_button_text, listener)
			.setTitle(R.string.error_dialog_title)
			.setMessage(dialogBody)
			.show();
	}

	private void stageImage(String token) {
		AsyncTask<String, Void, String> stageImageTask = new AsyncTask<String, Void, String>() {
			@Override
				protected String doInBackground(String... params) {
					Log.d(TAG, "doInBackground");
					Log.d(TAG, "doInBackground passed in token is" + params[0]);
					try {
						//String uri = "https://graph.facebook.com/100005720562778/staging_resources";
						String uri = "https://graph.facebook.com/me/staging_resources";
						//String token = "BAADJTZCh0pA8BAIGX71M0lnVJfdshs4JAx5ra38C9uVfHMTyTWlezd2dTuExBq1BUIEJcoCEtVTH5WcCzNe9LYlRsldge5JmAbhfehePsDUjcLQI6skPLFyth0cfmbGgxIuXbJ2gGZCSRCzooGAW5aEXWZCJqy8NuehNMiuWFg0KCCTDZBEo4rU8dPx95dEkjbXXJCeMaW1qHUlSbMVoDHmUB7SlqZCxa6ahwtxTNzgZDZD";
						HttpResponse response = null;
						try {        
							HttpClient client = new DefaultHttpClient();
							HttpPost post = new HttpPost(uri);
							MultipartEntity entity = new MultipartEntity();
							//WishItem wish_item = WishItemManager.getInstance(_ctx).retrieveItembyId(_itemId);
							//String picPath = wish_item.getFullsizePicPath();
							File file = new File("/data/local/tmp/images.jpg");
							Log.d(TAG, "UPLOAD: file length = " + file.length());
							Log.d(TAG, "UPLOAD: file exist = " + file.exists());

							entity.addPart("file", new FileBody(file, "image/jpeg"));
							entity.addPart("access_token", new StringBody(params[0]));
							post.setEntity(entity);
							response = client.execute(post);
						}
						catch (ClientProtocolException e) {
							Log.d(TAG, "exception");
							e.printStackTrace();
						}
						catch (IOException e) {
							Log.d(TAG, "exception");
							e.printStackTrace();
						}   

						HttpEntity entity = response.getEntity();
						if (entity == null) {
							Log.d(TAG, "entity is null");
							return "";
						}

						String result = "";
						try {
							Log.d(TAG, "UPLOAD: respose code: " + response.getStatusLine().toString());
							result = EntityUtils.toString(entity);
							return result;
						}
						catch (IOException e) {
							e.printStackTrace();
						}

						//return response;
					} catch (Exception e) {
						//this.exception = e;
					}
					return "";
				}
			@Override
				protected void onPostExecute(String result) {
					Log.d(TAG, "response is " + result);
				}
		};
		stageImageTask.execute(token);
	}

	@Override
		public void onCreate(Bundle savedInstanceState) {
			Log.d(TAG, "onCreate");
			super.onCreate(savedInstanceState);
			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);

			//setContentView(R.layout.login);

			_ctx = this;
			Bundle extras = getIntent().getExtras();
			_itemId = extras.getLong("itemId");

			//LoginButton authButton = (LoginButton) findViewById(R.id.facebook_login_button);
			//authButton.setOnErrorListener(new OnErrorListener() {
			//	@Override
			//	public void onError(FacebookException error) {
			//		Log.i(WishList.LOG_TAG, "Error " + error.getMessage());
			//	}
			//});
			//authButton.setSessionStatusCallback(callback);
			//authButton.setPublishPermissions(PERMISSIONS);
			//authButton.setReadPermissions(Arrays.asList("basic_info","email"));
			//init(savedInstanceState);
			//handleAnnounce();
		}

	@Override
		public void onResume() {
			super.onResume();
			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			Session session = Session.getActiveSession();
			if (session != null &&
					(session.isOpened() || session.isClosed()) ) {
				onSessionStateChange(session, session.getState(), null);
			}
			uiHelper.onResume();
		}

	@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			//if (requestCode == REAUTH_ACTIVITY_CODE) {
				uiHelper.onActivityResult(requestCode, resultCode, data);
			//}
		}

	@Override
		public void onSaveInstanceState(Bundle bundle) {
			super.onSaveInstanceState(bundle);
			//bundle.putBoolean(PENDING_ANNOUNCE_KEY, pendingAnnounce);
			uiHelper.onSaveInstanceState(bundle);
		}

	@Override
		public void onPause() {
			super.onPause();
			uiHelper.onPause();
		}

	@Override
		public void onDestroy() {
			super.onDestroy();
			uiHelper.onDestroy();
		}

	protected void populateOGAction(OpenGraphAction action) {
		if (wishUrl != null) {
			MakeAction makeAction = action.cast(MakeAction.class);
			WishGraphObject wish = GraphObject.Factory.create(WishGraphObject.class);
			wish.setUrl(wishUrl);
			makeAction.setWish(wish);
		}
	}
} 
