package com.androiddev.mywishlist;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.androiddev.mywithlist.R;

public class WishItemCursorAdapter extends SimpleCursorAdapter{
	private int _layout;
	private Context _context;
	private String[] _from;
	private int[] _to;
	public WishItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		_layout = layout;
		_context = context;
		_from = from;
		_to = to;
	}
	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		
//		Cursor cursor = getCursor();
//		cursor.moveToPosition(position);
//		LayoutInflater inflate = LayoutInflater.from(_context);
//		View v = inflate.inflate(_layout, parent, false);
//		
//		TextView itemView = (TextView) v.findViewById(R.id.item);
//		TextView addrView = (TextView) v.findViewById(R.id.addr);
//		TextView dateView = (TextView) v.findViewById(R.id.date);
//		
//		 
//		if (itemView != null) {
//			int index = cursor.getColumnIndex(_from[0]);
//			String item = cursor.getString(index);
//			itemView.setText(item);
//		}
//		
//		if (addrView != null) {
//			int index = cursor.getColumnIndex(_from[1]);
//			String addr = cursor.getString(index);
//			itemView.setText(addr);
//		}
//		
//		if (dateView != null) {
//			int index = cursor.getColumnIndex(_from[2]);
//			String date = cursor.getString(index);
//			itemView.setText(date);
//		}
//			
//		
//		return v;
//	}

}
