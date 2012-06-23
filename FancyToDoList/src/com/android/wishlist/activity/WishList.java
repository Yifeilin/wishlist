package com.android.wishlist.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
//import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

//import com.android.wishlist.activity;
import com.android.wishlist.R;
import com.android.wishlist.db.DBAdapter;
import com.android.wishlist.db.ItemDBAdapter;
import com.android.wishlist.db.ItemDBAdapter.ItemsCursor;
import com.android.wishlist.util.WishListItemCursorAdapter;

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
	static final private int DIALOG_MAIN = 0;
	static final private int DIALOG_VIEW = 1;

//	static final private int DETAIL_INFO_ACT = 2;
	// static final private int TAKE_PICTURE = 1;
//	static final private int POST_ITEM = 3;

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
	private static final int EDIT_ITEM = 0;
	private static final int ADD_ITEM = 1;
	private String viewOption = "list";

	private ViewFlipper myViewFlipper;
	private ListView myListView;
	private GridView myGridView;
	// private EditText mySearchText;
//	private Spinner myViewSpinner;
	private ImageButton backImageButton;
	private ImageButton viewImageButton;
	private ImageButton searchImageButton;

	// private WishListDataBase wishListDB;
	private ItemsCursor wishItemCursor;
	private WishListItemCursorAdapter wishListItemAdapterCursor;

	private DBAdapter myDBAdapter;
	private ItemDBAdapter myItemDBAdapter;
//	private LocationDBAdapter myLocationDBAdapter;
	
	private long selectedItem_id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			viewOption = savedInstanceState.getString("viewOption");
		}

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

		// Listener for myListView.
		// When clicked, it starts a new activity to display the clicked item's
		// detailed info.
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item in the list view has been clicked
				// and get its _id in database
				long item_id = getDBItemID(v, LIST_MODE);
				if (item_id == -1) {
//					Log.d(LOG_TAG, "item id == -1");
					return;
				}

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
				long item_id = getDBItemID(v, GRID_MODE);
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
		
		// open the db for operations of location table
//		myLocationDBAdapter = new LocationDBAdapter(this);
//		myLocationDBAdapter.open();

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
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter
//				.createFromResource(this, R.array.views_array,
//						android.R.layout.simple_spinner_item);

//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		myViewSpinner.setAdapter(adapter);

		backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
		backImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				//close this activity
 				finish();
 				
 				//start the WishList activity and move the focus to the newly added item
// 				Intent home = new Intent(WishList.this, DashBoard.class);
// 				startActivity(home);
 				//onSearchRequested();
 				
 			}
 
		});		

		searchImageButton = (ImageButton) findViewById(R.id.imageButton_search);
		searchImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				onSearchRequested();
 			}
 
		});		
		
		viewImageButton = (ImageButton) findViewById(R.id.imageButton_view);
		viewImageButton.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View view) {
 				showDialog(DIALOG_VIEW);
 			}
 
		});
