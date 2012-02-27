package com.tvshowtrakt;

import greendroid.app.GDActivity;
import android.app.Activity;
import android.os.Bundle;

public class WatchlistActivity extends GDActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.watchlist);
		setTitle("Watchlist");
	}

}