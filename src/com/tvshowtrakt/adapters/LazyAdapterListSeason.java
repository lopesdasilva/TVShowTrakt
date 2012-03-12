package com.tvshowtrakt.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.tvshowtrakt.R;
import com.tvshowtrakt.SeasonActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LazyAdapterListSeason extends BaseAdapter {

	private SeasonActivity activity;
	private static LayoutInflater inflater = null;

	private List<TvShowEpisode> showList;
	AQuery aq;

	public LazyAdapterListSeason(SeasonActivity a, List<TvShowEpisode> list,
			AQuery aq) {

		activity = a;
		this.showList = list;

		this.aq = aq;
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
			vi = inflater.inflate(R.layout.season_itemlist, null);
		TvShowEpisode show = showList.get(position);
		aq = aq.recycle(vi);
		aq.id(R.id.textViewEpisodeTitle).text(show.title);
		aq.id(R.id.textViewEpisodeOverview).text(show.overview);
		aq.id(R.id.textViewEpisodeNumber).text(show.season + "x" + show.number);
		aq.id(R.id.textViewEpisodePercentage).text(
				show.ratings.percentage + " %");
		aq.id(R.id.textViewEpisodeVotes).text(show.ratings.votes + " votes");

		SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy");
		Date d= new Date();
		if (show.firstAired.after(d))
			aq.id(R.id.textViewEpisodeAirDate).text("airs "+sdf1.format(show.firstAired));
		else
			aq.id(R.id.textViewEpisodeAirDate).text("aired "+sdf1.format(show.firstAired));
		if (show.watched)
			aq.id(R.id.imageViewEpisodeSeen).visible();
		// if(show.inWatchlist)
		// aq.id(R.id.imageViewEpisodeInWatchlist).visible();
		aq.id(R.id.imageViewEpisodeScreen).image(show.images.screen, true,
				true, 300, 0);

		return vi;
	}
}