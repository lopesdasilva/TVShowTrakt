package com.tvshowtrakt;

import greendroid.app.GDActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class ProfileActivity extends GDActivity {

	private boolean login;
	private String username;
	private String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.profile);
		setTitle("Profile");
		
		TextView v= (TextView) findViewById(R.id.textViewProfile);
		
		//Obter as prefer�ncias da aplica��o
				getPrefs();
		v.setText(username);
		
	}
	
	/**
	 * Metodo para obter as preferencias da applica��o
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
	 * Metodo para obter as preferencias da applica��o, quando esta � retomada,
	 * ao inv�s de iniciada
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