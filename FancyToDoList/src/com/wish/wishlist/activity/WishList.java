package com.wish.wishlist.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.wish.wishlist.R;
import com.wish.wishlist.db.DBAdapter;
import com.wish.wishlist.db.ItemDBAdapter;
import com.wish.wishlist.db.ItemDBAdapter.ItemsCursor;
import com.wish.wishlist.model.WishItem;
import com.wish.wishlist.model.WishItemManager;
import com.wish.wishlist.util.WishListItemCursorAdapter;
import com.wish.wishlist.util.social.ShareHelper;

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
@SuppressLint("NewApi")
public class WishList extends Activity {
	static final private int DIALOG_MAIN = 0;
	static final private int DIALOG_VIEW = 1;
	static final private int DIALOG_FILTER = 2;
	static final private int POST_ITEM = 3;

	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	private static final String SORT_BY_KEY = "SORT_BY_KEY";

	// other view mode can be extended in the future
	private static final int LIST_MODE = 1;
	private static final int GRID_MODE = 2;

	private ItemsCursor.SortBy SORT_BY = ItemsCursor.SortBy.item_name;
	private Map<String,String> _where = new HashMap<String, String>();
	private String nameQuery = null;
	public static final String LOG_TAG = "WishList";
	private static final int EDIT_ITEM = 0;
	private static final int ADD_ITEM = 1;
	private String _viewOption = "list";

	private ViewFlipper myViewFlipper;
	private ListView myListView;
	private GridView myGridView;
	private Button _addNew;
	// private EditText mySearchText;
//	private Spinner myViewSpinner;
	private ImageButton backImageButton;
	private ImageButton viewImageButton;
	private ImageButton searchImageButton;

	private ItemsCursor _wishItemCursor;
	private WishListItemCursorAdapter wishListItemAdapterCursor;

	private DBAdapter myDBAdapter;
	private ItemDBAdapter myItemDBAdapter;
	
	private long _selectedItem_id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
		_viewOption = pref.getString("viewOption", "list");

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();

		setContentView(R.layout.main);

		setUpActionBar();

		// get the resources by their IDs
		myViewFlipper = (ViewFlipper) findViewById(R.id.myFlipper);
		myListView = (ListView) findViewById(R.id.myListView);
		myGridView = (GridView) findViewById(R.id.myGridView);
		_addNew = (Button) findViewById(R.id.addNewWishButton);
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

		_addNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent editItem = new Intent(WishList.this, EditItemInfo.class);
				startActivityForResult(editItem, ADD_ITEM);
			}
		});

		// register context menu for both listview and gridview
		registerForContextMenu(myListView);
		registerForContextMenu(myGridView);

		// open the database for operations of Item table
		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();

		// check if the activity is started from search
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// activity is started from search, get the search query and
			// displayed the searched items
			nameQuery = intent.getStringExtra(SearchManager.QUERY);

			// displaySearchItem(query, SORT_BY);
			populateItems(nameQuery, SORT_BY, _where);
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

