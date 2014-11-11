package com.wish.wishlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/***
 * TagDBManager provides access to operations on data in ItemCategory table
 */
public class TagItemDBManager extends DBManager {
    public static final String TAG_ID = "tag_id";
	public static final String ITEM_ID = "item_id";
	public static final String DB_TABLE = "TagItem";

    private static TagItemDBManager _instance = null;

    public static TagItemDBManager instance(Context ctx) {
        if (_instance == null) {
            _instance = new TagItemDBManager(ctx.getApplicationContext());
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
	private TagItemDBManager(Context ctx) {
        super(ctx);
	}

    public long Tag_item(String tagName, long itemId) {
        long tagId = TagDBManager.instance(mCtx).createTag(tagName);
        return Tag_item(tagId, itemId);
    }

    public long Tag_item(long tagId, long itemId) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(TAG_ID, tagId);
        initialValues.put(ITEM_ID, itemId);
        long rowId = mDb.replace(DB_TABLE, null, initialValues);
        close();
        return rowId;
    }

    public void Untag_item(String tagName, long itemId) {
        long tagId = TagDBManager.instance(mCtx).getIdByName(tagName);
        Untag_item(tagId, itemId);
    }

    public void Untag_item(long tagId, long itemId) {
        open();
        String where = TAG_ID + "=" + tagId + " AND " + ITEM_ID + "=" + itemId;
        mDb.delete(DB_TABLE, where, null);
        close();

        //Delete the tag in the tag table if no item is referencing it
        if (!tagExists(tagId)) {
            TagDBManager.instance(mCtx).deleteTag(tagId);
        }
    }

    Boolean tagExists(long tagId) {
        open();
        Cursor cursor = mDb.query(true, DB_TABLE, new String[] { TAG_ID }, TAG_ID + "=" + tagId, null, null, null, null, null);
        Boolean exists = cursor.getCount() >= 1;
        close();
        return exists;
    }

    public ArrayList<String> tags_of_item(long itemId) {
        open();
        Cursor cursor = mDb.query(true, DB_TABLE, new String[] { TAG_ID }, ITEM_ID + "=" + itemId, null, null, null, null, null);
        ArrayList<String> ids = new ArrayList<String>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(cursor.getColumnIndexOrThrow(TAG_ID)));
        }
        close();
        if (ids.isEmpty()) {
            //We don't have any tags for this item, return an empty tag list
            return new ArrayList<String>();
        }
        ArrayList<String> tags = TagDBManager.instance(mCtx).getTagsByIds(ids.toArray(new String[ids.size()]));
        return tags;
    }

    public ArrayList<Long> ItemIds_by_tag(String tagName) {
        long tagId = TagDBManager.instance(mCtx).getIdByName(tagName);
        open();
        Cursor cursor = mDb.query(true, DB_TABLE, new String[] { ITEM_ID }, TAG_ID + "=" + tagId, null, null, null, null, null);
        ArrayList<Long> ItemIds = new ArrayList<Long>();
        while (cursor.moveToNext()) {
            long item_id = cursor.getLong(cursor.getColumnIndexOrThrow(ITEM_ID));
            ItemIds.add(new Long(item_id));
        }
        close();
        return ItemIds;
    }
}
