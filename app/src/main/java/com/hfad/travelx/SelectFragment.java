package com.hfad.travelx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class SelectFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_select, container, false);
       tabLayout = v.findViewById(R.id.List_tabs);
       viewPager = v.findViewById(R.id.List_pager);
        ListPagerAdapter listPagerAdapter = new ListPagerAdapter(getChildFragmentManager());
        this.viewPager.setAdapter(listPagerAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
       return v;
    }
}