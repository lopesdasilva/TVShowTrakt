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

public class LazyAdapterGalleryTrending extends BaseAdapter {

	private Activity activity;
	private String[] data;
	private String[] mNames;
	private static LayoutInflater inflater = null;
	public ImageLoaderMedium imageLoader;
	private boolean[] mSeen;
	private AQuery aq;

	public LazyAdapterGalleryTrending(Activity a, String[] d,
			String[] moviesNames, boolean[] moviesSeen) {
		mNames = moviesNames;
		mSeen = moviesSeen;
		activity = a;
		data = d;
		aq = new AQuery(activity);
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new
		// ImageLoaderMedium(activity.getApplicationContext());
	}

	public int getCount() {
		return data.length;
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
			vi = inflater.inflate(R.layout.itemgallery, null);

		ImageView seen = (ImageView) vi.findViewById(R.id.seentag);
		if (mSeen[position])
			seen.setVisibility(ImageView.VISIBLE);
		aq = aq.recycle(vi);
		Bitmap placeholder = aq.getCachedImage(R.drawable.poster);
		aq.id(R.id.image).image(data[position], true, true, 90, 0, placeholder,
				AQuery.FADE_IN);
		return vi;
	}
}