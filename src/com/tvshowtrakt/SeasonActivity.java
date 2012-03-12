package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.tvshowtrakt.adapters.LazyAdapterListSeason;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import greendroid.app.GDActivity;

public class SeasonActivity extends GDActivity {
	
	private TvShow show;
	private int activeSeason;
	private AQuery aq;
	private boolean login;
	private String username;
	private String password;
	private ProgressDialog pg;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private SeasonActivity seasonActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.season);
		
		
		Bundle extras = getIntent().getExtras();
		show = (TvShow) extras.get("Show");
		activeSeason = (Integer) extras.get("Season");
		
		seasonActivity=this;
		aq=new AQuery(this);
		setTitle(show.title);
		aq.id(R.id.textViewSeasonNumber).text("Season "+activeSeason);
		
		getPrefs();
		updateSeason(activeSeason);
		
	}

	
	private void updateSeason(int season) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Season "+season,
				true, true);
		new updateSeason().execute(season+"");

	}
	private class updateSeason extends AsyncTask<String, Void, List<TvShowEpisode>> {
		private Exception e = null;

		@Override
		protected List<TvShowEpisode> doInBackground(String... params) {
			List<TvShowEpisode> a = null;
			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				a=manager.showService().season(show.imdbId, Integer.parseInt(params[0])).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return a;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(List<TvShowEpisode> result) {

			if (e != null) {
				goBlooey(e);
			} else {
				LazyAdapterListSeason lazyAdapter = new LazyAdapterListSeason(
						seasonActivity, result, aq);
//				watchlistList = result;
				aq.id(R.id.listViewSeason).adapter(lazyAdapter);
//				registerForContextMenu(aq.id(R.id.listViewWatchlist)
//						.getView());
				// aq.id(R.id.listViewWatchlist).visible();
				pg.dismiss();

			}
		}
	}
	/**
	 * Em caso de erro a excepção será tratada aqui.
	 */
	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());

		builder.setTitle("Connection Error")
				.setMessage("Movie Trakt can not connect with trakt service")
				.setPositiveButton("OK", null).show();
	}
	/**
	 * Metodo para obter as preferencias da applicação
	 */
	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		login = prefs.getBoolean("login", false);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}

	/**
	 * Metodo para obter as preferencias da applicação, quando esta é retomada,
	 * ao invés de iniciada
	 */
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		login = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}
	
}
