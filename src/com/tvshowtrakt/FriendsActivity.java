package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.UserProfile;
import com.tvshowtrakt.adapters.LazyAdapterListFriends;

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
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
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
	
	private ProgressDialog pg;

	private static final int REFRESH = 0;
	private static final int SEARCH = 1;
	
	AQuery aq ;

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

		
		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		friendsActivity=this;
		
		mFriendList = (ListView) findViewById(R.id.friendsList);
		aq = new AQuery(this);
		getPrefs();
		updateFriends();
		

	}

	private void updateFriends() {
		pg=ProgressDialog.show(this, "Please Wait","Loading Friends",true, true);
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

		protected void onPostExecute(final List<UserProfile> result) {
			if (e == null) {
				
				aq.id(R.id.textViewNumberOfFriends).text(result.size()+"");
				lazyAdapter = new LazyAdapterListFriends(friendsActivity,result,aq);
				mFriendList.setAdapter(lazyAdapter);
				mFriendList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent i = new Intent(getApplicationContext(),
								ProfileActivity.class);
						i.putExtra("User", result.get(arg2).username);
						startActivity(i);
						
					}
				});
				pg.dismiss();
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(friendsActivity,e);

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
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			updateFriends();
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