package com.tvshowtrakt.adapters;

import java.util.ArrayList;

import com.tvshowtrakt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {

	// Tiago
	private Context mContext;
	private ArrayList<String> mItems;

	public GalleryAdapter(Context c, ArrayList<String> items) {
		mContext = c;
		mItems = items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.grid_item, null);

			ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);

			TextView tv = (TextView) v.findViewById(R.id.grid_item_text);

			switch (position) {
			case 0:
				iv.setImageResource(R.drawable.icon_calendar);
				tv.setText("Calendar");
				break;
			case 1:
				iv.setImageResource(R.drawable.icon_library);
				tv.setText("Library");
				break;
			case 2:
				iv.setImageResource(R.drawable.icon_seen);
				tv.setText("Seen");
				break;
			case 3:
				iv.setImageResource(R.drawable.icon_friends);
				tv.setText("Friends");
				break;
			case 4:
				iv.setImageResource(R.drawable.icon_watchlist);
				tv.setText("Watchlist");
				break;
			case 5:
				iv.setImageResource(R.drawable.icon_history);
				tv.setText("History");
				break;
			case 6:
				iv.setImageResource(R.drawable.icon_profile);
				tv.setText("Profile");
				break;
			case 7:
				iv.setImageResource(R.drawable.icon_recommendations);
				tv.setText("Recommended");
				break;
			case 8:
				iv.setImageResource(R.drawable.iconrecommendations);
				tv.setText("My Shows");
				break;
			}
		}
		return v;

	}
}