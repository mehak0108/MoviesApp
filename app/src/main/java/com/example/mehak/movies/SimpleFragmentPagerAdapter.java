package com.example.mehak.movies;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private static int NUM_ITEMS = 2;

    public SimpleFragmentPagerAdapter(Context context,FragmentManager fm){
        super(fm);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new OngoingFragment();
        } else {
            return new RetroFragment();
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.ongoing_);
            case 1:
                return mContext.getString(R.string.retro);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