//		// set the default spinner option
//		if (_viewOption == "list") {
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
//					_viewOption = "list";
//					populateItems(nameQuery, SORT_BY);
//					myViewFlipper.setDisplayedChild(0);
//
//				}
//				// grid view is selected
//				else if (pos == 1) {
//					_viewOption = "grid";
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
	private void onSort(ItemsCursor.SortBy sortBy, Map<String,String> where) {
		populateItems(null, sortBy, where);
	}

	/***
	 * initial display of items in both list and grid view, called when the
	 * activity is created
	 * 
	 * @param sortBy
	 */
	private void initializeView(ItemsCursor.SortBy sortBy) {
		_wishItemCursor = myItemDBAdapter.getItems(sortBy, _where);
		if (myItemDBAdapter.getItemsCount() == 0) {
			myViewFlipper.setDisplayedChild(2);
			return;
		}

		if (_viewOption.equals("list")) {
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
	// _wishItemCursor = myItemDBAdapter.searchItems(itemName);
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
	private void populateItems(String searchName, ItemsCursor.SortBy sortBy, Map<String,String> where) {

		if (searchName == null) {
			// Get all of the rows from the Item table
			// Keep track of the TextViews added in list lstTable
			// _wishItemCursor = wishListDB.getItems(sortBy);
			_wishItemCursor = myItemDBAdapter.getItems(sortBy, where);

		} else {
			_wishItemCursor = myItemDBAdapter.searchItems(searchName, sortBy);
		}

		updateView();
	}

	/***
	 * update either list view or grid view according view option
	 */
	private void updateView() {
		if (myItemDBAdapter.getItemsCount() == 0) {
			myViewFlipper.setDisplayedChild(2);
			return;
		}

		if (_viewOption.equals("list")) {
			// Update the list view
			updateListView();
			myViewFlipper.setDisplayedChild(0);

		}

		else if (_viewOption.equals("grid")) {
			// Update the grid view
			updateGridView();
			myViewFlipper.setDisplayedChild(1);

		}
	}

	private void updateGridView() {
		if (_wishItemCursor != null) {
			_wishItemCursor.requery();
			int resID = R.layout.wishitem_photo;

			String[] from = new String[] { ItemDBAdapter.KEY_ID,
					ItemDBAdapter.KEY_PHOTO_URL };

			int[] to = new int[] { R.id.txtItemID_Grid, R.id.imgPhotoGrid };
			wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
					resID, _wishItemCursor, from, to);

			myGridView.setAdapter(wishListItemAdapterCursor);
			wishListItemAdapterCursor.notifyDataSetChanged();
		} else {
			// give message about empty cursor
		}

	}

	private void updateListView() {

		if (_wishItemCursor != null) {
			_wishItemCursor.requery();
			int resID = R.layout.wishitem_single;

			String[] from = new String[] { 
					ItemDBAdapter.KEY_ID,
					ItemDBAdapter.KEY_PHOTO_URL,
					ItemDBAdapter.KEY_NAME,
					ItemDBAdapter.KEY_PRICE,
					ItemDBAdapter.KEY_STORENAME,
					ItemDBAdapter.KEY_ADDRESS,
					ItemDBAdapter.KEY_COMPLETE};
			
			int[] to = new int[] {
					R.id.txtItemID, 
					R.id.imgPhoto,
					R.id.txtName,
					R.id.txtPrice,
					R.id.txtStore, 
					R.id.txtAddress,
					R.id.checkmark_complete};
			
			wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
					resID, _wishItemCursor, from, to);

			myListView.setAdapter(wishListItemAdapterCursor);
			wishListItemAdapterCursor.notifyDataSetChanged();
		}

		else {
			// give message about empty cursor
		}

	}

	private void deleteItem(long item_id){
		_selectedItem_id = item_id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete the wish?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						myItemDBAdapter.deleteItem(_selectedItem_id);
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

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//search view is part the action bar in honeycomeb and up
			//Get the SearchView and set the searchable configuration
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
			// Assumes current activity is the searchable activity
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
		}
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

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_item_context, menu);

		AdapterView.AdapterContextMenuInfo menu_info;
		menu_info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		//get the position of the item in the list
		//this is the position of the items among all items including the invisible ones
		int pos = menu_info.position;
		View selected_view = null;
		_selectedItem_id = -1;
		if(_viewOption.equals("list")){
			//get the position of the items among the visible items
			pos = pos - myListView.getFirstVisiblePosition();
			selected_view = myListView.getChildAt(pos);
			if (selected_view != null) {
				_selectedItem_id = getDBItemID(selected_view, LIST_MODE);
			}
			else {
//				Log.d(WishList.LOG_TAG, "selected_view is null");
				return;
			}
		}
		else if(_viewOption.equals("grid")){
			pos = pos - myGridView.getFirstVisiblePosition();
			selected_view = myGridView.getChildAt(pos);
			if (selected_view != null) {
				_selectedItem_id = getDBItemID(selected_view, GRID_MODE);
			}
			else {
//				Log.d(WishList.LOG_TAG, "selected_view is null");
				return;
			}
		}
		else if(selected_view == null){
//			Log.d(WishList.LOG_TAG, "selected view is null");
			return;
		}

		WishItem wish_item = WishItemManager.getInstance(this).retrieveItembyId(_selectedItem_id);
		int complete = wish_item.getComplete();
		MenuItem mi = (MenuItem) menu.findItem(R.id.COMPLETE);
		if (complete == 1) {
			mi.setTitle("Mark as incomplete");
		}
		else {
			mi.setTitle("Mark as complete");
		}

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

		long itemId = item.getItemId();
		if (itemId ==  android.R.id.home) {
			finish();
			return true;
		}
		else if (itemId == R.id.menu_search) {
			//do nothing here, the search view is already configured in onCreateOptionsMenu()
			return true;
		}
		//menu view only appears in > Honeycomb
		else if (itemId == R.id.menu_view) {
			showDialog(DIALOG_VIEW);
			return true;
		}

		else if (itemId == R.id.menu_add) {
			// let user generate a wish item
			Intent editItem = new Intent(this, EditItemInfo.class);
			startActivityForResult(editItem, ADD_ITEM);
			return true;
		}
		else if (itemId == R.id.menu_map) {
			Intent mapIntent = new Intent(this, WishListMap.class);
			mapIntent.putExtra("type", "markAll");
			startActivity(mapIntent);
			return true;
		}
		//else if (itemId == R.id.menu_post) {
		//	Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
		//	startActivityForResult(snsIntent, POST_ITEM);
		//	return true;
		//}

