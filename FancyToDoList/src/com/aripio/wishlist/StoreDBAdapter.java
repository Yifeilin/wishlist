package com.aripio.wishlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * StoreDBAdapter provides access to operations on data in store table
 */
public class StoreDBAdapter {
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "store_name";
	public static final String KEY_LOCATION_ID = "location_id";

	public static final String DB_TABLE = "store";

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
	public Cursor getStore(long rowId) throws SQLException {

		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_LOCATION_ID},
				KEY_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
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
