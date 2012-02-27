package com.tvshowtrakt;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.adapters.GalleryAdapter;
import com.tvshowtrakt.adapters.LazyAdapterGalleryTrending;

public class GalleryActivity extends GDActivity {

	private GalleryAdapter mAdapter;
	private ArrayList<String> mItems = new ArrayList<String>();
	private boolean login;
	private String username;
	private String password;
	private String apikey = "a7b42c4fb5c50a85c68731b25cc3c1ed";
	public List<TvShow> trendingList;
	public LazyAdapterGalleryTrending galleryTrendingAdapter;
	public Gallery mGalleryTrending;
	public ProgressBar mProgressBarTrending;
	public TextView mTextViewUpdating;
	public GDActivity galleryActivity = this;
	private TextView mSeeMoreTrending;

	private static final int REFRESH = 0;
	private static final int SEARCH = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Atributos da actionBar
		getActionBar().setType(ActionBar.Type.Empty);
		setActionBarContentView(R.layout.home);
		setTitle("TVShow trakt");
		
		//Items da ActionBar
		addActionBarItem(ActionBarItem.Type.Refresh2, REFRESH);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		
		//Obter as preferências da aplicação
		getPrefs();

		
		
		// Variaveis de elementos do layout a ser usados
		mTextViewUpdating = (TextView) findViewById(R.id.textViewUpdating);
		mProgressBarTrending = (ProgressBar) findViewById(R.id.progressBarGalleryTrending);
		mGalleryTrending = (Gallery) findViewById(R.id.galleryTrending);
		mSeeMoreTrending = (TextView) findViewById(R.id.textViewSeeMoreTrending);
		
		//update da TrendingList
		updateTrending();

		//inicialiação do menuGrid
		for (int i = 0; i < 9; i++) {
			mItems.add(Integer.toString(i));
		}
		mAdapter = new GalleryAdapter(this, mItems);
		GridView g = (GridView) findViewById(R.id.gridview);
		g.setAdapter(mAdapter);
		//Acções do menuGrid
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
							SeenActivity.class));

					break;
				case 3:
					startActivity(new Intent(getApplicationContext(),
							FriendsActivity.class));

					break;
				case 4:
					startActivity(new Intent(getApplicationContext(),
							WatchlistActivity.class));

					break;
				case 5:
					startActivity(new Intent(getApplicationContext(),
							HistoryActivity.class));

					break;
				case 6:
					startActivity(new Intent(getApplicationContext(),
							ProfileActivity.class));

					break;
				case 7:
					startActivity(new Intent(getApplicationContext(),
							RecommendedActivity.class));

					break;
				case 8:
					startActivity(new Intent(getApplicationContext(),
							MyShowsActivity.class));

					break;

				}

			}

		});
		
		//Listener do See more Trending
		mSeeMoreTrending.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), TrendingActivity.class));
				
			}
			
		});
		
		mGalleryTrending.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent i = new Intent(getApplicationContext(),
						ShowActivity.class);
				i.putExtra("Show", trendingList.get(position));
				startActivity(i);				
			}
		});
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

	
	
	//Asynctask para obtenção da lista de trending tv shows
	private class downloadTrending extends
			AsyncTask<String, Void, ArrayList<TvShow>> {
		private Exception e = null;

		/**
		 * primeiro método a correr, usar o manager para obter os dados da api
		 */
		@Override
		protected ArrayList<TvShow> doInBackground(String... params) {

			try {

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username,
						new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);

				List<TvShow> mlist = manager.showService().trending().fire();

				ArrayList<TvShow> d = new ArrayList<TvShow>();
				// TODO URGENTE isto é para melhorar (passar como return)
				mlist = mlist.subList(0, 10);
				trendingList = mlist;

				for (TvShow c : mlist) {

					d.add(c);

				}
				return d;

			} catch (Exception e) {
				this.e = e;
			}
			return null;
		}

		/**
		 * Os resultados são passados para aqui e depois tratados aqui.
		 */
		protected void onPostExecute(ArrayList<TvShow> result) {
			if (e == null) {

				String[] tTitle = new String[result.size()];
				String[] tPosters = new String[result.size()];
				// TODO: verificar se é possivel implementar isto (Não da!)
				boolean[] tSeen = new boolean[result.size()];
				int i = 0;
				for (TvShow t : result) {

					tPosters[i] = t.images.poster;
					tSeen[i] = false;

					tTitle[i] = t.title + " (" + t.year + ")";
					i++;
				}
				galleryTrendingAdapter = new LazyAdapterGalleryTrending(
						galleryActivity, tPosters, tTitle, tSeen);
				mGalleryTrending.setAdapter(galleryTrendingAdapter);

				mProgressBarTrending.setVisibility(ProgressBar.GONE);
				mTextViewUpdating.setVisibility(TextView.GONE);
				mGalleryTrending.setVisibility(Gallery.VISIBLE);
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
		new downloadTrending().execute();
		mProgressBarTrending.setVisibility(ProgressBar.VISIBLE);
		mTextViewUpdating.setVisibility(TextView.VISIBLE);

	}

}
