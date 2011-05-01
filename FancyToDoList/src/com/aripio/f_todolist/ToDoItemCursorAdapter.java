package com.aripio.f_todolist;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ToDoItemCursorAdapter extends SimpleCursorAdapter{	
	public ToDoItemCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}	
}