//		else if (itemId == R.id.menu_scan) {
//			IntentIntegrator.initiateScan(this);
//			return true;
//		}

		//sort submenu
			else if (itemId == R.id.menu_sortByTime) {
				SORT_BY = ItemsCursor.SortBy.date_time;
				onSort(SORT_BY, _where);
				return true;
			}

			else if (itemId == R.id.menu_sortByName) {
				SORT_BY = ItemsCursor.SortBy.item_name;
				onSort(SORT_BY, _where);
				return true;
			}

			else if (itemId == R.id.menu_sortByPrice) {
				SORT_BY = ItemsCursor.SortBy.price;
				onSort(SORT_BY, _where);
				return true;
			}

	//		else if (itemId == R.id.menu_sortByPriority) {
	//			SORT_BY = ItemsCursor.SortBy.priority;
	//			onSort(SORT_BY);
	//			return true;
	//		}
	//
		else if (itemId == R.id.menu_filter) {
				showDialog(DIALOG_FILTER);
		}

		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);

		long itemId = item.getItemId();
		if (itemId == R.id.REMOVE) {
			deleteItem(_selectedItem_id);
			return true;
		}
		else if (itemId == R.id.EDIT) {
			Intent editItem = new Intent(this, EditItemInfo.class);
			editItem.putExtra("item_id", _selectedItem_id);
			startActivityForResult(editItem, EDIT_ITEM);
			return true;
		}
	//	else if (itemId == R.id.POST): {
	//		Intent snsIntent = new Intent(this, WishItemPostToSNS.class);
	//		snsIntent.putExtra("wishItem", "test");
	//		startActivityForResult(snsIntent, POST_ITEM);
	//		return true;
	//	}
		else if (itemId == R.id.MARK) {
//			String address = myItemDBAdapter.getItemAddress(_selectedItem_id);
//			if (address.equals("unknown")||address.equals("")){
//				Toast toast = Toast.makeText(this, "location unknown", Toast.LENGTH_SHORT);
//				toast.show();
//			}
//			else{
				
				// get the latitude and longitude of the clicked item
				double[] dLocation = new double[2];
				dLocation = myItemDBAdapter.getItemLocation(_selectedItem_id);
				
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

		else if (itemId == R.id.SHARE) {
			//Display display = getWindowManager().getDefaultDisplay(); 
			//int width = display.getWidth();  // deprecated
			//int height = display.getHeight();  // deprecated
			ShareHelper share = new ShareHelper(this, _selectedItem_id);
			share.share();
			//Intent sendIntent = new Intent();
			//sendIntent.setAction(Intent.ACTION_SEND);
			//sendIntent.putExtra(Intent.EXTRA_TEXT, message);
			//sendIntent.putExtra(Intent.EXTRA_STREAM, wish_item.getFullsizePicUri());
			//sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
			//sendIntent.setType("text/plain");
			//sendIntent.setType("*/*");
			//List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(sendIntent, 0);
			//startActivity(sendIntent);
			//Intent chooserIntent = Intent.createChooser(sendIntent, "Share using");
			//List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(chooserIntent, 0);
			//for (ResolveInfo info : resInfo) {
				//Log.d(LOG_TAG, "packageName " + info.activityInfo.packageName.toLowerCase());
				//Log.d(LOG_TAG, "name        " + info.activityInfo.name.toLowerCase());
			//}
			//startActivity(Intent.createChooser(sendIntent, "Share using"));
			return true;
		}

		else if (itemId == R.id.COMPLETE) {
			WishItem wish_item = WishItemManager.getInstance(this).retrieveItembyId(_selectedItem_id);
			if (wish_item.getComplete() == 1) {
				wish_item.setComplete(0);
			}
			else {
				wish_item.setComplete(1);
			}
			wish_item.save();
			updateView();
		}
		return false; }

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
					if (items[item].equals("List")) {
						// Recall populate here is inefficient
						_viewOption = "list";
						populateItems(nameQuery, SORT_BY, _where);
					}
					else {
						_viewOption = "grid";
						populateItems(nameQuery, SORT_BY, _where);
					}
					SharedPreferences pref = getPreferences(MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putString("viewOption", _viewOption);
					editor.commit();
					//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				}
			});
			dialog = builder.create();
			break;

		case DIALOG_FILTER:
			final String BY_ALL = "All";
			final String BY_COMPLETED = "Completed";
			final String BY_INPROGRESS = "In progress";
			final CharSequence[] options = {BY_ALL, BY_COMPLETED, BY_INPROGRESS};

			AlertDialog.Builder optionBuilder = new AlertDialog.Builder(WishList.this);
			optionBuilder.setTitle("Filter wishes");
			optionBuilder.setSingleChoiceItems(options, 1, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

						if (options[item].equals(BY_ALL)) {
							_where.clear();
						}

						else if (options[item].equals(BY_COMPLETED)) {
							_where.put("complete", "1");
						}
						else {
							_where.put("complete", "0");
						}
					}
			});

			optionBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});

			optionBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					populateItems(null, SORT_BY, _where);
				}
			});

			dialog = optionBuilder.create();
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
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		
		// save the position of the currently selected item in the list
		if (_viewOption.equals("list")) {
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
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY)) {
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
			}
		//	_viewOption = savedInstanceState.getString("viewOption");
		}
		
