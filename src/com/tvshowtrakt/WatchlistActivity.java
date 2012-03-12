package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import greendroid.app.GDActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.watchlist);
		setTitle("Watchlist");

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
			List<TvShow> a = null;
			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				a = manager.userService().watchlistShows(username).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return a;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(List<TvShow> result) {

			if (e != null) {
				goBlooey(e);
			} else {
				if (result.size() != 0) {
					LazyAdapterListWatchlist lazyAdapter = new LazyAdapterListWatchlist(
							watchlistActivity, result, aq);
					watchlistList = result;
					aq.id(R.id.listViewWatchlist).adapter(lazyAdapter);
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
			if (e != null)
				goBlooey(e);
			else {
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

	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Connection Error")
				.setMessage("Movie Trakt can not connect with trakt service")
				.setPositiveButton("OK", null).show();
	}
}