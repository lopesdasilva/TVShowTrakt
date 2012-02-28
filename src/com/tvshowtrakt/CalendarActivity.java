package com.tvshowtrakt;

import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.tvshowtrakt.adapters.LazyAdapterListSeasons;

import greendroid.app.GDActivity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
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
	private CalendarActivity showActivity;
	ListView mListSeasons;
	/* (non-Javadoc)
	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.show_seasons);
		setTitle("Calendar haoo");

		showActivity=this;
		getPrefs();
	
		
		mListSeasons = (ListView) findViewById(R.id.listViewSeasons);
		new downloadSeasons().execute("tt0412142");
		
		
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
	
	
	private class downloadSeasons extends
	AsyncTask<String, Void, List<TvShowSeason>> {
private Exception e = null;

/**
 * primeiro método a correr, usar o manager para obter os dados da api
 */
@Override
protected List<TvShowSeason> doInBackground(String... params) {

	try {

		ServiceManager manager = new ServiceManager();
		manager.setAuthentication(username,
				new Password().parseSHA1Password(password));
		manager.setApiKey(apikey);
		//
		return manager.showService().seasons(params[0]).fire();

	} catch (Exception e) {
		this.e = e;
	}
	return null;
}

/**
 * Os resultados são passados para aqui e depois tratados aqui.
 */
protected void onPostExecute(List<TvShowSeason> result) {
	if (e == null) {
		TextView t = (TextView) findViewById(R.id.textView1);
		t.setText("Number of Seasons " + result.size());

		int i = 0;
		String number[] = new String[result.size()];
		
		String poster[] = new String[result.size()];
		
		for (TvShowSeason showSeason : result) {
			number[i] = showSeason.season + "";
			poster[i] = showSeason.images.poster;
			i++;
		}

		LazyAdapterListSeasons lazyAdapter = new LazyAdapterListSeasons(
				showActivity, number, poster);
		mListSeasons.setAdapter(lazyAdapter);

//		mShowInfo.setVisibility(LinearLayout.VISIBLE);
//		mLoading.setVisibility(LinearLayout.GONE);
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