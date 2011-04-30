package com.aripio.f_todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	String task;
	String addr;
	Date created;
	
	public String getTask(){
		return task;
	}
	
	public Date getCreated(){
		return created;
	}
	
	public String getAddr(){
		return addr;
	}
	
	public ToDoItem(String _task){
		this(_task, new Date(java.lang.System.currentTimeMillis()),null);
	}
	
	public ToDoItem(String _task, String _addr) {
		this(_task, new Date(java.lang.System.currentTimeMillis()), _addr);
	}
	
	
	public ToDoItem(String _task, Date _created, String _addr) {
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
