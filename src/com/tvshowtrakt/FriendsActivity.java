package com.tvshowtrakt;

import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.UserProfile;
import com.tvshowtrakt.adapters.LazyAdapterListFriends;

import greendroid.app.GDActivity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

// TODO: Auto-generated Javadoc
/**
 * Actividade que é iniciada quando é carregado em friends
 */
public class FriendsActivity extends GDActivity {

	private boolean login;
	private String username;
	private String password;

	public String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private FriendsActivity friendsActivity;
	public ListView mFriendList;
	public LazyAdapterListFriends lazyAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.friends);
		setTitle("Friends");

		friendsActivity=this;
		
		mFriendList = (ListView) findViewById(R.id.friendsList);
		
		getPrefs();
		new downloadFriendInfo().execute();

	}

	private class downloadFriendInfo extends
			AsyncTask<String, String, List<UserProfile>> {

		private Exception e;

		@Override
		protected List<UserProfile> doInBackground(String... params) {
			try {
				ServiceManager manager = new ServiceManager();

				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				return manager.userService().friends(username).fire();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				this.e = e;
			}

			return null;
		}

		protected void onPostExecute(List<UserProfile> result) {
			if (e == null) {
				

//				int i = 0;
//				for (UserProfile userProfile : result) {
//					nomes[i] = userProfile.username;
//					localizacao[i] = userProfile.location;
////					genero[i] = percorre.gender.name();
//					i++;
//				}

				lazyAdapter = new LazyAdapterListFriends(friendsActivity,result);
				mFriendList.setAdapter(lazyAdapter);

//				mFriendList.setVisibility(ListView.VISIBLE);
				
				
			} else
				goBlooey(e);

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
	 * Em caso de erro a excepção será tratada aqui.
	 */
	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());

		builder.setTitle("Connection Error")
				.setMessage("Movie Trakt can not connect with trakt service")
				.setPositiveButton("OK", null).show();
	}
}