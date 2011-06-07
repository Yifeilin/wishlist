package com.aripio.wishlist;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class WishListItemViewBinder implements 
SimpleCursorAdapter.ViewBinder  {
	
	@Override 
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) 
    { 
      int nImageIndex = cursor.getColumnIndexOrThrow(WishListDataBase.KEY_PHOTO_URL); 
      if(nImageIndex==columnIndex) 
      { 
          ImageView photoView = (ImageView)view; 
          //int type = Integer.parseInt(cursor.getString(nImageIndex)); 
          //photoView.setImageResource(R.drawable.chocolate); 
          int count = cursor.getCount();
          String pic_uri_str = cursor.getString(columnIndex);
          Uri pic_uri = Uri.parse(pic_uri_str);
          //"content://media/external/images/media/4"
          photoView.setImageURI(pic_uri);
           
          return true; 
      } 
      return false; 
    } 

}
