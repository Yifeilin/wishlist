package com.aripio.wishlist;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WishListItemViewBinder implements 
SimpleCursorAdapter.ViewBinder  {
//ResourceCursorAdapter.ViewBinder  {
	
	@Override 
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) 
    { 
      int nImageIndex = cursor.getColumnIndexOrThrow(WishListDataBase.KEY_PHOTO_URL); 
      int nDateIndex = cursor.getColumnIndexOrThrow(WishListDataBase.KEY_DATE); 
      
      //set the photo to the image view  
      if(columnIndex == nImageIndex) 
      { 
          ImageView photoView = (ImageView)view; 
          //int type = Integer.parseInt(cursor.getString(nImageIndex)); 
          //photoView.setImageResource(R.drawable.chocolate); 
          //int count = cursor.getCount();
          String pic_uri_str = cursor.getString(columnIndex);
          Uri pic_uri = Uri.parse(pic_uri_str);
          //"content://media/external/images/media/4"
          photoView.setImageURI(pic_uri);
           
          return true; 
      } 
      
      //set date and time to the text view in appropriate format
      if(columnIndex == nDateIndex)
      {
    	  TextView viewDate = (TextView) view;
    	  //viewDate.setText(cursor.getString(columnIndex));
    	  String dateTimeStr = cursor.getString(columnIndex);
 		  SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd");
 		  SimpleDateFormat sdfTo = new SimpleDateFormat("MMM dd, yyyy");

 		  String dateTimeStrNew = null;
 		  try {
			 dateTimeStrNew = sdfTo.format( sdfFrom.parse(dateTimeStr) );
		  } catch (ParseException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		  }
 			 
    	  viewDate.setText(dateTimeStrNew);
    	  return true;
      }
      
      return false; 
    } 

}
