package com.aripio.wishlist.activity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.aripio.wishlist.R;
import com.aripio.wishlist.db.ItemDBAdapter;
import com.aripio.wishlist.db.LocationDBAdapter;
import com.aripio.wishlist.db.StoreDBAdapter;
import com.aripio.wishlist.model.WishItem;
import com.aripio.wishlist.model.WishItemManager;
import com.aripio.wishlist.util.PositionManager;
import com.aripio.wishlist.util.AlbumStorageDirFactory;
import com.aripio.wishlist.util.BaseAlbumDirFactory;
import com.aripio.wishlist.util.FroyoAlbumDirFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
	private EditText myDescription;
	private EditText myPrice;
	private EditText myLocation;

	private ImageButton backImageButton;
	private ImageButton saveImageButton;
	private ImageButton mapImageButton;
	private ImageButton cameraImageButton;
	private ImageButton galleryImageButton;
//	private Button btnCancel;<
//	private Button btnPhoto;
	private ImageView imageItem;
	private Date mDate;
	private double lat = 0;
	private double lng = 0;
	private String addStr = "unknown";
	private Bitmap thumbnail;
	private Bitmap mImageBitmap;
	private String picture_str = Integer.toHexString(R.drawable.logo);//default pic is logo
	private String _fullsizePhotoPath = null;
	private String _newfullsizePhotoPath = null;
	private StoreDBAdapter mStoreDBAdapter;
	private LocationDBAdapter mLocationDBAdapter;
	private int mYear = -1;
	private int mMonth = -1;
	private int mDay = -1;
	private int mHour = 0;
	private int mMin = 0;
	private int mSec = 0;
	private long mItem_id = -1;
	private boolean mEditNew = true;
	
	private AlertDialog alert;
	static final private int TAKE_PICTURE = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);
		Log.d(WishList.LOG_TAG, "");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		// Open the Store table in the database
		mStoreDBAdapter = new StoreDBAdapter(this);
		mStoreDBAdapter.open();

		// Open the Location table in the database
		mLocationDBAdapter = new LocationDBAdapter(this);
		mLocationDBAdapter.open();

		//find the resources by their ids
		myItemName = (EditText) findViewById(R.id.itemname);
		myDescription = (EditText) findViewById(R.id.description);
		myPrice = (EditText) findViewById(R.id.price);
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
				i.putExtra("fullsize_pic_str", _fullsizePhotoPath);
				startActivity(i);
			}
		});
		
		//get item id from previous intent, if there is an item id, we know this EditItemInfo is launched
		//from ItemDetail, so fill the empty box
		Intent i = getIntent();
		mItem_id = i.getLongExtra("item_id", -1);
		
		if(mItem_id != -1) {
			mEditNew = false;
			
			WishItem item = WishItemManager.getInstance(this).retrieveItembyId(mItem_id);

			myItemName.setText(item.getName());
			myDescription.setText(item.getDesc());
			myPrice.setText(Double.toString(item.getPrice()));
			myLocation.setText(item.getAddress());
			picture_str = item.getPicStr();
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
 				PositionManager pManager = new PositionManager(EditItemInfo.this);
 				Location location = pManager.getCurrentLocation();
 				
 				if (location == null){
 					Toast.makeText(EditItemInfo.this, "location not available", Toast.LENGTH_LONG);
 					myLocation.setText("unknown");
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
			Log.d(WishList.LOG_TAG, "savedInstanceState != null");
			// restore the current selected item in the list
			_newfullsizePhotoPath = savedInstanceState.getString("newfullsizePhotoPath");
			_fullsizePhotoPath = savedInstanceState.getString("fullsizePhotoPath");
			thumbnail = savedInstanceState.getParcelable("bitmap");
			imageItem.setImageBitmap(thumbnail);
			
			Log.d(WishList.LOG_TAG, "_newfullsizePhotoPath " + _newfullsizePhotoPath);			
			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath " + _fullsizePhotoPath);
			
			if (thumbnail == null) {
				Log.d(WishList.LOG_TAG, "thumbnail null");
			}
			else {
				Log.d(WishList.LOG_TAG, "thumbnail not null");
			}
		}
		else{
			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
		}
	}

	/***
	 * Save user input as a wish item
	 */
	private void saveWishItem() {

//		//get the location
//		PositionManager pManager = new PositionManager(this);
//		Location location = pManager.getCurrentLocation();
//		
//		if (location == null){
//			Toast.makeText(this, "location not available", Toast.LENGTH_LONG);
//
//		}
//		else{
//			//get current latitude and longitude
//			lat = location.getLatitude();
//			lng = location.getLongitude();
//			
//			//getCuttentAddStr using geocode, may take a while, need to put this to a separate thread
//			addStr = pManager.getCuttentAddStr();
//		}
		
		if(myItemName.getText().toString().length() == 0){
			Toast toast = Toast.makeText(this, "Please give a name to your wish", Toast.LENGTH_SHORT);
			toast.show();
			return;
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
			mHour = c.get(Calendar.HOUR_OF_DAY);//24 hour format
			mMin = c.get(Calendar.MINUTE);
			mSec = c.get(Calendar.SECOND);
		}

		// Format the date_time and save it as a string 
		mDate = new Date(mYear - 1900, mMonth, mDay, mHour, mMin, mSec);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(mDate);

		// insert the location to the Location table in database
		long locationID = mLocationDBAdapter.addLocation(lat, lng, addStr, -1, "N/A", "N/A", "N/A", "N/A", "N/A");

		// insert the store to the Store table in database, linked to the location
		long storeID = mStoreDBAdapter.addStore(storeName, locationID);

		WishItem item = new WishItem(this, -1, storeID, storeName, itemName, itemDesc, 
				date, picture_str, _fullsizePhotoPath, itemPrice,
				itemLocation, itemPriority);
		
		item.save();
		//close this activity
		finish();
		
		//start the WishList activity and move the focus to the newly added item
		Intent wishList = new Intent(this, WishList.class);
		startActivity(wishList);

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
				else {
					Log.d(WishList.LOG_TAG, "TAKE_PICTURE: not RESULT_OK");
				}
				break;
			} 
		}//switch
	}
	
