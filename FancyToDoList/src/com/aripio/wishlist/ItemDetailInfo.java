package com.aripio.wishlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

public class ItemDetailInfo extends Activity {

	private EditText myItemName;
	private EditText myDescription;
	private Button btnSave;
	private Button btnCancel;
	private Button btnDate;
	private Button btnPhoto;
	private RadioButton radioHigh;
	private RadioButton radioMedm;
	private RadioButton radioLow;
	private ImageView imageItem;
	private Date mDate;
	private Bitmap thumbnail;
	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private String priority;
	private WishListDBAdapter wishListDBAdapter;
	private int mYear;
    private int mMonth;
    private int mDay;
	static final private int DATE_DIALOG_ID = 0;
	static final private int TAKE_PICTURE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_info);
		
		myItemName  = (EditText) findViewById(R.id.itemname);
		myDescription  = (EditText) findViewById(R.id.description);
		
		btnSave = (Button) findViewById(R.id.button_save);
		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnDate = (Button) findViewById(R.id.button_date);
		btnPhoto = (Button) findViewById(R.id.button_photo);
		
		radioHigh = (RadioButton) findViewById(R.id.radio_high);
		radioMedm = (RadioButton) findViewById(R.id.radio_medium);
		radioLow = (RadioButton) findViewById(R.id.radio_low);
		
		imageItem = (ImageView) findViewById(R.id.image_photo);
		
		// get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        wishListDBAdapter = new WishListDBAdapter(this);
		// Open or create the database
		wishListDBAdapter.open();
		
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year-1900;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
                String date = sdf.format(new Date(mYear, mMonth, mDay));
                btnDate.setText(date);
            }
        };
    	
    	radioHigh.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				priority = "high";
				radioHigh.setBackgroundColor(R.color.red);
			}   		
    	});
    	
    	radioMedm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				priority = "medium";
				v.setBackgroundColor(R.color.yellow);
			}   		
    	});
    	
    	radioLow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				priority = "Low";
				v.setBackgroundColor(R.color.green);
			}   		
    	});
        
		btnSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				saveWishItem();
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
		WishItem newItem = new WishItem(itemName, itemDesc, date, priority, thumbnail);
		wishListDBAdapter.insertTask(newItem);
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
