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
    public static final String TAG_ID = "tag_id";
	public static final String ITEM_ID = "item_id";
	public static final String DB_TABLE = "TagItem";
	private static final String TAG="TagItemDBManager";

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
