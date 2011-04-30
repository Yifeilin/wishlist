package com.aripio.f_todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	String task;
	String addr;
	String created;
	
	public String getTask(){
		return task;
	}
	
	public String getCreated(){
		return created;
	}
	
	public String getAddr(){
		return addr;
	}
	
	public ToDoItem(String _task){
		this(_task, null, null);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		created = sdf.format(now);
		
		
	}
	
	public ToDoItem(String _task, String _addr) {
		this(_task, null, _addr);
		Date now = new Date(java.lang.System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		created = sdf.format(now);
	}
	
	
	public ToDoItem(String _task, String _created, String _addr) {
		task = _task;
		created = _created;
		addr = _addr;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(created);
		return "(" + dateString + ") " + task + " " + addr;
	}
}
