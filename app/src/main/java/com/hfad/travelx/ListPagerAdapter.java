package com.hfad.travelx;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ListPagerAdapter extends FragmentPagerAdapter {
    public ListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return 2;
    }

    @Override // androidx.fragment.app.FragmentPagerAdapter
    public Fragment getItem(int position) {
        if (position == 0) {
            return new VideoListFragment();
        }
        if (position != 1) {
            return null;
        }
        return new PhotoListFragment();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Videos";
        }
        if (position != 1) {
            return null;
        }
        return "Photos";
    }
}
