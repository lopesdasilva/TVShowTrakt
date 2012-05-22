package com.tvshowtrakt;

import java.util.ArrayList;
import java.util.List;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListSearch;

import extras.Blooye;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;

public class SearchableActivity extends GDActivity {

	boolean rating;
	String username;
	String password;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private static final int SEARCH = 1;

	ListView searchResults;
	private LinearLayout mUpdating;
	private SearchableActivity searchableActivitity;
	public List<TvShow> showsList;
	ServiceManager manager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO SEARCHLIST WITH COVERS
		setActionBarContentView(R.layout.search);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		setTitle("Search Results");

		getPrefs();

		mUpdating = (LinearLayout) findViewById(R.id.loading);
		searchResults = (ListView) findViewById(R.id.searchResults);

		searchResults.setClickable(true);
		searchResults
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						Intent i = new Intent(getApplicationContext(),
								ShowActivity.class);
						i.putExtra("Show", showsList.get(position));
						startActivity(i);

					}
				});
		searchableActivitity = this;
		Intent intent = getIntent();
		// // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		String query = intent.getStringExtra(SearchManager.QUERY);
		doMySearch(query);

	}

	private void doMySearch(String query) {

		searchResults.setVisibility(ListView.GONE);
		mUpdating.setVisibility(LinearLayout.VISIBLE);
		new Searching().execute(query);

	}

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}

	private class Searching extends AsyncTask<String, Void, ArrayList<TvShow>> {
		private Exception e = null;

		@Override
		protected ArrayList<TvShow> doInBackground(String... params) {

			try {

				manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				List<TvShow> tList = manager.searchService()
						.shows(params[0].toString()).fire();

				ArrayList<TvShow> d = new ArrayList<TvShow>();
				// TODO: isto é para melhorar (passar como return)
				showsList = tList;
				for (TvShow c : tList) {
					//
					d.add(c);
				}
				return d;

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(ArrayList<TvShow> result) {

			if (e == null) {

				String[] mTitle = new String[result.size()];
				String[] mPosters = new String[result.size()];
				boolean[] mSeen = new boolean[result.size()];
				String[] mLovedPercentage = new String[result.size()];
				String[] mVotes = new String[result.size()];
				String[] mDescription = new String[result.size()];

				int i = 0;
				for (TvShow t : result) {

					mPosters[i] = t.images.poster;

					// Ainda nao é suportado pela API
					// if(t.stats.plays!=null && t.stats.plays ==
					// t.episodes.size())
					// mSeen[i]=true;
					// else
					mSeen[i] = false;
					mLovedPercentage[i] = t.ratings.percentage + " %";
					mVotes[i] = t.ratings.votes + " votes";
					mDescription[i] = t.overview;
					mTitle[i] = t.title + " (" + t.year + ")";
					i++;
				}

				LazyAdapterListSearch lazyAdapter = new LazyAdapterListSearch(
						searchableActivitity, mPosters, mTitle, mSeen,
						mLovedPercentage, mVotes, mDescription);
				searchResults.setAdapter(lazyAdapter);

				searchResults.setVisibility(ListView.VISIBLE);

				mUpdating.setVisibility(LinearLayout.GONE);
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(searchableActivitity, e);
		}

	}

	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case SEARCH:

			this.startSearch(null, false, Bundle.EMPTY, false);
			break;

		default:
			return super.onHandleActionBarItemClick(item, position);

		}
		return true;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);      
		String query = intent.getStringExtra(SearchManager.QUERY);
		doMySearch(query);

	}
}
