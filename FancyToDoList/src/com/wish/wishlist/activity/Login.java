package com.wish.wishlist.activity;

import java.util.Arrays;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.facebook.FacebookException;

import com.wish.wishlist.R;

public class Login extends Activity {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    //private TextView textInstructionsOrLink;
    //private Button buttonLoginLogout;
    //private Session.StatusCallback statusCallback = new SessionStatusCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
		LoginButton authButton = (LoginButton) findViewById(R.id.facebook_login_button);
		authButton.setOnErrorListener(new OnErrorListener() {
			@Override
			public void onError(FacebookException error) {
				//Log.i(WishList.LOG_TAG, "Error " + error.getMessage());
			}
		});
		// set permission list, Don't foeget to add email
		authButton.setReadPermissions(Arrays.asList("basic_info","email"));

		// session state call back event
		authButton.setSessionStatusCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Log.i(WishList.LOG_TAG,"Access Token"+ session.getAccessToken());
					Request.executeMeRequestAsync(session,
						new Request.GraphUserCallback() {
							@Override
						public void onCompleted(GraphUser user,Response response) {
							if (user != null) {
								Log.i(WishList.LOG_TAG,"User ID "+ user.getId());
								Log.i(WishList.LOG_TAG,"Email "+ user.asMap().get("email"));
								//lblEmail.setText(user.asMap().get("email").toString());
							}
						}
						});
				}
			}
		});

//        buttonLoginLogout = (Button)findViewById(R.id.buttonLoginLogout);
//       textInstructionsOrLink = (TextView)findViewById(R.id.instructionsOrLink);

//        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

 //       Session session = Session.getActiveSession();
 //       if (session == null) {
 //           if (savedInstanceState != null) {
 //               session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
 //           }
 //           if (session == null) {
 //               session = new Session(this);
 //           }
 //           Session.setActiveSession(session);
 //           if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
 //               session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
 //           }
 //       }

 //       updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Session session = Session.getActiveSession();
        //Session.saveSession(session, outState);
    }

 //   private void updateView() {
 //       Session session = Session.getActiveSession();
 //       if (session.isOpened()) {
 //           textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
 //           buttonLoginLogout.setText("logout");
 //           buttonLoginLogout.setOnClickListener(new OnClickListener() {
 //               public void onClick(View view) { onClickLogout(); }
 //           });
 //       } else {
 //           textInstructionsOrLink.setText("Login to create a link to fetch account data");
 //           buttonLoginLogout.setText("login");
 //           buttonLoginLogout.setOnClickListener(new OnClickListener() {
 //               public void onClick(View view) { onClickLogin(); }
 //           });
 //       }
 //   }

 //   private void onClickLogin() {
 //       Session session = Session.getActiveSession();
 //       if (!session.isOpened() && !session.isClosed()) {
 //           session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
 //       } else {
 //           Session.openActiveSession(this, true, statusCallback);
 //       }
 //   }

  //  private void onClickLogout() {
  //      Session session = Session.getActiveSession();
  //      if (!session.isClosed()) {
  //          session.closeAndClearTokenInformation();
  //      }
  //  }

  //  private class SessionStatusCallback implements Session.StatusCallback {
  //      @Override
  //      public void call(Session session, SessionState state, Exception exception) {
  //          updateView();
  //      }
  //  }
}
