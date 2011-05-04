package com.aripio.wishlist;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

public class ItemDetailInfo extends Activity {

	private EditText myItemName;
	private EditText myDescription;
	private Button btnSave;
	private Button btnCancel;
	private Button btnDate;
	private RadioButton radioHigh;
	private RadioButton radioMedm;
	private RadioButton radioLow;
	private Date mDate;
	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private String priority;
	private OnClickListener radioListener;
	
	private int mYear;
    private int mMonth;
    private int mDay;
	static final private int DATE_DIALOG_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_info);
		
		myItemName  = (EditText) findViewById(R.id.itemname);
		myDescription  = (EditText) findViewById(R.id.description);
		
		btnSave = (Button) findViewById(R.id.button_save);
		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnDate = (Button) findViewById(R.id.button_date);
		
		radioHigh = (RadioButton) findViewById(R.id.radio_high);
		radioMedm = (RadioButton) findViewById(R.id.radio_medium);
		radioLow = (RadioButton) findViewById(R.id.radio_low);
		
//		// get the current date
//        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//		
//        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//
//            public void onDateSet(DatePicker view, int year, 
//                                  int monthOfYear, int dayOfMonth) {
//                mYear = year;
//                mMonth = monthOfYear;
//                mDay = dayOfMonth;
//            }
//        };
//        
//        radioListener = new OnClickListener() {
//    	    public void onClick(View v) {
//    	        // Perform action on clicks
//    	        RadioButton rb = (RadioButton) v;
//    	        rb.setChecked(true);
//    	    }
//    	};
//    	
//    	radioHigh.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				priority = "high";
//				v.setBackgroundColor(R.color.red);
//			}   		
//    	});
//    	
//    	radioMedm.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				priority = "medium";
//				v.setBackgroundColor(R.color.yellow);
//			}   		
//    	});
//    	
//    	radioLow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				priority = "Low";
//				v.setBackgroundColor(R.color.green);
//			}   		
//    	});
//        
//		btnSave.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				saveWishItem();
//			}					
//		});
//		
//		btnDate.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				showDialog(DATE_DIALOG_ID);			
//			}			
//		});	
	}
	/***
	 * Save user input as a wish item
	 */
	private void saveWishItem() {
		String itemName = myItemName.getText().toString();
		String itemDesc = myDescription.getText().toString();
		mDate = new Date(mYear, mMonth, mDay);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    mYear, mMonth, mDay);
	    }
	    return null;
	}	
}
