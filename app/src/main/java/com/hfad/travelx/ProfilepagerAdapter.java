package com.hfad.travelx;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class ProfilepagerAdapter extends FragmentPagerAdapter {
    public ProfilepagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return 2;
    }

    @Override // androidx.fragment.app.FragmentPagerAdapter
    public Fragment getItem(int position) {
        if (position == 0) {
            return new YourPostsFragment();
        }
        if (position != 1) {
            return null;
        }
        return new YourPhotosFragment();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Posts";
        }
        if (position != 1) {
            return null;
        }
        return "Destination Added";
    }
}
