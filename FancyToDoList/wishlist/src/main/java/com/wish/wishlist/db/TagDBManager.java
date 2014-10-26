package com.wish.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * TagDBManager provides access to operations on data in ItemCategory table
 */
public class TagDBManager extends DBManager {
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";

	public static final String DB_TABLE = "Tag";
	private static final String TAG="TagDBManager";

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TagDBManager(Context ctx) {
        super(ctx);
	}

	/**
	 * Create a new itemCategory. If the car is successfully created return the new rowId
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
	 * Delete the itemCategory with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteItemCategory(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all itemCategories in the database
	 * 
	 * @return Cursor over all cars
	 */
	public Cursor getAllItemCategory() {

		return this.mDb.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME },
				null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the itemCategory that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching itemCategory, if found
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
	 * Update the itemCategory.
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
