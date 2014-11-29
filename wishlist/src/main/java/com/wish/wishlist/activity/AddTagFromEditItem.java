package com.wish.wishlist.activity;

/**
 * Created by jiawen on 2014-11-02.
 */

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;

public class AddTagFromEditItem extends AddTag {
    public final static String TAGS = "tags";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> tags = getIntent().getStringArrayListExtra(TAGS);
        if (tags != null) {
            for (String tag : tags) {
                completionView.addObject(tag);
                currentTags.add(tag);
            }
        }
    }

    protected void onSave() {
        //Get the text after the last token in the view. This text has not been tokenized, but it should be regarded as a tag
        String lastTag = completionView.getText().toString().replaceFirst(PREFIX, "").replace(",", "").trim();
        if (!lastTag.isEmpty()) {
            currentTags.add(lastTag);
        }
        ArrayList<String> tags = new ArrayList<String>();
        tags.addAll(currentTags);

        //send the tags back to the EditItemInfo activity and close this activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(TAGS, tags);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
