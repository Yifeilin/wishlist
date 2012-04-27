package com.aripio.wishlist.db;

//import com.aripio.wishlist.WishListDataBase.ItemsCursor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.util.Log;

public class ItemDBAdapter {
	public static final String KEY_ID = "_id";
	public static final String KEY_STORE_ID = "store_id";
	//public static final String KEY_LOCATION_ID = "location_id";
	public static final String KEY_NAME = "item_name";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE_TIME = "date_time";
	public static final String KEY_STORENAME = "store_name";
	public static final String KEY_PHOTO_URL = "picture";
	public static final String KEY_FULLSIZE_PHOTO_PATH = "fullsize_picture";
	public static final String KEY_PRICE = "price";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_PRIORITY = "priority";

	public static final String DB_TABLE = "Item";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DBAdapter.DB_NAME, null, DBAdapter.DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public ItemDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the wishlist database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public ItemDBAdapter open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Open the wishlist database by passing the instance of the db.
	 * its difference from open() is that it uses the db passed in as mDb
	 * instead of getting mDb from calling this.mDbHelper.getWritableDatabase();
	 * open(SQLiteDatabase db) is only called in DBAdapter.DatabaseHelper.onCreate() for 
	 * inserting items into the item table the first time wishlist database is
	 * created
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 *         
	 */
	public ItemDBAdapter open(SQLiteDatabase db) throws SQLException {
		//this.mDbHelper = new DatabaseHelper(this.mCtx);
		//this.mDbHelper.getWritableDatabase();
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = db;
		return this;
	}

	/**
	 * close return type: void
	 */
	public void close() {
		this.mDbHelper.close();
	}

	/**
	 * Create a new item. If the item is successfully created return the new
	 * rowId for that item, otherwise return a -1 to indicate failure.
	 * 
	 * @param name
	 * @return rowId or -1 if failed
	 */
	public long createItem(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		return this.mDb.insert(DB_TABLE, null, initialValues);
	}

	// /**
	// * Delete the car with the given rowId
	// *
	// * @param rowId
	// * @return true if deleted, false otherwise
	// */
	// public boolean deleteItem(long rowId) {
	//
	//	        return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	// }

	// /**
	// * Return a Cursor over the list of all items in the database
	// *
	// * @return Cursor over all cars
	// */
	// public Cursor getAllItems() {
	//
	// return this.mDb.query(DB_TABLE, new String[] { KEY_ID,
	// KEY_NAME}, null, null, null, null, null);
	// }

	// /**
	// * Return a Cursor positioned at the car that matches the given rowId
	// * @param rowId
	// * @return Cursor positioned to matching car, if found
	// * @throws SQLException if car could not be found/retrieved
	// */
	// public Cursor getItem(long rowId) throws SQLException {
	//
	// Cursor mCursor =
	//
	// this.mDb.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NAME
	// }, KEY_ID + "=" + rowId, null, null, null, null, null);
	// if (mCursor != null) {
	// mCursor.moveToFirst();
	// }
	// return mCursor;
	// }

	// /**
	// * Update the item.
	// *
	// * @param rowId
	// * @param name
	// * @return true if the note was successfully updated, false otherwise
	// */
	// public boolean updateItem(long rowId, String name, String model,
	// String year){
	// ContentValues args = new ContentValues();
	// args.put(KEY_NAME, name);
	//
	// return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) >0;
	// }

