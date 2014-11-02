package com.wish.wishlist.activity;

/**
 * Created by jiawen on 2014-11-02.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

public class AddTag extends Activity {
    MyCustomAdapter dataAdapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);

        //Generate list View from ArrayList
        displayListView();
    }

    private void displayListView() {
        ArrayList<String> tagList;
        TagDBManager manager = new TagDBManager(this);
        manager.open();
        tagList = manager.getAllTags();
        manager.close();

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this, R.layout.tag_list, tagList);
        ListView listView = (ListView) findViewById(R.id.taglist);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                String tag = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
            }
        });

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> originalList;
        private ArrayList<String> tagList;
        private CountryFilter filter;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<String> tagList) {
            super(context, textViewResourceId, tagList);
            this.tagList = new ArrayList<String>();
            this.tagList.addAll(tagList);
            this.originalList = new ArrayList<String>();
            this.originalList.addAll(tagList);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new CountryFilter();
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

        private class CountryFilter extends Filter
        {
            @Override
            protected FilterResults performFiltering (CharSequence constraint){

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<String> filteredItems = new ArrayList<String>();

                    for (int i = 0, l = originalList.size(); i < l; i++)
                    {
                        String tag = originalList.get(i);
                        if (tag.toLowerCase().contains(constraint))
                            filteredItems.add(tag);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
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
                for (int i = 0, l = tagList.size(); i < l; i++)
                    add(tagList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }
}
