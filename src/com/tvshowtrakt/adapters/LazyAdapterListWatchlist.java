package com.tvshowtrakt.adapters;

import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.TvShow;
import com.tvshowtrakt.R;
import com.tvshowtrakt.WatchlistActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LazyAdapterListWatchlist extends BaseAdapter {

	private WatchlistActivity activity;
	private static LayoutInflater inflater = null;

	private List<TvShow> showList;
	AQuery aq;

	public LazyAdapterListWatchlist(WatchlistActivity a, List<TvShow> list,
			AQuery aq) {

		activity = a;
		this.showList = list;

		this.aq= aq;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return showList.size();
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
			vi = inflater.inflate(R.layout.watchlist_itemlist, null);
		TvShow show = showList.get(position);
		aq = aq.recycle(vi);
		aq.id(R.id.imageViewPoster).image(show.images.poster, true, true, 100,
				0);
		if (show.inWatchlist)
			aq.id(R.id.watchlist).visible();
		aq.id(R.id.textViewDescription).text(show.overview);
		aq.id(R.id.textViewVotes).text(show.ratings.votes+" votes");
		aq.id(R.id.textViewPercentagedLove).text(show.ratings.percentage + " %");
		aq.id(R.id.textViewShowTitle).text(show.title);

		return vi;
	}
}