package com.aripio.wishlist;

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
	
/*************************************************************************************/	
/********************	Query string to create table Item	**************************/
/******************** 	and insert default items			**************************/
/*************************************************************************************/	
	//Query string to create table "Item"
	private static final String CREATE_TABLE_ITEM = "create table "
			+ ItemDBAdapter.DB_TABLE 
			+ " (" 
			+ ItemDBAdapter.KEY_ID			+ " integer primary key autoincrement, " 
			+ ItemDBAdapter.KEY_STORE_ID 	+ " INTEGER, "
			+ ItemDBAdapter.KEY_NAME 		+ " TEXT, " 
			+ ItemDBAdapter.KEY_DESCRIPTION + " TEXT, " 
			+ ItemDBAdapter.KEY_DATE_TIME 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_STORENAME 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_PHOTO_URL 	+ " TEXT, " 
			+ ItemDBAdapter.KEY_PRICE 		+ " REAL, " 
			+ ItemDBAdapter.KEY_LOCATION 	+ " TEXT, "
			+ ItemDBAdapter.KEY_PRIORITY 	+ " INTEGER"
			+ ");";

	//Query string to insert item1 in table "Item"
	private static final String INSERT_DEFAULT_ITEM1 = "INSERT INTO "
			+ ItemDBAdapter.DB_TABLE 
			+ " (" 
			+ ItemDBAdapter.KEY_ID 			+ ", "
			+ ItemDBAdapter.KEY_STORE_ID	+ ", "
			+ ItemDBAdapter.KEY_NAME		+ ", "
			+ ItemDBAdapter.KEY_DESCRIPTION	+ ", "
			+ ItemDBAdapter.KEY_DATE_TIME	+ ", "
			// + ItemDBAdapter.KEY_STORENAME + ", "
			+ ItemDBAdapter.KEY_PHOTO_URL 	+ ", "
			+ ItemDBAdapter.KEY_PRICE		+ ", "
			+ ItemDBAdapter.KEY_LOCATION	+ ", "
			+ ItemDBAdapter.KEY_PRIORITY 	
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"						+ ", " 	
			+ "'1'"							+ ", " 
			+ "'Car'" 						+ ", "
			+ "'It is a very nice car'"		+ ", " 
			+ "'1983-07-06 11:30:00'"		+ ", "
			+ "'7f020004'" 					+ ", "
			+ "'25000.0'" 					+ ", "
			+ "'BMW, Shanghai, China'" 		+ ", "
			+ "'0'"
			+ ");";

	//Query string to insert item2 in table "Item"
	private static final String INSERT_DEFAULT_ITEM2 = "INSERT INTO "
			+ ItemDBAdapter.DB_TABLE 		
			+ " ("
			+ ItemDBAdapter.KEY_ID 			+ ", "
			+ ItemDBAdapter.KEY_STORE_ID	+ ", "
			+ ItemDBAdapter.KEY_NAME		+ ", "
			+ ItemDBAdapter.KEY_DESCRIPTION	+ ", "
			+ ItemDBAdapter.KEY_DATE_TIME	+ ", "
			// + ItemDBAdapter.KEY_STORENAME + ", "
			+ ItemDBAdapter.KEY_PHOTO_URL	+ ", "
			+ ItemDBAdapter.KEY_PRICE		+ ", " 
			+ ItemDBAdapter.KEY_LOCATION 	+ ", "
			+ ItemDBAdapter.KEY_PRIORITY 
			+ ") "
			+ "VALUES"
			+ "("
			+ "NULL"						+ ", "
			+ "'2'"							+ ", "
			+ "'Book'" 						+ ", "
			+ "'It is a great book'"		+ ", "
			+ "'1984-03-17 18:22:35'"		+ ", "
			+ "'7f020003'" 					+ ", "
			+ "'9.9'"						+ ", "
			+ "'Chapter store, Toronto, ON Canada'" + ", "
			+ "'3'"
			+ ");";
	
	//Query string to insert item3 in table "Item"
	private static final String INSERT_DEFAULT_ITEM3 = "INSERT INTO "
			+ ItemDBAdapter.DB_TABLE + " (" + ItemDBAdapter.KEY_ID + ", "
			+ ItemDBAdapter.KEY_STORE_ID
			+ ", "
			+ ItemDBAdapter.KEY_NAME
			+ ", "
			+ ItemDBAdapter.KEY_DESCRIPTION
			+ ", "
			+ ItemDBAdapter.KEY_DATE_TIME
			+ ", "
			// + ItemDBAdapter.KEY_STORENAME + ", "
			+ ItemDBAdapter.KEY_PHOTO_URL + ", " + ItemDBAdapter.KEY_PRICE
			+ ", " + ItemDBAdapter.KEY_LOCATION + ", "
			+ ItemDBAdapter.KEY_PRIORITY + ") " + "VALUES" + "(" + "NULL"
			+ ", " + "'3'" + ", " + "'Cake'" + ", "
			+ "'It is a delicious cake'" + ", " + "'2011-05-03 03:40:50'"
			+ ", " + "'7f020006'" + ", " + "'6.99'" + ", "
			+ "'YuanZu Food, Jiaxing, Zhejiang, China'" + ", " + "'2'" + ");";

	//Query string to insert item4 in table "Item"
	private static final String INSERT_DEFAULT_ITEM4 = "INSERT INTO "
			+ ItemDBAdapter.DB_TABLE + " (" + ItemDBAdapter.KEY_ID + ", "
			+ ItemDBAdapter.KEY_STORE_ID
			+ ", "
			+ ItemDBAdapter.KEY_NAME
			+ ", "
			+ ItemDBAdapter.KEY_DESCRIPTION
			+ ", "
			+ ItemDBAdapter.KEY_DATE_TIME
			+ ", "
			// + ItemDBAdapter.KEY_STORENAME + ", "
			+ ItemDBAdapter.KEY_PHOTO_URL + ", " + ItemDBAdapter.KEY_PRICE
			+ ", " + ItemDBAdapter.KEY_LOCATION + ", "
			+ ItemDBAdapter.KEY_PRIORITY + ") " + "VALUES" + "(" + "NULL"
			+ ", " + "'4'" + ", " + "'Heart'" + ", "
			+ "'It is a warm beating heart'" + ", " + "'2000-11-23 08:17:38'"
			+ ", " + "'7f020008'" + ", " + "'324.49'" + ", "
			+ "'Hospital, Beijing, China'" + ", " + "'1'" + ");";

