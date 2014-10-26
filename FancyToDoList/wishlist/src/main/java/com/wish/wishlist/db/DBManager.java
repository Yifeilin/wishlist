package com.wish.wishlist.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
 * DBManager is the base class of various subclasses to access data in db table
 */
public class DBManager {
	//public static final String DB_TABLE = "user";
	protected DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;

	protected final Context mCtx;
    private static final String TAG="DBManager";

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
	public DBManager(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the store database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 *
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws android.database.SQLException
	 *             if the database could be neither opened or created
	 */
	public DBManager open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Open the WishList database by passing the instance of the db.
	 * its difference from open() is that it uses the db passed in as mDb
	 * instead of getting mDb from calling this.mDbHelper.getWritableDatabase();
	 * open(SQLiteDatabase db) is only called in DBAdapter.DatabaseHelper.onCreate() for 
	 * inserting items into the item table the first time WishList database is
	 * created
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 *         
	 */
	public DBManager open(SQLiteDatabase db) throws SQLException {
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

    protected SQLiteDatabase readableDB() {
        return mDbHelper.getReadableDatabase();
    }

    protected SQLiteDatabase writableDB() {
        return mDbHelper.getWritableDatabase();
    }
}
