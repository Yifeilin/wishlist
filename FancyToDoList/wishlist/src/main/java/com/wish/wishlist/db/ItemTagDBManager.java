package com.wish.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
 * TagDBManager provides access to operations on data in ItemCategory table
 */
public class ItemTagDBManager extends DBManager {
	public static final String KEY_ID = "_id";
	public static final String ITEM_ID = "item_id";
    public static final String TAG_ID = "tag_id";

	public static final String DB_TABLE = "ItemTag";
	private static final String TAG="ItemTagDBManager";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DBAdapter.DB_NAME, null, DBAdapter.DB_VERSION);
			//I have to have the follwoing code, otherwise, the DBAdapter.DB_VERSION
			//is somehow not passed to the super and the db version will be incorrect
			//this will trigger a onDowngrade() and cause a crash. I don't know why
			//Is it an android bug or is it because I don't understand java?
			//this seems to only happne on > anroid 4.03
			//the same applies to other DBAdapter
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
	public ItemTagDBManager(Context ctx) {
        super(ctx);
	}
}