//		// set the default spinner option
//		if (viewOption == "list") {
//			myViewSpinner.setSelection(0);
//		} else {
//			myViewSpinner.setSelection(1);
//		}
//
//		myViewSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int pos, long id) {
//
//				// list view is selected
//				if (pos == 0) {
//					// Recall populate here is inefficient
//					viewOption = "list";
//					populateItems(nameQuery, SORT_BY);
//					myViewFlipper.setDisplayedChild(0);
//
//				}
//				// grid view is selected
//				else if (pos == 1) {
//					viewOption = "grid";
//					populateItems(nameQuery, SORT_BY);
//					myViewFlipper.setDisplayedChild(1);
//
//				}
//				// Toast.makeText(parent.getContext(), "The view is " +
//				// parent.getItemAtPosition(pos).toString(),
//				// Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView parent) {
//				// Do nothing.
//			}
//		});

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
	public long getDBItemID(View v, int viewMode) {
		TextView itemIdTextView = null;
		// Note that txtItemID is not visible in the UI but can be retrieved
		switch (viewMode) {
		case LIST_MODE:
			itemIdTextView = (TextView) v.findViewById(R.id.txtItemID);
			break;
		case GRID_MODE:
			itemIdTextView = (TextView) v.findViewById(R.id.txtItemID_Grid);
			break;
		default:
//			Log.d(LOG_TAG, "View mode not specified correctly.");
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

		if (viewOption.equals("list")) {
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

		if (viewOption.equals("list")) {
			// Update the list view
			updateListView();
			myViewFlipper.setDisplayedChild(0);

		}

		else if (viewOption.equals("grid")) {
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

			String[] from = new String[] { 
					ItemDBAdapter.KEY_ID,
					ItemDBAdapter.KEY_PHOTO_URL,
					ItemDBAdapter.KEY_NAME,
					ItemDBAdapter.KEY_PRICE,
					ItemDBAdapter.KEY_STORENAME,
					ItemDBAdapter.KEY_ADDRESS};
			
			int[] to = new int[] {
					R.id.txtItemID, 
					R.id.imgPhoto,
					R.id.txtName,
					R.id.txtPrice,
					R.id.txtStore, 
					R.id.txtAddress};
			
			wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
					resID, wishItemCursor, from, to);

			myListView.setAdapter(wishListItemAdapterCursor);
			wishListItemAdapterCursor.notifyDataSetChanged();
		}

		else {
			// give message about empty cursor
		}

	}

	private void deleteItem(long item_id){
		selectedItem_id = item_id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete the wish?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						myItemDBAdapter.deleteItem(selectedItem_id);
						updateView();
					}
				});
		builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//return false;
					}
				});

		AlertDialog alert;
		alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
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
		//menu.setHeaderTitle("Selected Wish Item");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_item_context, menu);
		return;
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
//		case (R.id.menu_search): {
//			// should provide search service
//			onSearchRequested();
//			return true;
//		}
		case (R.id.menu_add): {
			// let user generate a wish item
			Intent editItem = new Intent(this, EditItemInfo.class);
			startActivityForResult(editItem, ADD_ITEM);
			return true;
		}
		case (R.id.menu_map): {
			Intent mapIntent = new Intent(this, WishListMap.class);
			mapIntent.putExtra("type", "markAll");			
			startActivity(mapIntent);
			return true;
		}
//		case (R.id.menu_post): {
//			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
//			startActivityForResult(snsIntent, POST_ITEM);
//			return true;
//		}

//		case (R.id.menu_scan): {
//			IntentIntegrator.initiateScan(this);
//			return true;
//		}

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

