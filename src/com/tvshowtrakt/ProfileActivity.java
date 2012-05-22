package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.entities.UserProfile;

import extras.Blooye;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends GDActivity {

	private boolean login;
	private String username;
	private String password;

	private String user;

	private static final int REFRESH = 0;
	private static final int SEARCH = 1;
	private ProfileActivity profileActivity;
	private String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private ProgressDialog pg;
	AQuery aq;
	// //ADASASDASd

	int sem = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.profile);
		setTitle("Profile");

		profileActivity = this;

		// Obter as preferências da aplicação
		getPrefs();

		user = username;
		Bundle extras = getIntent().getExtras();
		if (extras != null)
			user = (String) extras.get("User");

		// Items da ActionBar
//		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);

		aq = new AQuery(this);

		updateProfile(user);

	}

	private void updateProfile(String username) {
		pg = ProgressDialog.show(this, "Please Wait", "Loading Profile", true,
				true);
		aq.id(R.id.MainLayout).invisible();
		new Profile().execute(username);

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

	// Asynctask para obtenção dos Shows visualizados.
	private class Profile extends AsyncTask<String, Void, UserProfile> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		protected UserProfile doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				return manager.userService().profile(params[0]).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(UserProfile profile) {
			if (e == null) {
				try{
				aq.id(R.id.imageViewPhoto).image(profile.avatar);

				TextView textview_username = (TextView) findViewById(R.id.textViewUsername);
				textview_username.setText(user);
				TextView textview_location = (TextView) findViewById(R.id.textViewLocation);
				textview_location.setText(profile.gender.name() + " from "
						+ profile.location);
				TextView textview_watched_episodes = (TextView) findViewById(R.id.TextViewEpisodesWatched);
				textview_watched_episodes
						.setText(profile.stats.episodes.watched + "");
				TextView textview_watched_shows = (TextView) findViewById(R.id.TextViewShowsWatched);
				textview_watched_shows
						.setText(profile.stats.shows.library + "");

				new Loved().execute(user);
				sem++;
				if (sem == 3) {
					pg.dismiss();
					aq.id(R.id.MainLayout).visible();
				}
				}catch (Exception e){
					Blooye.goBlooey(profileActivity, e);
				}
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(profileActivity, e);
		}

	}

	// Asynctask para obtenção dos Shows que o utilizador Gosta.
	private class Loved extends AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected List<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				return manager.userService().libraryShowsLoved(params[0])
						.fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(List<TvShow> result) {
			if (e == null) {

				TextView showsloved = (TextView) findViewById(R.id.showsloved);
				showsloved.setText(result.size() + "");
				int watched_episodes = 0;
				TextView episodesloved = (TextView) findViewById(R.id.episodesloved);
				episodesloved.setText(watched_episodes + "");

				new Collected().execute(user);
				sem++;
				if (sem == 3) {
					pg.dismiss();
					aq.id(R.id.MainLayout).visible();

				}
			} else
				goBlooey(e);
		}

		/**
		 * Em caso de erro a excepção será tratada aqui.
		 */
		private void goBlooey(Throwable t) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getApplicationContext());

			builder.setTitle("Connection Error")
					.setMessage(
							"Movie Trakt can not connect with trakt service")
					.setPositiveButton("OK", null).show();
		}

	}

	// Asynctask para obtenção dos Shows que o utilizador Gosta.
	private class Collected extends AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected List<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				return manager.userService().libraryShowsCollection(params[0])
						.fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(List<TvShow> result) {
			if (e == null) {

				TextView showscollected = (TextView) findViewById(R.id.showscollected);
				showscollected.setText(result.size() + "");
				int watched_episodes = 0;
				for (TvShow show : result) {

					for (TvShowSeason season : show.seasons) {

						for (int number : season.episodes.numbers) {
							watched_episodes++;

						}
					}
				}
				TextView episodescollected = (TextView) findViewById(R.id.episodescollected);
				episodescollected.setText(watched_episodes + "");
				sem++;
				if (sem == 3) {
					pg.dismiss();
					aq.id(R.id.MainLayout).visible();
				}
			} else
				goBlooey(e);
		}

		/**
		 * Em caso de erro a excepção será tratada aqui.
		 */
		private void goBlooey(Throwable t) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getApplicationContext());

			builder.setTitle("Connection Error")
					.setMessage(
							"Movie Trakt can not connect with trakt service")
					.setPositiveButton("OK", null).show();
		}

	}

	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			updateProfile(user);
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