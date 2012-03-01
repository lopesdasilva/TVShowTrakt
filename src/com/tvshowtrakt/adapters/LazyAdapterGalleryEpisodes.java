package com.tvshowtrakt.adapters;

import imageloaders.ImageLoaderMedium;

import com.androidquery.AQuery;
import com.tvshowtrakt.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapterGalleryEpisodes extends BaseAdapter {

	private Activity activity;
	private String[] mFanArt;
	private String[] mNames;
	private static LayoutInflater inflater = null;
	public ImageLoaderMedium imageLoader;
	private String[] mEpisodes;
	private String[] mNumbers;
	private boolean[] mWatched;
	private AQuery listAQ;
	
//	 mFanArt, mName, mEpisode
	public LazyAdapterGalleryEpisodes(Activity a, String[] fanArt, String[] name,
			String[] episodes,String[] number,boolean watched[]) {
		mNames = name;
		mEpisodes = episodes;
		activity = a;
		mFanArt = fanArt;
		mNumbers=number;
		mWatched=watched;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listAQ= new AQuery(activity);
//		imageLoader = new ImageLoaderMedium(activity.getApplicationContext());
	}

	public int getCount() {
		return mEpisodes.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.calendar_page_itemlist_gallery, null);
		AQuery aq=listAQ.recycle(vi);
		
		aq.id(R.id.textViewShowName).text(mNames[position]);
		aq.id(R.id.textViewEpisodeName).text(mEpisodes[position]);
		aq.id(R.id.textViewEpisodeNumber).text(mNumbers[position]);
		if(mWatched[position])
		aq.id(R.id.imageViewCalendarSeen).visible();
//		TextView mName = (TextView) vi.findViewById(R.id.textViewShowName);
//		TextView mEpisode = (TextView) vi.findViewById(R.id.textViewEpisodeName);
//		
//		mName.setText(mNames[position]);
//		mEpisode.setText(mEpisodes[position]);
//		TextView mNumber = (TextView) vi.findViewById(R.id.textViewEpisodeNumber);
//				mNumber.setText(mNumbers[position]);
//		ImageView watched = (ImageView) vi.findViewById(R.id.imageViewCalendarSeen);
//		if (!mWatched[position])
//		watched.setVisibility(ImageView.INVISIBLE);

		
		
		
		aq.id(R.id.imageViewEpisode).progress(R.id.progressBarEpisode).image(mFanArt[position],true, true, 200,R.drawable.placeholder);
		return vi;
	}
}