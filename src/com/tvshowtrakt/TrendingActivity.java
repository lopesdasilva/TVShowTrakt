package com.tvshowtrakt;

import java.util.ArrayList;
import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.UserProfile.Stats.Shows;
import com.tvshowtrakt.adapters.LazyAdapterListTrending;

import extras.Blooye;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;

public class TrendingActivity extends GDActivity {

	boolean rating;
	String username;
	String password;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private TrendingActivity trendingActivity;
	public List<TvShow> trendingList;
	public LazyAdapterListTrending lazyAdapter;
	public ListView mTrendingList;
	public View mProgressBar;
	private LinearLayout mUpdating;
	public TvShow selectedShow;
	private static final int SEARCH = 0;
	private static final int REFRESH = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.trending);
		setTitle("Trending");

		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);

		// Obter as preferências da aplicação
		getPrefs();

		trendingActivity = this;

		// Variaveis de elementos do layout a ser usados
		mUpdating = (LinearLayout) findViewById(R.id.loading);
		mTrendingList = (ListView) findViewById(R.id.trendingList);

		// listeners
		registerForContextMenu(mTrendingList);

		// update da TrendingList
		updateTrending();

	}

	// ContextMenu para cada elemento da lista
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo cmi = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(trendingList.get(cmi.position).title);
		if (trendingList.get(cmi.position).inWatchlist)
			menu.add(1, cmi.position, 0, "Remove from Watchlist");
		else
			menu.add(1, cmi.position, 0,"Add to Watchlist" );
		menu.add(2, cmi.position, 0, "Loved it");
		menu.add(3, cmi.position, 0, "Hated it");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		selectedShow = trendingList.get(item.getItemId());

		switch (item.getGroupId()) {
		case 1:
			if (selectedShow.inWatchlist)
				Toast.makeText(getApplicationContext(),
						"Removing from your watchlist", Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(getApplicationContext(),
						"Adding to your watchlist", Toast.LENGTH_SHORT).show();
			new updateTvShow().execute(MarkingTypes.WATCHLIST);
			break;
		}
		return true;
	}

	// Asynctask para obtenção da lista de trending tv shows
	private class downloadTrending extends
			AsyncTask<String, Void, ArrayList<TvShow>> {
		private Exception e = null;

		@Override
		protected ArrayList<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				List<TvShow> slist = manager.showService().trending().fire();
				ArrayList<TvShow> d = new ArrayList<TvShow>();
				// TODO URGENTE isto é para melhorar (passar como return)
				trendingList = slist;
				for (TvShow c : slist) {
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
				boolean[] mWatchlist = new boolean[result.size()];
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
					mWatchlist[i] = t.inWatchlist;
					mLovedPercentage[i] = t.ratings.percentage + " %";
					mVotes[i] = t.ratings.votes + " votes";
					mDescription[i] = t.overview;
					mTitle[i] = t.title + " (" + t.year + ")";
					i++;
				}

				lazyAdapter = new LazyAdapterListTrending(trendingActivity,
						mPosters, mTitle, mWatchlist, mLovedPercentage, mVotes,
						mDescription);
				mTrendingList.setAdapter(lazyAdapter);

				mUpdating.setVisibility(LinearLayout.GONE);
				mTrendingList.setVisibility(ListView.VISIBLE);
				mTrendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						Intent i = new Intent(getApplicationContext(),
								ShowActivity.class);
						i.putExtra("Show", trendingList.get(position));
						startActivity(i);

					}
				});

			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(trendingActivity,e);
		}

	}

	private class updateTvShow extends AsyncTask<MarkingTypes, Void, String> {
		private Exception e = null;

		@Override
		protected String doInBackground(MarkingTypes... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				switch (params[0]) {
				case HATE:
					break;
				case LOVE:
					break;
				case WATCHLIST:
					if (selectedShow.inWatchlist)
						manager.showService().unwatchlist()
								.title(selectedShow.title, selectedShow.year)
								.fire();
					else
						manager.showService().watchlist()
								.title(selectedShow.title, selectedShow.year)
								.fire();
					break;
				case SEEN:
					break;
				case COLLECTION:
					break;

				}

			} catch (Exception e) {
				this.e = e;
			}
			return "null";
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(String result) {
			if (e != null){
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(trendingActivity,e);
			}
			updateTrending();
		}
	}

	

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}

	private void updateTrending() {
		mUpdating.setVisibility(LinearLayout.VISIBLE);
		mTrendingList.setVisibility(ListView.GONE);
		new downloadTrending().execute();

	}
	
	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			Toast.makeText(getApplicationContext(), "Updating TrendingList",
					Toast.LENGTH_SHORT).show();
			updateTrending();
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
