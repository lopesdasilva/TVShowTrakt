package com.tvshowtrakt.adapters;


import com.tvshowtrakt.R;

import imageloaders.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapterListSeasons extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader;
	private String[] number; 
	private String[] poster;
	private String[] episodesNumber;
	
	
	public LazyAdapterListSeasons(Activity a, String[] number, String[] poster,String[] episodesNumber) {
		this.number=number;
		this.poster=poster;
		this.activity=a;
		this.episodesNumber=episodesNumber;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return poster.length;
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
			vi = inflater.inflate(R.layout.show_seasons_itemlist, null);

		TextView mEpNumber = (TextView) vi.findViewById(R.id.textViewEpisodesNumber);
		mEpNumber.setText(episodesNumber[position]);

		TextView mNumber = (TextView) vi.findViewById(R.id.textViewSeasonNumber);
		mNumber.setText("Season "+number[position]);

		ImageView image = (ImageView) vi.findViewById(R.id.image);

		imageLoader.DisplayImage(poster[position], activity, image);

		return vi;
	}
}