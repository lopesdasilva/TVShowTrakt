package com.tvshowtrakt;

import greendroid.app.GDActivity;
import android.os.Bundle;


/**
 * Actividade que Ž iniciada quando Ž carregado em calend‡rio
 */
public class CalendarActivity extends GDActivity {

	/* (non-Javadoc)
	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.calendar);
		setTitle("Calendar haoo");

	}

}