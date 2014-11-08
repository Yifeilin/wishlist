package com.wish.wishlist.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wish.wishlist.db.TagItemDBManager;

import java.util.ArrayList;

/**
 * Created by jiawen on 2014-11-03.
 */
public class AddTag extends TagList {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> tags = TagItemDBManager.instance(AddTag.this).tags_of_item(mItem_id);
        for (String tag : tags) {
            completionView.addObject(tag);
            currentTags.add(tag);
        }
    }

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

