package com.aripio.wishlist;

import java.io.OutputStream;

import com.aripio.wishlist.ItemDBAdapter.ItemsCursor;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

//import com.aripio.wishlist.WishListDataBase.ItemsCursor;
/***
 * WishList.java is responsible for displaying wish items in either list or grid view and
 * providing access to functions of manipulating items such as adding, deleting and editing
 * items, sorting items, searching items, viewing item detailed info. and etc.
 * 
 * Item display is via binding the list view or grid view
 * to the Item table in the database using WishListItemCursorAdapter
 *  
 * switching between list and grid view is realized using viewflipper
 * 
 * sorting items is via "SELECT ... ORDER BY" query to the database
 *  
 */ 
public class WishList extends Activity {
	// Assign a unique ID for each menu item
	static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	static final private int HELP_TODO = Menu.FIRST + 2;
	static final private int DETAIL_TODO = Menu.FIRST + 3;
	static final private int POST_TODO = Menu.FIRST + 4;
	static final private int SORT_TODO = Menu.FIRST + 5;
	static final private int MARK_TODO = Menu.FIRST + 6;

	static final private int DIALOG_MAIN = 0;

	static final private int DETAIL_INFO_ACT = 2;
	static final private int TAKE_PICTURE = 1;
	static final private int POST_ITEM = 3;

	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	private static final String SORT_BY_KEY = "SORT_BY_KEY";

	private ItemsCursor.SortBy SORT_BY = ItemsCursor.SortBy.item_name;
	static final String LOG_TAG = "WishList";
	private String viewOption = "list";

	private ViewFlipper myViewFlipper;
	private ListView myListView;
	private GridView myGridView;
	private EditText mySearchText;
	private Spinner myViewSpinner;

	// private WishListDataBase wishListDB;
	private ItemsCursor wishItemCursor;
	private WishListItemCursorAdapter wishListItemAdapterCursor;

	private DBAdapter myDBAdapter;
	private ItemDBAdapter myItemDBAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//get the resources by their IDs
		myViewFlipper = (ViewFlipper) findViewById(R.id.myFlipper);
		myListView = (ListView) findViewById(R.id.myListView);
		myGridView = (GridView) findViewById(R.id.myGridView);
		mySearchText = (EditText) findViewById(R.id.mySearchText);
		myViewSpinner = (Spinner) findViewById(R.id.myViewSpinner);

		//set the spinner for switching between list and grid views
		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.views_array,
						android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myViewSpinner.setAdapter(adapter);

		myViewSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				// list view is selected
				if (pos == 0) {
					myViewFlipper.setDisplayedChild(0);
					viewOption = "list";

				}
				// grid view is selected
				else if (pos == 1) {
					myViewFlipper.setDisplayedChild(1);
					viewOption = "grid";

				}
				// Toast.makeText(parent.getContext(), "The view is " +
				// parent.getItemAtPosition(pos).toString(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView parent) {
				// Do nothing.
			}
		});

		//called when an item in the list view is clicked.
		//it starts a new activity to display the clicked item's detailed info.
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item in the list view has been clicked
				// and get its _id in database
				long item_id = getDBItemID(position);
				
				// Create an intent to show the item detail.
				// Pass the item_id along so the next activity can use it to
				// retrieve the info. about the item from database
				Intent i = new Intent(WishList.this, WishItemDetail.class);
				i.putExtra("item_id", item_id);
				i.putExtra("position", position);
				startActivity(i);

			}

		});

		//called when an item in the grid view is clicked.
		//it starts a new activity to display the clicked item's detailed info.
		myGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item has been clicked and get its _id in database
				long item_id = getDBItemID(position);
							
				// Create an intent to show the item detail.
				// Pass the item_id along so the next activity can use it to
				// retrieve the info. about the item from database
				Intent i = new Intent(WishList.this, WishItemDetail.class);
				i.putExtra("item_id", item_id);
				i.putExtra("position", position);
				startActivity(i);

			}

		});

		// mySearchText.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(View view, int keyCode, KeyEvent event) {
		// if (event.getAction() == KeyEvent.ACTION_DOWN)
		// if (keyCode == KeyEvent.KEYCODE_ENTER) {
		// Intent searchIntent = new Intent(Intent.ACTION_SEARCH);
		// searchIntent.putExtra("query", mySearchText.getText().toString());
		// startActivity(searchIntent);
		// }
		// return false;
		// }
		// });
		registerForContextMenu(myListView);
		restoreUIState();

		// Open or create the database
		// wishListDB = WishListDataBase.getDBInstance(this);

		//myDBAdapter is effective only when the database is first created
		myDBAdapter = new DBAdapter(this);
		myDBAdapter.open();

		//open the database for operations of Item table
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();

		//display all the items saved in the Item table
		//sorted by item name
		initializeView(ItemsCursor.SortBy.item_name);

	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	/***
	 * get the _id of the item in Item table 
	 * whose position in the list/grid view is pos.
	 */
	public long getDBItemID(int pos) {

		View selected_view = null;
		TextView itemIdTextView = null;
		if (viewOption == "list") {
			selected_view = myListView.getChildAt(pos);
			itemIdTextView = (TextView) selected_view.findViewById(R.id.txtItemID);
		}

		else if (viewOption == "grid") {
			selected_view = myGridView.getChildAt(pos);
			itemIdTextView = (TextView) selected_view.findViewById(R.id.txtItemID_Grid);
		}

		long item_id = Long.parseLong(itemIdTextView.getText().toString());
		return item_id;

	}

	/***
	 * called when sort by time is selected
	 */
	private void onSortByTime() {
		populateItems(ItemsCursor.SortBy.date_time);
	}

	/***
	 * called when sort by name is selected
	 */
	private void onSortByName() {
		populateItems(ItemsCursor.SortBy.item_name);
	}

	/***
	 * called when sort by price is selected
	 */
	private void onSortByPrice() {
		populateItems(ItemsCursor.SortBy.price);
	}

	/***
	 * called when sort by priority is selected
	 */
	private void onSortByPriority() {
		populateItems(ItemsCursor.SortBy.priority);
	}

	/***
	 * initial display of items in both list and grid view,
	 * called when the activity is created 
	 * @param sortBy
	 */
	private void initializeView(ItemsCursor.SortBy sortBy) {
		wishItemCursor = myItemDBAdapter.getItems(sortBy);
		updateListView();
		updateGridView();

	}

	/***
	 * display the items in either list or grid view
	 * sorted by "sortBy" 
	 * @param sortBy
	 */
	private void populateItems(ItemsCursor.SortBy sortBy) {

		// Get all of the rows from the Item table
		// Keep track of the TextViews added in list lstTable
		// wishItemCursor = wishListDB.getItems(sortBy);
		wishItemCursor = myItemDBAdapter.getItems(sortBy);

		updateView();
	}

	/***
	 * update either list view or grid view according view option
	 */
	private void updateView() {

		if (viewOption == "list") {
			// Update the list view
			updateListView();

		}

		else if (viewOption == "grid") {
			// Update the grid view
			updateGridView();

		}

	}

	private void updateGridView() {
		wishItemCursor.requery();
		int resID = R.layout.wishitem_photo;

		String[] from = new String[] { ItemDBAdapter.KEY_ID,
				ItemDBAdapter.KEY_PHOTO_URL };

		int[] to = new int[] { R.id.txtItemID_Grid, R.id.imgPhotoGrid };
		wishListItemAdapterCursor = new WishListItemCursorAdapter(this, resID,
				wishItemCursor, from, to);

		myGridView.setAdapter(wishListItemAdapterCursor);
		wishListItemAdapterCursor.notifyDataSetChanged();

	}

	private void updateListView() {

		wishItemCursor.requery();
		int resID = R.layout.wishitem_single;

		String[] from = new String[] { ItemDBAdapter.KEY_ID,
				ItemDBAdapter.KEY_PHOTO_URL, ItemDBAdapter.KEY_NAME,
				ItemDBAdapter.KEY_DATE_TIME };

		int[] to = new int[] { R.id.txtItemID, R.id.imgPhoto, R.id.txtName,
				R.id.txtDate };
		wishListItemAdapterCursor = new WishListItemCursorAdapter(this, resID,
				wishItemCursor, from, to);

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

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if(keyCode == KeyEvent.KEYCODE_SEARCH){
	// if(mySearchText.getVisibility() == View.VISIBLE)
	// mySearchText.setVisibility(View.INVISIBLE);
	// else
	// {
	// mySearchText.setVisibility(View.VISIBLE);
	// mySearchText.requestFocus();
	// }
	// return true;
	// }
	// return false;
	// }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Selected Wish Item");
		menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
		menu.add(0, DETAIL_TODO, Menu.NONE, R.string.detail);
		menu.add(0, POST_TODO, Menu.NONE, R.string.post);
		menu.add(0, MARK_TODO, Menu.NONE, R.string.mark);
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
			// should provide search service
			onSearchRequested();
			return true;
		}
		case (R.id.menu_add): {
			// let user generate a wish item
			Intent detailInfo = new Intent(this, EditItemInfo.class);
			startActivity(detailInfo);
			return true;
		}
		case (R.id.menu_map): {
			Intent mapIntent = new Intent(this, WishListNewMap.class);
			startActivity(mapIntent);
			return true;
		}
		case (R.id.menu_post): {
			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
			startActivityForResult(snsIntent, POST_ITEM);
			return true;
		}
		case (R.id.menu_sortByTime): {
			onSortByTime();
			return true;

		}

		case (R.id.menu_sortByName): {
			onSortByName();
			return true;

		}

		case (R.id.menu_sortByPrice): {
			onSortByPrice();
			return true;

		}

		case (R.id.menu_sortByPriority): {
			onSortByPriority();
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

		View selected_view = myListView.getChildAt(index);
		TextView itemIdTextView = (TextView) selected_view
		.findViewById(R.id.txtItemID);
		TextView dateTextView = (TextView) selected_view
		.findViewById(R.id.txtDate);
		long item_id = Long.parseLong(itemIdTextView.getText().toString());

		switch (item.getItemId()) {
		case (REMOVE_TODO): {
			// wishListDB.deleteItem(item_id);
			myItemDBAdapter.deleteItem(item_id);
			updateView();
			return true;
		}
		case (DETAIL_TODO): {
			Intent detailInfo = new Intent(this, EditItemInfo.class);
			startActivity(detailInfo);
			return true;
		}
		case (POST_TODO): {
			String date = dateTextView.getText().toString();
			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
			snsIntent.putExtra("wishItem", date);
			startActivityForResult(snsIntent, POST_ITEM);
			return true;
		}

		case (MARK_TODO):{
			Intent mapIntent = new Intent(this, WishListNewMap.class);
			
			//get the latitude and longitude of the clicked item
			double[] dLocation = new double[2];
			dLocation = myItemDBAdapter.getItemLocation(item_id);
			
			mapIntent.putExtra("latitude", dLocation[0]);
			mapIntent.putExtra("longitude", dLocation[1]);
			
			startActivity(mapIntent);
			return true;

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
		// SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		// Read the UI state values, specifying default values.
		// String text = settings.getString(TEXT_ENTRY_KEY, "");
		// Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
		// Restore the UI to the previous state.
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_INDEX_KEY, myListView
				.getSelectedItemPosition());
		outState.putString(SORT_BY_KEY, SORT_BY.name());
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
		// wishListDB.close();
		myDBAdapter.close();
		myItemDBAdapter.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Activity.RESULT_OK) {
			switch (requestCode) {

			case DETAIL_INFO_ACT:
				// should retrieve the info from data and construct a wish item
				// object
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}
}