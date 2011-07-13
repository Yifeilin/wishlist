package com.aripio.wishlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/***
 * WishListItemViewBinder defines how the item's photo and date_time are displayed in 
 * the view. 
 * 
 * It retrieves the image file from the picture_uri saved in database and set the image
 * to the view
 * 
 * It retrieves the date_time from the database and converts it to "July 6, 1983" format
 * for display in the view 
 */
public class WishListItemViewBinder implements SimpleCursorAdapter.ViewBinder {

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		int nImageIndex = cursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PHOTO_URL);
		int nDateIndex = cursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DATE_TIME);

		// set the photo to the image view
		if (columnIndex == nImageIndex) {
			Bitmap bitmap = null;
			
			//get the ImageView in which the photo should be displayed
			ImageView photoView = (ImageView) view;

			// read in the picture string from db
			// the string could be a resource id or a uri from content provider
			String pic_str = cursor.getString(columnIndex);
			
			//check if pic_str is null, which user added this item without taking a pic.
			if (pic_str == null){
				//do nothing
				return true;
			}

			// check if pic_str is a resId or a uri
			try {
				//if it's a resID, the following decodeResource will not 
				//throw exception (this need to be changed for performance)
				int picResId = Integer.valueOf(pic_str, 16).intValue();
				bitmap = BitmapFactory.decodeResource(view.getContext()
						.getResources(), picResId);
			} catch (NumberFormatException e) {
				// Not a resId, so it must be a content provider uri
				// thus set image from uri
				Uri pic_uri = Uri.parse(pic_str);
				photoView.setImageURI(pic_uri);
				return true;
			}

			// exception is not thrown, so it is resID.
			// set the image decoded from resID
			photoView.setImageBitmap(bitmap);

			return true;
		}

		// set date and time to the text view in appropriate format
		if (columnIndex == nDateIndex) {
			
			//get the TextView in which the date and time will be displayed
			TextView viewDate = (TextView) view;
			
			//get the date_time string from db and reformat it
			String dateTimeStr = cursor.getString(columnIndex);
			SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTo = new SimpleDateFormat("MMM dd, yyyy");

			String dateTimeStrNew = null;
			try {
				dateTimeStrNew = sdfTo.format(sdfFrom.parse(dateTimeStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//set the reformatted date_time
			viewDate.setText(dateTimeStrNew);
			return true;
		}

		return false;
	}

}
