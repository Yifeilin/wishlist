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
import com.aripio.wishlist.model.WishItem;
import com.aripio.wishlist.model.WishItemManager;
import com.aripio.wishlist.util.DateTimeFormatter;
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
//	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private static final String TAG = "WishItemDetail";
	private static final int EDIT_ITEM = 0;

	private ListView myListView;
	// private WishListDataBase wishListDB;
	private ItemDBAdapter myItemDBAdapter;

	private ImageView mPhotoView;
	private TextView mNameView;
	private TextView mDescrptView;
	private View mDetailView;
	private TextView mDateView;
	private TextView mPriceView;
	private TextView mStoreView;
	private TextView mLocationView;
	private ImageButton backImageButton;
//	private ImageButton shareImageButton;
	private ImageButton deleteImageButton;
	private ImageButton editImageButton;
	
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
		
		backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
		backImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				//close this activity
 				finish();
 				//start the WishList activity and move the focus to the newly added item
// 				Intent home = new Intent(WishItemDetail.this, DashBoard.class);
// 				startActivity(home);
 				//onSearchRequested();
 				
 			}
		});	

//		shareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
//		shareImageButton.setOnClickListener(new OnClickListener() {
// 			@Override
//			public void onClick(View view) {
////				shareItem();
// 			}
//		});

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
		mItem_id = i.getLongExtra("item_id", -1);
		mPosition = i.getIntExtra("position", 0);

		WishItem item = WishItemManager.getInstance(this).retrieveItembyId(mItem_id);

		// get the resources by their IDs		
		mDetailView = findViewById(R.id.itemDetail);
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
		if (fullsize_picture_str != null) {
			Log.d("wishlist", "fullsize_picture_str == " + fullsize_picture_str);
			Bitmap bitmap = BitmapFactory.decodeFile(fullsize_picture_str, null);
			if (bitmap == null) {
				Log.d("wishlist", "bitmap == null");
			}
			else {
				mPhotoView.setImageBitmap(bitmap);
			}
		}
		
		//check if pic_str is null, which user added this item without taking a pic.
		if (fullsize_picture_str == null) {
			Log.d("wishlist", "fullsize_picture_str == null");
			picture_str = item.getPicStr();
			if (picture_str == null){
				Log.d("wishlist", "picture_str == null");
			}
			else{
				Bitmap bitmap = null;
				Uri photoUri = Uri.parse(picture_str);

				// check if pic_str is a resId
				try {
					// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
					int picResId = Integer.valueOf(picture_str, 16).intValue();
					bitmap = BitmapFactory.decodeResource(mPhotoView.getContext()
							.getResources(), picResId);
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case EDIT_ITEM: {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					long id = data.getLongExtra("itemID", -1);
					if (id != -1) {
						WishItem item = WishItemManager.getInstance(this).retrieveItembyId(mItem_id);
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

}