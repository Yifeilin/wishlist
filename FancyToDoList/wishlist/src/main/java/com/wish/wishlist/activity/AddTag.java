package com.wish.wishlist.activity;

/**
 * Created by jiawen on 2014-11-02.
 */

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.wish.wishlist.R;
import com.wish.wishlist.db.TagDBManager;
import com.wish.wishlist.db.TagItemDBManager;

public class AddTag extends Activity {
    TagListAdapter tagsAdapter = null;
    final static String ITEM_ID = "item_id";

    long mItem_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);

        setUpActionBar();

        mItem_id = getIntent().getLongExtra(ITEM_ID, -1);
        showTags();
    }

    private void showTags() {
        ArrayList<String> tagList;
        TagDBManager manager = new TagDBManager(this);
        manager.open();
        tagList = manager.getAllTags();
        manager.close();

        tagsAdapter = new TagListAdapter(this, R.layout.tag_list, tagList);
        ListView listView = (ListView) findViewById(R.id.taglist);
        // Assign adapter to ListView
        listView.setAdapter(tagsAdapter);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                String tag = (String) parent.getItemAtPosition(position);
                TagItemDBManager manager = new TagItemDBManager(AddTag.this);
                manager.open();
                manager.Tag_item(tag, mItem_id);
                manager.close();
                finish();
            }
        });

        EditText tagFilter = (EditText) findViewById(R.id.tagFilter);
        tagFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tagsAdapter.getFilter().filter(s.toString());
            }
        });
    }

    @Override
    //needed for action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar_edititeminfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        else if (id == R.id.menu_done) {
            //this replaced the saveImageButton used in GingerBread
            // app icon save in action bar clicked;
            EditText tagFilter = (EditText) findViewById(R.id.tagFilter);
            String tag = tagFilter.getText().toString();
            TagItemDBManager manager = new TagItemDBManager(this);
            manager.open();
            manager.Tag_item(tag, mItem_id);
            manager.close();
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setUpActionBar() {
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            findViewById(R.id.addTagView_header).setVisibility(View.GONE);
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class TagListAdapter extends ArrayAdapter<String> {
        private ArrayList<String> originalList;
        private ArrayList<String> tagList;
        private TagFilter filter;

        public TagListAdapter(Context context, int textViewResourceId, ArrayList<String> tagList) {
            super(context, textViewResourceId, tagList);
            this.tagList = new ArrayList<String>();
            this.tagList.addAll(tagList);
            this.originalList = new ArrayList<String>();
            this.originalList.addAll(tagList);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new TagFilter();
            }
            return filter;
        }

        private class ViewHolder {
            TextView tag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.tag_list, null);
                holder = new ViewHolder();
                holder.tag = (TextView) convertView.findViewById(R.id.tagName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String tag = tagList.get(position);
            holder.tag.setText(tag);
            return convertView;
        }

        private class TagFilter extends Filter
        {
            @Override
            protected FilterResults performFiltering (CharSequence constraint){

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<String> filteredItems = new ArrayList<String>();

                    for (String tag : originalList) {
                        if (tag.toLowerCase().contains(constraint)) {
                            filteredItems.add(tag);
                        }
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else {
                    synchronized (this)
                    {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults (CharSequence constraint, FilterResults results){
                tagList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
                clear();
                for (String tag : tagList) {
                    add(tag);
                }
                notifyDataSetInvalidated();
            }
        }
    }
}
