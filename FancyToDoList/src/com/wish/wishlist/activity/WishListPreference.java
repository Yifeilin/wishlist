package com.wish.wishlist.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

import com.wish.wishlist.R;
import com.wish.wishlist.view.ReleaseNotesView;
 
public class WishListPreference extends PreferenceActivity {
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.wishlist_preference);
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
					Toast.makeText(getBaseContext(), "The rate app has been clicked", Toast.LENGTH_LONG).show();
					return true;
				}
			});
		}
}
