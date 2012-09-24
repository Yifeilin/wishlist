package com.wish.wishlist.db;

import com.wish.wishlist.R;

import android.content.Context;
import android.util.Log;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * The DBAdapter class only gets called when the app first starts 
 * and its responsibility is to create/upgrade the tables. 
 * All other access to the data in the database 
 * is done through the individual "adapter" class.
 */
public class DBAdapter {
	
	private static DBAdapter instance = null;
	
	public static DBAdapter getInstance(Context contenxt) {
		if (instance == null) {
			instance = new DBAdapter(contenxt);
		}
		return instance;
	}
	//Database name
	public static final String DB_NAME = "WishList";

	//Database version
	public static final int DB_VERSION = 2;
	private static final String TAG="DBAdapter";

	public static final Patch[] PATCHES = new Patch[] {
		new Patch() {//db version 1 already done in onCreate
			public void apply(SQLiteDatabase db) {
				//db.execSQL("create table ...");
			}
			public void revert(SQLiteDatabase db) {
				//db.execSQL("drop table ...");
			}
		}
		, new Patch() {//db version 2
			public void apply(SQLiteDatabase db) {
				//delete sample items
				String sql = "DELETE FROM "
				+ ItemDBAdapter.DB_TABLE
				+ " WHERE "
				+ ItemDBAdapter.KEY_PHOTO_URL
				+ " LIKE '%sample'";
				
				//Log.d(TAG, "sql:" + sql);
				db.execSQL(sql);

				//add user table
				db.execSQL(CREATE_TABLE_USER);
			}
			public void revert(SQLiteDatabase db) {  }
		}
	};
	
	//Query string to create table "Item"
	private static final String CREATE_TABLE_ITEM = "create table "
			+ ItemDBAdapter.DB_TABLE 
			+ " (" 
			+ ItemDBAdapter.KEY_ID			+ " integer primary key autoincrement, " 
			+ ItemDBAdapter.KEY_STORE_ID 	+ " INTEGER, "
			+ ItemDBAdapter.KEY_STORENAME	+ " TEXT, "
			+ ItemDBAdapter.KEY_NAME 		+ " TEXT, " 
			+ ItemDBAdapter.KEY_DESCRIPTION + " TEXT, " 
			+ ItemDBAdapter.KEY_DATE_TIME 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_PHOTO_URL 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_FULLSIZE_PHOTO_PATH 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_PRICE 		+ " REAL, " 
			+ ItemDBAdapter.KEY_ADDRESS 	+ " TEXT, "
			+ ItemDBAdapter.KEY_PRIORITY 	+ " INTEGER"
			+ ");";


	//Query string to create table "ItemCategory"
	private static final String CREATE_TABLE_ITEMCATEGORY = "create table "
			+ ItemCategoryDBAdapter.DB_TABLE + " ("
			+ ItemCategoryDBAdapter.KEY_ID
			+ " integer primary key autoincrement, " 
			+ ItemCategoryDBAdapter.KEY_NAME + " TEXT"
			+ ");"; 

	//Query string to create table "store"
	private static final String CREATE_TABLE_STORE = "create table "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID				+ " integer primary key autoincrement, " 
			+ StoreDBAdapter.KEY_LOCATION_ID	+ " INTEGER, "
			+ StoreDBAdapter.KEY_NAME 			+ " TEXT" 
			+ ");";
	
	//Query string to create table "location"
	private static final String CREATE_TABLE_LOCATION = "create table "
			+ LocationDBAdapter.DB_TABLE
			+ " ("
			+ LocationDBAdapter.KEY_ID
			+ " integer primary key autoincrement, "
			+ LocationDBAdapter.KEY_LATITUDE
			+ " REAL,"
			+ LocationDBAdapter.KEY_LONGITUDE
			+ " REAL,"
			+ LocationDBAdapter.KEY_ADDSTR
			+ " TEXT,"
//			+ LocationDBAdapter.KEY_ADDLINE2
//			+ " TEXT,"
//			+ LocationDBAdapter.KEY_ADDLINE3
//			+ " TEXT,"
			+ LocationDBAdapter.KEY_STREET_NO
			+ " INTEGER," 
			+ LocationDBAdapter.KEY_STREET
			+ " TEXT," 
			+ LocationDBAdapter.KEY_CITY + " TEXT,"
			+ LocationDBAdapter.KEY_STATE + " TEXT,"
			+ LocationDBAdapter.KEY_COUNTRY + " TEXT,"
			+ LocationDBAdapter.KEY_POSTCODE + " TEXT" + ");";

	//Query string to create table "user"
	private static final String CREATE_TABLE_USER = "create table "
			+ UserDBAdapter.DB_TABLE 
			+ " (" 
			+ UserDBAdapter.KEY_ID			+ " integer primary key autoincrement, " 
			+ UserDBAdapter.USER_ID	+ " TEXT, "
			+ UserDBAdapter.USER_KEY	+ " TEXT, "
			+ UserDBAdapter.USER_DISPLAY_NAME	+ " TEXT, " 
			+ UserDBAdapter.USER_EMAIL + " TEXT" 
			+ ");";

	private static Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/** * Constructor
	 * 
	 * @param contenxt
	 */
	private DBAdapter(Context contenxt) {
		context = contenxt;
	}
	
	public void createDB() {
		this.DBHelper = new DatabaseHelper(context);
		
		//according android sdk document, 
		//we must call open() getWritableDatabase() or getReadableDatabase() to actually create the tables;
		open();
		close();
	}

	//private static class DatabaseHelper extends SQLiteOpenHelper {
	// not sure why DatabaseHelper needs to be static
	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			//super(context, DB_NAME, null, DB_VERSION);
			super(context, DB_NAME, null, PATCHES.length); //
			Log.d(TAG, "PATCHES.length" + String.valueOf(PATCHES.length));
		}

		/***onCreate is called when the database is first created
		 * (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table "item" and insert into the table
			db.execSQL(CREATE_TABLE_ITEM);
			// create table "itemCategory"
			db.execSQL(CREATE_TABLE_ITEMCATEGORY);
			//create table "store"
			db.execSQL(CREATE_TABLE_STORE);
			//create table "location"
			db.execSQL(CREATE_TABLE_LOCATION);
			//create table "user"
			db.execSQL(CREATE_TABLE_USER);
		}

		/***
		 * (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Adding any table mods to this guy here
			for (int i=oldVersion; i<newVersion; i++) {
				PATCHES[i].apply(db);
			}
		}

		//API LEVEL 11 starts to support onDowngrade
	//	@Override
	//	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	//		for (int i=oldVersion; i>newVersion; i++) {
	//			PATCHES[i-1].revert(db);
	//		}
	//	}
	}

	private static class Patch {
		public void apply(SQLiteDatabase db) {}
		public void revert(SQLiteDatabase db) {}
	}

	/**
	 * open the db
	 * 
	 * @return this
	 * @throws SQLException
	 *             return type: DBAdapter
	 */
	public DBAdapter open() throws SQLException {
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * close the db return type: void
	 */
	public void close() {
		this.DBHelper.close();
		this.db.close();
	}

}
