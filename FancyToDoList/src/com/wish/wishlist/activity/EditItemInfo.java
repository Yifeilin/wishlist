package com.wish.wishlist.activity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wish.wishlist.R;
import com.wish.wishlist.db.LocationDBAdapter;
import com.wish.wishlist.db.StoreDBAdapter;
import com.wish.wishlist.model.WishItem;
import com.wish.wishlist.model.WishItemManager;
import com.wish.wishlist.util.PositionManager;
import com.wish.wishlist.util.camera.PhotoFileCreater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/*** EditItemInfo.java is responsible for reading in the info. of a newly added item 
 * including its name, description, time, price, location and photo, and saving them
 * as a row in the Item table in the database
 */
public class EditItemInfo extends Activity {

	private EditText myItemName;
	private EditText myNote;
	private EditText myPrice;
	private EditText myStore;
	private EditText myLocation;

	private ImageButton backImageButton;
	private ImageButton saveImageButton;
	private ImageButton mapImageButton;
	private ImageButton cameraImageButton;
//	private ImageButton galleryImageButton;
//	private Button btnCancel;
//	private Button btnPhoto;
	private ImageView imageItem;
	private Date mDate;
	private double lat = Double.MIN_VALUE;
	private double lng = Double.MIN_VALUE;
	private String addStr = "unknown";
	private Bitmap _thumbnail;
//	private Bitmap mImageBitmap;
	//private String picture_str = Integer.toHexString(R.drawable.logo);//default pic is logo
	private String picture_str = Integer.toHexString(R.drawable.empty_photo_200by200);//default pic is W
	private String _fullsizePhotoPath = null;
	private String _newfullsizePhotoPath = null;
	private StoreDBAdapter mStoreDBAdapter;
	private LocationDBAdapter mLocationDBAdapter;
	PositionManager pManager;
	private int mYear = -1;
	private int mMonth = -1;
	private int mDay = -1;
	private int mHour = 0;
	private int mMin = 0;
	private int mSec = 0;
	private long mItem_id = -1;
	private long mLocation_id = -1;
	private long mStore_id = -1;
	private boolean mEditNew = true;
	
	private AlertDialog alert;
	static final private int TAKE_PICTURE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_item);
//		Log.d(WishList.LOG_TAG, "");
		
		// Open the Store table in the database
		mStoreDBAdapter = new StoreDBAdapter(this);
		mStoreDBAdapter.open();

		// Open the Location table in the database
		mLocationDBAdapter = new LocationDBAdapter(this);
		mLocationDBAdapter.open();
		
		pManager = new PositionManager(EditItemInfo.this);
		pManager.startLocationUpdates();

		//find the resources by their ids
		myItemName = (EditText) findViewById(R.id.itemname);
		myNote = (EditText) findViewById(R.id.note);
		myPrice = (EditText) findViewById(R.id.price);
		myStore = (EditText) findViewById(R.id.store);
		myLocation = (EditText) findViewById(R.id.location);
		

		backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
		saveImageButton = (ImageButton) findViewById(R.id.imageButton_save);
		mapImageButton = (ImageButton) findViewById(R.id.imageButton_map);
		cameraImageButton = (ImageButton) findViewById(R.id.imageButton_camera);
		//galleryImageButton = (ImageButton) findViewById(R.id.imageButton_gallery);
		//btnCancel = (Button) findViewById(R.id.button_cancel);
		//btnPhoto = (Button) findViewById(R.id.button_photo);

		imageItem = (ImageView) findViewById(R.id.image_photo);

		imageItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(EditItemInfo.this, FullscreenPhoto.class);
				//i.putExtra("pic_str", picture_str);
				if (_fullsizePhotoPath != null) {
					i.putExtra("fullsize_pic_str", _fullsizePhotoPath);
					startActivity(i);
				}
			}
		});
		
		
		Intent i = getIntent();
		
		//get the fullsizephotopatch, if it is not null, EdiItemInfo is launched from
		//dashboard camera
		_fullsizePhotoPath = i.getStringExtra("fullsizePhotoPath");
		if (_fullsizePhotoPath != null) {
			setPic();
		}
		
		//get item id from previous intent, if there is an item id, we know this EditItemInfo is launched
		//by editing an existing item, so fill the empty box
		mItem_id = i.getLongExtra("item_id", -1);
		
		if(mItem_id != -1) {
			mEditNew = false;
			
			mapImageButton.setVisibility(View.GONE);
			
			WishItem item = WishItemManager.getInstance(this).retrieveItembyId(mItem_id);
			mLocation_id = item.getLocatonId();
			mStore_id = item.getStoreId();

			myItemName.setText(item.getName());
			myNote.setText(item.getDesc());
			String priceStr = item.getPriceAsString();
			if (priceStr != null) {
				myPrice.setText(priceStr);
			}
			myLocation.setText(item.getAddress());
			myStore.setText(item.getStoreName());
			picture_str = item.getPicStr();
			_fullsizePhotoPath = item.getFullsizePicPath();
			Bitmap bitmap = null;
			
			//check if pic_str is null, which user added this item without taking a pic.
			if (picture_str != null){
				Uri picture_Uri = Uri.parse(picture_str);
				
				// check if pic_str is a resId
				try {
					// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
					int picResId = Integer.valueOf(picture_str, 16).intValue();
					bitmap = BitmapFactory.decodeResource(imageItem.getContext()
							.getResources(), picResId);
					// it is resource id.
					imageItem.setImageBitmap(bitmap);

				} catch (NumberFormatException e) {
					// Not a resId, so it must be a content provider uri
					picture_Uri = Uri.parse(picture_str);
					imageItem.setImageURI(picture_Uri);
				}
			}

		}
		

		backImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				navigateBack();
 			}
		});	

		saveImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
				saveWishItem();
 			}
 
		});
		
		mapImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				//get the location
 				Location location = pManager.getCurrentLocation();
 				
 				if (location == null){
 					Toast.makeText(EditItemInfo.this, "location not available", Toast.LENGTH_LONG);
 					myLocation.setText("unknown");
 					
 					//need better value to indicate it's not valid lat and lng
 					lat = Double.MIN_VALUE;
 					lng = Double.MIN_VALUE;
 				}
 				else{
 					//get current latitude and longitude
 					lat = location.getLatitude();
 					lng = location.getLongitude();
 					
 					//getCuttentAddStr using geocode, may take a while, need to put this to a separate thread
 					addStr = pManager.getCuttentAddStr();
 	 				myLocation.setText(addStr);
 				}
			
 			}
 
		});

		cameraImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				dispatchTakePictureIntent();
 				//getThumbailPicture();
 			}
 
		});

