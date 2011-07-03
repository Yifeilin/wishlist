package com.aripio.wishlist;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WishListItemCursorAdapter extends SimpleCursorAdapter {
	public WishListItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);

		setViewBinder(new WishListItemViewBinder());
	}

	// public class WishListItemCursorAdapter{
	// //to bind datetime, because it needs to be reformated
	// DateCursorAdapter DCAdapter;
	// //to bind photo and name
	// ItemSimpleCursorAdapter ISCAdapter;
	//	
	// //constructor
	// WishListItemCursorAdapter(Context context, int layout, Cursor c){
	//		
	// }
	//	
	// public class DateCursorAdapter extends ResourceCursorAdapter {
	// //private WishListDataBase wishListDB;
	//		
	// public DateCursorAdapter(Context context, int layout, Cursor c) {
	// super(context, layout, c);
	// // Open or create the database
	// //wishListDB = WishListDataBase.getDBInstance(this);
	// }
	//			
	// @Override
	// public void bindView(View view, Context context, Cursor cursor) {
	//	    	 
	//	    	
	// TextView viewDate = (TextView) view.findViewById(R.id.txtDate);
	// viewDate.setText(cursor.getString(
	// cursor.getColumnIndex(WishListDataBase.KEY_DATE)));
	//	
	//	
	// }
	// }
	//	
	// public class ItemSimpleCursorAdapter extends SimpleCursorAdapter{
	// public ItemSimpleCursorAdapter(Context context, int layout, Cursor c,
	// String[] from, int[] to) {
	// super(context, layout, c, from, to);
	//			
	// setViewBinder(new WishListItemViewBinder());
	// }
	// }

}// end of WishListItemCursorAdapter

