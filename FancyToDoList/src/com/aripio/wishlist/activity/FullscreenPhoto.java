package com.aripio.wishlist.activity;

import com.aripio.wishlist.R;
import android.app.Activity;
import android.content.Intent;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FullscreenPhoto extends Activity {
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   setContentView(R.layout.fullscreen_photo);
		   Intent intent = getIntent();
		   String picture_str = intent.getStringExtra("pic_str");
		   ImageView imageItem = (ImageView) findViewById(R.id.fullscreen_photo);

		   Bitmap bitmap = null;
			
			//check if pic_str is null, which user added this item without taking a pic.
			if (picture_str != null){
				Uri picture_Uri = Uri.parse(picture_str);
				
				// check if pic_str is a resId
				try {
					// view.getContext().getResources().getDrawable(Integer.parseInt(pic_str));
					int picResId = Integer.valueOf(picture_str, 16).intValue();
					bitmap = BitmapFactory.decodeResource(imageItem.getContext()
							.getResources(), picResId);
					// it is resource id.
					imageItem.setImageBitmap(bitmap);

				} catch (NumberFormatException e) {
					// Not a resId, so it must be a content provider uri
					picture_Uri = Uri.parse(picture_str);
					imageItem.setImageURI(picture_Uri);

				}
			}
		   
//		    imageItem.setLayoutParams( new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
//
//		    
//		            imageView.setImageResource(imageId);
//		            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

		   }

}