//		case (R.id.menu_sortByPriority): {
//			SORT_BY = ItemsCursor.SortBy.priority;
//			onSort(SORT_BY);
//			return true;
//		}

		}
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		//get the position of the item in the list
		//this is the position of the items among all items including the invisible ones
		int pos = menuInfo.position;
		View selected_view = null;
		long item_id = -1;
		if(viewOption.equals("list")){
			//get the position of the items among the visible items
			pos = pos - myListView.getFirstVisiblePosition();
			selected_view = myListView.getChildAt(pos);
			if (selected_view != null) {
				item_id = getDBItemID(selected_view, LIST_MODE);
			}
			else {
//				Log.d(WishList.LOG_TAG, "selected_view is null");
				return false;
			}
		}
		else if(viewOption.equals("grid")){
			pos = pos - myGridView.getFirstVisiblePosition();
			selected_view = myGridView.getChildAt(pos);
			if (selected_view != null) {
				item_id = getDBItemID(selected_view, GRID_MODE);
			}
			else {
//				Log.d(WishList.LOG_TAG, "selected_view is null");
				return false;
			}
		}
		else if(selected_view == null){
//			Log.d(WishList.LOG_TAG, "selected view is null");
			return false;
		}
		
		switch (item.getItemId()) {
		case (R.id.REMOVE_TODO): {
			// wishListDB.deleteItem(item_id);
			deleteItem(item_id);
			return true;
		}
		case (R.id.EDIT_TODO): {
			Intent editItem = new Intent(this, EditItemInfo.class);
			editItem.putExtra("item_id", item_id);
			startActivityForResult(editItem, EDIT_ITEM);
			return true;
		}
//		case (R.id.POST_TODO): {
//			String date = dateTextView.getText().toString();
//			Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
//			snsIntent.putExtra("wishItem", date);
//			startActivityForResult(snsIntent, POST_ITEM);
//			return true;
//		}
		case (R.id.MARK_TODO): {
//			String address = myItemDBAdapter.getItemAddress(item_id);
//			if (address.equals("unknown")||address.equals("")){
//				Toast toast = Toast.makeText(this, "location unknown", Toast.LENGTH_SHORT);
//				toast.show();
//			}
//			else{
				
				// get the latitude and longitude of the clicked item
				double[] dLocation = new double[2];
				dLocation = myItemDBAdapter.getItemLocation(item_id);
				
				if (dLocation[0] == Double.MIN_VALUE && dLocation[1] == Double.MIN_VALUE) {
					Toast toast = Toast.makeText(this, "location unknown", Toast.LENGTH_SHORT);
					toast.show();
				}
				else {
					Intent mapIntent = new Intent(this, WishListMap.class);
					mapIntent.putExtra("type", "markOne");
					mapIntent.putExtra("latitude", dLocation[0]);
					mapIntent.putExtra("longitude", dLocation[1]);
					startActivity(mapIntent);
				}
//			}
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
		case DIALOG_VIEW:
			final CharSequence[] items = {"List", "Grid"};

			AlertDialog.Builder builder = new AlertDialog.Builder(WishList.this);
			builder.setTitle("Show wishes in");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if(items[item].equals("List")){
						// Recall populate here is inefficient
						viewOption = "list";
						populateItems(nameQuery, SORT_BY);
						myViewFlipper.setDisplayedChild(0);
					}

					else{
						viewOption = "grid";
						populateItems(nameQuery, SORT_BY);
						myViewFlipper.setDisplayedChild(1);
					}
					//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				}
			});
			dialog = builder.create();
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
		saveUIState();
	}
	
	private void saveUIState() {
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
		SharedPreferences settings = this.getPreferences(MODE_PRIVATE);
		// Read the UI state values, specifying default values.
		viewOption = settings.getString("viewOption", "list");
		// Restore the UI to the previous state.
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		
		// save the position of the currently selected item in the list
		//savedInstanceState.putString("viewOption", viewOption);
		saveUIState();
		
		if (viewOption.equals("list")) {
		savedInstanceState.putInt(SELECTED_INDEX_KEY,
				myListView.getSelectedItemPosition());
		}
		else {
		savedInstanceState.putInt(SELECTED_INDEX_KEY,
				myGridView.getSelectedItemPosition());
		}
		// save the current sort criterion
		savedInstanceState.putString(SORT_BY_KEY, SORT_BY.name());
		
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore the current selected item in the list
		int pos = -1;
		restoreUIState();
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY)) {
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
			}
		//	viewOption = savedInstanceState.getString("viewOption");
		}
		
//		if (viewOption.equals("list")) {
			myListView.setSelection(pos);
//		}
//		else {
			myGridView.setSelection(pos);		
//		}
		
		updateView();
		
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
//		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//		if (scanResult != null) {
//			Context context = getApplicationContext();
//			CharSequence text = scanResult.getContents();
//			int duration = Toast.LENGTH_SHORT;
//			Toast toast = Toast.makeText(context, text, duration);
//			toast.show();
//		}

		switch (requestCode) {
		case EDIT_ITEM: {
			if (resultCode == Activity.RESULT_OK) {

			}
			else {

			}
			break;
		}
		case ADD_ITEM: {
			if (resultCode == Activity.RESULT_OK) {

			}
			else {

			}
			break;

		}
		}
//		if (requestCode == Activity.RESULT_OK) {
//			switch (requestCode) {
//
//			case DETAIL_INFO_ACT:
//				// should retrieve the info from data and construct a wish item
//				// object
//				break;			
//			}
//		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		restoreUIState();
		updateView();
	}
}