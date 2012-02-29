package com.tvshowtrakt;

import java.util.Date;
import java.util.List;

import views.CalendarViewPager;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterGalleryEpisodes;
import com.tvshowtrakt.adapters.LazyAdapterGalleryTrending;
import com.tvshowtrakt.adapters.LazyAdapterListCalendar;
import com.tvshowtrakt.adapters.LazyAdapterListTrending;
import com.tvshowtrakt.adapters.ViewPagerAdapterCalendar;
import com.viewpagerindicator.TitlePageIndicator;

import greendroid.app.GDActivity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Actividade que é iniciada quando é carregado em calendário
 */
public class CalendarActivity extends GDActivity {

	private boolean login;
	private String password;
	private String username;
	public String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private CalendarActivity calendarActivity;
	ListView mListSeasons;
	private ViewPagerAdapterCalendar mAdapter;
	private CalendarViewPager mPager;
	private TitlePageIndicator mIndicator;
	public boolean[] tSeen;
	Gallery mGalleryEpisodes;
	public LazyAdapterGalleryEpisodes galleryEpisodesAdapter;

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
		mAdapter = new ViewPagerAdapterCalendar(getApplicationContext());
		//
		mPager = (CalendarViewPager) findViewById(R.id.pager);
		mPager.setPagingEnabled(false);
		mPager.setAdapter(mAdapter);
		//
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setCurrentItem(1);

//		updateCalendarInfo(1);
		updateCalendarInfo(0);
	}

	private void updateCalendarInfo(int i) {
		switch (i) {
		case 0:
			// mPager.setVisibility(CalendarViewPager.INVISIBLE);
			new downloadCalendarInfo().execute("0");
			break;
		case 1:
			// mPager.setVisibility(CalendarViewPager.INVISIBLE);
			new downloadCalendarInfo().execute("1");
			break;
		case 2:
			// mPager.setVisibility(CalendarViewPager.INVISIBLE);
			new downloadCalendarInfo().execute("2");
			break;
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

	private class downloadCalendarInfo extends
			AsyncTask<String, Void, List<CalendarDate>> {
		private Exception e = null;
		int page;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected List<CalendarDate> doInBackground(String... params) {
			page = Integer.parseInt(params[0]);
			// One Week
			long i = 604800000;
			try {
				Date d = new Date();
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				switch (page) {
				case 0:
					d.setTime(d.getTime() - i);
					return manager.userService().calendarShows(username)
							.date(d).fire();
				case 1:
					return manager.userService().calendarShows(username)
							.date(d).fire();
				case 2:
					d.setTime(d.getTime() + i);
					return manager.userService().calendarShows(username)
							.date(d).fire();
				}

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(List<CalendarDate> result) {
			if (e == null) {
				LazyAdapterListCalendar lazyAdapter = new LazyAdapterListCalendar(
						calendarActivity, result);
				ListView l = null;
				switch (page) {
				case 0:
					l = (ListView) findViewById(R.id.listViewCalendar_last);
					break;
				case 1:
					l = (ListView) findViewById(R.id.listViewCalendar_this);
					break;
				case 2:
					l = (ListView) findViewById(R.id.listViewCalendar_next);
					break;
				}

				l.setAdapter(lazyAdapter);
				// mPager.setVisibility(CalendarViewPager.VISIBLE);

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