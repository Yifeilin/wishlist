package com.wish.wishlist.activity;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wish.wishlist.R;
import com.wish.wishlist.view.ReleaseNotesView;
 
public class WishListPreference extends PreferenceActivity {
	private ImageButton _backImageButton;

	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.wishlist_preference);
			setContentView(R.layout.preference_parent);

			setUpActionBar();


			// Get the custom preference
			Preference releaseNotes = (Preference) findPreference("releaseNotes");
			releaseNotes.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {
					//Toast.makeText(getBaseContext(), "The release notes has been clicked", Toast.LENGTH_LONG).show();
			//		SharedPreferences customSharedPreference = getSharedPreferences(
			//			"myCustomSharedPrefs", Activity.MODE_PRIVATE);
			//		SharedPreferences.Editor editor = customSharedPreference;
			//	.edit();
			//editor.putString("myCustomPref",
			//	"The preference has been clicked");
			//editor.commit();
				   ReleaseNotesView view = new ReleaseNotesView(WishListPreference.this); 
				   view.show();
					return true;
				}
			});

			Preference rateApp = (Preference) findPreference("rateApp");
			rateApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {
					Uri uri = Uri.parse("market://details?id=" + getPackageName());
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {
					}
					//Toast.makeText(getBaseContext(), "The rate app has been clicked", Toast.LENGTH_LONG).show();
					return true;
				}
			});
		}

		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case android.R.id.home:
					finish();
					return true;
				default:
					return super.onOptionsItemSelected(item);
				}
			}

	private void setUpActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			findViewById(R.id.preference_header).setVisibility(View.GONE);
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else {
			// we use the header instead of action bar for GingerBread and lower
			findViewById(R.id.preference_header).findViewById(R.id.imageButton_back_logo).setVisibility(View.VISIBLE);

			_backImageButton = (ImageButton) findViewById(R.id.imageButton_back_logo);
			_backImageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setResult(RESULT_CANCELED, null);
					finish();
				}
			});	
		}
	}
}
