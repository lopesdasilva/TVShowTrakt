package com.tvshowtrakt.adapters;

import imageloaders.ImageLoaderMedium;

import com.tvshowtrakt.R;

import android.app.Activity;
import android.content.Context;
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
//	 mFanArt, mName, mEpisode
	public LazyAdapterGalleryEpisodes(Activity a, String[] fanArt, String[] name,
			String[] episodes) {
		mNames = name;
		mEpisodes = episodes;
		activity = a;
		mFanArt = fanArt;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoaderMedium(activity.getApplicationContext());
	}

	public int getCount() {
		return mFanArt.length;
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
	
		TextView mName = (TextView) vi.findViewById(R.id.textViewShowName);
		TextView mEpisode = (TextView) vi.findViewById(R.id.textViewEpisodeName);
		
		mName.setText(mNames[position]);
		mEpisode.setText(mEpisodes[position]);
		
		
		ImageView image = (ImageView) vi.findViewById(R.id.imageViewEpisode);
		
		image.setScaleType(ImageView.ScaleType.FIT_XY);
		imageLoader.DisplayImage(mFanArt[position], activity, image);
		return vi;
	}
}