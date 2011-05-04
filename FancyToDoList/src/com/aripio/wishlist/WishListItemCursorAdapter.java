package com.aripio.wishlist;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class WishListItemCursorAdapter extends SimpleCursorAdapter{	
	public WishListItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}	
}
