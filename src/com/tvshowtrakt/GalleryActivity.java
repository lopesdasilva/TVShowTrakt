package com.tvshowtrakt;

import extras.Blooye;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.impl.entity.LaxContentLengthStrategy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.UserProfile;
import com.tvshowtrakt.adapters.GalleryAdapter;
import com.tvshowtrakt.adapters.LazyAdapterGalleryTrending;
import com.tvshowtrakt.adapters.LazyAdapterListFriends;
import com.tvshowtrakt.adapters.LazyAdapterListFriendsHistory;
import com.tvshowtrakt.adapters.LazyAdapterListLibrary;

public class GalleryActivity extends GDActivity {

	private GalleryAdapter mAdapter;
	private ArrayList<String> mItems = new ArrayList<String>();
	private boolean login;
	private String username;
	private String password;
	private String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	public LazyAdapterGalleryTrending galleryTrendingAdapter;
	public Gallery mGalleryTrending;
	public ProgressBar mProgressBarTrending;
	public TextView mTextViewUpdating;
	public GalleryActivity galleryActivity = this;
	private TextView mSeeMoreTrending;
	private AQuery aq;
	protected List<TvShow> trendingList;

	private static final int REFRESH = 0;
	private static final int SEARCH = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Atributos da actionBar
		getActionBar().setType(ActionBar.Type.Empty);
		setActionBarContentView(R.layout.home);
		setTitle("TV trakt");

		// Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);

		// Obter as preferências da aplicação
		getPrefs();

		// Variaveis de elementos do layout a ser usados
		mTextViewUpdating = (TextView) findViewById(R.id.textViewUpdating);
		mProgressBarTrending = (ProgressBar) findViewById(R.id.progressBarGalleryTrending);
		mGalleryTrending = (Gallery) findViewById(R.id.galleryTrending);
		mSeeMoreTrending = (TextView) findViewById(R.id.textViewSeeMoreTrending);

		// update da TrendingList
		updateTrending();

		// inicialiação do menuGrid
		for (int i = 0; i < 6; i++) {
			mItems.add(Integer.toString(i));
		}
		mAdapter = new GalleryAdapter(this, mItems);
		GridView g = (GridView) findViewById(R.id.gridview);
		g.setAdapter(mAdapter);
		// Acções do menuGrid
		g.setOnItemClickListener(new OnItemClickListener() {
			/**
			 * Listener para as ações da Grid TODO: pensar se vale a pena por
			 * isto numa classe nova
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int option,
					long arg3) {

				switch (option) {
				case 0:
					startActivity(new Intent(getApplicationContext(),
							CalendarActivity.class));
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(),
							LibraryActivity.class));
					break;
				case 2:
					startActivity(new Intent(getApplicationContext(),
							RecommendedActivity.class));
					break;
				case 3:
					startActivity(new Intent(getApplicationContext(),
							WatchlistActivity.class));
					break;
				case 4:
					startActivity(new Intent(getApplicationContext(),
							FriendsActivity.class));
					break;
				case 5:
					startActivity(new Intent(getApplicationContext(),
							ProfileActivity.class));
					break;

				}

			}

		});

		// Listener do See more Trending
		mSeeMoreTrending.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						TrendingActivity.class));

			}

		});

		mGalleryTrending.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent i = new Intent(getApplicationContext(),
						ShowActivity.class);
				i.putExtra("Show", trendingList.get(position));
				startActivity(i);
			}
		});

		aq = new AQuery(this);
		updateFriendsHistory();
	}

	private void updateFriendsHistory() {

		aq.id(R.id.slidingDrawerFriendsHistory).invisible();
		new downloadFriendsHistory().execute();

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

	// Asynctask para obtenção da lista de trending tv shows
	private class downloadTrending extends
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

				return manager.showService().trending().fire();

			} catch (Exception e) {
				this.e = e;
			}
			return new LinkedList<TvShow>();

		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(List<TvShow> result) {
			if (e == null) {
				trendingList = result.subList(0, 20);
				DisplayMetrics metrics = new DisplayMetrics();
				galleryActivity.getWindowManager().getDefaultDisplay()
						.getMetrics(metrics);
				// para a galleria ficar alinhada a esquerda

				galleryTrendingAdapter = new LazyAdapterGalleryTrending(
						galleryActivity, trendingList, aq);

				MarginLayoutParams mlp = (MarginLayoutParams) mGalleryTrending
						.getLayoutParams();
				mlp.setMargins(-(metrics.widthPixels / 2 + 100), mlp.topMargin,
						mlp.rightMargin, mlp.bottomMargin);
				mGalleryTrending.setAdapter(galleryTrendingAdapter);

				mProgressBarTrending.setVisibility(ProgressBar.GONE);
				mTextViewUpdating.setVisibility(TextView.GONE);
				mGalleryTrending.setVisibility(Gallery.VISIBLE);
			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(galleryActivity, e);
		}

	}

	/**
	 * Metodo para definir as acções da ActionBar
	 */
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {

		case REFRESH:
			Toast.makeText(getApplicationContext(), "Refreshing Lists",
					Toast.LENGTH_SHORT).show();
			updateTrending();
			break;

		case SEARCH:
			this.startSearch(null, false, Bundle.EMPTY, false);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);

		}
		return true;
	}

	/**
	 * Metodo a chamar para fazer o update da lista de Trending TV shows
	 */
	private void updateTrending() {
		mGalleryTrending.setVisibility(Gallery.GONE);
		mProgressBarTrending.setVisibility(ProgressBar.VISIBLE);
		mTextViewUpdating.setVisibility(TextView.VISIBLE);
		new downloadTrending().execute();

	}

	private class downloadFriendsHistory extends
			AsyncTask<String, Void, List<UserProfile>> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected List<UserProfile> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				return manager.userService().friends(username).fire();

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(List<UserProfile> result) {
			if (e == null) {
				final LinkedList<UserProfile> result_aux = new LinkedList<UserProfile>();
				for (UserProfile friend : result) {
					if (friend.watched.size() != 0
							&& friend.watched.get(0).show != null)
						result_aux.add(friend);
				}
				if (result_aux.size() != 0) {
					aq.id(R.id.slidingDrawerFriendsHistory).visible();
					LazyAdapterListFriendsHistory friendsAdapter = new LazyAdapterListFriendsHistory(
							galleryActivity, result_aux, aq);
					aq.id(R.id.listViewFriendsHistory).adapter(friendsAdapter)
							.itemClicked(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									Intent i = new Intent(
											getApplicationContext(),
											SeasonActivity.class);
									i.putExtra(
											"Show",
											result_aux.get(arg2).watched.get(0).show);
									i.putExtra(
											"Season",
											result_aux.get(arg2).watched.get(0).episode.season);
									startActivity(i);

								}
							});
				}

			} else
				/**
				 * Em caso de erro a excepção será tratada aqui.
				 */
				Blooye.goBlooey(galleryActivity, e);
		}

	}

	protected void onDestroy() {

		super.onDestroy();

		// clean the file cache when root activity exit
		// the resulting total cache size will be less than 3M
		AQUtility.cleanCacheAsync(this);

	}

}
