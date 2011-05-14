package com.aripio.wishlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

public class EditItemInfo extends Activity {

	private EditText myItemName;
	private EditText myDescription;
	private Button btnSave;
	private Button btnCancel;
	private Button btnDate;
	private Button btnPhoto;
//	private RadioButton radioHigh;
//	private RadioButton radioMedm;
//	private RadioButton radioLow;
	private ImageView imageItem;
	private Date mDate;
	private Bitmap thumbnail;
	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private String date;
	private WishListDataBase wishListDB; 
//	private WishListDBAdapter wishListDBAdapter;
	private int mYear;
    private int mMonth;
    private int mDay;
    private AlertDialog alert;
	static final private int DATE_DIALOG_ID = 0;
	static final private int TAKE_PICTURE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);
		
		myItemName  = (EditText) findViewById(R.id.itemname);
		myDescription  = (EditText) findViewById(R.id.description);
		
		btnSave = (Button) findViewById(R.id.button_save);
		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnDate = (Button) findViewById(R.id.button_date);
		btnPhoto = (Button) findViewById(R.id.button_photo);
		
//		radioHigh = (RadioButton) findViewById(R.id.radio_high);
//		radioMedm = (RadioButton) findViewById(R.id.radio_medium);
//		radioLow = (RadioButton) findViewById(R.id.radio_low);
//		radioLow.setChecked(true);
		
		imageItem = (ImageView) findViewById(R.id.image_photo);
		
		// get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
       // wishListDBAdapter = new WishListDBAdapter(this);
		// Open or create the database
		//wishListDBAdapter.open();
        wishListDB = WishListDataBase.getDBInstance(this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   EditItemInfo.this.finish();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		alert = builder.create();
		
		
		
        myItemName.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER){
						myItemName.setSelected(false);				
					}
        	return false;
			}
        });
        
        myDescription.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER){
						myDescription.setSelected(false);				
					}
        	return false;
			}
        });
        
        
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year-1900;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
                date = sdf.format(new Date(mYear, mMonth, mDay));
                btnDate.setText(date);
            }
        };
        
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				saveWishItem();
			}					
		});
		
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {			
				alert.show();
			}					
		});
		
		btnDate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);			
			}			
		});
		
		btnPhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getThumbailPicture();
			}
			
		});
	}
	/***
	 * Save user input as a wish item
	 */
	private void saveWishItem() {
		String itemName = myItemName.getText().toString();
		String itemDesc = myDescription.getText().toString();
		mDate = new Date(mYear, mMonth, mDay);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String date = sdf.format(mDate);
		wishListDB.addItem(itemName, itemDesc, date, -1, thumbnail);
		finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){ 
			case TAKE_PICTURE: 
				// Check if the result includes a thumbnail Bitmap
				if (data != null) {
					if (data.hasExtra("data")) {
						thumbnail = data.getParcelableExtra("data");
						imageItem.setImageBitmap(thumbnail);
					}
				}
				break;
			}
		}
	}
	
	private void getThumbailPicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE);
	}
}