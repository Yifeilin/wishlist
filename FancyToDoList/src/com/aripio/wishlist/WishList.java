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

public class WishList extends Activity {
	// Assign a unique ID for each menu item
	static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	static final private int HELP_TODO = Menu.FIRST + 2;
	static final private int DETAIL_TODO = Menu.FIRST + 3;
	static final private int POST_TODO = Menu.FIRST + 4;
	static final private int SORT_TODO = Menu.FIRST + 5;

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

		// String sortBy_str = savedInstanceState.getString(SORT_BY_KEY);
		// SORT_BY = ItemsCursor.SortBy.valueOf(sortBy_str);
		// Colour c = (Colour) Enum.Parse(typeof(Colour), "Red", true);

		setContentView(R.layout.main);

		myViewFlipper = (ViewFlipper) findViewById(R.id.myFlipper);
		myListView = (ListView) findViewById(R.id.myListView);
		myGridView = (GridView) findViewById(R.id.myGridView);
		mySearchText = (EditText) findViewById(R.id.mySearchText);
		myViewSpinner = (Spinner) findViewById(R.id.myViewSpinner);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.views_array,
						android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myViewSpinner.setAdapter(adapter);

		myViewSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				// list view
				if (pos == 0) {
					myViewFlipper.setDisplayedChild(0);
					viewOption = "list";

				}

