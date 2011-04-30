package com.aripio.f_todolist;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ToDoItemCursorAdapter extends SimpleCursorAdapter{
	private int _layout;
	private Context _context;
	private String[] _from;
	private int[] _to;
	public ToDoItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		_layout = layout;
		_context = context;
		_from = from;
		_to = to;
	}
	
}
