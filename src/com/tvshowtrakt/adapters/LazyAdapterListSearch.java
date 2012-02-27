package com.tvshowtrakt.adapters;

import imageloaders.ImageLoader;

import com.tvshowtrakt.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapterListSearch extends BaseAdapter {

	private Activity activity;
	private String[] data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private String[] text;
	private boolean[] seen;
	private String[] percentage;
	private String[] votes;
	private String[] overview;

	public LazyAdapterListSearch(Activity a, String[] d, String[] text, boolean[] mSeen,
			String[] p, String[] v, String[] o) {
		this.text = text;
		this.seen = mSeen;
		activity = a;
		data = d;
		percentage = p;
		votes = v;
		overview = o;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public LazyAdapterListSearch(Activity a, String[] d, String[] text, boolean[] mSeen) {
		this.text = text;
		this.seen = mSeen;
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
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
			vi = inflater.inflate(R.layout.itemlist_search, null);

		ImageView mSeen = (ImageView) vi.findViewById(R.id.imageViewSeen);
		if (seen[position])
			mSeen.setVisibility(ImageView.VISIBLE);
		else
			mSeen.setVisibility(ImageView.GONE);
		TextView mtextPerc = (TextView) vi.findViewById(R.id.textViewLovedPerc);
		mtextPerc.setText(percentage[position]);

		TextView mtext = (TextView) vi.findViewById(R.id.text);
		mtext.setText(text[position]);

		TextView mOverview = (TextView) vi
				.findViewById(R.id.textViewDescription);
		mOverview.setText(overview[position]);

		TextView mVotes = (TextView) vi.findViewById(R.id.textViewVotes);
		mVotes.setText(votes[position]);

		ImageView image = (ImageView) vi.findViewById(R.id.image);

		imageLoader.DisplayImage(data[position], activity, image);

		return vi;
	}
}