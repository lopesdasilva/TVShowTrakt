package com.tvshowtrakt;

import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;

import extras.Blooye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";

	private EditText mUsername;
	private EditText mPassword;
	private TextView mForgot;
	private ImageView mSignin;
	private ProgressBar mProgressBar;

	public LoginActivity loginActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		
		loginActivity=this;
		// Variaveis de elementos do layout a ser usados
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mForgot = (TextView) findViewById(R.id.forgot);
		mSignin = (ImageView) findViewById(R.id.signin);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mSignin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Signing in",
						Toast.LENGTH_SHORT).show();
				mProgressBar.setVisibility(ProgressBar.VISIBLE);
				new Signin().execute();

			}
		});

		// TODO: Ver se da na API (penso que não dê)
		//TODO: substituir por new account?
		mForgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Forgot",
						Toast.LENGTH_SHORT).show();

			}
		});

	}
	//Asynctask para login no trakt.tv
	private class Signin extends AsyncTask<String, Void, String[]> {
		private Exception e = null;

		@Override
		protected String[] doInBackground(String... params) {

			try {
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(mUsername.getText().toString(),
						new Password().parseSHA1Password(mPassword.getText()
								.toString()));
				manager.setApiKey(apikey);

				Response response = manager.accountService().test().fire();

				return new String[] { response.status,
						response.message };

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(String[] result) {
			mProgressBar.setVisibility(ProgressBar.GONE);
			if (result == null)
				e = new Exception("wrong pass", new Throwable());
			if (e == null) {
				Toast.makeText(getApplicationContext(), "Signed On",
						Toast.LENGTH_SHORT).show();
				setPrefs();
				Intent i = new Intent(getApplicationContext(),
						HomeActivity.class);
				startActivity(i);
				finish();

			} else {

				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooeyLogin(loginActivity,e);
			}

		}

	}

	public void setPrefs() {

		// Set the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("login", true);

		Preference editTextPref = new Preference(getBaseContext());

		editTextPref.setKey("username");
		editTextPref.setSummary(mUsername.getText().toString());

		editor.putString("username", mUsername.getText().toString());
		editor.putString("password", mPassword.getText().toString());
		editor.commit();

	}

}
