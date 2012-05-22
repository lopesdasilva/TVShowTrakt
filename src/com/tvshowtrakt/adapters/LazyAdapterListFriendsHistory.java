package com.tvshowtrakt.adapters;

import java.util.LinkedList;
import java.util.List;

import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.ActivityItem;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.UserProfile;
import com.tvshowtrakt.GalleryActivity;
import com.tvshowtrakt.LibraryActivity;
import com.tvshowtrakt.R;
import com.tvshowtrakt.WatchlistActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class LazyAdapterListFriendsHistory extends BaseAdapter {

	private GalleryActivity activity;
	private static LayoutInflater inflater = null;

	List<UserProfile> friendsList;
	AQuery aq;

	public LazyAdapterListFriendsHistory(GalleryActivity a,
			List<UserProfile> list, AQuery aq) {

		activity = a;
		this.friendsList = list;

		this.aq = aq;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return friendsList.size();
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
			vi = inflater.inflate(R.layout.home_friends_itemlist, null);

		UserProfile friend = friendsList.get(position);

		
		aq = aq.recycle(vi);
		Bitmap poster;
		poster = aq.getCachedImage(R.drawable.poster);
		aq.id(R.id.imageViewFriendAvatar).image(friend.avatar, true, false,
				100, 0, poster, 0);
		aq.id(R.id.textViewFriendUsername).text(friend.username);
		aq.id(R.id.textViewFriendShowTitle).text(friend.watched.get(0).show.title);
		aq.id(R.id.textViewFriendEpisodeTitle).text(friend.watched.get(0).episode.title);
		aq.id(R.id.textViewFriendEpisodeNumber).text(friend.watched.get(0).episode.season+"x"+friend.watched.get(0).episode.number);

		return vi;
	}
}