package com.wish.wishlist.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.wish.wishlist.R;
import com.wish.wishlist.db.ItemDBAdapter;
import com.wish.wishlist.db.ItemDBAdapter.ItemsCursor;
import com.wish.wishlist.model.WishItem;
import com.wish.wishlist.model.WishItemManager;
import com.wish.wishlist.util.DateTimeFormatter;
import com.wish.wishlist.util.ImageManager;
import com.wish.wishlist.util.social.ShareHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/***
 * WishItemDetail is responsible for displaying the detailed info. of an item. 
 * It also handles the left/right swipe gesture form user, which correspond to 
 * navigating to the previous and next item, respectively.
 * 
 * the order of the items during swiping is the order of the items displayed in 
 * the WishList activity
 */
public class WishItemDetail extends Activity {
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private static final int EDIT_ITEM = 0;

	// private WishListDataBase wishListDB;
	private ItemDBAdapter myItemDBAdapter;

	private ImageView mPhotoView;
	private TextView mNameView;
	private TextView mDescrptView;
	private TextView mDateView;
	private TextView mPriceView;
	private TextView mStoreView;
	private TextView mLocationView;
	private ImageButton backImageButton;
//	private ImageButton shareImageButton;
	private ImageButton deleteImageButton;
	private ImageButton editImageButton;
	private ImageButton shareImageButton;
	
	private long mItem_id = -1;
	private int mPosition;
	private int mPrevPosition;
	private int mNextPosition;
	private AlertDialog alert;
	private String picture_str = Integer.toHexString(R.drawable.logo);//default pic is logo
	private String fullsize_picture_str=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wishitem_detail);

		setUpActionBar();


//		shareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
//		shareImageButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
////				shareItem();
//			}
//		});

		
		// Remember the id of the item user clicked
		// in the previous activity (WishList.java)
		Intent i = getIntent();
		mItem_id = i.getLongExtra("item_id", -1);
		mPosition = i.getIntExtra("position", 0);

		WishItem item = WishItemManager.getInstance(this).retrieveItembyId(mItem_id);
		double lat = item.getLatitude();
		double lng = item.getLongitude();
		String address = item.getAddress();
		
		if (lat != Double.MIN_VALUE && lng != Double.MIN_VALUE && (address.equals("unknown") || address.equals(""))) {
			//we have a location by gps, but don't have an address
			Geocoder gc = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = gc.getFromLocation(lat, lng, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address add = addresses.get(0);
					for (int k = 0; k < add.getMaxAddressLineIndex()+1; k++)
						sb.append(add.getAddressLine(k)).append("\n");
				}
				address = sb.toString();
			} catch (IOException e) {
				address = "unknown";
			}
			item.setAddress(address);
			item.save();
		}

		mNameView = (TextView) findViewById(R.id.itemNameDetail);
		mDescrptView = (TextView) findViewById(R.id.itemDesriptDetail);
		mDateView = (TextView) findViewById(R.id.itemDateDetail);
		mPriceView = (TextView) findViewById(R.id.itemPriceDetail);
		mStoreView = (TextView) findViewById(R.id.itemStoreDetail);
		mLocationView = (TextView) findViewById(R.id.itemLocationDetail);
		mPhotoView = (ImageView) findViewById(R.id.imgPhotoDetail);
		
		showItemInfo(item);

