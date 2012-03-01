package com.tvshowtrakt.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.tvshowtrakt.R;

import imageloaders.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class LazyAdapterListCalendar extends BaseAdapter {

	private Activity activity;
	private String[] data;
	private static LayoutInflater inflater = null;
	private List<CalendarDate> calendarDate;
	Gallery mGalleryEpisodes;
	LazyAdapterGalleryEpisodes galleryEpisodesAdapter;
	DisplayMetrics metrics;
	MarginLayoutParams mlp ;

	public LazyAdapterListCalendar(Activity calendarActivity,
			List<CalendarDate> listCalendarDate) {
		activity = calendarActivity;
		this.calendarDate = listCalendarDate;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	

	public int getCount() {
		return calendarDate.size();
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
			vi = inflater.inflate(R.layout.calendar_itemlist, null);

		TextView t = (TextView) vi.findViewById(R.id.textViewDate);
		SimpleDateFormat sdf1 = new SimpleDateFormat("E MMM dd, yyyy");
		t.setText(sdf1.format(calendarDate.get(position).date));

		String mFanArt[] = new String[calendarDate.get(position).episodes
				.size()];
		String mName[] = new String[calendarDate.get(position).episodes.size()];
		String mEpisode[] = new String[calendarDate.get(position).episodes
				.size()];
		String mNumber[] = new String[calendarDate.get(position).episodes
				.size()];
		boolean mWatched[] = new boolean[calendarDate.get(position).episodes
				.size()];
		int i = 0;
		for (CalendarTvShowEpisode e : calendarDate.get(position).episodes) {
			mFanArt[i] = e.episode.images.screen;
			mName[i] = e.show.title;
			mNumber[i] = e.episode.season + "x" + e.episode.number;
			mEpisode[i] = e.episode.title;
			mWatched[i] = e.episode.watched;
			i++;
		}

		// para a galleria ficar alinhada a esquerda
		metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		galleryEpisodesAdapter = new LazyAdapterGalleryEpisodes(activity,
				mFanArt, mName, mEpisode, mNumber, mWatched);
		mGalleryEpisodes = (Gallery) vi.findViewById(R.id.galleryEpisodes);
		activity.registerForContextMenu(mGalleryEpisodes);
		// para a galleria ficar alinhada a esquerda
		mlp = (MarginLayoutParams) mGalleryEpisodes
				.getLayoutParams();
		mlp.setMargins(-(metrics.widthPixels / 2), mlp.topMargin,
				mlp.rightMargin, mlp.bottomMargin);
		mGalleryEpisodes.setTag(calendarDate.get(position).date.toString());
		mGalleryEpisodes.setAdapter(galleryEpisodesAdapter);


		return vi;
	}
}