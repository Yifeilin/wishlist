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
		// The meal object
		public WishGraphObject getMeal();
		public void setWish(WishGraphObject wish);
	}

    /**
     * Used to inspect the response from posting an action
     */
    private interface PostResponse extends GraphObject {
        String getId();
    }

	private String wishChoiceUrl = "http://samples.ogp.me/320819528045680";
	private static final String TAG = "FacebookPost";
	private static final String POST_ACTION_PATH = "me/beans_wishlist:make";
	private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private ProgressDialog progressDialog;

	private boolean pendingAnnounce;

	//private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

    /**
     * Notifies that the session token has been updated.
     */
	private void tokenUpdated() {
		if (pendingAnnounce) {
			handleAnnounce();
		}
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				tokenUpdated();
			} else {
				makeMeRequest(session);
			}
		}
	}

	private void makeMeRequest(final Session session) {
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
						Log.d(TAG, "user id:" + user.getId());
						Log.d(TAG, "user name:" + user.getName());
						//profilePictureView.setProfileId(user.getId());
						//userNameView.setText(user.getName());
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
		if (savedInstanceState != null) {
			pendingAnnounce = savedInstanceState.getBoolean(PENDING_ANNOUNCE_KEY, false);
		}

		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			makeMeRequest(session);
		}
	}

	private void handleAnnounce() {
		pendingAnnounce = false;
		Session session = Session.getActiveSession();

		if (session == null || !session.isOpened()) {
			return;
		}

		List<String> permissions = session.getPermissions();
		if (!permissions.containsAll(PERMISSIONS)) {
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
					MakeAction wishAction = GraphObject.Factory.create(MakeAction.class);
					//for (BaseListElement element : listElements) {
					//   element.populateOGAction(eatAction);
					//}
					Request request = new Request(Session.getActiveSession(),
							POST_ACTION_PATH, null, HttpMethod.POST);
					request.setGraphObject(wishAction);
					return request.executeAndWait();
				}

			@Override
				protected void onPostExecute(Response response) {
					onPostActionResponse(response);
				}
		};

		task.execute();
	}

	private void requestPublishPermissions(Session session) {
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

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void populateOGAction(OpenGraphAction action) {
		if (wishChoiceUrl != null) {
			MakeAction makeAction = action.cast(MakeAction.class);
			WishGraphObject wish = GraphObject.Factory.create(WishGraphObject.class);
			wish.setUrl(wishChoiceUrl);
			makeAction.setWish(wish);
		}
	}
} 
