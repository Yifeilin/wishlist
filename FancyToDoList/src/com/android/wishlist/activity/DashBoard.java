package com.android.wishlist.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.wishlist.R;
import com.android.wishlist.util.camera.PhotoFileCreater;


public class DashBoard extends Activity {
	static final private int TAKE_PICTURE = 1;
	private static final int EDIT_ITEM = 2;
	private ImageButton prefImageButton;
	private String _fullsizePhotoPath = null;
	private String _newfullsizePhotoPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		
		if (savedInstanceState != null) {
			Log.d(WishList.LOG_TAG, "savedInstanceState != null");
			// restore the current selected item in the list
			_newfullsizePhotoPath = savedInstanceState.getString("newfullsizePhotoPath");
			_fullsizePhotoPath = savedInstanceState.getString("fullsizePhotoPath");
			
			Log.d(WishList.LOG_TAG, "_newfullsizePhotoPath " + _newfullsizePhotoPath);
			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath " + _fullsizePhotoPath);
		}
		else{
			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
		}
		
		prefImageButton = (ImageButton) findViewById(R.id.imageButton_pref);
		prefImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				//navigateBack();
 			}
		});	
	}
	
	/**
	 * Handle the click of a Feature button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickFeature (View v)
	{
	    int id = v.getId ();
	    switch (id) {
	      case R.id.home_btn_new_item :
	           startActivityForResult(new Intent(getApplicationContext(), EditItemInfo.class), EDIT_ITEM);
	           break;
	      case R.id.home_btn_wishlist :
	           startActivity (new Intent(getApplicationContext(), WishList.class));
	           break;
	      case R.id.home_btn_camera :
	          // startActivity (new Intent(getApplicationContext(), IntentIntegrator.class));
//	    	   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	  		   startActivityForResult(intent, TAKE_PICTURE);
				dispatchTakePictureIntent();
	    	  
	           break;
//	      case R.id.home_btn_scan :
////	           startActivity (new Intent(getApplicationContext(), F4Activity.class));
//	    	   IntentIntegrator.initiateScan(this);
//	           break;
//	      case R.id.home_btn_settings :
//	           startActivity (new Intent(getApplicationContext(), F5Activity.class));
//	           break;
//	      case R.id.home_btn_help :
////	           startActivity (new Intent(getApplicationContext(), F6Activity.class));
//	           break;
	      default: 
	    	   break;
	    }
	}
	
	/**
	 * Handle the click on the home button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickHome (View v)
	{
	    goHome (this);
	}
	
	/**
	 * Go back to the home activity.
	 * 
	 * @param context Context
	 * @return void
	 */

	public void goHome(Context context) 
	{
//	    final Intent intent = new Intent(context, HomeActivity.class);
//	    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    context.startActivity (intent);
	}

	/**
	 * Handle the click on the search button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickSearch (View v)
	{
	    //startActivity (new Intent(getApplicationContext(), SearchActivity.class));
	}

	/**
	 * Handle the click on the About button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickAbout (View v)
	{
	    //startActivity (new Intent(getApplicationContext(), AboutActivity.class));
	}
	
	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 *
	 * <p> This method is needed because we have a custom title bar rather than the default Android title bar.
	 * See the theme definitons in styles.xml.
	 * 
	 * @param textViewId int
	 * @return void
	 */

	
	public void setTitleFromActivityLabel (int textViewId)
	{
	    TextView tv = (TextView) findViewById (textViewId);
	    if (tv != null) tv.setText (getTitle ());
	} 
	
	private void dispatchTakePictureIntent() {	

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = PhotoFileCreater.getInstance().setUpPhotoFile();
			_newfullsizePhotoPath = PhotoFileCreater.getInstance().getfullsizePhotoPath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			Log.d("wishlist", "IOException" + e.getMessage());
			e.printStackTrace();
			f = null;
			_newfullsizePhotoPath = null;
			return;
		}
		startActivityForResult(takePictureIntent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case TAKE_PICTURE: {
				if (resultCode == RESULT_OK) {
					Log.d(WishList.LOG_TAG, "TAKE_PICTURE: RESULT_OK");
					_fullsizePhotoPath = String.valueOf(_newfullsizePhotoPath);
					_newfullsizePhotoPath = null;
					Intent i = new Intent(getApplicationContext(), EditItemInfo.class);
					i.putExtra("fullsizePhotoPath", _fullsizePhotoPath);
					startActivity (i);
					
				}
				else {
					Log.d(WishList.LOG_TAG, "TAKE_PICTURE: not RESULT_OK");
				}
				break;
			}
			case EDIT_ITEM: {
				if (resultCode == RESULT_OK) {
					// Create an intent to show the item detail.
					// Pass the item_id along so the next activity can use it to
					// retrieve the info. about the item from database
					long id = -1;
					if (data != null) {
						id = data.getLongExtra("itemID", -1);
					}
					
					if (id != -1) {
//						finish();
						Intent i = new Intent(DashBoard.this, WishItemDetail.class);
						i.putExtra("item_id", id);
						startActivity(i);
					}
				}
				else {
					
				}
			}
		}//switch
	}
	
	//this will make the photo taken before to show up if user cancels taking a second photo
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		Log.d(WishList.LOG_TAG, "");
		savedInstanceState.putString("newfullsizePhotoPath", _newfullsizePhotoPath);
		savedInstanceState.putString("fullsizePhotoPath", _fullsizePhotoPath);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore the current selected item in the list
		if (savedInstanceState != null) {
			Log.d(WishList.LOG_TAG, "savedInstanceState != null");
			_newfullsizePhotoPath = savedInstanceState.getString("newfullsizePhotoPath");
			_fullsizePhotoPath = savedInstanceState.getString("fullsizePhotoPath");
		}
		else {
			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
		}
	}



}