/*************************************************************************************/	
/********************	Query string to create table ItemCategory	**************************/
/******************** 	and insert default ItemCategory			**************************/
/*************************************************************************************/	
	//Query string to create table "ItemCategory"
	private static final String CREATE_TABLE_ITEMCATEGORY = "create table "
			+ ItemCategoryDBAdapter.DB_TABLE + " ("
			+ ItemCategoryDBAdapter.KEY_ID
			+ " integer primary key autoincrement, " 
			+ ItemCategoryDBAdapter.KEY_NAME + " TEXT"
			+ ");"; 

/*************************************************************************************/	
/********************	Query string to create table store	**************************/
/******************** 	and insert default stores			**************************/
/*************************************************************************************/
	//Query string to create table "store"
	private static final String CREATE_TABLE_STORE = "create table "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID				+ " integer primary key autoincrement, " 
			+ StoreDBAdapter.KEY_LOCATION_ID	+ " INTEGER, "
			+ StoreDBAdapter.KEY_NAME 			+ " TEXT" 
			+ ");";
	
	//Query string to insert store1 in table "store"
	private static final String INSERT_DEFAULT_STORE1 = "INSERT INTO "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID 			+ ", "
			+ StoreDBAdapter.KEY_LOCATION_ID	+ ", "
			+ StoreDBAdapter.KEY_NAME
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'1'"								+ ", " 
			+ "'BMW store'"			
			+ ");";
	
	//Query string to insert store2 in table "store"
	private static final String INSERT_DEFAULT_STORE2 = "INSERT INTO "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID 			+ ", "
			+ StoreDBAdapter.KEY_LOCATION_ID	+ ", "
			+ StoreDBAdapter.KEY_NAME
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'2'"								+ ", " 
			+ "'Chapter'"			
			+ ");";
	
	//Query string to insert store3 in table "store"
	private static final String INSERT_DEFAULT_STORE3 = "INSERT INTO "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID 			+ ", "
			+ StoreDBAdapter.KEY_LOCATION_ID	+ ", "
			+ StoreDBAdapter.KEY_NAME
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'3'"								+ ", " 
			+ "'Yuan Zu Food'"			
			+ ");";
	
	//Query string to insert store4 in table "store"
	private static final String INSERT_DEFAULT_STORE4 = "INSERT INTO "
			+ StoreDBAdapter.DB_TABLE 
			+ " (" 
			+ StoreDBAdapter.KEY_ID 			+ ", "
			+ StoreDBAdapter.KEY_LOCATION_ID	+ ", "
			+ StoreDBAdapter.KEY_NAME
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'4'"								+ ", " 
			+ "'Hospital'"			
			+ ");";

	/*************************************************************************************/	
	/********************	Query string to create table location	**********************/
	/******************** 	and insert default location			**************************/
	/*************************************************************************************/
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

	//Query string to insert location1 in table "location"
	private static final String INSERT_DEFAULT_LOCATION1 = "INSERT INTO "
			+ LocationDBAdapter.DB_TABLE 
			+ " (" 
			+ LocationDBAdapter.KEY_ID 			+ ", "
			+ LocationDBAdapter.KEY_LATITUDE	+ ", "
			+ LocationDBAdapter.KEY_LONGITUDE	+ ", "
			+ LocationDBAdapter.KEY_ADDSTR	//+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE2	+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE3
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'43.698643'"						+ ", " 
			+ "'-79.390368'"					+ ", "
			+ "'187 Balliol St\nToronto, ON M4S 1C8\nCanada'"				//+ ", "
			//+ "'Toronto, ON M4S 1C8'"			+ ", "
			//+ "'Canada'"
			+ ");";
	
	//Query string to insert location2 in table "location"
	private static final String INSERT_DEFAULT_LOCATION2 = "INSERT INTO "
			+ LocationDBAdapter.DB_TABLE 
			+ " (" 
			+ LocationDBAdapter.KEY_ID 			+ ", "
			+ LocationDBAdapter.KEY_LATITUDE	+ ", "
			+ LocationDBAdapter.KEY_LONGITUDE	+ ", "
			+ LocationDBAdapter.KEY_ADDSTR	//+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE2	+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE3
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'43.650997'"						+ ", " 
			+ "'-79.476740'"					+ ", "
			+ "'2243 Bloor ST W\nToronto, ON M6S 1N7\nCanada'"				//+ ", "
