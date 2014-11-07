package com.wish.wishlist.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wish.wishlist.db.TagItemDBManager;

/**
 * Created by jiawen on 2014-11-03.
 */
public class AddTag extends TagList {

    protected void setTagClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = (String) parent.getItemAtPosition(position);
                completionView.addObject(tag);
            }
        });
    }

    protected void onDone() {
        //this replaced the saveImageButton used in GingerBread
        // app icon save in action bar clicked;
        for (Object tag_obj : completionView.getObjects()) {
            TagItemDBManager.instance(AddTag.this).Tag_item(tag_obj.toString(), mItem_id);
        }
        finish();
    }
}

