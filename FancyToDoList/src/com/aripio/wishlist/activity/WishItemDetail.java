package com.aripio.wishlist.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.aripio.wishlist.R;
import com.aripio.wishlist.R.anim;
import com.aripio.wishlist.R.id;
import com.aripio.wishlist.R.layout;
import com.aripio.wishlist.db.ItemDBAdapter;
import com.aripio.wishlist.db.LocationDBAdapter;
import com.aripio.wishlist.db.StoreDBAdapter;
import com.aripio.wishlist.db.ItemDBAdapter.ItemsCursor;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private static final String TAG = "WishItemDetail";

	private ListView myListView;
	// private WishListDataBase wishListDB;
	private ItemDBAdapter myItemDBAdapter;
	private ItemsCursor wishItemCursor;
	
	private StoreDBAdapter myStoreDBAdapter;
	private Cursor mStoreCursor;
	
	private LocationDBAdapter myLocationDBAdapter;
	private Cursor mLocationCursor;

	private Handler mHandler;
	private ImageView mPhotoView;
	private TextView mNameView;
	private TextView mDescrptView;
	private View mDetailView;
	private TextView mDateView;
	private TextView mPriceView;
	private TextView mStoreView;
	private TextView mLocationView;
	private ImageButton homeImageButton;
	private ImageButton shareImageButton;
	private ImageButton deleteImageButton;
	private ImageButton editImageButton;
	
	private long mItem_id;
	private int mPosition;
	private int mPrevPosition;
	private int mNextPosition;
	private AlertDialog alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wishitem_detail);
		
		homeImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
		homeImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				//close this activity
 				finish();
 				
 				//start the WishList activity and move the focus to the newly added item
 				Intent home = new Intent(WishItemDetail.this, DashBoard.class);
 				startActivity(home);
 				//onSearchRequested();
 				
 			}
		});	

		shareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
		shareImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
//				shareItem();
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
		
		// Remember the id of the item user clicked
		// in the previous activity (WishList.java)
		Intent i = getIntent();
		mItem_id = i.getLongExtra("item_id", 1);
		mPosition = i.getIntExtra("position", 0);

		// open the Item table in the DB and
		// retrieve the info. of the item via its id
		// wishListDB = WishListDatabase.getDBInstance(this);
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();
		
		myStoreDBAdapter = new StoreDBAdapter(this);
		myStoreDBAdapter.open();
		
		myLocationDBAdapter = new LocationDBAdapter(this);
		myLocationDBAdapter.open();
		
		
		// wishItemCursor = wishListDB.getItem(mItem_id);
		
		// get item
		wishItemCursor = myItemDBAdapter.getItem(mItem_id);
		
		// get store_id from Item table
		// get store name from store table
		long storeID = wishItemCursor.getLong(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_STORE_ID));
		
		mStoreCursor = myStoreDBAdapter.getStore(storeID);
//		String storeName = mStoreCursor.getString(mStoreCursor.
//				getColumnIndexOrThrow(StoreDBAdapter.KEY_NAME));
		String storeName = myStoreDBAdapter.getStoreName(storeID);
		
		// get location
		long locationID = mStoreCursor.getLong(mStoreCursor
				.getColumnIndexOrThrow(StoreDBAdapter.KEY_LOCATION_ID));
		
//		mLocationCursor = myLocationDBAdapter.getLocation(locationID);
//		double latitude = mLocationCursor.getDouble(mLocationCursor.
//				getColumnIndexOrThrow(LocationDBAdapter.KEY_LATITUDE));
//		
//		double longitude = mLocationCursor.getDouble(mLocationCursor.
//				getColumnIndexOrThrow(LocationDBAdapter.KEY_LONGITUDE));
		
//		String addStr =  mLocationCursor.getString(mLocationCursor.
//				getColumnIndexOrThrow(LocationDBAdapter.KEY_ADDSTR));
		String addStr = myLocationDBAdapter.getAddress(locationID);
		
//		String addLine2 =  mLocationCursor.getString(mLocationCursor.
//				getColumnIndexOrThrow(LocationDBAdapter.KEY_ADDLINE2));
//		
//		String addLine3 =  mLocationCursor.getString(mLocationCursor.
//				getColumnIndexOrThrow(LocationDBAdapter.KEY_ADDLINE3));
		
		//String addLine = addLine1 + "\n" + addLine2 + "\n" + addLine3;

		startManagingCursor(wishItemCursor);
		String photoStr = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PHOTO_URL));

		String itemName = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_NAME));

		String itemDescrpt = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DESCRIPTION));

		String itemDate = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DATE_TIME));

		String itemPrice = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PRICE));

//		String itemLocation = wishItemCursor.getString(wishItemCursor
//				.getColumnIndexOrThrow(ItemDBAdapter.KEY_LOCATION));

		String itemPriority = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PRIORITY));

		// format the date time
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat(
				"MMMM dd, yyyy, hh:mm aaa");

		String dateTimeStrNew = null;
		try {
			dateTimeStrNew = sdfTo.format(sdfFrom.parse(itemDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// format the price
		String priceStrNew = "$" + itemPrice;

		// get the resources by their IDs		
		mDetailView = findViewById(R.id.itemDetail);
		mPhotoView = (ImageView) findViewById(R.id.imgPhotoDetail);
		mNameView = (TextView) findViewById(R.id.itemNameDetail);
		mDescrptView = (TextView) findViewById(R.id.itemDesriptDetail);
		mDateView = (TextView) findViewById(R.id.itemDateDetail);
		mPriceView = (TextView) findViewById(R.id.itemPriceDetail);
		mStoreView = (TextView) findViewById(R.id.itemStoreDetail);
		mLocationView = (TextView) findViewById(R.id.itemLocationDetail);
		// mPriorityView = (TextView) findViewById(R.id.itemDateDetail);

		Bitmap bitmap = null;
		
		//check if pic_str is null, which user added this item without taking a pic.
		if (photoStr != null){
			Uri photoUri = Uri.parse(photoStr);
			
			// check if pic_str is a resId
			try {
				// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
				int picResId = Integer.valueOf(photoStr, 16).intValue();
				bitmap = BitmapFactory.decodeResource(mPhotoView.getContext()
						.getResources(), picResId);
				// it is resource id.
				mPhotoView.setImageBitmap(bitmap);

			} catch (NumberFormatException e) {
				// Not a resId, so it must be a content provider uri
				photoUri = Uri.parse(photoStr);
				mPhotoView.setImageURI(photoUri);

			}
		}

		//display the item info. in the views
		// mPhotoView.setImageURI(photoUri);
		mNameView.setText(itemName);
		mDescrptView.setText(itemDescrpt);
		mDateView.setText(dateTimeStrNew);
		mPriceView.setText(priceStrNew);
		mStoreView.setText("Store: " + storeName);
		//mLocationView.setText(itemLocation);
		//mLocationView.setText("latitude:" + Double.toString(latitude) + " longitude:" + Double.toString(longitude));
		mLocationView.setText(addStr);	

		// set the gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());

		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

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
		ItemsCursor c = myItemDBAdapter.getItems(ItemsCursor.SortBy.item_name);
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
		ItemsCursor c = myItemDBAdapter.getItems(ItemsCursor.SortBy.item_name);
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
						myItemDBAdapter.deleteItem(mItem_id);
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
		startActivity(i);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

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

}