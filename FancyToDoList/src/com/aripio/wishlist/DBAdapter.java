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
			+ "'0'"							+ ", " 
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
			+ "'0'"							+ ", "
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
			+ ", " + "'0'" + ", " + "'Cake'" + ", "
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
			+ ", " + "'0'" + ", " + "'Heart'" + ", "
			+ "'It is a warm beating heart'" + ", " + "'2000-11-23 08:17:38'"
			+ ", " + "'7f020008'" + ", " + "'324.49'" + ", "
			+ "'Hospital, Beijing, China'" + ", " + "'1'" + ");";

	//Query string to create table "ItemCategory"
	private static final String CREATE_TABLE_ITEMCATEGORY = "create table "
			+ ItemCategoryDBAdapter.DB_TABLE + " ("
			+ ItemCategoryDBAdapter.KEY_ID
			+ " integer primary key autoincrement, " 
			+ ItemCategoryDBAdapter.KEY_NAME + " TEXT"
			+ ");"; 

	//Query string to create table "store"
	private static final String CREATE_TABLE_STORE = "create table "
			+ StoreDBAdapter.DB_TABLE + " (" + StoreDBAdapter.KEY_ID
			+ " integer primary key autoincrement, " 
			+ StoreDBAdapter.KEY_NAME + " TEXT" 
			+ ");";

	//Query string to create table "location"
	private static final String CREATE_TABLE_LOCATION = "create table "
			+ LocationDBAdapter.DB_TABLE
			+ " ("
			+ LocationDBAdapter.KEY_ID
			+ " integer primary key autoincrement, "
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

			// create table itemCategory, store and location
			db.execSQL(CREATE_TABLE_ITEMCATEGORY);
			db.execSQL(CREATE_TABLE_STORE);
			db.execSQL(CREATE_TABLE_LOCATION);
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
