package com.android.wishlist.db;

import com.android.wishlist.R;

import android.content.Context;
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
	//Database name
	public static final String DB_NAME = "WishList";

	//Database version
	public static final int DB_VERSION = 1;
	
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

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * 
	 * @param contenxt
	 */
	public DBAdapter(Context contenxt) {
		this.context = contenxt;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	//private static class DatabaseHelper extends SQLiteOpenHelper {
	// not sure why DatabaseHelper needs to be static
	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/***onCreate is called when the database is first created
		 * (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table "item" and insert into the table
			// 4 default items
			db.execSQL(CREATE_TABLE_ITEM);
			ItemDBAdapter mItemDBAdapter = new ItemDBAdapter(context);
			mItemDBAdapter.open(db);
			String picUrl;
			picUrl = Integer.toHexString(R.drawable.new_ipad) + "sample";
			mItemDBAdapter.addItem(	1,
									"Apple Store",
									"ipad",
									"It is the new ipad with retina display",
									"2012-03-11 11:30:00",
									picUrl,
									" ",
									529f,
									"220 Yonge Street, Toronto, ON, M5B 2H1",
									0);
			
			picUrl = Integer.toHexString(R.drawable.cake) + "sample";
			mItemDBAdapter.addItem(	2, 
									"dessert store",
									"chocolate cake", 
									"It looks delicisous", 
									"2012-03-17 18:22:35", 
									picUrl,
									" ",
									2.99f,
									"2243 Bloor ST W\nToronto, ON M6S 1N7\nCanada",
									3);
			picUrl = Integer.toHexString(R.drawable.tiffany) + "sample";
			mItemDBAdapter.addItem(	3,
									"tiffany",
									"tiffany necklace", 
									"beautiful", 
									"2012-06-03 03:40:50", 
									picUrl,
									" ",
									389f,
									"85 Bloor Street West, Toronto, Ontario\nM5S 1M1 Canada",
									2);
			picUrl = Integer.toHexString(R.drawable.d3) + "sample";
			mItemDBAdapter.addItem(	4, 
									"Best buy",
									"diablo 3",
									"waiting for this game for years", 
									"2012-05-15 08:17:38", 
									picUrl,
									" ",
									59.0f,
									"65 Dundas Street West\nToronto, ON, M5G 2C3",
									1);
			
			picUrl = Integer.toHexString(R.drawable.mini_cooper) + "sample";
			mItemDBAdapter.addItem(	5, 
									"BMW store",
									"mini cooper",
									"i like its color", 
									"2012-06-20 13:05:22", 
									picUrl,
									" ",
									20000.0f,
									"Toronto",
									1);
			
			picUrl = Integer.toHexString(R.drawable.sjobs_bio) + "sample";
			mItemDBAdapter.addItem(	6, 
									"Indigo",
									"steve jobs biograhpy",
									"a must-read book", 
									"2012-06-22 19:08:20", 
									picUrl,
									" ",
									30.0f,
									"259 Richmond Street West Toronto ON M5V 3M6",
									1);

			mItemDBAdapter.close();

			// create table "itemCategory"
			db.execSQL(CREATE_TABLE_ITEMCATEGORY);
			// to be added
			
			//create table "store" and insert 4 default stores
			db.execSQL(CREATE_TABLE_STORE);
			StoreDBAdapter mStoreDBAdapter = new StoreDBAdapter(context);
			mStoreDBAdapter.open(db);
			mStoreDBAdapter.addStore("Apple Store", 		1);
			mStoreDBAdapter.addStore("dessert store",   		2);
			mStoreDBAdapter.addStore("tiffany",	3);
			mStoreDBAdapter.addStore("Best buy",		4);
			mStoreDBAdapter.addStore("BMW store",		5);
			mStoreDBAdapter.addStore("Indigo",		6);
			mStoreDBAdapter.close();
			
			//create table "location" and insert 4 default locations
			db.execSQL(CREATE_TABLE_LOCATION);
			LocationDBAdapter mLocationDBAdapter = new LocationDBAdapter(context);
			mLocationDBAdapter.open(db);
			mLocationDBAdapter.addLocation(43.698643,
										   -79.390368, 
										   "220 Yonge Street, Toronto, ON, M5B 2H1",
										   0, null, null, null, null, null);
			mLocationDBAdapter.addLocation(43.650997,
										   -79.476740, 
										   "2243 Bloor ST W\nToronto, ON M6S 1N7\nCanada",
										   0, null, null, null, null, null);
			mLocationDBAdapter.addLocation(43.707563,
										   -79.398328, 
										   "85 Bloor Street West, Toronto, Ontario\nM5S 1M1 Canadas",
										   0, null, null, null, null, null);
			mLocationDBAdapter.addLocation(43.652243,
										   -79.371197, 
										   "65 Dundas Street West\nToronto, ON, M5G 2C3",
										   0, null, null, null, null, null);
			
			mLocationDBAdapter.addLocation(43.652243,
					   -79.371197, 
					   "Toronto",
					   0, null, null, null, null, null);
			
			mLocationDBAdapter.addLocation(43.652243,
					   -79.371197, 
					   "259 Richmond Street West Toronto ON M5V 3M6 ",
					   0, null, null, null, null, null);

			mLocationDBAdapter.close();
		
		}

		/***
		 * (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Adding any table mods to this guy here
			
		}
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
	}

}
