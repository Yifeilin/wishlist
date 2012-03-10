package com.aripio.wishlist.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.aripio.wishlist.R;
import com.aripio.wishlist.barscanner.IntentIntegrator;
import com.aripio.wishlist.barscanner.IntentResult;
import com.aripio.wishlist.db.DBAdapter;
import com.aripio.wishlist.db.ItemDBAdapter;
import com.aripio.wishlist.db.ItemDBAdapter.ItemsCursor;
import com.aripio.wishlist.util.WishListItemCursorAdapter;

/***
 * WishList.java is responsible for displaying wish items in either list or grid
 * view and providing access to functions of manipulating items such as adding,
 * deleting and editing items, sorting items, searching items, viewing item
 * detailed info. and etc.
 * 
 * Item display is via binding the list view or grid view to the Item table in
 * the database using WishListItemCursorAdapter
 * 
 * switching between list and grid view is realized using viewflipper
 * 
 * sorting items is via "SELECT ... ORDER BY" query to the database
 * 
 */
public class WishList extends Activity {
	// Assign a unique ID for each menu item
	// static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	// static final private int HELP_TODO = Menu.FIRST + 2;
	static final private int DETAIL_TODO = Menu.FIRST + 3;
	static final private int POST_TODO = Menu.FIRST + 4;
	// static final private int SORT_TODO = Menu.FIRST + 5;
	static final private int MARK_TODO = Menu.FIRST + 6;

	static final private int DIALOG_MAIN = 0;

	static final private int DETAIL_INFO_ACT = 2;
	// static final private int TAKE_PICTURE = 1;
	static final private int POST_ITEM = 3;

	// private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	// private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	private static final String SORT_BY_KEY = "SORT_BY_KEY";

	// other view mode can be extended in the future
	private static final int LIST_MODE = 1;
	private static final int GRID_MODE = 2;

	private ItemsCursor.SortBy SORT_BY = ItemsCursor.SortBy.item_name;
	private String nameQuery = null;
	static final String LOG_TAG = "WishList";
	private String viewOption = "list";

	private ViewFlipper myViewFlipper;
	private ListView myListView;
	private GridView myGridView;
	// private EditText mySearchText;
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

		// Get the saved UI preferences in onPause, the default option is list
		// SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		// viewOption = preferences.getString("viewOption", "list");

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();

		setContentView(R.layout.main);

		// get the resources by their IDs
		myViewFlipper = (ViewFlipper) findViewById(R.id.myFlipper);
		myListView = (ListView) findViewById(R.id.myListView);
		myGridView = (GridView) findViewById(R.id.myGridView);
		// mySearchText = (EditText) findViewById(R.id.mySearchText);
		myViewSpinner = (Spinner) findViewById(R.id.myViewSpinner);

		// Listener for myListView.
		// When clicked, it starts a new activity to display the clicked item's
		// detailed info.
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item in the list view has been clicked
				// and get its _id in database
				long item_id = getDBItemID(position, LIST_MODE);

