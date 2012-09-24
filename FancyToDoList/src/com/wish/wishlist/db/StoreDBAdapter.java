package com.wish.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * StoreDBAdapter provides access to opexarations on data in store table
 */
public class StoreDBAdapter {
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "store_name";
	public static final String KEY_LOCATION_ID = "location_id";

	public static final String DB_TABLE = "store";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;
	private static final String TAG="StoreDBAdapter";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DBAdapter.DB_NAME, null, DBAdapter.DB_VERSION);
			Log.d(TAG, "DBAdapter.DB_VERSION" + String.valueOf(DBAdapter.DB_VERSION));
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
	public StoreDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the store database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public StoreDBAdapter open() throws SQLException {
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
	public StoreDBAdapter open(SQLiteDatabase db) throws SQLException {
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
	 * Add a new store. If the store is successfully created return the new rowId
	 * for that store, otherwise return a -1 to indicate failure.
	 * 
	 * @param name
	 * @return rowId or -1 if failed
	 */
	public long addStore(String name, long locationID) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LOCATION_ID, locationID);		
		initialValues.put(KEY_NAME, name);
		return this.mDb.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Delete the store with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteStore(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all stores in the database
	 * 
	 * @return Cursor over all stores
	 */
	public Cursor getAllStores() {

		return this.mDb.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_LOCATION_ID},
				null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the store that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching store, if found
	 * @throws SQLException
	 *             if store could not be found/retrieved
	 */
	public Cursor getStore(long _id) throws SQLException {

		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_LOCATION_ID},
				KEY_ID + "=" + _id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Return a store name that matches the given Id
	 * 
	 * @param rowId
	 * @return a String of store name positioned to matching store id, if found; otherwise, return null
	 */
	public String getStoreName(long _id){
		String storeName = null;
		Cursor mCursor =

			this.mDb.query(true, DB_TABLE, new String[] { KEY_NAME },
					KEY_ID + "=" + _id, null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
				
				storeName = mCursor.getString(mCursor.
						getColumnIndexOrThrow(StoreDBAdapter.KEY_NAME));
				
			}
		return storeName;
	}

	/**
	 * Update the store.
	 * 
	 * @param rowId
	 * @param name
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateStore(long rowId, String name, long locationID) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_LOCATION_ID, locationID);

		return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}

}
