package com.aripio.wishlist;

import com.aripio.wishlist.WishListDataBase.ItemsCursor;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Provides access to the WishItem database.  Since this is not a Content Provider, no
 * other applications will have access to the database.
 */
public class WishListDataBase extends  SQLiteOpenHelper {
	/** The name of the database file on the file system */
    private static final String DATABASE_NAME = "WishList";
    /** The version of the database that this class understands. */
    private static final int DATABASE_VERSION = 2;
    /** Keep track of context so that we can load SQL from string resources */
	private Context mContext;
	
	public static final String KEY_NAME = "name";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "create_date";
	public static final String KEY_ITEMID = "_id";
	public static final String KEY_STORENAME = "store_name";

	private static WishListDataBase instance;
	/**
	 * Use singleton pattern to return a unique instance of WishListDataBase
	 * @param context
	 * @return
	 */
	public static synchronized WishListDataBase getDBInstance(Context context){
		if(instance == null){
			instance = new WishListDataBase(context);
		}
		else
			instance.setContext(context);
		
		return instance;		
	}
	
	public void setContext(Context context){
		this.mContext = context;
	}
	
	public WishListDataBase(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.mContext = context;
	}
	
	/** Constructor */
    private WishListDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sql = mContext.getString(R.string.WishListDataBase_Create).split("\n");
		db.beginTransaction();
		try {
			// Create tables & test data
			execMultipleSQL(db, sql);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
        	db.endTransaction();
        }		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(WishList.LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
	                newVersion + ", which will destroy all old data");

			String[] sql = mContext.getString(R.string.WishListDataBase_Upgrade).split("\n");
			db.beginTransaction();
			try {
				// Create tables & test data
				execMultipleSQL(db, sql);
				db.setTransactionSuccessful();
			} catch (SQLException e) {
	            Log.e("Error creating tables and debug data", e.toString());
	        } finally {
	        	db.endTransaction();
	        }
	        // This is cheating.  In the real world, you'll need to add columns, not rebuild from scratch
	        onCreate(db);		
	}
	
	/**
     * Execute all of the SQL statements in the String[] array
     * @param db The database on which to execute the statements
     * @param sql An array of SQL statements to execute
     */
    private void execMultipleSQL(SQLiteDatabase db, String[] sql){
    	for( String s : sql )
    		if (s.trim().length()>0)
    			db.execSQL(s);
    }
    
    /**
	 * Add a new item to the database.  The item will have a status of open.
	 * @param item_id	unique id of the item
	 * @param name			The item name
	 * @param description	The name description
	 */
	public void addItem(String name, String description, String date, int store_id, String picture_uri){
		String sql = String.format(
			"INSERT INTO WishItems (_id, name, description, create_date, store_id,  picture) " +
			"VALUES ( NULL, '%s', '%s', '%s', '%d', '%s')",
			 name, description, date, store_id, picture_uri);
		try{
			getWritableDatabase().execSQL(sql);
		} catch (SQLException e) {
            Log.e("Error writing new item", e.toString());
		}
	}
	
	/**
	 * Update a item in the database.
	 * @param _id		The id of the existing item
	 * @param name			The item name
	 * @param description	The item description
	 */
	public void editItem(long _id, String name, String description,String date, int store_id, Bitmap picture) {
		String sql = String.format(
				"UPDATE WishItems " +
				"SET name = '%s',  "+
				" description = '%s', "+
				" create_date = '%s', " +
				" store_id = '%d' " +
				"WHERE _id = '%d' ",
				name, description,date, store_id, _id);
		try{
			getWritableDatabase().execSQL(sql);
		} catch (SQLException e) {
            Log.e("Error writing an exsiting item", e.toString());
		}
	}
	
	/**
	 * Delete a item from the database.
	 * @param _id		The id of the item to delete
	 */
	public void deleteItem(long _id) {
		String sql = String.format(
				"DELETE FROM WishItems " +
				"WHERE _id = '%d' ",
				_id);
		try{
			getWritableDatabase().execSQL(sql);
		} catch (SQLException e) {
            Log.e("Error deleteing item", e.toString());
		}
	}

	/** Returns the number of Items */
	public int getItemsCount(){

		Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT count(*) FROM WishItems", null);
            if (0 >= c.getCount()) { return 0; }
            c.moveToFirst();
            return c.getInt(0);
        }
        finally {
            if (null != c) {
                try { c.close(); }
                catch (SQLException e) { }
            }
        }
	}
	
	 public static class ItemsCursor extends SQLiteCursor{
	    	public static enum SortBy{
	    		name,
	    		_id
	    	}
	    	private static final String QUERY = 
	    		"SELECT _id, name, description, create_date, store_id "+
	    	    "FROM WishItems "+
	    	    "ORDER BY ";
		    private ItemsCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
					String editTable, SQLiteQuery query) {
				super(db, driver, editTable, query);
			}
		    private static class Factory implements SQLiteDatabase.CursorFactory{
				@Override
				public Cursor newCursor(SQLiteDatabase db,
						SQLiteCursorDriver driver, String editTable,
						SQLiteQuery query) {
					return new ItemsCursor(db, driver, editTable, query);
				}
		    }
	    	public long getColItemsId(){
	    		return getLong(getColumnIndexOrThrow("_id"));
	    	}
			public String getColName(){
				return getString(getColumnIndexOrThrow("name"));
			}
	    	public int getColStoreId(){
	    		return Integer.parseInt(getString(getColumnIndexOrThrow("store_id")));
	    	}
	    	public String getColPicture(){
	    		return getString(getColumnIndexOrThrow("picture"));
	    	}	
			public String getColDescription(){
	    		return getString(getColumnIndexOrThrow("description"));
	    	}	
	    	public String getColCreateDate(){
	    		return getString(getColumnIndexOrThrow("create_date"));
	    	}	
	    }
	 
	 /** Return a sorted JobsCursor
	     * @param sortBy the sort criteria
	     */
	    public ItemsCursor getItems(ItemsCursor.SortBy sortBy) {
	    	String sql = ItemsCursor.QUERY+sortBy.toString();
	    	SQLiteDatabase d = getReadableDatabase();
	    	ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
	        	new ItemsCursor.Factory(),
	        	sql,
	        	null,
	        	null);
	        c.moveToFirst();
	        return c;
	    }
	    /**
	     * Return the cursor of item with id equal to _id
	     * @param _id
	     * @return
	     */
	public ItemsCursor getItem(long _id) {
		String sql = String.format(
				"SELECT * FROM WishItems " +
				"WHERE _id = '%d' ",
				_id);
		SQLiteDatabase d = getReadableDatabase();
		ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
				new ItemsCursor.Factory(), sql, null, null);
		return c;
	}

		public ItemsCursor searchItems(String query) {
			String sql = String.format("SELECT * FROM WishItems " + 
									   "WHERE name like '%s' ", query);
			SQLiteDatabase d = getReadableDatabase();
			ItemsCursor c = (ItemsCursor) d.rawQueryWithFactory(
					new ItemsCursor.Factory(), sql, null, null);
			return null;
		}
}
