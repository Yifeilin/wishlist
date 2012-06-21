package com.android.wishlist.util.camera;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.android.wishlist.util.camera.AlbumStorageDirFactory;
import com.android.wishlist.util.camera.BaseAlbumDirFactory;
import com.android.wishlist.util.camera.FroyoAlbumDirFactory;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class PhotoFileCreater {
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	String _fullsizePhotoPath;
	
	private static PhotoFileCreater instance = null;

	public static PhotoFileCreater getInstance() {
		if (instance == null){
			instance = new PhotoFileCreater();
		}
		return instance;
	}
	
	private PhotoFileCreater() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	public File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		_fullsizePhotoPath = f.getAbsolutePath();
		Log.d("wishlist", _fullsizePhotoPath);
		return f;
	}
	
	public String getfullsizePhotoPath() {
		return _fullsizePhotoPath; 
	}
	
	private String getAlbumName() {
		//return getString(R.string.album_name);
		return "WishListPhoto";
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("wishlist", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v("wishlist", "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

}


