package com.driving.senor.test;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.LocationFragment;
import fragments.LogFragment;
import fragments.SensorFragment;
import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 1/23/2016.
 */
public class TabsPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles;

    public TabsPageAdapter( FragmentManager fm , Context context) {
        super( fm );
        this.context = context;

        // Create titles from resource
        try {
            tabTitles = context.getResources().getStringArray(R.array.tabs);
        }
        catch ( Exception e ) {
            LogService.log( "Error getting string array: " + e.getMessage() );
            tabTitles = new String[] {} ;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle( int position ) {
        return this.tabTitles[position];
    }


    /**
     * Returns the required fragment by item position
     * @param position
     * @return
     */
    @Override
    public Fragment getItem( int position ) {

        switch (position) {
            case 0: return new SensorFragment();
            case 1: return new LocationFragment();
            case 2: return new LogFragment();

        }
        return null;
    }
}
