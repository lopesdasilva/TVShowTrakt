package com.tvshowtrakt.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.tvshowtrakt.R;

public class LazyAdapterGalleryCalendar extends BaseAdapter {

	private Activity activity;
	private List<CalendarTvShowEpisode> episodes;
	private LayoutInflater inflater;
	private AQuery aq;

	public LazyAdapterGalleryCalendar(Activity activity,
			List<CalendarTvShowEpisode> episodes) {
		this.activity = activity;
		this.episodes = episodes;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.aq = new AQuery(activity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return episodes.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.calendar_itemgallery, null);

		aq = aq.recycle(vi);

		if (episodes.get(position).episode.watched)
			aq.id(R.id.imageViewSeenTag).visible();
		aq.id(R.id.textViewEpisodeTitle).text(
				episodes.get(position).episode.title);
		aq.id(R.id.textViewEpisodeNumber).text(
				episodes.get(position).episode.season + "x"
						+ episodes.get(position).episode.number);
		aq.id(R.id.textViewShowTitle).text(episodes.get(position).show.title);
		aq.id(R.id.imageViewEpisode).image(
				episodes.get(position).episode.images.screen, true, true, 200,
				0);

		return vi;
	}

}
