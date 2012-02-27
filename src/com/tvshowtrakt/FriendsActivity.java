package com.tvshowtrakt;

import greendroid.app.GDActivity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;




// TODO: Auto-generated Javadoc
/**
 * Actividade que Ž iniciada quando Ž carregado em friends
 */
public class FriendsActivity extends GDActivity {

	/* (non-Javadoc)
	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.friends);
		setTitle("Friends");

	}

//public class FriendsActivity extends Activity {
//
//	/* (non-Javadoc)
//	 * @see greendroid.app.GDActivity#onCreate(android.os.Bundle)
//	 */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.friends);
//		
//		
//		ViewPagerAdapter adapter = new ViewPagerAdapter( this );
//	    ViewPager pager =
//	        (ViewPager)findViewById( R.id.viewpager );
//	    TitlePageIndicator indicator =
//	        (TitlePageIndicator)findViewById( R.id.indicator );
//	    pager.setAdapter( adapter );
//	    indicator.setViewPager( pager );
//
//	}

}