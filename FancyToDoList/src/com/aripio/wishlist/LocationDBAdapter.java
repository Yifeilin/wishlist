package com.aripio.wishlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDBAdapter {

	public static final String KEY_ID = "_id";
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
	 * Open the cars database. If it cannot be opened, try to create a new
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
	 * close return type: void
	 */
	public void close() {
		this.mDbHelper.close();
	}

	/**
	 * Create a new car. If the car is successfully created return the new rowId
	 * for that car, otherwise return a -1 to indicate failure.
	 * 
	 * @param name
	 * @return rowId or -1 if failed
	 */
	public long createLocation(int streetNO, String street, String city,
			String state, String country, String postcode) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_STREET_NO, streetNO);
		initialValues.put(KEY_STREET, street);
		initialValues.put(KEY_CITY, city);
		initialValues.put(KEY_STATE, state);
		initialValues.put(KEY_COUNTRY, country);
		initialValues.put(KEY_POSTCODE, postcode);

		return this.mDb.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Delete the car with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteLocation(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all cars in the database
	 * 
	 * @return Cursor over all cars
	 */
	public Cursor getAllLocation() {

		return this.mDb.query(DB_TABLE, null, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the car that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching car, if found
	 * @throws SQLException
	 *             if car could not be found/retrieved
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
	 * Update the car.
	 * 
	 * @param rowId
	 * @param name
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateLocation(long rowId, int streetNO, String street,
			String city, String state, String country, String postcode) {
		ContentValues args = new ContentValues();
		args.put(KEY_STREET_NO, streetNO);
		args.put(KEY_STREET, street);
		args.put(KEY_CITY, city);
		args.put(KEY_STATE, state);
		args.put(KEY_COUNTRY, country);
		args.put(KEY_POSTCODE, postcode);

		return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}

}