				// grid view
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

		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item has been clicked and get its _id in database
				long item_id = getDBItemID(position);
				// View selected_view = myListView.getChildAt(position);
				// TextView itemIdTextView = (TextView)
				// selected_view.findViewById(R.id.txtItemID);
				// //TextView dateTextView = (TextView)
				// selected_view.findViewById(R.id.txtDate);
				// //String item_id_str = itemIdTextView.getText().toString();
				// long item_id =
				// Long.parseLong(itemIdTextView.getText().toString());
				//				
				// Create an intent to show the item detail.
				// Pass the item_id along so the next activity can use it to
				// retrieve the info. about the item from database
				Intent i = new Intent(WishList.this, WishItemDetail.class);
				i.putExtra("item_id", item_id);
				i.putExtra("position", position);
				startActivity(i);

			}

		});

		myGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// find which item has been clicked and get its _id in database
				long item_id = getDBItemID(position);
				// View selected_view = myListView.getChildAt(position);
				// TextView itemIdTextView = (TextView)
				// selected_view.findViewById(R.id.txtItemID);
				// //TextView dateTextView = (TextView)
				// selected_view.findViewById(R.id.txtDate);
				// //String item_id_str = itemIdTextView.getText().toString();
				// long item_id =
				// Long.parseLong(itemIdTextView.getText().toString());
				//				
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

		// saveDefaultImage();
		// Open or create the database

		// wishListDB = WishListDataBase.getDBInstance(this);

		myDBAdapter = new DBAdapter(this);
		myDBAdapter.open();

		myItemDBAdapter = new ItemDBAdapter(this);
		myItemDBAdapter.open();

		// populateItemList(ItemsCursor.SortBy.name);
		// populateItemGrid(ItemsCursor.SortBy.name);
		initializeView(ItemsCursor.SortBy.item_name);
		// populateItems(ItemsCursor.SortBy.name);

		// myViewFlipper.showNext();

		// int a = 0;
		// int b = 0;
	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	// get the _ID of the item in wishitem database
	// whose position in the listview is pos.
	public long getDBItemID(int pos) {

		View selected_view = null;
		TextView itemIdTextView = null;
		if (viewOption == "list") {
			selected_view = myListView.getChildAt(pos);
			itemIdTextView = (TextView) selected_view
					.findViewById(R.id.txtItemID);

		}

		else if (viewOption == "grid") {
			selected_view = myGridView.getChildAt(pos);
			itemIdTextView = (TextView) selected_view
					.findViewById(R.id.txtItemID_Grid);

		}

		// TextView dateTextView = (TextView)
		// selected_view.findViewById(R.id.txtDate);
		// String item_id_str = itemIdTextView.getText().toString();
		long item_id = Long.parseLong(itemIdTextView.getText().toString());
		return item_id;

	}

	private void onSortByTime() {
		// populateItemList(ItemsCursor.SortBy.create_date);
		populateItems(ItemsCursor.SortBy.date_time);
	}

	private void onSortByName() {
		// populateItemList(ItemsCursor.SortBy.name);
		populateItems(ItemsCursor.SortBy.item_name);
	}

	private void onSortByPrice() {
		// populateItemList(ItemsCursor.SortBy.price);
		populateItems(ItemsCursor.SortBy.price);
	}

	private void onSortByPriority() {
		// populateItemList(ItemsCursor.SortBy.priority);
		populateItems(ItemsCursor.SortBy.priority);
	}

	private void initializeView(ItemsCursor.SortBy sortBy) {
		// wishItemCursor = wishListDB.getItems(sortBy);
		wishItemCursor = myItemDBAdapter.getItems(sortBy);
		updateListView();
		updateGridView();

	}

	private void populateItems(ItemsCursor.SortBy sortBy) {

		// Get all of the rows from the database and create the table
		// Keep track of the TextViews added in list lstTable
		// wishItemCursor = wishListDB.getItems(sortBy);
		wishItemCursor = myItemDBAdapter.getItems(sortBy);

		updateView();

		// if (viewOption == "list"){
		// // Update the list view
		// updateListView();
		//    		
		// }
		//    	
		// else if(viewOption == "grid"){
		// // Update the list view
		// updateGridView();
		//    		
		// }
	}

	// private void populateItemList(ItemsCursor.SortBy sortBy) {
	//   
	// // Get all of the rows from the database and create the table
	// // Keep track of the TextViews added in list lstTable
	// wishItemCursor = wishListDB.getItems(sortBy);
	//    	
	// // Update the list view
	// updateListView();
	// }
	//	
	// private void populateItemGrid(ItemsCursor.SortBy sortBy){
	// // Get all of the rows from the database and create the table
	// // Keep track of the TextViews added in list lstTable
	// wishItemCursor = wishListDB.getItems(sortBy);
	//    	
	// // Update the list view
	// updateGridView();
	//		
	// }

	// this function needs to be re-written, because everytime the app
	// starts, it will write additional image data to the media content provider
	private void saveDefaultImage() {
		// write some sample image to content provider
		Bitmap bookBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.book);
		// Bitmap car

		ContentValues values = new ContentValues();
		values.put(Media.MIME_TYPE, "image/jpeg");

		Uri PHOTO_URI = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI,
				values);
		ContentResolver cr = getContentResolver();
		// final Uri PHOTO_URI =
		// Uri.parse("content://com.aripio.wishlist.DefaultPhotoProvider");

		String[] columns = { Media.MIME_TYPE };
		Cursor cur = managedQuery(PHOTO_URI, columns, null, null, null);
		int count = cur.getCount();

		if (count == 0) {
			Uri uri = cr.insert(PHOTO_URI, values);

			String picture_uri = null;

			try {
				OutputStream outStream = getContentResolver().openOutputStream(
						uri);
				bookBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);

				outStream.close();
				// picture_uri = uri.getEncodedPath();
				picture_uri = uri.toString();

				int a = 0;
				int b = 0;

			} catch (Exception e) {
				Log.e(WishList.LOG_TAG, "exception while writing image", e);
			}

		}
		// ContentProviderClient crc =
		// cr.acquireContentProviderClient(PHOTO_URI);
		// crc.query(PHOTO_URI, null, selection, selectionArgs, null);

	}

	private void updateGridView() {
		wishItemCursor.requery();
		int resID = R.layout.wishitem_photo;

		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_NAME, WishListDataBase.KEY_DESCRIPTION,
		// WishListDataBase.KEY_DATE};
		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_PHOTO_URL, WishListDataBase.KEY_NAME,
		// WishListDataBase.KEY_DESCRIPTION, WishListDataBase.KEY_DATE};
		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_PHOTO_URL};

		String[] from = new String[] { ItemDBAdapter.KEY_ID,
				ItemDBAdapter.KEY_PHOTO_URL };

		// int[] to = new int[] {R.id.txtItemID, R.id.txtName, R.id.txtDesc,
		// R.id.txtDate};
		// int[] to = new int[] {R.id.txtItemID, R.id.imgPhoto, R.id.txtName,
		// R.id.txtDesc, R.id.txtDate};
		int[] to = new int[] { R.id.txtItemID_Grid, R.id.imgPhotoGrid };
		wishListItemAdapterCursor = new WishListItemCursorAdapter(this, resID,
				wishItemCursor, from, to);
		// wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
		// resID, wishItemCursor);

		// ImageAdapter m_imageAdapter = new ImageAdapter(this);
		// myGridView.setAdapter(m_imageAdapter);

		myGridView.setAdapter(wishListItemAdapterCursor);
		wishListItemAdapterCursor.notifyDataSetChanged();

	}

	private void updateView() {

		if (viewOption == "list") {
			// Update the list view
			updateListView();

		}

		else if (viewOption == "grid") {
			// Update the list view
			updateGridView();

		}

	}

	private void updateListView() {

		wishItemCursor.requery();
		int resID = R.layout.wishitem_single;

		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_NAME, WishListDataBase.KEY_DESCRIPTION,
		// WishListDataBase.KEY_DATE};
		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_PHOTO_URL, WishListDataBase.KEY_NAME,
		// WishListDataBase.KEY_DESCRIPTION, WishListDataBase.KEY_DATE};
		// String[] from = new String[] {WishListDataBase.KEY_ITEMID,
		// WishListDataBase.KEY_PHOTO_URL, WishListDataBase.KEY_NAME,
		// WishListDataBase.KEY_DATE};

		String[] from = new String[] { ItemDBAdapter.KEY_ID,
				ItemDBAdapter.KEY_PHOTO_URL, ItemDBAdapter.KEY_NAME,
				ItemDBAdapter.KEY_DATE_TIME };
		// int[] to = new int[] {R.id.txtItemID, R.id.txtName, R.id.txtDesc,
		// R.id.txtDate};
		// int[] to = new int[] {R.id.txtItemID, R.id.imgPhoto, R.id.txtName,
		// R.id.txtDesc, R.id.txtDate};
		int[] to = new int[] { R.id.txtItemID, R.id.imgPhoto, R.id.txtName,
				R.id.txtDate };
		wishListItemAdapterCursor = new WishListItemCursorAdapter(this, resID,
				wishItemCursor, from, to);
		// wishListItemAdapterCursor = new WishListItemCursorAdapter(this,
		// resID, wishItemCursor);

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

		// int list_index = myListView.getCheckedItemPosition ();
		// WishListItemCursorAdapter cursor = (WishListItemCursorAdapter)
		// myListView.getItemAtPosition(list_index);
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
			// removeItem(index);
			// updateListView();
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
		// updateListView();
	}
}