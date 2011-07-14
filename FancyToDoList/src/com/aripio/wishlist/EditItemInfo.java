package com.aripio.wishlist;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/*** EditItemInfo.java is responsible for reading in the info. of a newly added item 
 * including its name, description, time, price, location and photo, and saving them
 * as a row in the Item table in the database
 */
public class EditItemInfo extends Activity {

	private EditText myItemName;
	private EditText myDescription;
	private EditText myPrice;
	private EditText myLocation;

	private Button btnSave;
	private Button btnCancel;
	private Button btnDate;
	private Button btnPhoto;
	private ImageView imageItem;
	private Date mDate;
	private Bitmap thumbnail;
	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private String date;
	private String picture_uri;
	// = R.drawable.chocolate
	// private WishListDataBase wishListDB;
	private ItemDBAdapter mItemDBAdapter;
	private StoreDBAdapter mStoreDBAdapter;
	private LocationDBAdapter mLocationDBAdapter;
	private int mYear = -1;
	private int mMonth = -1;
	private int mDay = -1;
	private int mHour = 0;
	private int mMin = 0;
	private int mSec = 0;

	private AlertDialog alert;
	static final private int DATE_DIALOG_ID = 0;
	static final private int TAKE_PICTURE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);

		//find the resources by their ids
		myItemName = (EditText) findViewById(R.id.itemname);
		myDescription = (EditText) findViewById(R.id.description);
		myPrice = (EditText) findViewById(R.id.price);
		myLocation = (EditText) findViewById(R.id.location);

		btnSave = (Button) findViewById(R.id.button_save);
		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnDate = (Button) findViewById(R.id.button_date);
		btnPhoto = (Button) findViewById(R.id.button_photo);

		imageItem = (ImageView) findViewById(R.id.image_photo);

		// Open the Item table in the database
		// wishListDB = WishListDataBase.getDBInstance(this);
		mItemDBAdapter = new ItemDBAdapter(this);
		mItemDBAdapter.open();
		
		// Open the Store table in the database
		mStoreDBAdapter = new StoreDBAdapter(this);
		mStoreDBAdapter.open();
	
		// Open the Location table in the database
		mLocationDBAdapter = new LocationDBAdapter(this);
		mLocationDBAdapter.open();
		

		// get the current date_time
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?").setCancelable(
				false).setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EditItemInfo.this.finish();
					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alert = builder.create();

		//set the keyListener for the Item Name EditText
		myItemName.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						myItemName.setSelected(false);
					}
				return false;
			}
		});

		//set the keyListener for the Item Description EditText
		myDescription.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						myDescription.setSelected(false);
					}
				return false;
			}
		});

		//set the keyListener for the Item Price EditText
		myPrice.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						myPrice.setSelected(false);
					}
				return false;
			}
		});


		//set the keyListener for the Item Location EditText
		myLocation.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						myLocation.setSelected(false);
					}
				return false;
			}
		});

		mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
				date = sdf.format(new Date(mYear - 1900, mMonth, mDay));
				btnDate.setText(date);
			}
		};

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveWishItem();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.show();
			}
		});

		btnDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		btnPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getThumbailPicture();
			}

		});
	}

	/***
	 * Save user input as a wish item
	 */
	private void saveWishItem() {

		//get the location
		PositionManager pManager = new PositionManager(this);
		Location location = pManager.getCurrentLocation();
		
		double lat = 0;
		double lng = 0;
		String addStr = "unknown";
		
		if (location == null){
			Toast.makeText(this, "location not available", Toast.LENGTH_LONG);

		}
		else{
			//get current latitude and longitude
			lat = location.getLatitude();
			lng = location.getLongitude();
			
			//getCuttentAddStr using geocode, may take a while, need to put this to a separate thread
			addStr = pManager.getCuttentAddStr();
		}
		
		//define variables to hold the item info.
		String itemName = "N/A";
		String itemDesc = "N/A";
		float itemPrice = 0;
		String itemLocation = "N/A";
		int itemPriority = 0;
		
		String storeName = "N/A";

		try {
			// read in the name, description, price and location of the item
			// from the EditText
			itemName = myItemName.getText().toString();
			itemDesc = myDescription.getText().toString();
			itemPrice = Float.valueOf(myPrice.getText().toString());
			itemLocation = myLocation.getText().toString();

		}

		catch (NumberFormatException e) {
			// need some error message here
			// price format incorrect
			e.toString();
			itemPrice = 0;
		}

		// user did not specify date_time, use "now" as default date_time
		if (mYear == -1) {
			// get the current date_time
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mHour = c.get(Calendar.HOUR);
			mMin = c.get(Calendar.MINUTE);
			mSec = c.get(Calendar.SECOND);
		}

		// Format the date_time and save it as a string 
		mDate = new Date(mYear - 1900, mMonth, mDay, mHour, mMin, mSec);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
		String date = sdf.format(mDate);

		// insert the location to the Location table in database
		long locationID = mLocationDBAdapter.addLocation(lat, lng, addStr, -1, "N/A", "N/A", "N/A", "N/A", "N/A");

		// insert the store to the Store table in database, linked to the location
		long storeID = mStoreDBAdapter.addStore(storeName, locationID);
	
		
		// insert the item to the Item table in database, linked to the store
		mItemDBAdapter.addItem(storeID, itemName, itemDesc, date, picture_uri,
				itemPrice, itemLocation, itemPriority);
		
		//close this activity
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				// Check if the result includes a thumbnail Bitmap
				if (data != null) {
					if (data.hasExtra("data")) {
						thumbnail = data.getParcelableExtra("data");
						imageItem.setImageBitmap(thumbnail);

						// store thumbnail in the media content provider 
						// and get the uri of the thumbnail
						ContentValues values = new ContentValues();
						values.put(Media.MIME_TYPE, "image/jpeg");
						Uri uri = getContentResolver().insert(
								Media.EXTERNAL_CONTENT_URI, values);

						//compress the thumbnail to JPEG and write the JEPG to 
						//the content provider. Save the uri of the JEPG as a string,
						//which will be inserted in the column "picture_uri" of
						//the Item table
						try {
							OutputStream outStream = getContentResolver()
									.openOutputStream(uri);
							thumbnail.compress(Bitmap.CompressFormat.JPEG, 50,
									outStream);

							outStream.close();
							picture_uri = uri.toString();

						} catch (Exception e) {
							Log.e(WishList.LOG_TAG,
									"exception while writing image", e);
						}
					}
				}
				break;
			}
		}
	}

	private void getThumbailPicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE);
	}
}
