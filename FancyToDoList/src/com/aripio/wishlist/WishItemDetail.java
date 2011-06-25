/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aripio.wishlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.aripio.wishlist.WishListDataBase.ItemsCursor;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays the detail of an item.
 */
public class WishItemDetail extends Activity {
	//private WishList mWishList;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	private static final String TAG = "WishItemDetail";
	
	private ListView myListView;
	private WishListDataBase wishListDB;
	private ItemsCursor wishItemCursor;

//    private static final int MENU_RADAR = Menu.FIRST + 1;
//
//    private static final int MENU_MAP = Menu.FIRST + 2;
//
//    private static final int MENU_AUTHOR = Menu.FIRST + 3;
//
//    private static final int MENU_VIEW = Menu.FIRST + 4;
//
//    private static final int DIALOG_NO_RADAR = 1;

    //PanoramioItem mItem;

    private Handler mHandler;

    private ImageView mPhotoView;

    private TextView mNameView;

    private TextView mDescrptView;
    
    private View mDetailView;
    
    private TextView mDateView;
    
    private TextView mPriceView;
    
    private TextView mLocationView;
    
    //private long id_pos[];
    private long mItem_id;
    
    private int mPosition;
    private int mPrevPosition;
    private int mNextPosition;

    //private int mMapZoom;

    //private int mMapLatitudeE6;

    //private int mMapLongitudeE6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.view_image);
        setContentView(R.layout.wishitem_detail);
        
		//myListView = (ListView) findViewById(R.id.myListView);

        // Remember the id of the item user selected
        Intent i = getIntent();
        mItem_id = i.getLongExtra("item_id", 0);
        mPosition = i.getIntExtra("position", 0);
        
        //mItem_id = getDBItemID(mPosition);
        
		// retrieve the info. of the item from DB
		wishListDB = WishListDataBase.getDBInstance(this);
		
        wishItemCursor = wishListDB.getItem(mItem_id);
        
