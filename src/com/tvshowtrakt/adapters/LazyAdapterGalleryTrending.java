package com.tvshowtrakt.adapters;

import java.util.List;

import imageloaders.ImageLoaderMedium;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.TvShow;
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
	private List<TvShow> trendingList;
	private static LayoutInflater inflater = null;
	public ImageLoaderMedium imageLoader;
	private AQuery aq;

	public LazyAdapterGalleryTrending(Activity a, List<TvShow> trendingList,
			AQuery aq) {
		this.trendingList = trendingList;
		activity = a;
		this.aq = aq;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return trendingList.size();
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

		TvShow show = trendingList.get(position);
		aq = aq.recycle(vi);

		Bitmap placeholder = aq.getCachedImage(R.drawable.poster);
		aq.id(R.id.image).image(show.images.poster, true, true, 90, 0,
				placeholder, AQuery.FADE_IN);
		return vi;
	}
}