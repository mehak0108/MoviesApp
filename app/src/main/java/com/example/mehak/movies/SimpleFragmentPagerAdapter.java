package com.example.mehak.movies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public SimpleFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return OngoingFragment.newInstance(0, "Ongoing");
            case 1:
                return RetroFragment.newInstance(1, "Retro");
            default:
                return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Ongoing";
            case 1:
                return "Retro";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