				// Create an intent to show the item detail.
				// Pass the item_id along so the next activity can use it to
				// retrieve the info. about the item from database
				Intent i = new Intent(WishList.this, WishItemDetail.class);
				i.putExtra("item_id", item_id);
				i.putExtra("position", position);
				startActivity(i);
			}
		});

		// Listener for myGridView
		// When clicked, it starts a new activity to display the clicked item's
		// detailed info.
		myGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item has been clicked and get its _id in database
				long item_id = getDBItemID(position, GRID_MODE);

				// Create an intent to show the item detail.
				// Pass the item_id along so the next activity can use it to
				// retrieve the info. about the item from database
				Intent i = new Intent(WishList.this, WishItemDetail.class);
				i.putExtra("item_id", item_id);
				i.putExtra("position", position);
				startActivity(i);

			}

		});

		// register context menu for both listview and gridview
		registerForContextMenu(myListView);
		registerForContextMenu(myGridView);

		restoreUIState();

		// Open or create the database
		// wishListDB = WishListDataBase.getDBInstance(this);

		// myDBAdapter is effective only when the database is first created
		myDBAdapter = new DBAdapter(this);
		myDBAdapter.open();

		// open the database for operations of Item table
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();

		// check if the activity is started from search
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// activity is started from search, get the search query and
			// displayed the searched items
			nameQuery = intent.getStringExtra(SearchManager.QUERY);

			// displaySearchItem(query, SORT_BY);
			populateItems(nameQuery, SORT_BY);
		} else {
			// activity is not started from search
			// display all the items saved in the Item table
			// sorted by item name
			initializeView(SORT_BY);

		}

		// set the spinner for switching between list and grid views
		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.views_array,
						android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myViewSpinner.setAdapter(adapter);

		// set the default spinner option
		if (viewOption == "list") {
			myViewSpinner.setSelection(0);
		} else {
			myViewSpinner.setSelection(1);
		}

		myViewSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				// list view is selected
				if (pos == 0) {
					// Recall populate here is inefficient
					viewOption = "list";
					populateItems(nameQuery, SORT_BY);
					myViewFlipper.setDisplayedChild(0);

				}
				// grid view is selected
				else if (pos == 1) {
					viewOption = "grid";
					populateItems(nameQuery, SORT_BY);
					myViewFlipper.setDisplayedChild(1);

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

	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	/**
	 * Get the ID of the item in Item table whose position in the list/grid view
	 * is position.
	 * 
	 * @param position
	 *            : position of item in the list
	 * @param viewMode
	 *            : the current view mode
	 * @return the Item ID
	 */
	public long getDBItemID(int position, int viewMode) {

		View selectedView = null;
		TextView itemIdTextView = null;
		// Note that txtItemID is not visible in the UI but can be retrieved
		switch (viewMode) {
		case LIST_MODE:
			selectedView = myListView.getChildAt(position);
			itemIdTextView = (TextView) selectedView
					.findViewById(R.id.txtItemID);
			break;
		case GRID_MODE:
			selectedView = myGridView.getChildAt(position);
			itemIdTextView = (TextView) selectedView
					.findViewById(R.id.txtItemID_Grid);
			break;
		default:
			Log.e(LOG_TAG, "View mode not specified correctly.");
			return -1;
		}
		long item_id = Long.parseLong(itemIdTextView.getText().toString());
		return item_id;
	}
	
	/***
	 * called when sort is selected
	 * @param sortBy
	 *            : enum defined in ItemsCursor which determines the sort order 
	 *              of the selected rows in db
	 */
	private void onSort(ItemsCursor.SortBy sortBy) {
		populateItems(null, sortBy);
	}

	/***
	 * initial display of items in both list and grid view, called when the
	 * activity is created
	 * 
	 * @param sortBy
	 */
	private void initializeView(ItemsCursor.SortBy sortBy) {
		wishItemCursor = myItemDBAdapter.getItems(sortBy);

		if (viewOption == "list") {
			updateListView();
			myViewFlipper.setDisplayedChild(0);
		}

		else {
			updateGridView();
			myViewFlipper.setDisplayedChild(1);
		}

	}

	/***
	 * display the items matching the search in both list and grid view, called
	 * when the activity is created through search quest
	 * 
	 * @param sortBy
	 * @param itemName
	 *            the name to search
	 */

	// private void displaySearchItem(String itemName, ItemsCursor.SortBy
	// sortBy){
	// wishItemCursor = myItemDBAdapter.searchItems(itemName);
	// // updateListView();
	// // updateGridView();
	// updateView();
	//
	// }

	/***
	 * display the items in either list or grid view sorted by "sortBy"
	 * 
	 * @param sortBy
	 * @param searchName
	 *            : the item name to match, null for all items
	 */
	private void populateItems(String searchName, ItemsCursor.SortBy sortBy) {

		if (searchName == null) {
			// Get all of the rows from the Item table
			// Keep track of the TextViews added in list lstTable
			// wishItemCursor = wishListDB.getItems(sortBy);
			wishItemCursor = myItemDBAdapter.getItems(sortBy);

		} else {
			wishItemCursor = myItemDBAdapter.searchItems(searchName, sortBy);
		}

		updateView();
	}

	/***
	 * update either list view or grid view according view option
	 */
	private void updateView() {

		if (viewOption == "list") {
			// Update the list view
			updateListView();
			myViewFlipper.setDisplayedChild(0);

		}

		else if (viewOption == "grid") {
			// Update the grid view
			updateGridView();
			myViewFlipper.setDisplayedChild(1);

		}
	}

	private void updateGridView() {
		if (wishItemCursor != null) {
			wishItemCursor.requery();
			int resID = R.layout.wishitem_photo;

			String[] from = new String[] { ItemDBAdapter.KEY_ID,
					ItemDBAdapter.KEY_PHOTO_URL };

			int[] to = new int[] { R.id.txtItemID_Grid, R.id.imgPhotoGrid };
			wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
					resID, wishItemCursor, from, to);

			myGridView.setAdapter(wishListItemAdapterCursor);
			wishListItemAdapterCursor.notifyDataSetChanged();
		} else {
			// give message about empty cursor
		}

	}

	private void updateListView() {

		if (wishItemCursor != null) {
			wishItemCursor.requery();
			int resID = R.layout.wishitem_single;

			String[] from = new String[] { ItemDBAdapter.KEY_ID,
					ItemDBAdapter.KEY_PHOTO_URL, ItemDBAdapter.KEY_NAME,
					ItemDBAdapter.KEY_DATE_TIME };

			int[] to = new int[] { R.id.txtItemID, R.id.imgPhoto, R.id.txtName,
					R.id.txtDate };
			wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
					resID, wishItemCursor, from, to);

			myListView.setAdapter(wishListItemAdapterCursor);
			wishListItemAdapterCursor.notifyDataSetChanged();

		}

		else {
			// give message about empty cursor
		}

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
			Intent mapIntent = new Intent(this, WishListMap.class);
			startActivity(mapIntent);
			return true;
		}
		case (R.id.menu_post): {
			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
			startActivityForResult(snsIntent, POST_ITEM);
			return true;
		}

		case (R.id.menu_scan): {
			IntentIntegrator.initiateScan(this);
			return true;
		}

		case (R.id.menu_sortByTime): {
			SORT_BY = ItemsCursor.SortBy.date_time;
			onSort(SORT_BY);
			return true;

		}

		case (R.id.menu_sortByName): {
			SORT_BY = ItemsCursor.SortBy.item_name;
			onSort(SORT_BY);
			return true;
		}

		case (R.id.menu_sortByPrice): {
			SORT_BY = ItemsCursor.SortBy.price;
			onSort(SORT_BY);
			return true;
		}

		case (R.id.menu_sortByPriority): {
			SORT_BY = ItemsCursor.SortBy.priority;
			onSort(SORT_BY);
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

		case (MARK_TODO): {
			Intent mapIntent = new Intent(this, WishListMap.class);

			// get the latitude and longitude of the clicked item
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
		SharedPreferences uiState = getPreferences(MODE_PRIVATE);
		// Get the preferences editor.
		SharedPreferences.Editor editor = uiState.edit();
		// Add the UI state preference values.
		editor.putString("viewOption", viewOption); // value to store
		// Commit the preferences.
		editor.commit();
	}

	private void restoreUIState() {
		// Get the activity preferences object.
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		// Read the UI state values, specifying default values.
		viewOption = settings.getString("viewOption", "list");
		// Restore the UI to the previous state.
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// save the position of the currently selected item in the list
		savedInstanceState.putInt(SELECTED_INDEX_KEY,
				myListView.getSelectedItemPosition());
		// save the current sort criterion
		savedInstanceState.putString(SORT_BY_KEY, SORT_BY.name());
		//
		// int c = 0;
		// int k = 0;

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore the current selected item in the list
		int pos = -1;
		if (savedInstanceState != null)
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
		myListView.setSelection(pos);

		// restore the sort order
		// SORT_BY =
		// ItemsCursor.SortBy.valueOf(savedInstanceState.getString(SORT_BY_KEY));
		//
		// int a = 0;
		// int b = 0;

		// restore the view type(list/grid)

		// restore the search results

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
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			Context context = getApplicationContext();
			CharSequence text = scanResult.getContents();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		if (requestCode == Activity.RESULT_OK) {
			switch (requestCode) {

			case DETAIL_INFO_ACT:
				// should retrieve the info from data and construct a wish item
				// object
				break;			
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}
}