//		galleryImageButton.setOnClickListener(new OnClickListener() {
// 			@Override
//			public void onClick(View view) {
// 				//open gallery;
// 			}
// 
//		});

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
		myNote.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						myNote.setSelected(false);
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

//		btnCancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				alert.show();
//			}
//		});

//		btnPhoto.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				getThumbailPicture();
//			}
//
//		});
		
		if (savedInstanceState != null) {
//			Log.d(WishList.LOG_TAG, "savedInstanceState != null");
			// restore the current selected item in the list
			_newfullsizePhotoPath = savedInstanceState.getString("newfullsizePhotoPath");
			_fullsizePhotoPath = savedInstanceState.getString("fullsizePhotoPath");
			_thumbnail = savedInstanceState.getParcelable("bitmap");
			imageItem.setImageBitmap(_thumbnail);
			
//			Log.d(WishList.LOG_TAG, "_newfullsizePhotoPath " + _newfullsizePhotoPath);			
//			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath " + _fullsizePhotoPath);
			
//			if (_thumbnail == null) {
//				Log.d(WishList.LOG_TAG, "_thumbnail null");
//			}
//			else {
//				Log.d(WishList.LOG_TAG, "_thumbnail not null");
//			}
		}
//		else{
//			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
//		}
	}

	/***
	 * Save user input as a wish item
	 */
	private void saveWishItem() {

		if(myItemName.getText().toString().trim().length() == 0){
			Toast toast = Toast.makeText(this, "Please give a name to your wish", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		
		//define variables to hold the item info.
		String itemName = "";
		String itemDesc = "";
		String itemStoreName = "";
		double itemPrice = 0;
		int itemPriority = 0;
		
		try {
			// read in the name, description, price and location of the item
			// from the EditText
			itemName = myItemName.getText().toString().trim();
			itemDesc = myNote.getText().toString().trim();
			itemStoreName = myStore.getText().toString().trim();
			addStr = myLocation.getText().toString().trim();
			itemPrice = Double.valueOf(myPrice.getText().toString().trim());
		}

		catch (NumberFormatException e) {
			// need some error message here
			// price format incorrect
			e.toString();
			itemPrice = Double.MIN_VALUE;
		}

		// user did not specify date_time, use dddd"now" as default date_time
		if (mYear == -1) {
			// get the current date_time
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mHour = c.get(Calendar.HOUR_OF_DAY);//24 hour format
			mMin = c.get(Calendar.MINUTE);
			mSec = c.get(Calendar.SECOND);
		}

		// Format the date_time and save it as a string 
		mDate = new Date(mYear - 1900, mMonth, mDay, mHour, mMin, mSec);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(mDate);

		if (mEditNew) {//we are creating a new item
			// insert the location to the Location table in database
			mLocation_id = mLocationDBAdapter.addLocation(lat, lng, addStr, -1, "N/A", "N/A", "N/A", "N/A", "N/A");

			// insert the store to the Store table in database, linked to the location
			mStore_id = mStoreDBAdapter.addStore(itemStoreName, mLocation_id);
		}
		else {//we are editing an existing item
			mStoreDBAdapter.updateStore(mStore_id, itemStoreName, mLocation_id);
		}

		WishItem item = new WishItem(this, mItem_id, mStore_id, itemStoreName, itemName, itemDesc, 
				date, picture_str, _fullsizePhotoPath, itemPrice, lat, lng, 
				addStr, itemPriority);
		
		mItem_id = item.save();
		//close this activity
		Intent resultIntent = new Intent();
		resultIntent.putExtra("itemID", mItem_id);
		setResult(RESULT_OK, resultIntent);
		finish();
		
//		//start the WishList activity and move the focus to the newly added item
//		Intent wishList = new Intent(this, WishList.class);
//		startActivity(wishList);

	}

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case DATE_DIALOG_ID:
//			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
//					mDay);
//		}
//		return null;
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case TAKE_PICTURE: {
				if (resultCode == RESULT_OK) {
					Log.d(WishList.LOG_TAG, "TAKE_PICTURE: RESULT_OK");
					_fullsizePhotoPath = String.valueOf(_newfullsizePhotoPath);
					_newfullsizePhotoPath = null;
					handleBigCameraPhoto();
				}
//				else {
//					Log.d(WishList.LOG_TAG, "TAKE_PICTURE: not RESULT_OK");
//				}
				break;
			} 
		}//switch
	}
	
	private void dispatchTakePictureIntent() {	

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = PhotoFileCreater.getInstance().setUpPhotoFile();
			_newfullsizePhotoPath = PhotoFileCreater.getInstance().getfullsizePhotoPath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
//			Log.d("wishlist", "IOException" + e.getMessage());
//			e.printStackTrace();
			f = null;
			_newfullsizePhotoPath = null;
			return;
		}
		startActivityForResult(takePictureIntent, TAKE_PICTURE);
	}
		
