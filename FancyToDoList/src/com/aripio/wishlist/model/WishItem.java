package com.aripio.wishlist.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aripio.wishlist.db.ItemDBAdapter;

import android.content.Context;
import android.graphics.Bitmap;

public class WishItem {
	private final Context _ctx;
	private long _id = -1;
	private long _storeId;
	private String _name;
	private String _comments;
	private String _desc;
	private String _date;
	private String _picStr;
	private String _fullsizePicPath;
	private int _priority;
	private Bitmap _thumbnail;
	//private Bitmap _fullsizePhoto;
	private String _store_name;
	//public static final String KEY_PHOTO_URL = "picture";
	private float _price;
	private String _address;

	public WishItem(Context ctx ,String name) {
		this(ctx, name, null, null);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		_date = sdf.format(now);
	}

	public WishItem(Context ctx, String name, String addr) {
		this(ctx, name, null, addr);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		_date = sdf.format(now);
	}

	public WishItem(Context ctx, String name, String created, String addr) {
		_ctx = ctx;
		_name = name;
		_date = created;
		_desc = addr;
	}

	public WishItem(Context ctx, long storeId, String name, String desc, 
			String date, String picStr, String fullsizePicPath, float price, 
			String address, int priority) {
		_fullsizePicPath = fullsizePicPath;
		_price = price;
		_address = address;
		_picStr = picStr;
		_storeId = storeId;
		_ctx = ctx;
		_name = name;
		_desc = desc;
		_date = date;
		_priority = priority;
		//_thumbnail = thumbnail;
	}

	public Bitmap getThumbnail() {
		return _thumbnail;
	}

	public void setThumbnail(Bitmap thumnail) {
		this._thumbnail = thumnail;
	}
	
	public void setStoreName(String storeName){
		this._store_name = storeName;
	}
	
	public String getStoreName(){
		return this._store_name;
	}
	
	public void setPrice(float p){
		this._price = p;
	}
	
	public double getPrice(){
		return this._price;
	}
	
	public void setAddress(String add){
		this._address = add;
	}
	
	public String getAddress(){
		return this._address;
	}

	public String getPriorityStr() {
		return Integer.toString(_priority);
	}
	
	public int getPriority() {
		return _priority;
	}

	public void setPriority(String priority) {
		this._priority = Integer.getInteger(priority);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String date) {
		this._date = date;
	}

	public String getDesc() {
		return _desc;
	}

	public void setDesc(String desc) {
		this._desc = desc;
	}
	
	public String getComments() {
		return _comments;
	}

	public void setComments(String com) {
		this._comments = com;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(_date);
		return "(" + dateString + ") " + _name + " " + _desc;
	}
	
	public boolean save() {
		ItemDBAdapter mItemDBAdapter = new ItemDBAdapter(_ctx);
		mItemDBAdapter.open();
		if(_id == -1) {
			mItemDBAdapter.addItem(_storeId, _name, _desc, _date, _picStr, _fullsizePicPath, 
					_price, _address, _priority);
		}
		else {
			mItemDBAdapter.updateItem(_id, _storeId, _name, _desc, _date, _picStr, _fullsizePicPath, 
					_price, _address, _priority);
		}
		return true;
	}
}
