package com.tvshowtrakt;

import greendroid.app.GDActivity;
import android.app.Activity;
import android.os.Bundle;

public class LibraryActivity extends GDActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.library);
		setTitle("Library");
	}

}