//	private void handleSmallCameraPhoto(Intent intent) {
////		Bundle extras = intent.getExtras();
////		mImageBitmap = (Bitmap) extras.get("data");
//		//mImageView.setImageBitmap(mImageBitmap);
//		//mVideoUri = null;
////		mImageView.setVisibility(View.VISIBLE);
////		mVideoView.setVisibility(View.INVISIBLE);
//		
//		_thumbnail = intent.getParcelableExtra("data");
//		imageItem.setImageBitmap(_thumbnail);
//
//		// store _thumbnail in the media content provider 
//		// and get the uri of the _thumbnail
//		ContentValues values = new ContentValues();
//		values.put(Media.MIME_TYPE, "image/jpeg");
//		Uri uri = getContentResolver().insert(
//				Media.EXTERNAL_CONTENT_URI, values);
//
//		//compress the _thumbnail to JPEG and write the JEPG to 
//		//the content provider. Save the uri of the JEPG as a string,
//		//which will be inserted in the column "picture_uri" of
//		//the Item table
//		try {
//			OutputStream outStream = getContentResolver()
//					.openOutputStream(uri);
//			_thumbnail.compress(Bitmap.CompressFormat.JPEG, 50,
//					outStream);
//
//			outStream.close();
//			picture_str = uri.toString();
//		} catch (Exception e) {
//			Log.e(WishList.LOG_TAG,
//					"exception while writing image", e);
//		}
//	}

	private void handleBigCameraPhoto() {

		if (_fullsizePhotoPath != null) {
//			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath == " + _fullsizePhotoPath);
			setPic();
			//galleryAddPic();
			//_fullsizePhotoPath = null;
		}
//		else {
//			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath == null");
//		}
	}
	
	private boolean navigateBack(){
		//all fields are empty
		if(myItemName.getText().toString().length() == 0 &&
				myNote.getText().toString().length() == 0 &&
				myPrice.getText().toString().length() == 0 &&
				myLocation.getText().toString().length() == 0 &&
				myStore.getText().toString().length() == 0){

			setResult(RESULT_CANCELED, null);
			finish();
			return false;
		}
		
		//only show warnning if user is editing a new item
		if(mEditNew){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Discard the wish?").setCancelable(
					false).setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							setResult(RESULT_CANCELED, null);
							EditItemInfo.this.finish();
							//return super.onKeyDown(keyCode, event);
						}
					}).setNegativeButton("No",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							//return false;
						}
					});
			alert = builder.create();
			alert.show();
		}
		else{
			setResult(RESULT_CANCELED, null);
			finish();
		}
	return false;
		
	}
	
	/***
	 * called when the "return" button is clicked
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back button.
			return navigateBack();
		}
		return false;
	}
	
	private void setPic() {
		//MediaStore.Images.thumbnails.getThumbnail();
		int width =128;
		int height=128;
		Bitmap bitmap;
//		Bitmap thumbnail;
		
		bitmap = BitmapFactory.decodeFile(_fullsizePhotoPath, null);
		
		if (bitmap == null) {
//			Log.d(WishList.LOG_TAG, "bitmap null");
			return;
		}
		
		else {
			Log.d(WishList.LOG_TAG, "bitmap is not null");
			_thumbnail = android.media.ThumbnailUtils.extractThumbnail(bitmap, width, height);
			if (_thumbnail == null) {
//				Log.d(WishList.LOG_TAG, "_thumbnail null");
				return;
			}
			else {
//				Log.d(WishList.LOG_TAG, "_thumbnail is not null");
				imageItem.setImageBitmap(_thumbnail);
			}
		}
		
		ContentValues values = new ContentValues();
		values.put(Media.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(
				Media.EXTERNAL_CONTENT_URI, values);

		//compress the _thumbnail to JPEG and write the JEPG to 
		//the content provider. Save the uri of the JEPG as a string,
		//which will be inserted in the column "picture_uri" of
		//the Item table
		try {
			OutputStream outStream = getContentResolver()
					.openOutputStream(uri);
			_thumbnail.compress(Bitmap.CompressFormat.JPEG, 100,
					outStream);

			outStream.close();
			picture_str = uri.toString();
		} catch (Exception e) {
//			Log.e(WishList.LOG_TAG,
//					"exception while writing image", e);s
		}

		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

//		/* Get the size of the ImageView */s
//		int targetW = imageItem.getWidth();
//		int targetH = imageItem.getHeight();
//
//		/* Get the size of the image */
//		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//		bmOptions.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//		int photoW = bmOptions.outWidth;
//		int photoH = bmOptions.outHeight;
//		
//		/* Figure out which way needs to be reduced less */
//		int scaleFactor = 1;
//		if ((targetW > 0) || (targetH > 0)) {
//			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
//		}
//
//		/* Set bitmap options to scale the image decode target */
//		bmOptions.inJustDecodeBounds = false;
//		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;
//
//		/* Decode the JPEG file into a Bitmap */
//		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//		
//		//to-do save mCurrentPhotoPath to db
//		
//		/* Associate the Bitmap to the ImageView */
//		imageItem.setImageBitmap(bitmap);
////		mImageView.setImageBitmap(bitmap);
////		mVideoUri = null;
//		imageItem.setVisibility(View.VISIBLE);
////		mVideoView.setVisibility(View.INVISIBLE);
	}
	
	//this will make the photo taken before to show up if user cancels taking a second photo
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
//		Log.d(WishList.LOG_TAG, "");
		savedInstanceState.putString("newfullsizePhotoPath", _newfullsizePhotoPath);
		savedInstanceState.putString("fullsizePhotoPath", _fullsizePhotoPath);
		savedInstanceState.putParcelable("bitmap", _thumbnail);
		if (_thumbnail == null) {
			Log.d(WishList.LOG_TAG, "saved _thumbnail is null");
		}
		else {
			Log.d(WishList.LOG_TAG, "saved _thumbnail is not null");
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore the current selected item in the list
		if (savedInstanceState != null) {
//			Log.d(WishList.LOG_TAG, "savedInstanceState != null");
			_newfullsizePhotoPath = savedInstanceState.getString("newfullsizePhotoPath");
			_fullsizePhotoPath = savedInstanceState.getString("fullsizePhotoPath");
			_thumbnail = savedInstanceState.getParcelable("bitmap");
			imageItem.setImageBitmap(_thumbnail);			
		}
//		else {
//			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
//		}
	}

}