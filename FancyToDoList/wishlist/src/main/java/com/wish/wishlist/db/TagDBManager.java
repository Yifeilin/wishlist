package com.wish.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

/***
 * TagDBManager provides access to operations on data in ItemCategory table
 */
public class TagDBManager extends DBManager {
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";

	public static final String DB_TABLE = "Tag";
	private static final String TAG="TagDBManager";

    private static TagDBManager _instance = null;

    public static TagDBManager instance(Context ctx) {
        if (_instance == null) {
            _instance = new TagDBManager(ctx.getApplicationContext());
        }
        return _instance;
    }

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	private TagDBManager(Context ctx) {
        super(ctx);
	}

	/**
	 * Create a new tag. If the tag exists, replace it. If successfully created return the new rowId
	 * for that tag, otherwise return a -1 to indicate failure.
	 * 
	 * @param name
	 * @return rowId or -1 if failed
	 */
	public long createTag(String name) {
        open();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
        long rowId = mDb.replace(DB_TABLE, null, initialValues);
        close();
        return rowId;
	}

	/**
	 * Delete the tag with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteTag(long rowId) {

		return this.mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
	}

	/**
	 * Return a Cursor over the list of all tags in the database
	 * 
	 * @return Cursor over all cars
	 */
	public ArrayList<String> getAllTags() {
        open();
        ArrayList<String> tagList = new ArrayList<String>();
		Cursor cursor = mDb.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME }, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String tagName = cursor.getString(cursor.getColumnIndexOrThrow(TagDBManager.KEY_NAME));
                tagList.add(tagName);
            } while (cursor.moveToNext());
        }
        close();
        return tagList;
	}

    public ArrayList<String> getTagsByIds(String[] ids) {
        ArrayList<String> tags = new ArrayList<String>();
        open();
        String query = "SELECT * FROM Tag"
                + " WHERE rowId IN (" + makePlaceholders(ids.length) + ")";
        Cursor cursor = mDb.rawQuery(query, ids);
        while (cursor.moveToNext()) {
            tags.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
        }
        close();
        return tags;
    }

    public long getIdByName(String name) {
        long tagId = -1;
        String where = KEY_NAME + " = ?";
        open();
        Cursor cursor = mDb.query(DB_TABLE, new String[] { KEY_ID }, where, new String[]{name}, null, null, null);
        while (cursor.moveToNext()) {
            tagId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID));
        }
        close();
        return tagId;
    }

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }


	/**
	 * Return a Cursor positioned at the Tag that matches the given rowId
	 * 
	 * @param rowId
	 * @return Cursor positioned to matching tags, if found
	 * @throws SQLException
	 *             if car could not be found/retrieved
	 */
	public Cursor getTag(long rowId) throws SQLException {

		Cursor mCursor =

		this.mDb.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NAME },
				KEY_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Update the Tag.
	 * 
	 * @param rowId
	 * @param name
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateTag(long rowId, String name) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);

		return this.mDb.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}
}
