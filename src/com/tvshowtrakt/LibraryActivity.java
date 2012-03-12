package com.tvshowtrakt;

import java.util.ArrayList;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListLibrary;
import com.tvshowtrakt.adapters.LazyAdapterListTrending;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import greendroid.app.GDActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LibraryActivity extends GDActivity {

	private boolean rating;
	private String username;
	private String password;
	AQuery aq;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	LibraryActivity libraryActivity;
	private ProgressDialog pg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.library);
		setTitle("Library");

		getPrefs();

		aq=new AQuery(this);
		libraryActivity = this;
		updateLibrary();
	}

	private void updateLibrary() {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Library",true,true);
		new downloadLibrary().execute();

	}

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}

	private class downloadLibrary extends
			AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		@Override
		protected List<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				return manager.userService().libraryShowsAll("lopesdasilva").fire();


			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(List<TvShow> result) {

			if (e == null) {
				LazyAdapterListLibrary lazyAdapter = new LazyAdapterListLibrary(libraryActivity, result, aq);
				
				aq.id(R.id.gridViewLibrary).adapter(lazyAdapter);
				
				pg.dismiss();
				
				

			} else
				goBlooey(e);
		}

	}

	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Connection Error")
				.setMessage("Movie Trakt can not connect with trakt service")
				.setPositiveButton("OK", null).show();
	}

}