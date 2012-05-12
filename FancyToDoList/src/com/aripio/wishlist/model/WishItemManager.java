package com.aripio.wishlist.model;

import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.aripio.wishlist.db.ItemDBAdapter;
import com.aripio.wishlist.db.LocationDBAdapter;
import com.aripio.wishlist.db.StoreDBAdapter;
import com.aripio.wishlist.db.ItemDBAdapter.ItemsCursor;

public class WishItemManager {
	static private Context _ctx;
	private static WishItemManager instance = null;

	public static WishItemManager getInstance(Context ctx) {
		if (instance == null){
			instance = new WishItemManager(ctx);
		}

		return instance;
	}
	
	private WishItemManager(Context ctx) {
		_ctx = ctx;
	}
	
	public List<WishItem> retrieve(long itemId) {
		return null;
	}
	
	public WishItem retrieveItembyId(long itemId) {
	
		ItemDBAdapter mItemDBAdapter;
		StoreDBAdapter mStoreDBAdapter;
		LocationDBAdapter mLocationDBAdapter;
		
		mItemDBAdapter = new ItemDBAdapter(_ctx);
		mItemDBAdapter.open();

		// Open the Store table in the database
		mStoreDBAdapter = new StoreDBAdapter(_ctx);
		mStoreDBAdapter.open();

		// Open the Location table in the database
		mLocationDBAdapter = new LocationDBAdapter(_ctx);
		mLocationDBAdapter.open();
		
		ItemsCursor wishItemCursor;		
		Cursor mStoreCursor;
		
		wishItemCursor = mItemDBAdapter.getItem(itemId);
		long storeID = wishItemCursor.getLong(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_STORE_ID));
		
		mStoreCursor = mStoreDBAdapter.getStore(storeID);
		//String storeName = mStoreDBAdapter.getStoreName(storeID);
		
		long locationID = mStoreCursor.getLong(mStoreCursor
				.getColumnIndexOrThrow(StoreDBAdapter.KEY_LOCATION_ID));
		String itemLocation = mLocationDBAdapter.getAddress(locationID);
		
		String storeName = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_STORENAME));		
		
		String picture_str = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PHOTO_URL));
		
		String fullsize_pic_path = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_FULLSIZE_PHOTO_PATH));
		 
		String itemName = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_NAME));

		String itemDesc = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DESCRIPTION));

		String date = wishItemCursor.getString(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DATE_TIME));

		float itemPrice = wishItemCursor.getFloat(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PRICE));

		int itemPriority = wishItemCursor.getInt(wishItemCursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PRIORITY));
		
		WishItem item = new WishItem(_ctx, itemId, storeID, storeName, itemName, itemDesc, 
				date, picture_str, fullsize_pic_path, itemPrice,
				itemLocation, itemPriority);
		
		mItemDBAdapter.close();
		mStoreDBAdapter.close();
		mLocationDBAdapter.close();

		return item;

	}

}
