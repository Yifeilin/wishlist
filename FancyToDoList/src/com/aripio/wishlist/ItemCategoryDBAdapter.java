package com.aripio.wishlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemCategoryDBAdapter {
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "category_name";

	public static final String DB_TABLE = "ItemCategory";

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
	public ItemCategoryDBAdapter(Context ctx) {
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
	public ItemCategoryDBAdapter open() throws SQLException {
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
	public long createItemCategory(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		return this.mDb.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Delete the car with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteItemCategory(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all cars in the database
	 * 
	 * @return Cursor over all cars
	 */
	public Cursor getAllItemCategory() {

		return this.mDb.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME },
				null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the car that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching car, if found
	 * @throws SQLException
	 *             if car could not be found/retrieved
	 */
	public Cursor getItemCategory(long rowId) throws SQLException {

		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NAME },
				KEY_ID + "=" + rowId, null, null, null, null, null);
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
	public boolean updateItemCategory(long rowId, String name, String model,
			String year) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);

		return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}

}
