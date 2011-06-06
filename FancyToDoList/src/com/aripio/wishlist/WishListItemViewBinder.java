package com.aripio.wishlist;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class WishListItemViewBinder implements 
SimpleCursorAdapter.ViewBinder  {
	
	@Override 
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) 
    { 
      int nImageIndex = cursor.getColumnIndex(WishListDataBase.KEY_PHOTO_URL); 
      if(nImageIndex==columnIndex) 
      { 
          ImageView photoView = (ImageView)view; 
          //int type = Integer.parseInt(cursor.getString(nImageIndex)); 
          photoView.setImageResource(R.drawable.chocolate); 
          return true; 
      } 
      return false; 
    } 

}
