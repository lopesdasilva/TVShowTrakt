package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.tvshowtrakt.adapters.LazyAdapterListSeason;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import extras.Blooye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;

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
	public List<TvShowEpisode> seasonList;

	private static final int SEARCH = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.season);

		addActionBarItem(ActionBarItem.Type.Search, SEARCH);

		Bundle extras = getIntent().getExtras();
		show = (TvShow) extras.get("Show");
		activeSeason = (Integer) extras.get("Season");

		seasonActivity = this;
		aq = new AQuery(this);
		setTitle(show.title);
		aq.id(R.id.textViewSeasonNumber).text("Season " + activeSeason);

		ListView mSeasonList = (ListView) findViewById(R.id.listViewSeason);

		// listeners
		registerForContextMenu(mSeasonList);

		getPrefs();
		updateSeason(activeSeason);

	}

	private void updateSeason(int season) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Season "
				+ season, true, true);
		new updateSeason().execute(season + "");

	}

	private class updateSeason extends
			AsyncTask<String, Void, List<TvShowEpisode>> {
		private Exception e = null;

		@Override
		protected List<TvShowEpisode> doInBackground(String... params) {
			List<TvShowEpisode> a = null;
			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				a = manager.showService()
						.season(show.imdbId, Integer.parseInt(params[0]))
						.fire();

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
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(seasonActivity, e);
			} else {
				LazyAdapterListSeason lazyAdapter = new LazyAdapterListSeason(
						seasonActivity, result, aq);
				seasonList = result;
				
				aq=aq.recycle(aq.id(R.id.listViewSeason).getView());
				aq.id(R.id.listViewSeason).adapter(lazyAdapter);

				pg.dismiss();

			}
		}
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo cmi = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(seasonList.get(cmi.position).title);

		menu.add(1, cmi.position, 0, "Share");
		if (seasonList.get(cmi.position).watched)
			menu.add(2, cmi.position, 1, "Mark as unseen");
		else
			menu.add(3, cmi.position, 1, "Mark as seen");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		TvShowEpisode selectedEpisode = seasonList.get(item.getItemId());

		switch (item.getGroupId()) {
		case 1:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT, selectedEpisode.title);
			i.putExtra(Intent.EXTRA_TEXT, selectedEpisode.url);
			startActivity(Intent.createChooser(i, "Share "
					+ selectedEpisode.title));
			break;
		case 3:
			markEpisodeSeen(selectedEpisode);
			break;
		case 2:
			markEpisodeUnseen(selectedEpisode);
			break;

		}

		return true;
	}

	private void markEpisodeUnseen(TvShowEpisode selectedEpisode) {
		pg = ProgressDialog.show(this, "Please Wait",
				"Marking episode as unseen");
		new markEpisodeUnseen().execute(selectedEpisode);
	}

	private void markEpisodeSeen(TvShowEpisode selectedEpisode) {
		pg = ProgressDialog
				.show(this, "Please Wait", "Marking episode as seen");
		new markEpisodeSeen().execute(selectedEpisode);

	}

	public class markEpisodeUnseen extends
			AsyncTask<TvShowEpisode, String, String> {
		Exception e = null;

		@Override
		protected String doInBackground(TvShowEpisode... params) {

			try {
				TvShowEpisode episode = params[0];
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				manager.showService().episodeUnseen(show.imdbId)
						.episode(episode.season, episode.number).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
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
				Blooye.goBlooey(seasonActivity, e);
			} else {

				pg.dismiss();
				updateSeason(activeSeason);
			}
		}
	}

	public class markEpisodeSeen extends
			AsyncTask<TvShowEpisode, String, String> {
		Exception e = null;

		@Override
		protected String doInBackground(TvShowEpisode... params) {

			try {
				TvShowEpisode episode = params[0];
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				manager.showService().episodeSeen(show.imdbId)
						.episode(episode.season, episode.number).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
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
				Blooye.goBlooey(seasonActivity, e);
			} else {

				pg.dismiss();
				updateSeason(activeSeason);
			}
		}
	}
}
