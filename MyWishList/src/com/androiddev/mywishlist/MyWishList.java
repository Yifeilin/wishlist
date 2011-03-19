package com.androiddev.mywishlist;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;

import com.androiddev.mywithlist.R;

public class MyWishList extends Activity {
    /** Called when the activity is first created. */
	
	/** menu index */
	static final private int ADD_ITEM = Menu.FIRST;
	static final private int REM_ITEM = Menu.FIRST+1;
	static final private int CHK_ITEM = Menu.FIRST+2;
	
	private boolean addingNew = false; 
	private ListView myListView;
	private EditText myEditText;
	
	private LocationManager mLocationManager;
	private Location mLocation;
	
	private MyDBAdapter wishItemDBAdapter; 
	private Cursor wishListCursor;
	private WishItemCursorAdapter wishItemCursorAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myListView = (ListView)findViewById(R.id.myWishListView);
        myEditText = (EditText) findViewById(R.id.myEditText);
        
        wishItemDBAdapter = new MyDBAdapter(this);
        
        wishItemDBAdapter.open();
        
    
         
        
        
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String locationprovider =
		mLocationManager.getBestProvider(criteria,true);
		mLocation = mLocationManager.getLastKnownLocation(locationprovider);
			
		myEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {						
						try {
							Geocoder mGC = new Geocoder(
									getApplicationContext(), Locale.ENGLISH);

							List<Address> addresses = mGC.getFromLocation(
									40.88301, -72.9795, 1);
							StringBuilder addr = new StringBuilder();
							Address currentAddr = null;
							if (addresses != null) {
								currentAddr = addresses.get(0);

								for (int i = 0; i < currentAddr
										.getMaxAddressLineIndex(); i++) {
									addr.append(currentAddr.getAddressLine(i));
									if(i != currentAddr
											.getMaxAddressLineIndex() - 1)
										addr.append("\n");
								}
							}
			
							MyWishItem newItem = new MyWishItem(myEditText
									.getText().toString(), addr.toString());
								
							wishItemDBAdapter.insertWishItem(newItem);
							filldata();
							// myListView.setAdapter(wishItemCursorAdapter);
							wishItemCursorAdapter.notifyDataSetChanged();
							
							myEditText.setText("");
							cancelAdd();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
				return false;
			}
        });
        registerForContextMenu(myListView);
       
        filldata();
		wishItemCursorAdapter.notifyDataSetChanged();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem itemAdd = menu.add(0, ADD_ITEM, Menu.NONE, R.string.add);
		MenuItem itemRem = menu.add(0, REM_ITEM, Menu.NONE, R.string.del);
		MenuItem itemChk = menu.add(0, CHK_ITEM, Menu.NONE, R.string.chk);
		
		itemAdd.setIcon(R.drawable.add);
		itemRem.setIcon(R.drawable.del);
		
		itemAdd.setShortcut('0', 'a');
		itemRem.setShortcut('1', 'd');
		itemChk.setShortcut('2', 'c');
		return true;
	}
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select To Do Item");
		menu.add(0, REM_ITEM, Menu.NONE, R.string.del);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		int idx = myListView.getSelectedItemPosition();
		String removeTitle = getString(addingNew ? R.string.cal : R.string.del);
		MenuItem removeItem = menu.findItem(REM_ITEM);
		removeItem.setTitle(removeTitle);
		removeItem.setVisible(addingNew || idx > -1);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		int index = myListView.getSelectedItemPosition();
		switch (item.getItemId()) {
		case (REM_ITEM): {
			if (addingNew) {
				cancelAdd();
			} else {
				//AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				removeItem(index);
			}
			return true;
		}
		case (ADD_ITEM): {
			addNewItem();
			return true;
		}
		}
		return false;
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch (item.getItemId()) {
		case (REM_ITEM): {
			AdapterView.AdapterContextMenuInfo menuInfo;
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			int index = menuInfo.position;
			removeItem(index);
			return true;
		}
		}
		return false;
	}

	private void cancelAdd() {
		addingNew = false;
		myEditText.setVisibility(View.GONE);
	}

	private void addNewItem() {
		addingNew = true;
		myEditText.setVisibility(View.VISIBLE);
		myEditText.requestFocus();
	}
	
	private void removeItem(long _index) {
		
		wishItemDBAdapter.removeWishItem(_index);
		filldata();
	}
	
	public void filldata(){
		wishListCursor = wishItemDBAdapter.getAllWishItemCursor();
		startManagingCursor(wishListCursor);
		
		
		int resID = R.layout.wishlist_item;
	       
	        
	    String[] from = new String[] {MyDBAdapter.KEY_ITEM, MyDBAdapter.KEY_ADDR, MyDBAdapter.KEY_DATE};
	    int[] to = new int[] {R.id.item, R.id.addr, R.id.date}; 
		wishItemCursorAdapter = new WishItemCursorAdapter(this, resID, wishListCursor, from, to);
		myListView.setAdapter(wishItemCursorAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		wishItemDBAdapter.close();
	}
}