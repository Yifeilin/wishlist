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
        //Get the text after the last token in the view. This text has not been tokenized, but it should be regarded as a tag
        String lastTag = completionView.getText().toString().replaceFirst(PREFIX, "").replace(",", "").trim();
        if (!lastTag.isEmpty()) {
            currentTags.add(lastTag);
        }
        //this replaced the saveImageButton used in GingerBread
        // app icon save in action bar clicked;
        ArrayList<String> oldTags = TagItemDBManager.instance(AddTag.this).tags_of_item(mItem_id);

        //Remove the deleted tags
        for (String tag : oldTags) {
            if (!currentTags.contains(tag)) {
                TagItemDBManager.instance(AddTag.this).Untag_item(tag, mItem_id);
            }
            else {
                //Remove the tags we already have so the following for loop will not tag them again
                currentTags.remove(tag);
            }
        }

        //Add the new tags
        for (String tag : currentTags) {
            TagItemDBManager.instance(AddTag.this).Tag_item(tag, mItem_id);
        }
        finish();
    }
}

