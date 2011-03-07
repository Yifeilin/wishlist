package com.androiddev.mywishlist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.androiddev.mywithlist.R;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WishListItemAdapter extends ArrayAdapter<MyWishItem>{
	
	int resource;
	
	public WishListItemAdapter(Context _context, int _resource, List<MyWishItem> _items){
		super(_context, _resource, _items);
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout wishListView;

		MyWishItem item = getItem(position);
		String itemName = item.getItemName();
		Date createdDate = item.getCreated();
		String shopAddr = item.getShopAddress();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(createdDate);
		
		if (convertView == null) {
			wishListView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resource, wishListView, true);
		} else {
			wishListView = (LinearLayout) convertView;
		}
		TextView dateView = (TextView) wishListView.findViewById(R.id.rowDate);
		TextView taskView = (TextView) wishListView.findViewById(R.id.row);
		TextView addrView = (TextView) wishListView.findViewById(R.id.addr);
		dateView.setText(dateString);
		taskView.setText(itemName);
		addrView.setText(shopAddr);
		return wishListView;
	}
}
