package com.aripio.wishlist.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;

public class WishItem {
	private String name;
	private String comments;
	private String desc;
	private String date;
	private int priority;
	private Bitmap thumbnail;
	private Bitmap fullsizePhoto;
	private String store_name;
	//public static final String KEY_PHOTO_URL = "picture";
	private double price;
	private String address;

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumnail) {
		this.thumbnail = thumnail;
	}
	
	public void setStoreName(String storeName){
		this.store_name = storeName;
	}
	
	public String getStoreName(){
		return this.store_name;
	}
	
	public void setPrice(double p){
		this.price = p;
	}
	
	public double getPrice(){
		return this.price;
	}
	
	public void setAddress(String add){
		this.address = add;
	}
	
	public String getAddress(){
		return this.address;
	}

	public String getPriorityStr() {
		return Integer.toString(priority);
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = Integer.getInteger(priority);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public WishItem(String _task) {
		this(_task, null, null);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		date = sdf.format(now);
	}

	public WishItem(String _task, String _addr) {
		this(_task, null, _addr);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		date = sdf.format(now);
	}

	public WishItem(String _task, String _created, String _addr) {
		name = _task;
		date = _created;
		desc = _addr;
	}

	public WishItem(String name, String desc, String date, int priority,
			Bitmap thumbnail) {
		this.name = name;
		this.desc = desc;
		this.date = date;
		this.priority = priority;
		this.thumbnail = thumbnail;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(date);
		return "(" + dateString + ") " + name + " " + desc;
	}
	
	public boolean save() {
		return true;
		
	}
}
