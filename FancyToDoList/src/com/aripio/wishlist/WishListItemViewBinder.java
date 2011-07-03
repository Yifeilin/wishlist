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

public class WishListItemViewBinder implements SimpleCursorAdapter.ViewBinder {
	// ResourceCursorAdapter.ViewBinder {

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// int nImageIndex =
		// cursor.getColumnIndexOrThrow(WishListDataBase.KEY_PHOTO_URL);
		// int nDateIndex =
		// cursor.getColumnIndexOrThrow(WishListDataBase.KEY_DATE);

		int nImageIndex = cursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_PHOTO_URL);
		int nDateIndex = cursor
				.getColumnIndexOrThrow(ItemDBAdapter.KEY_DATE_TIME);

		// set the photo to the image view
		if (columnIndex == nImageIndex) {
			Bitmap bitmap = null;
			ImageView photoView = (ImageView) view;
			// int type = Integer.parseInt(cursor.getString(nImageIndex));
			// photoView.setImageResource(R.drawable.chocolate);
			// int count = cursor.getCount();

			// read in the picture string from db
			// it could be a resource id or a uri from content provider
			String pic_str = cursor.getString(columnIndex);

			// check if pic_str is a resId
			try {
				// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
				int picResId = Integer.valueOf(pic_str, 16).intValue();
				// Integer.parseInt(pic_str);
				bitmap = BitmapFactory.decodeResource(view.getContext()
						.getResources(), picResId);
			} catch (NumberFormatException e) {
				// Not a resId, so it must be a content provider uri
				Uri pic_uri = Uri.parse(pic_str);
				photoView.setImageURI(pic_uri);
				return true;
			}

			// it is resource id.
			photoView.setImageBitmap(bitmap);

			// pic_uri = Uri.parse("android.resource://com.aripio.wishlist/" +
			// R.drawable.car);
			// pic_uri =
			// Uri.parse("android.resource://com.aripio.wishlist/drawable/car");

			// photoView.setImageResource(R.drawable.car);

			return true;
		}

		// set date and time to the text view in appropriate format
		if (columnIndex == nDateIndex) {
			TextView viewDate = (TextView) view;
			// viewDate.setText(cursor.getString(columnIndex));
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

			viewDate.setText(dateTimeStrNew);
			return true;
		}

		return false;
	}

}
