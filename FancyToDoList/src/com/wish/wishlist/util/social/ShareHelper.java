package com.wish.wishlist.util.social;

import java.util.List; 
import android.util.Log; 
import android.app.Activity;
import android.app.AlertDialog; 
import android.content.Context; 
import android.content.DialogInterface; 
import android.content.Intent; 
import android.content.pm.ResolveInfo; 
import com.facebook.android.Facebook; 
import com.wish.wishlist.R;  
import com.wish.wishlist.util.social.ShareIntentListAdapter;  

public class ShareHelper {
	Context _ctx;
	String _subject;
	String _message;
	Facebook _facebook;

public ShareHelper(Context ctx, String subject, String message) {
	_ctx = ctx; 
	_subject = subject;
	_message = message;
	_facebook = null;
}

public Facebook share() {
	Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
	sendIntent.setType("*/*");
	List activities = _ctx.getPackageManager().queryIntentActivities(sendIntent, 0);
	AlertDialog.Builder builder = new AlertDialog.Builder(_ctx); 
	builder.setTitle("Share to..."); 
	final ShareIntentListAdapter adapter = new ShareIntentListAdapter((Activity)_ctx, R.layout.share_app_list, R.id.shareAppLabel, activities.toArray()); 
	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ResolveInfo info = (ResolveInfo) adapter.getItem(which);
			if(info.activityInfo.packageName.contains("facebook")) { 
				//new PostToFacebookDialog(_ctx, _message).show();
				Log.d("share", "facebook");
			}
			else {
				Log.d("share", "others");
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
				intent.setType("*/*");
				//intent.putExtra(Intent.EXTRA_SUBJECT, _subject);
				intent.putExtra(Intent.EXTRA_TEXT, _message);
				((Activity)_ctx).startActivity(intent);
			}
		}
	});

	builder.create().show();
	return _facebook;
}
}

