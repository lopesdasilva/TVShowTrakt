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

import greendroid.app.GDActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShowActivity extends GDActivity {

	private TvShow show;
	private ImageView mPoster;
	private ImageView mFanart;
	boolean login;
	String username;
	String password;
	public String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";

	ViewPagerAdapterSeasons mAdapter;
	TitlePageIndicator mIndicator;
	ViewPager mPager;

	private LinearLayout mShowInfo;
	private LinearLayout mLoading;
	public ListView mListSeasons;
	public Activity showActivity;
	private AQuery aq;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.show);

		Bundle extras = getIntent().getExtras();
		show = (TvShow) extras.get("Show");
		
		setTitle(show.title);
		
		showActivity = this;
		aq= new AQuery(this);
		// Obter as preferências da aplicação
		getPrefs();

	
		
		
		mFanart = (ImageView) findViewById(R.id.imageViewFanArt);
		mPoster = (ImageView) findViewById(R.id.imageViewPoster);

		Bitmap poster = aq.getCachedImage(R.drawable.poster);
		Bitmap placeholder =aq.getCachedImage(R.drawable.placeholder);
		aq.id(R.id.imageViewFanArt).image(show.images.fanart,true,true,200,0,placeholder,AQuery.FADE_IN);
		aq.id(R.id.imageViewPoster).image(show.images.poster,true,true,90,0,poster,AQuery.FADE_IN);
		aq.id(R.id.textViewPercentagedLove).text(show.ratings.percentage+" %");
		aq.id(R.id.textViewVotes).text(show.ratings.votes+" votes");

		mAdapter = new ViewPagerAdapterSeasons(getApplicationContext());
		//
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		//
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
//		mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//					@Override
//					public void onPageSelected(int position) {
//						switch (position) {
//
//						case 1:
//							
//							break;
//						}
//
//					}
//
//					@Override
//					public void onPageScrolled(int arg0, float arg1, int arg2) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onPageScrollStateChanged(int arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				});

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
		mLoading = (LinearLayout) findViewById(R.id.loading);
		mLoading.setVisibility(LinearLayout.VISIBLE);
		mShowInfo = (LinearLayout) findViewById(R.id.showInfo);
		mShowInfo.setVisibility(LinearLayout.INVISIBLE);
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

				// ServiceManager manager = new ServiceManager();
				// manager.setAuthentication(username,
				// new Password().parseSHA1Password(password));
				// manager.setApiKey(apikey);
				//
				// TvShow t = manager.showService().summary(params[0]).fire();
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
				
				TextView mOverview = (TextView) findViewById(R.id.textView_overview);
				TextView mCountry = (TextView) findViewById(R.id.textView_country);
				TextView mAirs = (TextView) findViewById(R.id.textView_airs);
				TextView mPremiered = (TextView) findViewById(R.id.textView_premiered);
				TextView mRuntime = (TextView) findViewById(R.id.textView_runtime);
				TextView mCertification = (TextView) findViewById(R.id.textView_certification);
				// TextView mNextEpisode = (TextView) findViewById(R.id.text);

				mOverview.setText(result.overview);
				mCountry.setText(result.country);
				mAirs.setText(result.airTime + " on " + result.network);
				SimpleDateFormat sdf1= new SimpleDateFormat("MMM dd, yyyy"); 
				mPremiered.setText( sdf1.format(result.firstAired));
				mRuntime.setText(result.runtime + "m");
				mCertification.setText(result.certification);

				mShowInfo.setVisibility(LinearLayout.VISIBLE);
				mLoading.setVisibility(LinearLayout.GONE);
			} else
				goBlooey(e);
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
				
				for (TvShowSeason showSeason : result) {
					number[i] = showSeason.season + "";
					episodesNumber[i]=showSeason.episodes.count+" episodes";
					poster[i] = showSeason.images.poster;
					i++;
				}
				mListSeasons = (ListView) findViewById(R.id.listViewSeasons);
				LazyAdapterListSeasons lazyAdapter = new LazyAdapterListSeasons(
						showActivity, number, poster,episodesNumber);
				mListSeasons.setAdapter(lazyAdapter);
				
//				mListSeasons.setVisibility(ListView.VISIBLE);
//				mPager.setVisibility(LinearLayout.VISIBLE);
//				mShowInfo.setVisibility(LinearLayout.VISIBLE);
//				mLoading.setVisibility(LinearLayout.GONE);
			} else
				goBlooey(e);
		}
	}

	private class downloadShowExtraInfo extends AsyncTask<String, Void, TvShow> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected TvShow doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				List<TvShow> showList = manager.userService()
						.libraryShowsAll(username).fire();
				for (TvShow s : showList) {
					if (s.imdbId == show.imdbId)
						return s;
				}
				new Exception();

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
				ImageView mSeen = (ImageView) findViewById(R.id.imageViewSeen);

			} else
				goBlooey(e);
		}
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
