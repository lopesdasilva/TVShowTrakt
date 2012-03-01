package com.tvshowtrakt.adapters;

import java.util.List;

import imageloaders.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jakewharton.trakt.entities.UserProfile;
import com.tvshowtrakt.FriendsActivity;
import com.tvshowtrakt.R;

public class LazyAdapterListFriends extends BaseAdapter {
	
	private Activity activity;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private List<UserProfile> userProfile;

	public LazyAdapterListFriends(Activity a, List<UserProfile> userProfile) {
		this.userProfile=userProfile;
		this.activity=a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//imageLoader = new ImageLoader(activity.getApplicationContext());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userProfile.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.friends_itemlist, null);
		
		TextView mNome = (TextView) vi.findViewById(R.id.username_friends);
		mNome.setText(userProfile.get(position).username);
		TextView mLocal = (TextView) vi.findViewById(R.id.informacao_friends);
//		mLocal.setText(genero[position]+" from "+localizacao[position]);
		
		return vi;
	}

}