//		if (_viewOption.equals("list")) {
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
//		myDBAdapter.close();
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
					// Create an intent to show the item detail.
					// Pass the item_id along so the next activity can use it to
					// retrieve the info. about the item from database
					long id = -1;
					if (data != null) {
						id = data.getLongExtra("itemID", -1);
					}
					
					if (id != -1) {
//						finish();
						Intent i = new Intent(WishList.this, WishItemDetail.class);
						i.putExtra("item_id", id);
						startActivity(i);
					}
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
		updateView();
	}

	private void setUpActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			findViewById(R.id.listView_header).setVisibility(View.GONE);
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else {
			// we use the header instead of action bar for GingerBread and lower
			findViewById(R.id.listView_header).findViewById(R.id.imageButton_back_logo).setVisibility(View.VISIBLE);
			findViewById(R.id.listView_header).findViewById(R.id.imageButton_viewType).setVisibility(View.VISIBLE);
			findViewById(R.id.listView_header).findViewById(R.id.imageButton_search).setVisibility(View.VISIBLE);

			backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
			backImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
					//start the WishList activity and move the focus to the newly added item
					//				Intent home = new Intent(WishList.this, DashBoard.class);
					//				startActivity(home);
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
		
			viewImageButton = (ImageButton) findViewById(R.id.imageButton_viewType);
			viewImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					showDialog(DIALOG_VIEW);
				}
		});
		}
	}
}
