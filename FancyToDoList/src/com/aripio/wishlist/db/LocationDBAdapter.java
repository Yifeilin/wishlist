package com.aripio.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * LocationDBAdapter provides access to operations on data in location table
 */
public class LocationDBAdapter {

	public static final String KEY_ID = "_id";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE= "longitude";
	public static final String KEY_ADDSTR = "addStr";
//	public static final String KEY_ADDLINE2 = "AddLine2";
//	public static final String KEY_ADDLINE3 = "AddLine3";
	public static final String KEY_STREET_NO = "street_no";
	public static final String KEY_STREET = "street";
	public static final String KEY_CITY = "city";
	public static final String KEY_STATE = "state";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_POSTCODE = "postcode";

	public static final String DB_TABLE = "location";

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
	public LocationDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the location database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public LocationDBAdapter open() throws SQLException {
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
	public LocationDBAdapter open(SQLiteDatabase db) throws SQLException {
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
	 * Add a new location. If the location is successfully created return the new rowId
	 * for that location, otherwise return a -1 to indicate failure.
	 * 
	 * @param name
	 * @return rowId or -1 if failed
	 */
	public long addLocation(double lat, double lng, String addStr, int streetNO, String street, String city,
			String state, String country, String postcode) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LATITUDE, lat);
		initialValues.put(KEY_LONGITUDE, lng);
		initialValues.put(KEY_ADDSTR, addStr);
//		initialValues.put(KEY_ADDLINE2, addLine2);
//		initialValues.put(KEY_ADDLINE3, addLine3);
		initialValues.put(KEY_STREET_NO, streetNO);
		initialValues.put(KEY_STREET, street);
		initialValues.put(KEY_CITY, city);
		initialValues.put(KEY_STATE, state);
		initialValues.put(KEY_COUNTRY, country);
		initialValues.put(KEY_POSTCODE, postcode);

		return this.mDb.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Delete the location with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteLocation(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all location in the database
	 * 
	 * @return Cursor over all location
	 */
	public Cursor getAllLocation() {

		return this.mDb.query(DB_TABLE, null, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the location that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching location, if found
	 * @throws SQLException
	 *             if location could not be found/retrieved
	 */
	public Cursor getLocation(long rowId) throws SQLException {

		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, null, KEY_ID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Return the address positioned at the location that matches the given rowId
	 * 
	 * @param rowId
	 * @return String of address matching location id, if found; otherwise, return null
	 */
	public String getAddress(long _id){

		String addressStr = null;
		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, new String[] { KEY_ADDSTR }, KEY_ID + "=" + _id, null, null,
				null, null, null);
		if (mCursor != null) {
			
			mCursor.moveToFirst();
			addressStr =  mCursor.getString(mCursor.
					getColumnIndexOrThrow(LocationDBAdapter.KEY_ADDSTR));
			
		}
		return addressStr;
	}

	/**
	 * Update the location.
	 * 
	 * @param rowId
	 * @param name
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateLocation(long rowId, double lat, double lng, int streetNO, String street,
			String city, String state, String country, String postcode) {
		ContentValues args = new ContentValues();
		args.put(KEY_LATITUDE, lat);
		args.put(KEY_LONGITUDE, lng);
		args.put(KEY_STREET_NO, streetNO);
		args.put(KEY_STREET, street);
		args.put(KEY_CITY, city);
		args.put(KEY_STATE, state);
		args.put(KEY_COUNTRY, country);
		args.put(KEY_POSTCODE, postcode);

		return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}

}
