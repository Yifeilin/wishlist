package com.aripio.wishlist;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WishListLoginReal extends Activity {
	private long rowId;
	private UserDB mDbHelper;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private TextView logInMsg;

	// private UserDataBase mDbHelper1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_real);

		mDbHelper = new UserDB(this);
		// mDbHelper1 = new UserDataBase(this);

		mDbHelper.open();
		// mDbHelper1.open();

		// rowId = mDbHelper.createUser("jiawen", "password");
		// long id1 = mDbHelper1.createUser("name", "password");
		// id = 2;

		// get the button resource in the xml file and assign it to a local
		// variable of type Button
		Button log_in = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);

		// get the edit text resource
		usernameEditText = (EditText) findViewById(R.id.txt_username);
		passwordEditText = (EditText) findViewById(R.id.txt_password);
		logInMsg = (TextView) findViewById(R.id.text_LogInMsg);

		// this is the action listener
		register.setOnClickListener(new OnClickListener() {

			// when user clicks "Register" button
			// finish this activity and save the user info in database
			public void onClick(View viewParam) {
				// for test
				// Cursor allC = mDbHelper.fetchAllUsers();
				// Create an array to specify the fields we want to display in
				// the list (only TITLE)
				// String[] from = new String[]{mDbHelper.KEY_NAME};

				// int number = allC.getCount();

				// read in the user name and password and verify its format
				User aUser = new User();
				String userName = usernameEditText.getText().toString();
				String userPass = passwordEditText.getText().toString();

				// check if input is empty
				if (userName.length() == 0 || userPass.length() == 0) {
					logInMsg.setText("user name or password cannot be empty");
					logInMsg.setVisibility(View.VISIBLE);
					return;
				}

				// input is not empty
				aUser.setName(usernameEditText.getText().toString());
				aUser.setPassword(passwordEditText.getText().toString());

				// check if this user name has already been created
				Cursor c = mDbHelper.fetchUser(aUser.getName());
				startManagingCursor(c);
				if (c == null || c.getCount() == 0) {
					// user name has not been created, add the user to database
					// pop up a window to inform user of successful user
					// creation
					// when user clicks "ok", bring user to the main activity
					long insert_id = mDbHelper.createUser(aUser.getName(),
							aUser.getPassword());
					logInMsg
							.setText("user account is created successfully, please log in");
					logInMsg.setVisibility(View.VISIBLE);
				}

				else {
					// user name has already been created, ask user to use
					// another
					// user name to register
					// String id = c.getString(
					// c.getColumnIndexOrThrow(UserDB.KEY_ROWID));
					// String returnName = c.getString(
					// c.getColumnIndexOrThrow(UserDB.KEY_NAME));
					// String returnPass = c.getString(
					// c.getColumnIndexOrThrow(UserDB.KEY_PASSWORD));

					logInMsg
							.setText("user name has been used, try another user name");
					logInMsg.setVisibility(View.VISIBLE);

				}

			}

		});

		log_in.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				// read in the user name and password and verify its format
				User logInUser = new User();
				String userName = usernameEditText.getText().toString();
				String userPass = passwordEditText.getText().toString();

				// check if input is empty
				if (userName.length() == 0 || userPass.length() == 0) {
					logInMsg.setText("user name or password cannot be empty");
					logInMsg.setVisibility(View.VISIBLE);
					return;
				}

				// input is not empty, save the name and password
				logInUser.setName(userName);
				logInUser.setPassword(userPass);

				// check if user name and password is correct
				Cursor c = mDbHelper.fetchUser(logInUser.getName());
				startManagingCursor(c);
				if (c == null || c.getCount() == 0) {
					// user name is incorrect, inform user to try again
					logInMsg
							.setText("user name is incorrect, please try again");
					logInMsg.setVisibility(View.VISIBLE);

				}

				// user name is correct, check password
				else {
					// int i = c.getColumnIndexOrThrow(UserDB.KEY_PASSWORD);
					String correctPass = c.getString(c
							.getColumnIndexOrThrow(UserDB.KEY_PASSWORD));

					if (!userPass.equals(correctPass)) {
						// password is incorrect
						// inform the user to retry
						logInMsg
								.setText("password is incorrect, please try again");
						logInMsg.setVisibility(View.VISIBLE);

					}

					else {
						// password is correct
						// launch the main activity
						logInMsg
								.setText("user name and password correct, loading...");
						logInMsg.setVisibility(View.VISIBLE);
						startActivity(new Intent(WishListLoginReal.this,
								WishList.class));
						WishListLoginReal.this.finish();

					}

				}

				// getUser();
				// saveState();
				// finish();

			}

		}); // end of register.setOnclickListener

	}
}