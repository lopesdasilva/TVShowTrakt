package com.tvshowtrakt;

import java.util.ArrayList;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListLibrary;
import com.tvshowtrakt.adapters.LazyAdapterListTrending;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import extras.Blooye;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class LibraryActivity extends GDActivity {

	private boolean rating;
	private String username;
	private String password;
	AQuery aq;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	LibraryActivity libraryActivity;
	private ProgressDialog pg;
	public List<TvShow> libraryList;
	private static final int REFRESH = 0;
	private static final int SEARCH = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.library);
		setTitle("Library");

		getPrefs();

		aq = new AQuery(this);
		libraryActivity = this;
		
		
		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		updateLibrary();
	}

	private void updateLibrary() {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Library", true,
				true);
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

	private class downloadLibrary extends AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		@Override
		protected List<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				return manager.userService().libraryShowsAll(username)
						.fire();

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
				libraryList = result;
				LazyAdapterListLibrary lazyAdapter = new LazyAdapterListLibrary(
						libraryActivity, result, aq);

				aq.id(R.id.gridViewLibrary).adapter(lazyAdapter);
				aq.id(R.id.gridViewLibrary).itemClicked(
						new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								
								Intent i = new Intent(getApplicationContext(),
										ShowActivity.class);
								i.putExtra("Show", libraryList.get(position));
								startActivity(i);

							}
						});
				pg.dismiss();

			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(libraryActivity,e);
		}

	}
	
	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			updateLibrary();
			break;

		case SEARCH:
			this.startSearch(null, false, Bundle.EMPTY, false);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);

		}
		return true;
	}

}