	/**
	 * Add a new item to the database. The item will have a status of open.
	 * 
	 * @param item_id
	 *            unique id of the item
	 * @param name
	 *            The item name
	 * @param description
	 *            The name description
	 */
	public void addItem(long store_id, String name, String description, String date_time,
			String picture_uri, String fullsize_picture_path, float price, String location,
			int priority) {
		// String sql = String.format(
		// "INSERT INTO ITEM (_id, name, description, create_date, store_id,  picture, price, location, priority) "
		// +
		// "VALUES ( NULL, '%s', '%s', '%s', '%d', '%s', '%f', '%s', '%d')",
		// name, description, date, store_id, picture_uri, price, location,
		// priority);
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_STORE_ID, store_id);
		//initialValues.put(KEY_LOCATION_ID, locationID);	
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_DATE_TIME, date_time);
		// initialValues.put(KEY_STORENAME, name);
		initialValues.put(KEY_PHOTO_URL, picture_uri);
		initialValues.put(KEY_FULLSIZE_PHOTO_PATH, fullsize_picture_path);
		initialValues.put(KEY_PRICE, price);
		initialValues.put(KEY_LOCATION, location);
		initialValues.put(KEY_PRIORITY, priority);

		this.mDb.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Update a item in the database.
	 * 
	 * @param _id
	 *            The id of the existing item
	 * @param name
	 *            The item name
	 * @param description
	 *            The item description
	 */
	public void updateItem(long _id, long store_id, String name, String description, String date_time,
			String picture_uri, String fullsize_picture_path, float price, String location,
			int priority) {

//		String sql = String.format("UPDATE Item " + "SET item_name = '%s',  "
//				+ " description = '%s', " + " date_time = '%s', "
//				+ " store_id = '%d' " + "WHERE _id = '%d' ", name, description,
//				date, store_id, _id);
//		try {
//			this.mDbHelper.getWritableDatabase().execSQL(sql);
//		} catch (SQLException e) {
//			Log.e("Error writing an exsiting item", e.toString());
//		}
		
		ContentValues initialValues = new ContentValues();

		//initialValues.put(KEY_ID, _id);
		initialValues.put(KEY_STORE_ID, store_id);
		//initialValues.put(KEY_LOCATION_ID, locationID);	
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_DATE_TIME, date_time);
		// initialValues.put(KEY_STORENAME, name);
		initialValues.put(KEY_PHOTO_URL, picture_uri);
		initialValues.put(KEY_FULLSIZE_PHOTO_PATH, fullsize_picture_path);
		initialValues.put(KEY_PRICE, price);
		initialValues.put(KEY_LOCATION, location);
		initialValues.put(KEY_PRIORITY, priority);
		
		String where = String.format("_id = '%d'", _id);
		this.mDb.update(DB_TABLE, initialValues, where, null);

	}

	/**
	 * Delete a item from the database.
	 * 
	 * @param _id
	 *            The id of the item to delete
	 */
	public void deleteItem(long _id) {
		//delete from item table
		String sql = String.format("DELETE FROM Item " + "WHERE _id = '%d' ",
				_id);
		try {
			this.mDbHelper.getWritableDatabase().execSQL(sql);
		} catch (SQLException e) {
			Log.e("Error deleteing item", e.toString());
		}
		
		//delete from location table
		
		
		
		//delete from store table
	}

	/** Returns the number of Items */
	public int getItemsCount() {

		Cursor c = null;
		try {
			c = this.mDbHelper.getReadableDatabase().rawQuery(
					"SELECT count(*) FROM Item", null);
			if (0 >= c.getCount()) {
				return 0;
			}
			c.moveToFirst();
			return c.getInt(0);
		} finally {
			if (null != c) {
				try {
					c.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public static class ItemsCursor extends SQLiteCursor {
		public static enum SortBy {
			item_name, date_time, price, priority, _id,

		}

		private static final String QUERY = "SELECT _id, item_name, description, date_time, store_id, picture, fullsize_picture, price, location, priority "
				+ "FROM Item " + "ORDER BY ";
		
//		private static final String QUERY_NAME = "SELECT _id, item_name, description, date_time, store_id, picture, price, location, priority "
//			+ "FROM Item " + "WHERE item_name LIKE Book " + "ORDER BY ";

		private ItemsCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
				String editTable, SQLiteQuery query) {
			super(db, driver, editTable, query);
		}

		private static class Factory implements SQLiteDatabase.CursorFactory {
			@Override
			public Cursor newCursor(SQLiteDatabase db,
					SQLiteCursorDriver driver, String editTable,
					SQLiteQuery query) {
				return new ItemsCursor(db, driver, editTable, query);
			}
		}

		public long getColItemsId() {
			return getLong(getColumnIndexOrThrow("_id"));
		}

		public String getColName() {
			return getString(getColumnIndexOrThrow("item_name"));
		}

		public int getColStoreId() {
			return Integer
					.parseInt(getString(getColumnIndexOrThrow("store_id")));
		}

		public String getColPicture() {
			return getString(getColumnIndexOrThrow("picture"));
		}
		
		public String getColFullsizePicture() {
			return getString(getColumnIndexOrThrow("fullsize_picture"));
		}

		public String getColDescription() {
			return getString(getColumnIndexOrThrow("description"));
		}

		public String getColCreateDate() {
			return getString(getColumnIndexOrThrow("date_time"));
		}

		public String getColPrice() {
			return getString(getColumnIndexOrThrow("price"));
		}

		public String getColLocation() {
			return getString(getColumnIndexOrThrow("location"));
		}

		public String getColPriority() {
			return getString(getColumnIndexOrThrow("priority"));
		}
	}

	/**
	 * Return a sorted ItemsCursor
	 * 
	 * @param sortBy
	 *            the sort criteria
	 */
	public ItemsCursor getItems(ItemsCursor.SortBy sortBy) {
		String sql = ItemsCursor.QUERY + sortBy.toString();
		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);
		c.moveToFirst();
		return c;
	}
	
//	/**
//	 * Return a sorted ItemsCursor matching the search quest by name
//	 * 
//	 * @param sortBy
//	 *            the sort criteria
//	 */
//	public ItemsCursor getItemsByName(String name, ItemsCursor.SortBy sortBy) {
//		String sql = ItemsCursor.QUERY_NAME + sortBy.toString();
//		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
//		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
//				new ItemsCursor.Factory(), sql, null, null);
//		c.moveToFirst();
//		return c;
//	}

	/**
	 * Return the cursor of item with id equal to _id
	 * 
	 * @param _id
	 * @return
	 */
	public ItemsCursor getItem(long _id) {
		String sql = String.format("SELECT * FROM Item " + "WHERE _id = '%d' ",
				_id);
		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	/**
	 * Return a sorted ItemsCursor matching the search quest by name
	 * 
	 */
	public ItemsCursor searchItems(String query) {
		String sql = String.format("SELECT * FROM Item "
				+ "WHERE item_name LIKE '%%%s%%' ", query);

		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	/**
	 * Return a sorted ItemsCursor matching the search quest by name
	 * ordered by sortBy
	 * 
	 */
	public ItemsCursor searchItems(String query, ItemsCursor.SortBy sortBy) {
		String sql = String.format("SELECT * FROM Item "
				+ "WHERE item_name LIKE '%%%s%%' " + "ORDER BY '%s'" , query, sortBy);
		
		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	/**
	 * get the latitude and longitude according to item id
	 * @param _id the _id of the item in table Item
	 * @return double[2] with [0] the latitude, [1] the longitude
	 */
	
	public double[] getItemLocation(long _id){
		double[] location = new double[2];
		// get the latitude and longitude from table location
		Cursor locationC = getItemLocationCursor(_id);
		if(locationC != null){
			double latitude = locationC.getDouble(locationC.
					getColumnIndexOrThrow(LocationDBAdapter.KEY_LATITUDE));
			
			double longitude = locationC.getDouble(locationC.
					getColumnIndexOrThrow(LocationDBAdapter.KEY_LONGITUDE));
			
			//storeDBA.close();
			//locationDBA.close();
			
			location[0] = latitude;
			location[1] = longitude;
		}
		
		return location;
	}
	
	public ArrayList<double[]> getAllItemLocation(){
		String sql = String.format("SELECT _id FROM Item");
		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);

		long id;
		ArrayList<double[]> locationList = new ArrayList<double[]>();
		if (c != null) {
			c.moveToFirst();
			while(!c.isAfterLast()){
				id = c.getLong(c.getColumnIndexOrThrow(KEY_ID));
				//skip the items having unknown locations
				if(!getItemAddress(id).equals("unknown")){
					double[] location = getItemLocation(id);
					locationList.add(location);
				}
				c.moveToNext();
			}
		}
		return locationList;
	}
	
	
	/**
	 * get the address string according to item id
	 * @param _id the _id of the item in table Item
	 * @return the address string of the item
	 */
	
	public String getItemAddress(long _id){
		String AddStr = null;
		Cursor locationC = getItemLocationCursor(_id);
		if(locationC != null){
			AddStr = locationC.getString(locationC.
					getColumnIndexOrThrow(LocationDBAdapter.KEY_ADDSTR));
		}
	
		return AddStr;
	}
	
	/**
	 * get the Cursor of table store according to item id
	 * @param long _id: the _id of the item in table Item
	 * @return the Cursor of store where the Item belongs to
	 */
	
	public Cursor getItemStoreCursor(long _id){
		String sql = String.format("SELECT store_id FROM Item " + "WHERE _id = '%d' ",
				_id);
		SQLiteDatabase d = this.mDbHelper.getReadableDatabase();
		ItemsCursor itemC = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);

		Cursor storeC = null;
		if (itemC != null) {
			//get the store id
			itemC.moveToFirst();
			long storeID = itemC.getLong(itemC
					.getColumnIndexOrThrow(ItemDBAdapter.KEY_STORE_ID));
			
			//open the store table
			StoreDBAdapter storeDBA;
			storeDBA = new StoreDBAdapter(mCtx);
			storeDBA.open();
			
			// get store cursor
			storeC = storeDBA.getStore(storeID);
						
			//close store table
			storeDBA.close();
			
		}
		
		return storeC;
	}
	
	/**
	 * get the Cursor of table location according to item id
	 * @param long _id: the _id of the item in table Item
	 * @return the Cursor of location where the Item is located
	 */
	
	public Cursor getItemLocationCursor(long _id){
		Cursor storeC = getItemStoreCursor(_id);
		Cursor locationC = null;
		if (storeC != null) {
			//open the location table
			LocationDBAdapter locationDBA;
			locationDBA = new LocationDBAdapter(mCtx);
			locationDBA.open();
			
			//get the location id
			long locationID = storeC.getLong(storeC
					.getColumnIndexOrThrow(StoreDBAdapter.KEY_LOCATION_ID));
			
			// get the latitude and longitude from table location
			locationC = locationDBA.getLocation(locationID);
			
			//close location table
			locationDBA.close();
			
		}
		
		return locationC;
	}

}
