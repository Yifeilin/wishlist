package com.aripio.wishlist;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;

public class WishItem {
	private String name;
	private String desc;
	private String date;
	private String priority;
	private Bitmap thumbnail;
	
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumnail) {
		this.thumbnail = thumnail;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate(){
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
	
	public WishItem(String _task){
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
	
	public WishItem(String name, String desc, String date, String priority, Bitmap thumbnail){
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
}
