package com.aripio.wishlist;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WishListSearchEngine extends Activity{
	private static final int MENU_SEARCH = 1;

    private TextView mTextView;
    private ListView mList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setContentView(R.layout.main);
        
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mTextView.setText(getString(R.string.search_results, query));  
        }
    }
}
