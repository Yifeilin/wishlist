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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.wish.wishlist.R;
import com.wish.wishlist.db.TagDBManager;

public class TagList extends Activity implements TokenCompleteTextView.TokenListener {
    final static String PREFIX = "Tags: ";
    TagsCompletionView completionView;
    ArrayAdapter<String> adapter;

    TagListAdapter tagsAdapter = null;
    protected final static String ITEM_ID = "item_id";

    protected long mItem_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);

        adapter = new FilteredArrayAdapter<String>(this, R.layout.tag_layout, new String[]{}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = (View)l.inflate(R.layout.tag_layout, parent, false);
                }

                String tag = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(tag);

                return convertView;
            }

            @Override
            protected boolean keepObject(String obj, String mask) {
                mask = mask.toLowerCase();
                return obj.toLowerCase().startsWith(mask);
            }
        };

        completionView = (TagsCompletionView)findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
        completionView.setTokenListener(this);

        if (savedInstanceState == null) {
            completionView.setPrefix(PREFIX);
        }

        setUpActionBar();

        mItem_id = getIntent().getLongExtra(ITEM_ID, -1);
        showTags();
    }

    private void showTags() {
        ArrayList<String> tagList;
        tagList = TagDBManager.instance(this).getAllTags();

        tagsAdapter = new TagListAdapter(this, R.layout.tag_list, tagList);
        ListView listView = (ListView) findViewById(R.id.taglist);
        // Assign adapter to ListView
        listView.setAdapter(tagsAdapter);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);
        setTagClick(listView);

        completionView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Remove the prefix and all the ',' in the string.
                String constraint = s.toString().replaceFirst(PREFIX, "").replace(",", "").trim();
                tagsAdapter.getFilter().filter(constraint);
            }
        });
    }

    protected void setTagClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = (String) parent.getItemAtPosition(position);
                finish();
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
            onDone();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void onDone() {}

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

    @Override
    public void onTokenAdded(Object token) {
        showTags();
    }

    @Override
    public void onTokenRemoved(Object token) {
        showTags();
    }
}
