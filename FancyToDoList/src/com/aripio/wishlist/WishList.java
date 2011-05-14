package com.aripio.wishlist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.aripio.wishlist.WishListDataBase.ItemsCursor;

public class WishList extends Activity {
	//Assign a unique ID for each menu item
	static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	static final private int HELP_TODO = Menu.FIRST + 2;
	static final private int DETAIL_TODO = Menu.FIRST + 3;
	static final private int POST_TODO = Menu.FIRST + 4;
	
	static final private int DIALOG_MAIN = 0;
	
	static final private int DETAIL_INFO_ACT = 2;
	static final private int TAKE_PICTURE = 1;
	static final private int POST_ITEM = 3;
	
	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	
	static final String LOG_TAG = "WishList";
	
	private ListView myListView;
	
	private WishListDataBase wishListDB;
	private ItemsCursor wishItemCursor;
	private WishListItemCursorAdapter wishListItemAdapterCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
			
		myListView = (ListView) findViewById(R.id.myListView);
		
		myListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
			
		});
		registerForContextMenu(myListView);
		restoreUIState();

		// Open or create the database
		wishListDB = WishListDataBase.getDBInstance(this);
			
		populateItemList(ItemsCursor.SortBy.name);
	}
    
    private void populateItemList(ItemsCursor.SortBy sortBy) {
   
    // Get all of the rows from the database and create the table
    // Keep track of the TextViews added in list lstTable
        wishItemCursor = wishListDB.getItems(sortBy); 	
    	
    // Update the list view
    	updateListView();
    }

	private void updateListView() {
		wishItemCursor.requery();
		int resID = R.layout.wishitem_single;
		String[] from = new String[] {WishListDataBase.KEY_ITEMID, WishListDataBase.KEY_NAME, WishListDataBase.KEY_DESCRIPTION, WishListDataBase.KEY_DATE};
	    int[] to = new int[] {R.id.txtItemID, R.id.txtName, R.id.txtDesc, R.id.txtDate}; 
	    wishListItemAdapterCursor = new WishListItemCursorAdapter(this, resID, wishItemCursor, from, to);
		myListView.setAdapter(wishListItemAdapterCursor);
		wishListItemAdapterCursor.notifyDataSetChanged();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu1, menu);
		return true;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Selected Wish Item");
		menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
		menu.add(0, DETAIL_TODO, Menu.NONE, R.string.detail);
		menu.add(0, POST_TODO, Menu.NONE, R.string.post);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
				
		switch (item.getItemId()) {
		case (R.id.menu_search): {
			//should provide search service
			return true;
		}
		case (R.id.menu_add): {
			// let user generate a wish item
			Intent detailInfo = new Intent(this, EditItemInfo.class);
			startActivity(detailInfo);
			return true;
		}
		case (R.id.menu_map):{
			Intent mapIntent = new Intent(this, WishListMap.class);
			startActivity(mapIntent);
			return true;
		}
		case (R.id.menu_post):{
			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
			startActivityForResult(snsIntent, POST_ITEM);
			return true;
		}
		}
		return false;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int index = menuInfo.position;
		
		//int list_index = myListView.getCheckedItemPosition ();
		//WishListItemCursorAdapter cursor = (WishListItemCursorAdapter) myListView.getItemAtPosition(list_index);
		View selected_view = myListView.getChildAt(index);
		TextView itemIdTextView = (TextView) selected_view.findViewById(R.id.txtItemID);
		TextView dateTextView = (TextView) selected_view.findViewById(R.id.txtDate);
		long item_id = Long.parseLong(itemIdTextView.getText().toString());
		
		switch (item.getItemId()) {
		case (REMOVE_TODO): {		
			wishListDB.deleteItem(item_id);
			//removeItem(index);
			updateListView();
			return true;
			}
		case (DETAIL_TODO):{
			Intent detailInfo = new Intent(this, EditItemInfo.class);
			startActivity(detailInfo);
			}
		case (POST_TODO):{
			String date = dateTextView.getText().toString();
			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
			snsIntent.putExtra("wishItem", date);
			startActivityForResult(snsIntent, POST_ITEM);
			}
		}
		return false;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_MAIN:
			dialog = null;
			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Get the activity preferences object.
		SharedPreferences uiState = getPreferences(0);
		// Get the preferences editor.
		SharedPreferences.Editor editor = uiState.edit();
		// Add the UI state preference values.	
		// Commit the preferences.
		editor.commit();
	}
	
	private void restoreUIState() {
		// Get the activity preferences object.
		//SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		// Read the UI state values, specifying default values.
		//String text = settings.getString(TEXT_ENTRY_KEY, "");
		//Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
		// Restore the UI to the previous state.
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int pos = -1;
		if (savedInstanceState != null)
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
		myListView.setSelection(pos);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Close the database
		wishListDB.close();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Activity.RESULT_OK){
		switch(requestCode){ 
		
		case DETAIL_INFO_ACT:
			//should retrieve the info from data and construct a wish item object
			}
		}
	}	
	@Override
	protected void onResume() {
		super.onResume();
		updateListView();
	}
}