package com.aripio.wishlist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aripio.wishlist.R;
import com.aripio.wishlist.barscanner.IntentIntegrator;


public class DashBoard extends Activity {
	static final private int TAKE_PICTURE = 1;
	private ImageButton prefImageButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		
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
	           startActivity (new Intent(getApplicationContext(), EditItemInfo.class));
	           break;
	      case R.id.home_btn_wishlist :
	           startActivity (new Intent(getApplicationContext(), WishList.class));
	           break;
	      case R.id.home_btn_camera :
	          // startActivity (new Intent(getApplicationContext(), IntentIntegrator.class));
	    	   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	  		   startActivityForResult(intent, TAKE_PICTURE);
	           break;
//	      case R.id.home_btn_scan :
////	           startActivity (new Intent(getApplicationContext(), F4Activity.class));
//	    	   IntentIntegrator.initiateScan(this);
//	           break;
	      case R.id.home_btn_settings :
//	           startActivity (new Intent(getApplicationContext(), F5Activity.class));
	           break;
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


}
