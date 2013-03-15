package com.wish.wishlist.util.camera;

import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import android.provider.MediaStore;
import com.wish.wishlist.util.camera.PhotoFileCreater;

public class CameraManager
{
	private Intent _intent;
	private String _photoPath;

	public CameraManager() {
		_intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);;
		File f = null;
		try {
			f = PhotoFileCreater.getInstance().setUpPhotoFile(false);
			_photoPath = PhotoFileCreater.getInstance().getfullsizePhotoPath();
			_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
//			Log.d("wishlist", "IOException" + e.getMessage());
//			e.printStackTrace();
			f = null;
			_photoPath= null;
		}
	}

	public Intent getCameraIntent() {
		return _intent;
	}

	public String getPhotoPath() {
		return _photoPath;
	}
}
