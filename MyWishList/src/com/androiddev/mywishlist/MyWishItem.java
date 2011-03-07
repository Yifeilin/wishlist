package com.androiddev.mywishlist;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Address;

public class MyWishItem {
	String itemName;
	Date created;
	String shopAddr;

	public String getItemName() {
		return itemName;
	}

	public Date getCreated() {
		return created;
	}

	public String getShopAddress(){
		return shopAddr;
	}
	public MyWishItem(String _itemName, String _shopAddr) {
		this(_itemName, _shopAddr, new Date(java.lang.System.currentTimeMillis()));
	}

	public MyWishItem(String _itemName, String _shopAddr, Date _created) {
		itemName = _itemName;
		created = _created;
		shopAddr = _shopAddr;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(created);
		return "(" + dateString + ")" + itemName;
	}
}
