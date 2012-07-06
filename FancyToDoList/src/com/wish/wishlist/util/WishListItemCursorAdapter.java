package com.wish.wishlist.util;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import com.wish.wishlist.R;
import com.wish.wishlist.db.ItemDBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WishListItemCursorAdapter extends SimpleCursorAdapter {
	public WishListItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		setViewBinder(new WishListItemViewBinder());
	}
	
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
		
		boolean _hasStoreName = false;

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

			int nImageIndex = cursor.getColumnIndexOrThrow(ItemDBAdapter.KEY_PHOTO_URL);
//			int nDateIndex = cursor
//					.getColumnIndexOrThrow(ItemDBAdapter.KEY_DATE_TIME);
			int nPriceIndex = cursor.getColumnIndexOrThrow(ItemDBAdapter.KEY_PRICE);
			
			int nStoreNameIndex = cursor.getColumnIndexOrThrow(ItemDBAdapter.KEY_STORENAME);

			int nAddIndex = cursor.getColumnIndexOrThrow(ItemDBAdapter.KEY_ADDRESS);
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
				
				boolean isSample = false;
				if (pic_str.endsWith("sample")) {
					pic_str = pic_str.substring(0, pic_str.length() - 6);
					isSample = true;
				}

				// check if pic_str is a resId or a uri
				try {
					//if it's a resID, the following decodeResource will not 
					//throw exception (this need to be changed for performance)
					int picResId = Integer.valueOf(pic_str, 16).intValue();
					if (isSample) {
						bitmap = BitmapFactory.decodeResource(view.getContext()
								.getResources(), picResId);
						int width =128;
						int height=128;
						bitmap = android.media.ThumbnailUtils.extractThumbnail(bitmap, width, height);
					}
					else {
						bitmap = BitmapFactory.decodeResource(view.getContext()
								.getResources(), picResId);
					}
				} catch (NumberFormatException e) {
					// Not a resId, so it must be a content provider uri
					// thus set image from uri (not a photo taken by cameara, but
					// a photo we manually added to the drawable folder
					Uri pic_uri = Uri.parse(pic_str);
					photoView.setImageURI(pic_uri);
					return true;
//					
//					picUrl = Integer.toHexString(R.drawable.mini_cooper);
//					bitmap = BitmapFactory.decodeResource(view.getContext()
//							.getResources(), R.drawable.mini_cooper);
				}

				// exception is not thrown, so it is resID.
				// set the image decoded from resID
				photoView.setImageBitmap(bitmap);

				return true;
			}

//			// set date and time to the text view in appropriate format
//			if (columnIndex == nDateIndex) {
//				
//				//get the TextView in which the date and time will be displayed
//				TextView viewDate = (TextView) view;
//				
//				//get the date_time string from db and reformat it
//				String dateTimeStr = cursor.getString(columnIndex);
//				SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd");
//				SimpleDateFormat sdfTo = new SimpleDateFormat("MMM dd, yyyy");
//
//				String dateTimeStrNew = null;
//				try {
//					dateTimeStrNew = sdfTo.format(sdfFrom.parse(dateTimeStr));
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				//set the reformatted date_time
//				viewDate.setText(dateTimeStrNew);
//				return true;
//			}
			
			else if (columnIndex == nPriceIndex) {
				TextView viewPrice = (TextView) view;
				float price = cursor.getFloat(columnIndex);
				//we use float.min_value to indicate price is not available
				if (price != Float.MIN_VALUE) {
					DecimalFormat Dec = new DecimalFormat("0.00");
					String priceStr = (Dec.format(price));
					priceStr = "$ " + priceStr;
					viewPrice.setText(priceStr);
					viewPrice.setVisibility(View.VISIBLE);
				}
				else {
					viewPrice.setVisibility(View.GONE);
				}
				return true;
			}
			
			else if (columnIndex == nStoreNameIndex){
				TextView viewStore = (TextView) view;
				String storeName = cursor.getString(columnIndex);	
				if (!storeName.equals("")) {
					_hasStoreName = true;
					storeName = "At " + storeName;
					viewStore.setText(storeName);
					viewStore.setVisibility(View.VISIBLE);
				}
				else {
					viewStore.setVisibility(View.GONE);
				}
				return true;
			}

			else if (columnIndex == nAddIndex) {
				TextView viewAddress = (TextView) view;
				String Address = cursor.getString(columnIndex);
				if (!Address.equals("unknown") && !Address.equals("")) {
					if (!_hasStoreName) {
						Address = "At " + Address;
					}
					viewAddress.setText(Address);
					viewAddress.setVisibility(View.VISIBLE);
				}
				else {
					viewAddress.setVisibility(View.GONE);
				}
				return true;
			}
		
			return false;
//			return true;
		}

	}


}