//	private void getThumbailPicture() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(intent, TAKE_PICTURE);
//	}
	
	private void dispatchTakePictureIntent() {	

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			//takePictureIntent.putExtra(getString(R.string.fullSizePhotoLocation), Uri.fromFile(f));
		} catch (IOException e) {
			Log.d("wishlist", "IOException" + e.getMessage());
			e.printStackTrace();
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
//		thumbnail = intent.getParcelableExtra("data");
//		imageItem.setImageBitmap(thumbnail);
//
//		// store thumbnail in the media content provider 
//		// and get the uri of the thumbnail
//		ContentValues values = new ContentValues();
//		values.put(Media.MIME_TYPE, "image/jpeg");
//		Uri uri = getContentResolver().insert(
//				Media.EXTERNAL_CONTENT_URI, values);
//
//		//compress the thumbnail to JPEG and write the JEPG to 
//		//the content provider. Save the uri of the JEPG as a string,
//		//which will be inserted in the column "picture_uri" of
//		//the Item table
//		try {
//			OutputStream outStream = getContentResolver()
//					.openOutputStream(uri);
//			thumbnail.compress(Bitmap.CompressFormat.JPEG, 50,
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
			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath == " + _fullsizePhotoPath);
			setPic();
			//galleryAddPic();
			//_fullsizePhotoPath = null;
		}
		else {
			Log.d(WishList.LOG_TAG, "_fullsizePhotoPath == null");
		}
	

	}
	
	private boolean navigateBack(){
		//all fields are empty
		if(myItemName.getText().toString().length() == 0 &&
				myDescription.getText().toString().length() == 0 &&
				myPrice.getText().toString().length() == 0 &&
				myLocation.getText().toString().length() == 0){

			EditItemInfo.this.finish();
			return false;
		}
		
		//only show warnning if user is editing a new item
		if(mEditNew){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Discard the wish?").setCancelable(
					false).setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
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
			EditItemInfo.this.finish();
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
	
	private String getAlbumName() {
		//return getString(R.string.album_name);
		return "WishListPhoto";
	}

	
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("wishlist", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v("wishlist", "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		_newfullsizePhotoPath = f.getAbsolutePath();
		Log.d("wishlist", _newfullsizePhotoPath);
		return f;
	}

	private void setPic() {
		//MediaStore.Images.Thumbnails.getThumbnail();
		int width =128;
		int height=128;
		Bitmap bitmap;
		Bitmap thumbnail;
		
		bitmap = BitmapFactory.decodeFile(_fullsizePhotoPath, null);
		
		if (bitmap == null) {
			Log.d(WishList.LOG_TAG, "bitmap null");
			return;
		}
		
		else {
			Log.d(WishList.LOG_TAG, "bitmap is not null");
			thumbnail = android.media.ThumbnailUtils.extractThumbnail(bitmap, width, height);
			if (thumbnail == null) {
				Log.d(WishList.LOG_TAG, "thumbnail null");
				return;
			}
			else {
				Log.d(WishList.LOG_TAG, "thumbnail is not null");
				imageItem.setImageBitmap(thumbnail);
			}
		}
		
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
			picture_str = uri.toString();
		} catch (Exception e) {
			Log.e(WishList.LOG_TAG,
					"exception while writing image", e);
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
		Log.d(WishList.LOG_TAG, "");
		savedInstanceState.putString("newfullsizePhotoPath", _newfullsizePhotoPath);
		savedInstanceState.putString("fullsizePhotoPath", _fullsizePhotoPath);
		savedInstanceState.putParcelable("bitmap", thumbnail);
		if (thumbnail == null) {
			Log.d(WishList.LOG_TAG, "saved thumbnail is null");
		}
		else {
			Log.d(WishList.LOG_TAG, "saved thumbnail is not null");
		}
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
			thumbnail = savedInstanceState.getParcelable("bitmap");
			imageItem.setImageBitmap(thumbnail);			
		}
		else {
			Log.d(WishList.LOG_TAG, "savedInstanceState == null");
		}
	}

}