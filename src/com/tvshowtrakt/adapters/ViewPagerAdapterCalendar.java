package com.tvshowtrakt.adapters;


import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tvshowtrakt.R;
import com.viewpagerindicator.TitleProvider;
 
public class ViewPagerAdapterCalendar extends PagerAdapter
    implements TitleProvider
{
    private static String[] titles = new String[]
    {
        "Last Week",
        "This Week",
        "Next Week",
    };
    private final Context context;
 
    public ViewPagerAdapterCalendar( Context context )
    {
        this.context = context;
    }
 
    @Override
    public String getTitle( int position )
    {
        return titles[ position ];
    }
 
    @Override
    public int getCount()
    {
        return titles.length;
    }
 
    @Override
    public Object instantiateItem( View collection, int position )
    {
    	LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view = inflater.inflate(R.layout.calendar_page, null);

        ((ViewPager) collection).addView(view,0 );

        return view;
    }
 
    @Override
    public void destroyItem( View pager, int position, Object view )
    {
    	
//        ((ViewPager)pager).removeView( (TextView)view );
    }
 
    @Override
    public boolean isViewFromObject( View view, Object object )
    {
        return view.equals( object );
    }
 
    @Override
    public void finishUpdate( View view ) {}
 
    @Override
    public void restoreState( Parcelable p, ClassLoader c ) {}
 
    @Override
    public Parcelable saveState() {
        return null;
    }
 
    @Override
    public void startUpdate( View view ) {}
}