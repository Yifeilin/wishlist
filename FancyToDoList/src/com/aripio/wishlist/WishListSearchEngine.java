package com.aripio.wishlist;

import com.aripio.wishlist.WishListDataBase.ItemsCursor;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class WishListSearchEngine extends Activity{
	private static final int MENU_SEARCH = 1;

    private TextView mTextView;
    private ListView mList;
    private WishListDataBase wishListDB;
    private ItemsCursor wishItemCursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        
        wishListDB = WishListDataBase.getDBInstance(this);
        
        setContentView(R.layout.main);
        
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY); 
           //wishItemCursor = wishListDB.searchItems(query);
            Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
            search.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            search.putExtra(SearchManager.QUERY, query);
            final Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
            if (appData != null) {
                search.putExtra(SearchManager.APP_DATA, appData);
            }
            startActivity(search);
        }
    }
}