//		// set the gesture detection
//		gestureDetector = new GestureDetector(new MyGestureDetector());
//
//		gestureListener = new View.OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				if (gestureDetector.onTouchEvent(event)) {
//					return true;
//				}
//				return false;
//			}
//		};
		
		mPhotoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(WishItemDetail.this, FullscreenPhoto.class);
				if (fullsize_picture_str != null) {
					i.putExtra("fullsize_pic_str", fullsize_picture_str);
					startActivity(i);
				}
			}
		});

	}
	
	private void showItemInfo(WishItem item) {
		fullsize_picture_str = item.getFullsizePicPath();
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();  // deprecated
		int height = display.getHeight();  // deprecated
		if (fullsize_picture_str != null) {
//			Log.d("wishlist", "fullsize_picture_str == " + fullsize_picture_str);
			Bitmap bitmap = ImageManager.getInstance().decodeSampledBitmapFromFile(fullsize_picture_str, width, height, true);
			//Bitmap bitmap = BitmapFactory.decodeFile(fullsize_picture_str, null);
			if (bitmap == null) {
//				Log.d("wishlist", "bitmap == null");
			}
			else {
				mPhotoView.setImageBitmap(bitmap);
			}
		}
		
		//check if pic_str is null, which user added this item without taking a pic.
		if (fullsize_picture_str == null) {
//			Log.d("wishlist", "fullsize_picture_str == null");
			picture_str = item.getPicStr();

			if (picture_str == null){
//				Log.d("wishlist", "picture_str == null");
			}
			else{
				if (picture_str.endsWith("sample")) {
					picture_str = picture_str.substring(0, picture_str.length() - 6);
				}
				Bitmap bitmap = null;
				Uri photoUri = Uri.parse(picture_str);

				// check if pic_str is a resId
				try {
					// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
					int picResId = Integer.valueOf(picture_str, 16).intValue();
					Log.d("wishlist", "width is " + String.valueOf(width));
					bitmap = ImageManager.getInstance().decodeSampledBitmapFromResource(mPhotoView.getContext().getResources(), picResId, width, height, true);
					//bitmap = BitmapFactory.decodeResource(mPhotoView.getContext()
					//		.getResources(), picResId);
					// it is resource id.
					mPhotoView.setImageBitmap(bitmap);

				} catch (NumberFormatException e) {
					// Not a resId, so it must be a content provider uri
					photoUri = Uri.parse(picture_str);
					mPhotoView.setImageURI(photoUri);
				}
			}
		}

		String dateTimeStr = item.getDate();
		String dateTimeStrNew = DateTimeFormatter.getInstance().getDateTimeString(dateTimeStr);
		
		mNameView.setText(item.getName());
		mDateView.setText(dateTimeStrNew);
		
		// format the price
		String priceStr = item.getPriceAsString();
		if (priceStr != null) {
			mPriceView.setText("$" + priceStr);	
			mPriceView.setVisibility(View.VISIBLE);
		}
		else {
			mPriceView.setVisibility(View.GONE);
		}
		
		//used as a note
		String descrptStr = item.getDesc();
		if (!descrptStr.equals("")) {
			mDescrptView.setText(descrptStr);
			mDescrptView.setVisibility(View.VISIBLE);
		}
		else {
			mDescrptView.setVisibility(View.GONE);
		}
		
		String storeName = item.getStoreName();
		if (!storeName.equals("")) {
			mStoreView.setText("At " + storeName);	
			mStoreView.setVisibility(View.VISIBLE);
		}
		else {
			mStoreView.setVisibility(View.GONE);
		}
		
		String address = item.getAddress();
		if (!address.equals("unknown") && !address.equals("")) {
			if (storeName.equals("")) {
				address = "At " + address;
			}
			mLocationView.setText(address);	
			mLocationView.setVisibility(View.VISIBLE);
		}
		else {
			mLocationView.setVisibility(View.GONE);
		}
	}

	/***
	 * get the _ID of the item in Item table
	 * whose position in the listview is next 
	 * to the current item
	 * 
	 * @return
	 */
	private long[] getNextDBItemID() {

		// Get all of the rows from the database in sorted order as in the
		long[] next_pos_id = new long[2];
		// ItemsCursor c = wishListDB.getItems(ItemsCursor.SortBy.name);
		// open the database for operations of Item table
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();
		ItemsCursor c = myItemDBAdapter.getItems(ItemsCursor.SortBy.item_name);
		myItemDBAdapter.close();
		long nextItemID;
		if (mPosition < c.getCount())
			mNextPosition = mPosition + 1;

		else
			mNextPosition = mPosition;

		c.move(mNextPosition);
		// nextItemID = c.getLong(
		// c.getColumnIndexOrThrow(WishListDataBase.KEY_ITEMID));
		nextItemID = c.getLong(c.getColumnIndexOrThrow(ItemDBAdapter.KEY_ID));

		// long item_id = Long.parseLong(itemIdTextView.getText().toString());
		next_pos_id[0] = mNextPosition;
		next_pos_id[1] = nextItemID;
		return next_pos_id;
	}
	
	/***
	 * get the _ID of the item in Item table
	 * whose position in the listview is previous 
	 * to the current item
	 * 
	 * @return
	 */

	private long[] getPrevDBItemID() {

		long[] prev_pos_id = new long[2];

		// ItemsCursor c = wishListDB.getItems(ItemsCursor.SortBy.name);
		// open the database for operations of Item table
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();
		ItemsCursor c = myItemDBAdapter.getItems(ItemsCursor.SortBy.item_name);
		myItemDBAdapter.close();
		long prevItemID;
		if (mPosition > 0)
			mPrevPosition = mPosition - 1;

		else
			mPrevPosition = mPosition;

		c.move(mPrevPosition);
		// prevItemID = c.getLong(
		// c.getColumnIndexOrThrow(WishListDataBase.KEY_ITEMID));
		prevItemID = c.getLong(c.getColumnIndexOrThrow(ItemDBAdapter.KEY_ID));
		// long item_id = Long.parseLong(itemIdTextView.getText().toString());
		prev_pos_id[0] = mPrevPosition;
		prev_pos_id[1] = prevItemID;
		return prev_pos_id;
	}
	
	private void deleteItem(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Discard the wish?").setCancelable(
				false).setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						WishItemManager.getInstance(WishItemDetail.this).deleteItembyId(mItem_id);
						WishItemDetail.this.finish();
						//return super.onKeyDown(keyCode, event);
					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alert = builder.create();
		alert.show();
	}
	
	private void editItem(){
		Intent i = new Intent(WishItemDetail.this, EditItemInfo.class);
		i.putExtra("item_id", mItem_id);
		//i.putExtra("position", position);
		startActivityForResult(i, EDIT_ITEM);
	}
	
	private void shareItem(){
		ShareHelper share = new ShareHelper(this, mItem_id);
		share.share();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case EDIT_ITEM: {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					long id = data.getLongExtra("itemID", -1);
					if (id != -1) {
						WishItem item = WishItemManager.getInstance(this).retrieveItembyId(id);
						showItemInfo(item);
					}
					
				}
				
			}
			else {

			}
			break;
		}
	}
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Toast.makeText(WishItemDetail.this, "swipe to right",
					// Toast.LENGTH_SHORT).show();

					//get the item id of the next item and
					//start a new activity to display the
					//next item's detailed info.
					long[] next_p_i = new long[2];
					next_p_i = getNextDBItemID();
					Intent i = new Intent(WishItemDetail.this,
							WishItemDetail.class);

					i.putExtra("position", (int) next_p_i[0]);
					i.putExtra("item_id", next_p_i[1]);

					startActivity(i);
					// Set the transition -> method available from Android 2.0
					// and beyond
					overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_right_out);

					// WishItemDetail.this.overridePendingTransition(0,0);

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Toast.makeText(WishItemDetail.this, "swipe to left",
					// Toast.LENGTH_SHORT).show();


					//get the item id of the previous item and
					//start a new activity to display the
					//previous item's detailed info.
					long[] prev_p_i = new long[2];
					prev_p_i = getPrevDBItemID();
					Intent i = new Intent(WishItemDetail.this,
							WishItemDetail.class);
					i.putExtra("position", (int) prev_p_i[0]);
					i.putExtra("item_id", prev_p_i[1]);

					startActivity(i);
					overridePendingTransition(R.anim.slide_right_in,
							R.anim.slide_left_out);
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event))
//			return true;
//		else
//			return false;
//	}

	/***
	 * called when the "return" button is clicked
	 * it closes the WishItemDetail activity and starts
	 * the WishList activity
	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			// do something on back.
//			startActivity(new Intent(WishItemDetail.this, WishList.class));
//			WishItemDetail.this.finish();
//
//			return true;
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_item_detail, menu);
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		long itemId = item.getItemId();
		if (itemId ==  android.R.id.home) {
			finish();
			return true;
		}
		else if (itemId == R.id.menu_item_detail_edit) {
			editItem();
			return true;
		}
		else if (itemId == R.id.menu_item_detail_share) {
			shareItem();
			return true;
		}
		else if (itemId == R.id.menu_item_detail_map) {
			double[] dLocation = new double[2];
			// open the database for operations of Item table
			myItemDBAdapter = new ItemDBAdapter(this);
			myItemDBAdapter.open();
			dLocation = myItemDBAdapter.getItemLocation(mItem_id);
			myItemDBAdapter.close();
			
			if (dLocation[0] == Double.MIN_VALUE && dLocation[1] == Double.MIN_VALUE) {
				Toast toast = Toast.makeText(this, "location unknown", Toast.LENGTH_SHORT);
				toast.show();
			}
			else {
				Intent mapIntent = new Intent(this, WishListMap.class);
				mapIntent.putExtra("type", "markOne");
				mapIntent.putExtra("latitude", dLocation[0]);
				mapIntent.putExtra("longitude", dLocation[1]);
				startActivity(mapIntent);
			}
			return true;
		}
		else if (itemId == R.id.menu_item_detail_delete) {
			deleteItem();
			return true;
		}
		return false;
	}

	private void setUpActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			findViewById(R.id.detailView_header).setVisibility(View.GONE);
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else {
			// we use the header instead of action bar for GingerBread and lower
			findViewById(R.id.detailView_header).findViewById(R.id.imageButton_back_logo).setVisibility(View.VISIBLE);
			findViewById(R.id.detailView_header).findViewById(R.id.imageButton_delete).setVisibility(View.VISIBLE);
			findViewById(R.id.detailView_header).findViewById(R.id.imageButton_edit).setVisibility(View.VISIBLE);
			findViewById(R.id.detailView_header).findViewById(R.id.imageButton_share).setVisibility(View.VISIBLE);

			backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
			backImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
					//start the WishList activity and move the focus to the newly added item
					//				Intent home = new Intent(WishItemDetail.this, DashBoard.class);
					//				startActivity(home);
					//onSearchRequested();
				}
			});

			deleteImageButton = (ImageButton) findViewById(R.id.imageButton_delete);
			deleteImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					deleteItem();
				}
			});

			editImageButton = (ImageButton) findViewById(R.id.imageButton_edit);
			editImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					editItem();
				}
			});

			shareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
			shareImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					shareItem();
				}
			});
		}
	}
}
