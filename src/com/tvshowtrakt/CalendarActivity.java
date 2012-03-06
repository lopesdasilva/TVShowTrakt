package com.tvshowtrakt;

import java.util.Date;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.tvshowtrakt.adapters.LazyAdapterListCalendar;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Actividade que é iniciada quando é carregado em calendário
 */
public class CalendarActivity extends GDActivity {

	private boolean login;
	private String password;
	private String username;
	public String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private CalendarActivity calendarActivity;
	private AQuery aq;
	private ProgressDialog pg;
	private Date d;
	public List<CalendarDate> calendarList;
	public int selectedShow;

	/*
	 * (non-Javadoc)
	 * 
	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.calendar);
		setTitle("Calendar");

		calendarActivity = this;
		getPrefs();

		aq = new AQuery(this);
		d = new Date();
		// new downloadCalendarInfo().execute(1);
		thisWeek(null);
		aq.id(R.id.textViewThisWeek).clicked(this, "thisWeek");
		aq.id(R.id.textViewLastWeek).clicked(this, "lastWeek");
		aq.id(R.id.textViewNextWeek).clicked(this, "nextWeek");

	}

	public void lastWeek(View button) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Calendar", true,
				true);
		new downloadCalendarInfo().execute(0);
	}

	public void thisWeek(View button) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Calendar", true,
				true);
		d = new Date();
		new downloadCalendarInfo().execute(1);
	}

	public void nextWeek(View button) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Calendar", true,
				true);
		new downloadCalendarInfo().execute(2);
	}

	public void updateCurrentWeek() {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Calendar", true,
				true);
		new downloadCalendarInfo().execute(1);
	}

	public class downloadCalendarInfo extends
			AsyncTask<Integer, String, List<CalendarDate>> {

		private Exception e = null;

		@Override
		protected List<CalendarDate> doInBackground(Integer... params) {

			try {
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				long i = 604800000;
				switch (params[0]) {
				case 0:
					d.setTime(d.getTime() - i);
					break;
				case 1:
					d.setTime(d.getTime());
					break;
				case 2:
					d.setTime(d.getTime() + i);
					break;
				}

				return manager.userService().calendarShows(username).date(d)
						.fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		protected void onPostExecute(List<CalendarDate> result) {
			calendarList = result;
			if (e == null) {
				LazyAdapterListCalendar calendarListAdapter = new LazyAdapterListCalendar(
						calendarActivity, result, new AQuery(calendarActivity));
				ListView l = (ListView) findViewById(R.id.listViewCalendar);
				l.setAdapter(calendarListAdapter);
				registerForContextMenu(l);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo cmi = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(calendarList.get(cmi.position).episodes
				.get(selectedShow).show.title);
		if (calendarList.get(cmi.position).episodes.get(selectedShow).episode.watched)
			menu.add(1, cmi.position, 0, "Mark as unseen");
		else
			menu.add(1, cmi.position, 0, "Mark as seen");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// "list:"+item.getItemId()+" item: "+selectedShow

		CalendarTvShowEpisode show = calendarList.get(item.getItemId()).episodes
				.get(selectedShow);

		markEpisodeSeen(show);

		return true;
	}

	private void markEpisodeSeen(CalendarTvShowEpisode show) {
		if (show.episode.watched) {
			Toast.makeText(this, "Marking " + show.show.title + " as unseen.",
					Toast.LENGTH_SHORT).show();
			new markEpisodeUnSeen().execute(show);
		} else {
			Toast.makeText(this, "Marking " + show.show.title + " as seen.",
					Toast.LENGTH_SHORT).show();
			new markEpisodeSeen().execute(show);
		}
	}

	public class markEpisodeUnSeen extends
			AsyncTask<CalendarTvShowEpisode, String, List<CalendarDate>> {

		private Exception e = null;

		@Override
		protected List<CalendarDate> doInBackground(
				CalendarTvShowEpisode... params) {

			try {
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				manager.showService()
						.episodeUnseen(params[0].show.imdbId)
						.episode(params[0].episode.season,
								params[0].episode.number).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		protected void onPostExecute(List<CalendarDate> result) {
			if (e == null) {
				Toast.makeText(calendarActivity, "Success", Toast.LENGTH_SHORT)
						.show();
				updateCurrentWeek();

			} else
				goBlooey(e);
		}

	}

	public class markEpisodeSeen extends
			AsyncTask<CalendarTvShowEpisode, String, List<CalendarDate>> {

		private Exception e = null;

		@Override
		protected List<CalendarDate> doInBackground(
				CalendarTvShowEpisode... params) {

			try {
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				manager.showService()
						.episodeSeen(params[0].show.imdbId)
						.episode(params[0].episode.season,
								params[0].episode.number).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		protected void onPostExecute(List<CalendarDate> result) {
			if (e == null) {
				Toast.makeText(calendarActivity, "Success", Toast.LENGTH_SHORT)
						.show();
				updateCurrentWeek();

			} else
				goBlooey(e);
		}

	}
}
