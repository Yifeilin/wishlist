package com.wish.wishlist.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.wish.wishlist.R;
import com.wish.wishlist.db.TagItemDBManager;

/**
 * Created by jiawen on 2014-11-03.
 */
public class AddTag extends TagList {

    protected void setTagClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                String tag = (String) parent.getItemAtPosition(position);
                TagItemDBManager.instance(AddTag.this).Tag_item(tag, mItem_id);
                finish();
            }
        });
    }

    protected void onDone() {
        //this replaced the saveImageButton used in GingerBread
        // app icon save in action bar clicked;
        EditText tagFilter = (EditText) findViewById(R.id.tagFilter);
        String tag = tagFilter.getText().toString();
        TagItemDBManager.instance(this).Tag_item(tag, mItem_id);
        finish();
    }
}

