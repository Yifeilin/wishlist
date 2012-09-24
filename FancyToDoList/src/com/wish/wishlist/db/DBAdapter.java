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
				//String sql = "DELETE FROM Item " + "WHERE _id = '%d' ");
				String sql = "DELETE FROM "
				+ ItemDBAdapter.DB_TABLE
				+ " WHERE "
				+ ItemDBAdapter.KEY_PHOTO_URL
				+ " LIKE '%sample'";
				
				Log.d(TAG, "sql:" + sql);
				db.execSQL(sql);
				//add user table
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

	private static Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
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
//			ItemDBAdapter mItemDBAdapter = new ItemDBAdapter(context);
//			mItemDBAdapter.open(db);
//			String picUrl;
//			picUrl = Integer.toHexString(R.drawable.new_ipad) + "sample";
//			mItemDBAdapter.addItem(	1,
//									"Apple Store",
//									"ipad",
//									"It is the new ipad with retina display",
//									"2012-03-11 11:30:00",
//									picUrl,
//									" ",
//									529f,
//									"220 Yonge Street, Toronto, ON, M5B 2H1",
//									0);
//			
//			picUrl = Integer.toHexString(R.drawable.cake) + "sample";
//			mItemDBAdapter.addItem(	2, 
//									"dessert store",
//									"chocolate cake", 
//									"It looks delicisous", 
//									"2012-03-17 18:22:35", 
//									picUrl,
//									" ",
//									2.99f,
//									"2243 Bloor ST W\nToronto, ON M6S 1N7\nCanada",
//									3);
//			picUrl = Integer.toHexString(R.drawable.tiffany) + "sample";
//			mItemDBAdapter.addItem(	3,
//									"tiffany",
//									"tiffany necklace", 
//									"beautiful", 
//									"2012-06-03 03:40:50", 
//									picUrl,
//									" ",
//									389f,
//									"85 Bloor Street West, Toronto, Ontario\nM5S 1M1 Canada",
//									2);
//			picUrl = Integer.toHexString(R.drawable.d3) + "sample";
//			mItemDBAdapter.addItem(	4, 
//									"Best buy",
//									"diablo 3",
//									"waiting for this game for years", 
//									"2012-05-15 08:17:38", 
//									picUrl,
//									" ",
//									59.0f,
//									"65 Dundas Street West\nToronto, ON, M5G 2C3",
//									1);
//			
//			picUrl = Integer.toHexString(R.drawable.mini_cooper) + "sample";
//			mItemDBAdapter.addItem(	5, 
//									"BMW store",
//									"mini cooper",
//									"i like its color", 
//									"2012-06-20 13:05:22", 
//									picUrl,
//									" ",
//									20000.0f,
//									"11 Sunlight Park Rd\nToronto, ON, M4M 1B5",
//									1);
//			
//			picUrl = Integer.toHexString(R.drawable.sjobs_bio) + "sample";
//			mItemDBAdapter.addItem(	6, 
//									"Indigo",
//									"steve jobs biograhpy",
//									"a must-read book", 
//									"2012-06-22 19:08:20", 
//									picUrl,
//									" ",
//									30.0f,
//									"259 Richmond Street West Toronto ON M5V 3M6",
//									1);
//
//			mItemDBAdapter.close();

			// create table "itemCategory"
			db.execSQL(CREATE_TABLE_ITEMCATEGORY);
			// to be added
			
			//create table "store" and insert 4 default stores
			db.execSQL(CREATE_TABLE_STORE);
//			StoreDBAdapter mStoreDBAdapter = new StoreDBAdapter(context);
//			mStoreDBAdapter.open(db);
//			mStoreDBAdapter.addStore("Apple Store", 		1);
//			mStoreDBAdapter.addStore("dessert store",   		2);
//			mStoreDBAdapter.addStore("tiffany",	3);
//			mStoreDBAdapter.addStore("Best buy",		4);
//			mStoreDBAdapter.addStore("BMW store",		5);
//			mStoreDBAdapter.addStore("Indigo",		6);
//			mStoreDBAdapter.close();
			
			//create table "location" and insert 4 default locations
			db.execSQL(CREATE_TABLE_LOCATION);
//			LocationDBAdapter mLocationDBAdapter = new LocationDBAdapter(context);
//			mLocationDBAdapter.open(db);
//			mLocationDBAdapter.addLocation(43.653929,
//					 						-79.3802132, 
//										   "220 Yonge Street, Toronto, ON, M5B 2H1",
//										   0, null, null, null, null, null);
//			mLocationDBAdapter.addLocation(43.6509499,
//											-79.477205, 
//										   "2243 Bloor ST W\nToronto, ON M6S 1N7\nCanada",
//										   0, null, null, null, null, null);
//			mLocationDBAdapter.addLocation(43.6694098,
//					 						-79.3904, 
//										   "85 Bloor Street West, Toronto, Ontario\nM5S 1M1 Canadas",
//										   0, null, null, null, null, null);
//			mLocationDBAdapter.addLocation(43.6555876,
//											-79.3835228, 
//										   "65 Dundas Street West\nToronto, ON, M5G 2C3",
//										   0, null, null, null, null, null);
//			
//			mLocationDBAdapter.addLocation(43.6561902,
//					 						-79.3489359, 
//										   "11 Sunlight Park Rd\nToronto, ON, M4M 1B5",
//										   0, null, null, null, null, null);
//			
//			mLocationDBAdapter.addLocation(43.6489324,
//					 						-79.3913844, 
//										   "259 Richmond Street West Toronto ON M5V 3M6 ",
//										   0, null, null, null, null, null);
//
//			mLocationDBAdapter.close();
		
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
