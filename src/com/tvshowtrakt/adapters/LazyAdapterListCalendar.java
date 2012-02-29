package com.tvshowtrakt.adapters;


import java.text.SimpleDateFormat;
import java.util.List;

import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.tvshowtrakt.CalendarActivity;
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
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapterListCalendar extends BaseAdapter {

	private Activity activity;
	private String[] data;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader;
	private List<CalendarDate> calendarDate;
	
	
	
	public LazyAdapterListCalendar(Activity calendarActivity,
			List<CalendarDate> listCalendarDate) {
		activity=calendarActivity;
		this.calendarDate=listCalendarDate;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
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
			SimpleDateFormat sdf1= new SimpleDateFormat("E MMM dd, yyyy"); //voce pode usar outras m‡scaras
			t.setText(sdf1.format(calendarDate.get(position).date));
			
			
			String mFanArt[] = new String[calendarDate.get(position).episodes.size()];
			String mName[] = new String[calendarDate.get(position).episodes.size()];
			String mEpisode[] = new String[calendarDate.get(position).episodes.size()];
			String mNumber[] = new String[calendarDate.get(position).episodes.size()];
			int i=0;
			for (CalendarTvShowEpisode e: calendarDate.get(position).episodes){
				mFanArt[i]=e.episode.images.screen;
				mName[i]=e.show.title;
				mNumber[i]=e.episode.season +"x"+e.episode.number;
				mEpisode[i]=e.episode.title;
				i++;
			}
			
			//para a galleria ficar alinhada a esquerda
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			
			LazyAdapterGalleryEpisodes galleryEpisodesAdapter = new LazyAdapterGalleryEpisodes(activity, mFanArt, mName, mEpisode,mNumber);
			Gallery mGalleryEpisodes = (Gallery) vi.findViewById(R.id.galleryEpisodes);
			
			//para a galleria ficar alinhada a esquerda
			MarginLayoutParams mlp = (MarginLayoutParams) mGalleryEpisodes.getLayoutParams();
			mlp.setMargins(-(metrics.widthPixels/2), 
			               mlp.topMargin, 
			               mlp.rightMargin, 
			               mlp.bottomMargin
			);
			
			mGalleryEpisodes.setAdapter(galleryEpisodesAdapter);
			
			
			
			
					//		ImageView image = (ImageView) vi.findViewById(R.id.image);
//
//		imageLoader.DisplayImage(data[position], activity, image);

		return vi;
	}
}