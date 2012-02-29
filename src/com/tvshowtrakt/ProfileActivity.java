package com.tvshowtrakt;

import java.util.ArrayList;
import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterGalleryTrending;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends GDActivity {

	private boolean login;
	private String username;
	private String password;
	
	private static final int SEARCH = 0;
	private ProfileActivity profileActivity;
	private String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.profile);
		setTitle("Profile");
		
		profileActivity=this;
		//Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		
		//Obter as preferências da aplicação
				getPrefs();
				new Watched().execute();
				new Loved().execute();
				new Collected().execute();
				
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
	
	//Asynctask para obtenção dos Shows visualizados.
	private class Watched extends
			AsyncTask<String, Void, List<TvShow>> {
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
				return manager.userService().libraryShowsWatched(username).fire();
				
				
				
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
				
				TextView showswatched = (TextView) findViewById(R.id.showswatched);
				showswatched.setText(result.size()+"");
				int watched_episodes =0;
//				for(TvShow ts: result){
//					watched_episodes = watched_episodes + ts.episodes.size();
//				}
//				TextView episodeswatched = (TextView) findViewById(R.id.episodeswatched);
//				episodeswatched.setText(watched_episodes+"");
				
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
	
	//Asynctask para obtenção dos Shows que o utilizador Gosta.
	private class Loved extends
			AsyncTask<String, Void, List<TvShow>> {
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
				return manager.userService().libraryShowsLoved(username).fire();
				
				
				
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
				showsloved.setText(result.size()+"");
				int watched_episodes =0;
//				for(TvShow ts: result){
//					watched_episodes = watched_episodes + ts.episodes.size();
//				}
//				TextView episodeswatched = (TextView) findViewById(R.id.episodeswatched);
//				episodeswatched.setText(watched_episodes+"");
				
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
	
	//Asynctask para obtenção dos Shows que o utilizador Gosta.
		private class Collected extends
				AsyncTask<String, Void, List<TvShow>> {
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
					return manager.userService().libraryShowsCollection(username).fire();
					
					
					
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
					showscollected.setText(result.size()+"");
					int watched_episodes =0;
//					for(TvShow ts: result){
//						watched_episodes = watched_episodes + ts.episodes.size();
//					}
//					TextView episodeswatched = (TextView) findViewById(R.id.episodeswatched);
//					episodeswatched.setText(watched_episodes+"");
					
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
	
}