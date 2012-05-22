package com.tvshowtrakt;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.LazyAdapterListRecommended;
import com.tvshowtrakt.adapters.LazyAdapterListWatchlist;

import extras.Blooye;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class RecommendedActivity extends GDActivity {

	private AQuery aq;
	private boolean rating;
	private String username;
	private String password;
	public ProgressDialog pg;
	String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	private RecommendedActivity recommendedActivity;
	private static final int REFRESH = 0;
	private static final int SEARCH = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.recommended);
		setTitle("Recommended");
		
		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		aq = new AQuery(this);
		// Obter as preferências da aplicação
		getPrefs();

		recommendedActivity = this;

		updateRecommendedList();
	}
	private void updateRecommendedList() {
		pg= ProgressDialog.show(recommendedActivity, "Please Wait", "Loading Recommended Shows",true,true);
		new updateRecommendedList().execute();
		
	}
	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username", "username");
		password = prefs.getString("password", "password");
	}
	
	private class updateRecommendedList extends AsyncTask<String, Void, List<TvShow>> {
		private Exception e = null;

		@Override
		protected List<TvShow> doInBackground(String... params) {
			List<TvShow> a = null;
			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				a = manager.recommendationsService().shows().fire();

			} catch (Exception e) {
				this.e = e;
			}
			return a;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(final List<TvShow> result) {

			if (e != null) {
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(recommendedActivity,e);
			} else {
				if (result.size() != 0) {
					LazyAdapterListRecommended adapter= new LazyAdapterListRecommended(recommendedActivity, result, aq);
					aq.id(R.id.listViewRecommended).adapter(adapter);
					aq.id(R.id.listViewRecommended).itemClicked(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Intent i = new Intent(getApplicationContext(),
									ShowActivity.class);
							i.putExtra("Show", result.get(arg2));
							startActivity(i);
							
						}
					});
					
					pg.dismiss();
				}

			}
		}
	}
	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			updateRecommendedList();
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