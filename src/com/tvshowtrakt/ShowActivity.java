package com.tvshowtrakt;

import java.text.SimpleDateFormat;
import java.util.List;


import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.tvshowtrakt.adapters.LazyAdapterListSeasons;
import com.tvshowtrakt.adapters.ViewPagerAdapterSeasons;
import com.viewpagerindicator.TitlePageIndicator;

import extras.Blooye;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ShowActivity extends GDActivity {

	private static final int SHARE = 0;
	private static final int SEARCH = 1;
	private TvShow show;
	boolean login;
	String username;
	String password;
	public String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";

	ViewPagerAdapterSeasons mAdapter;
	TitlePageIndicator mIndicator;
	ViewPager mPager;

	private LinearLayout mShowInfo;
	public ListView mListSeasons;
	public Activity showActivity;
	private AQuery aq;
	public List<TvShowSeason> listSeasons;
	ProgressDialog pg;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.show);

		Bundle extras = getIntent().getExtras();
		show = (TvShow) extras.get("Show");
		
		setTitle(show.title);
		
		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Share, SHARE);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		showActivity = this;
		aq= new AQuery(this);
		// Obter as preferências da aplicação
		getPrefs();

		mAdapter = new ViewPagerAdapterSeasons(getApplicationContext());
		//
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		//
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		// primeira vez para fazer o update
		updateSeason(show.imdbId);

		updateShow(show.imdbId);
		// Update Assincrono de elementos extra do show.
//		new downloadShowExtraInfo().execute(show.imdbId);

	}

	protected void updateSeason(String imdbId) {
//		mLoading= (LinearLayout) findViewById(R.id.loading);
//		mLoading.setVisibility(LinearLayout.VISIBLE);
//		mPager.setVisibility(LinearLayout.INVISIBLE);
//		mShowInfo = (LinearLayout) findViewById(R.id.showInfo);
//		mShowInfo.setVisibility(LinearLayout.INVISIBLE);
		// TODO: Async Task
		
//		mListSeasons.setVisibility(ListView.INVISIBLE);
		new downloadSeasons().execute(imdbId);

	}

	private void updateShow(String imdbID) {
		pg= ProgressDialog.show(showActivity, "Please Wait", "Loading "+show.title);
		aq.id(R.id.linearLayoutImages).invisible();
		aq.id(R.id.showInfo).invisible();
		new downloadShow().execute(imdbID);

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

	private class downloadShow extends AsyncTask<String, Void, TvShow> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected TvShow doInBackground(String... params) {

			try {
				if(show.overview==null){
				 ServiceManager manager = new ServiceManager();
				 manager.setAuthentication(username,
				 new Password().parseSHA1Password(password));
				 manager.setApiKey(apikey);
				//
				 show = manager.showService().summary(params[0]).fire();
				}
				return show;

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(TvShow result) {
			if (e == null) {
				Bitmap poster = aq.getCachedImage(R.drawable.poster);
				Bitmap placeholder =aq.getCachedImage(R.drawable.placeholder);
				aq.id(R.id.imageViewFanArt).image(show.images.fanart,true,true,200,0,placeholder,AQuery.FADE_IN);
				aq.id(R.id.imageViewPoster).image(show.images.poster,true,true,90,0,poster,AQuery.FADE_IN);
				aq.id(R.id.textViewPercentagedLove).text(show.ratings.percentage+" %");
				aq.id(R.id.textViewVotes).text(show.ratings.votes+" votes");
				
				
				aq.id(R.id.textView_overview).text(result.overview);
				aq.id(R.id.textView_certification).text(result.certification);
				aq.id(R.id.textView_country).text(result.country);
				aq.id(R.id.textView_runtime).text(result.runtime+"");
				aq.id(R.id.textView_airs).text(result.airTime + " on " + result.network);
				SimpleDateFormat sdf1= new SimpleDateFormat("MMM dd, yyyy"); 
				aq.id(R.id.textView_premiered).text(sdf1.format(result.firstAired));
				aq.id(R.id.linearLayoutImages).visible();
				aq.id(R.id.showInfo).visible();
				pg.dismiss();
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(showActivity,e);
		}
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
				
				int i = 0;
				String number[] = new String[result.size()];
				String episodesNumber[] = new String[result.size()];
				String poster[] = new String[result.size()];
				listSeasons=result;
				for (TvShowSeason showSeason : result) {
					number[i] = showSeason.season + "";
					episodesNumber[i]=showSeason.episodes.count+" episodes";
					poster[i] = showSeason.images.poster;
					
					i++;
				}
				
				mListSeasons = (ListView) findViewById(R.id.listViewSeasons);
				LazyAdapterListSeasons lazyAdapter = new LazyAdapterListSeasons(
						showActivity, number, poster,episodesNumber);
				mListSeasons.setOnItemClickListener( new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent i = new Intent(getApplicationContext(),
								SeasonActivity.class);
						i.putExtra("Show", show);
						i.putExtra("Season",listSeasons.get(arg2).season );
						startActivity(i);
						
					}
				});
				mListSeasons.setAdapter(lazyAdapter);
				
//				mListSeasons.setVisibility(ListView.VISIBLE);
//				mPager.setVisibility(LinearLayout.VISIBLE);
//				mShowInfo.setVisibility(LinearLayout.VISIBLE);
//				mLoading.setVisibility(LinearLayout.GONE);
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(showActivity,e);
		}
	}

	private class downloadShowExtraInfo extends AsyncTask<String, Void, List<TvShow>> {
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

				return  manager.userService()
						.libraryShowsAll(username).fire();
				
			

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
				
				for (TvShow s : result) {
					if (s.imdbId == show.imdbId)
						aq.id(R.id.imageViewSeen).visible();
				}
				

			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(showActivity,e);
		}
	}


	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case SHARE:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT, show.title);
			i.putExtra(Intent.EXTRA_TEXT, show.url);
			startActivity(Intent.createChooser(i, "Share "+show.title));
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
