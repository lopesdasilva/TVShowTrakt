package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import extras.Blooye;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class WatchlistActivity extends GDActivity {

	public WatchlistActivity watchlistActivity;
	private boolean rating;
	public String username;
	public String password;
	public ProgressDialog pg;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	AQuery aq;
	public List<TvShow> watchlistList;
	private TvShow selectedShow;
	private static final int REFRESH = 0;
	private static final int SEARCH = 1;
	LazyAdapterListWatchlist lazyAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.watchlist);
		setTitle("Watchlist");

		// Items da ActionBar
//		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		aq = new AQuery(this);
		// Obter as preferências da aplicação
		getPrefs();

		watchlistActivity = this;

		updateWatchlist();
	}

	private void updateWatchlist() {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Watchlist",
				true, true);
		new updateWatchlist().execute();

	}

	private class updateWatchlist extends AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		@Override
		protected List<TvShow> doInBackground(String... params) {
		
			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				return manager.userService().watchlistShows(username).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(final List<TvShow> result) {

			if (e != null) {
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(watchlistActivity, e);
			} else {
				if (result.size() != 0) {
					lazyAdapter = new LazyAdapterListWatchlist(
							watchlistActivity, result, aq);
					watchlistList = result;
					aq.id(R.id.listViewWatchlist).adapter(lazyAdapter);
					aq.id(R.id.listViewWatchlist).itemClicked(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Intent i = new Intent(getApplicationContext(),
									ShowActivity.class);
							i.putExtra("Show", result.get(arg2));
							startActivity(i);
							
						}
					});
					
					
					
					
					registerForContextMenu(aq.id(R.id.listViewWatchlist)
							.getView());
					// aq.id(R.id.listViewWatchlist).visible();
					pg.dismiss();
				}

			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		selectedShow = watchlistList.get(item.getItemId());

		pg = ProgressDialog.show(this, "Please Wait", "Removing "
				+ selectedShow.title + " from Watchlist", true, true);
		new removeFromWatchlist().execute();

		return true;
	}

	private class removeFromWatchlist extends
			AsyncTask<MarkingTypes, Void, String> {
		private Exception e = null;

		@Override
		protected String doInBackground(MarkingTypes... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				manager.showService().unwatchlist()
						.title(selectedShow.title, selectedShow.year).fire();

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
			if (e != null) {
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(watchlistActivity, e);
			} else {
				pg.dismiss();
				updateWatchlist();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo cmi = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(watchlistList.get(cmi.position).title);

		menu.add(1, cmi.position, 0, "Remove from Watchlist");
	}

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}
	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			updateWatchlist();
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