//			+ "'Toronto, ON M6S 1N7'"			+ ", "
//			+ "'Canada'"
			+ ");";
	
	//Query string to insert location3 in table "location"
	private static final String INSERT_DEFAULT_LOCATION3 = "INSERT INTO "
			+ LocationDBAdapter.DB_TABLE 
			+ " (" 
			+ LocationDBAdapter.KEY_ID 			+ ", "
			+ LocationDBAdapter.KEY_LATITUDE	+ ", "
			+ LocationDBAdapter.KEY_LONGITUDE	+ ", "
			+ LocationDBAdapter.KEY_ADDSTR	//+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE2	+ ", "
//			+ LocationDBAdapter.KEY_ADDLINE3
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'43.707563'"						+ ", " 
			+ "'-79.398328'"					+ ", "
			+ "'2315 Yonge ST\nToronto, ON M4P 2C7\nCanada'"					//+ ", "
//			+ "'Toronto, ON M4P 2C7'"			+ ", "
//			+ "'Canada'"
			+ ");";
	
	//Query string to insert location4 in table "location"
	private static final String INSERT_DEFAULT_LOCATION4 = "INSERT INTO "
			+ LocationDBAdapter.DB_TABLE 
			+ " (" 
			+ LocationDBAdapter.KEY_ID 			+ ", "
			+ LocationDBAdapter.KEY_LATITUDE	+ ", "
			+ LocationDBAdapter.KEY_LONGITUDE	+ ", "
			+ LocationDBAdapter.KEY_ADDSTR	//+ ", "
			//+ LocationDBAdapter.KEY_ADDLINE2	+ ", "
			//+ LocationDBAdapter.KEY_ADDLINE3
			+ ") "
			+ "VALUES"
			+ "(" 
			+ "NULL"							+ ", " 	
			+ "'43.652243'"						+ ", " 
			+ "'-79.371197'"					+ ", "
			+ "'252 Adelaide ST E\nToronto, ON M5A 2N4\nCanada'"				//+ ", "
//			+ "'Toronto, ON M5A 2N4'"			+ ", "
//			+ "'Canada'"
			+ ");";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * 
	 * @param ctx
	 */
	public DBAdapter(Context ctx) {
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/***onCreate is called when the database is first created
		 * (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table item and insert into the table
			// 4 default items
			db.execSQL(CREATE_TABLE_ITEM);
			db.execSQL(INSERT_DEFAULT_ITEM1);
			db.execSQL(INSERT_DEFAULT_ITEM2);
			db.execSQL(INSERT_DEFAULT_ITEM3);
			db.execSQL(INSERT_DEFAULT_ITEM4);

			// create table itemCategory
			db.execSQL(CREATE_TABLE_ITEMCATEGORY);
			// to be added
			
			//store
			db.execSQL(CREATE_TABLE_STORE);
			db.execSQL(INSERT_DEFAULT_STORE1);
			db.execSQL(INSERT_DEFAULT_STORE2);
			db.execSQL(INSERT_DEFAULT_STORE3);
			db.execSQL(INSERT_DEFAULT_STORE4);
			
			//location
			db.execSQL(CREATE_TABLE_LOCATION);
			db.execSQL(INSERT_DEFAULT_LOCATION1);
			db.execSQL(INSERT_DEFAULT_LOCATION2);
			db.execSQL(INSERT_DEFAULT_LOCATION3);
			db.execSQL(INSERT_DEFAULT_LOCATION4);
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