        startManagingCursor(wishItemCursor);
        String photoStr = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_PHOTO_URL));
        Uri photoUri =  Uri.parse(photoStr);
        
        String itemName = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_NAME));
        	
        String itemDescrpt = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_DESCRIPTION));
        	
        String itemDate = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_DATE));
        
        String itemPrice = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_PRICE));
     
        String itemLocation = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_LOCATION));
        
        String itemPriority = wishItemCursor.getString(
        		wishItemCursor.getColumnIndexOrThrow(WishListDataBase.KEY_PRIORITY));
        
        //format the date time
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("MMMM dd, yyyy, hh:mm aaa");
	
		String dateTimeStrNew = null;
		try {
			dateTimeStrNew = sdfTo.format( sdfFrom.parse(itemDate) );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//format the price
		String priceStrNew = "$"+itemPrice;
		
       
        //if (wishItemCursor == null || wishItemCursor.getCount() == 0){
        
       
        //long item_id = Long.parseLong(itemIdTextView.getText().toString());
//        mHandler = new Handler();

        mDetailView = findViewById(R.id.itemDetail);
        mPhotoView = (ImageView) findViewById(R.id.imgPhotoDetail);
        mNameView = (TextView) findViewById(R.id.itemNameDetail);
        mDescrptView = (TextView) findViewById(R.id.itemDesriptDetail);
        mDateView = (TextView) findViewById(R.id.itemDateDetail);
        mPriceView = (TextView) findViewById(R.id.itemPriceDetail);
        mLocationView = (TextView) findViewById(R.id.itemLocationDetail);
        //mPriorityView = (TextView) findViewById(R.id.itemDateDetail);
        
        Bitmap bitmap = null;
         
        //check if pic_str is a resId          
        try {
      	  	//view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
      	  	int picResId = Integer.valueOf(photoStr, 16).intValue();
            bitmap = BitmapFactory.decodeResource(mPhotoView.getContext().getResources(), picResId);
            //it is resource id.
            mPhotoView.setImageBitmap(bitmap);
            
        } catch (NumberFormatException e) {
            //Not a resId, so it must be a content provider uri
            photoUri = Uri.parse(photoStr);
            mPhotoView.setImageURI(photoUri);

        }
        
        //mPhotoView.setImageURI(photoUri);
        mNameView.setText(itemName);
        mDescrptView.setText(itemDescrpt);
        mDateView.setText(dateTimeStrNew);
        mPriceView.setText(priceStrNew);
        mLocationView.setText(itemLocation);
        
        
//        mDescrptView.setVisibility(View.GONE);
//        getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
//                Window.PROGRESS_VISIBILITY_ON);
        
        //setting the gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        //myListView.getDBItemID();

    }
    
    //get the _ID of the item in wishitem database 
    //whose position in the listview is pos.
    private long[] getNextDBItemID(){
		//View selected_view = myListView.getChildAt(pos);
		//TextView itemIdTextView = (TextView) selected_view.findViewById(R.id.txtItemID);
		//TextView dateTextView = (TextView) selected_view.findViewById(R.id.txtDate);
		//String item_id_str = itemIdTextView.getText().toString();
    	
        // Get all of the rows from the database in sorted order as in the 
    	// wish list
		// Open or create the database
		//wishLite
    	long[] next_pos_id = new long[2];
    	ItemsCursor c = wishListDB.getItems(ItemsCursor.SortBy.name);
    	long nextItemID;
    	if (mPosition < c.getCount())
        	mNextPosition = mPosition + 1;
   	
    	else 
    		mNextPosition = mPosition;
    		
 		c.move(mNextPosition);
        nextItemID = c.getLong( 
        		c.getColumnIndexOrThrow(WishListDataBase.KEY_ITEMID));
        
		//long item_id = Long.parseLong(itemIdTextView.getText().toString());
        next_pos_id[0] = mNextPosition;
        next_pos_id[1] = nextItemID;
		return next_pos_id;
    }
    
    private long[] getPrevDBItemID(){
    	
    	long[] prev_pos_id = new long[2];

    	ItemsCursor c = wishListDB.getItems(ItemsCursor.SortBy.name);
        long prevItemID;
        if (mPosition > 0)
        	mPrevPosition = mPosition - 1;
        
        else
        	mPrevPosition = mPosition;
        
        c.move(mPrevPosition);
        prevItemID = c.getLong( 
        		c.getColumnIndexOrThrow(WishListDataBase.KEY_ITEMID));
		//long item_id = Long.parseLong(itemIdTextView.getText().toString());
        prev_pos_id[0] = mPrevPosition;
        prev_pos_id[1] = prevItemID;   
		return prev_pos_id;
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                	viewFlipper.setInAnimation(slideLeftIn);
//                    viewFlipper.setOutAnimation(slideLeftOut);
//                	viewFlipper.showNext();
//                	Toast.makeText(WishItemDetail.this, "swipe to right", 
//                			Toast.LENGTH_SHORT).show();
                	
                	//getDBItemID(2);
                	long[] next_p_i = new long[2];
                	next_p_i = getNextDBItemID();
    		        Intent i = new Intent(WishItemDetail.this, WishItemDetail.class);
    		        
    		        i.putExtra("position", (int) next_p_i[0]);
    		        i.putExtra("item_id", next_p_i[1] );
    		        
    		        startActivity(i);
    		        //Set the transition -> method available from Android 2.0 and beyond  
    		        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);

    		        //WishItemDetail.this.overridePendingTransition(0,0);
    		        
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                	viewFlipper.setInAnimation(slideRightIn);
//                    viewFlipper.setOutAnimation(slideRightOut);
//                	viewFlipper.showPrevious();
//                	Toast.makeText(WishItemDetail.this, "swipe to left", 
//                			Toast.LENGTH_SHORT).show();
                	
                	long[] prev_p_i = new long[2];
                	prev_p_i = getPrevDBItemID();
    		        Intent i = new Intent(WishItemDetail.this, WishItemDetail.class);
    		        i.putExtra("position", (int) prev_p_i[0]);
    		        i.putExtra("item_id", prev_p_i[1]);
    		        
    		        startActivity(i);
    		        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    		        //WishItemDetail.this.overridePendingTransition(0,0);
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.add(0, MENU_RADAR, 0, R.string.menu_radar)
//                .setIcon(R.drawable.ic_menu_radar)
//                .setAlphabeticShortcut('R');
//        menu.add(0, MENU_MAP, 0, R.string.menu_map)
//                .setIcon(R.drawable.ic_menu_map)
//                .setAlphabeticShortcut('M');
//        menu.add(0, MENU_AUTHOR, 0, R.string.menu_author)
//                .setIcon(R.drawable.ic_menu_author)
//                .setAlphabeticShortcut('A');
//        menu.add(0, MENU_VIEW, 0, R.string.menu_view)
//                .setIcon(android.R.drawable.ic_menu_view)
//                .setAlphabeticShortcut('V');
//        return true;
//    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case MENU_RADAR: {
//            // Launch the radar activity (if it is installed)
//            Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
//            GeoPoint location = mItem.getLocation();
//            i.putExtra("latitude", (float)(location.getLatitudeE6() / 1000000f));
//            i.putExtra("longitude", (float)(location.getLongitudeE6() / 1000000f));
//            try {
//                startActivity(i);
//            } catch (ActivityNotFoundException ex) {
//                showDialog(DIALOG_NO_RADAR);
//            }
//            return true;
//        }
//        case MENU_MAP: {
//            // Display our custom map 
//            Intent i = new Intent(this, ViewMap.class);
//            i.putExtra(ImageManager.PANORAMIO_ITEM_EXTRA, mItem);
//            i.putExtra(ImageManager.ZOOM_EXTRA, mMapZoom);
//            i.putExtra(ImageManager.LATITUDE_E6_EXTRA, mMapLatitudeE6);
//            i.putExtra(ImageManager.LONGITUDE_E6_EXTRA, mMapLongitudeE6);
//            
//            startActivity(i);
//
//            return true;
//        }
//        case MENU_AUTHOR: {
//            // Display the author info page in the browser
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(mItem.getOwnerUrl()));
//            startActivity(i);
//            return true;
//        }
//        case MENU_VIEW: {
//            // Display the photo info page in the browser
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(mItem.getPhotoUrl()));
//            startActivity(i);
//            return true;
//        }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//        case DIALOG_NO_RADAR:
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            return builder.setTitle(R.string.no_radar_title)
//                .setMessage(R.string.no_radar)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton(android.R.string.ok, null).create();
//        }
//        return null;
//    }


//    /**
//     * Utility to load a larger version of the image in a separate thread.
//     *
//     */
//    private class LoadThread extends Thread {
//
//        public LoadThread() {
//        }
//
//        @Override
//        public void run() {
//            try {
//                String uri = mItem.getThumbUrl();
//                uri = uri.replace("thumbnail", "medium");
//                final Bitmap b = BitmapUtils.loadBitmap(uri);
//                mHandler.post(new Runnable() {
//                    public void run() {
//
//                        imgPhoto.setImageBitmap(b);
//                        mTitle.setText(mItem.getTitle());
//                        mOwner.setText(mItem.getOwner());
//                        mContent.setVisibility(View.VISIBLE);
//                        getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
//                                Window.PROGRESS_VISIBILITY_OFF);
//                    }
//                });
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//        }
//    }

}