package com.tvshowtrakt.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.CalendarDate;
import com.tvshowtrakt.CalendarActivity;
import com.tvshowtrakt.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;

public class LazyAdapterListCalendar extends BaseAdapter {
	private CalendarActivity activity;
	private List<CalendarDate> listCalendarDate;
	LayoutInflater inflater;
	private AQuery aq;
	Gallery gallery;

	public LazyAdapterListCalendar(CalendarActivity calendarActivity,
			List<CalendarDate> listCalendarDate, AQuery aquery) {
		activity = calendarActivity;
		this.listCalendarDate = listCalendarDate;

		aq = aquery;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listCalendarDate.size();
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
			vi = inflater.inflate(R.layout.calendar_itemlist, null);
		aq = aq.recycle(vi);
		SimpleDateFormat sdf1 = new SimpleDateFormat("E MMM dd, yyyy");
		aq.id(R.id.textView1).text(
				sdf1.format(listCalendarDate.get(position).date));

		// para a galleria ficar alinhada a esquerda
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		LazyAdapterGalleryCalendar calendarGalleryAdapter = new LazyAdapterGalleryCalendar(
				activity, listCalendarDate.get(position).episodes);
		gallery = (Gallery) vi.findViewById(R.id.galleryCalendar);

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				activity.selectedShow = arg2;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// para a galleria ficar alinhada a esquerda
		MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
		mlp.setMargins(-(metrics.widthPixels / 2), mlp.topMargin,
				mlp.rightMargin, mlp.bottomMargin);
		gallery.setAdapter(calendarGalleryAdapter);

		return vi;
	}

}