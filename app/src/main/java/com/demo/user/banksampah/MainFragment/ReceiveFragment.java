package com.demo.user.banksampah.MainFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.user.banksampah.Adapter.TabViewPagerAdapter;
import com.demo.user.banksampah.KantongFragment.ListOrderFragment;
import com.demo.user.banksampah.KantongFragment.SimpanFragment;
import com.demo.user.banksampah.R;

public class ReceiveFragment extends Fragment {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;

    public ReceiveFragment(){
    }

    public static ReceiveFragment newInstance(){
        return new ReceiveFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){

        final View rootView = layoutInflater.inflate(R.layout.fragment_receive, container, false);
        viewPager = rootView.findViewById(R.id.viewpager_content);

        tabLayout = rootView.findViewById(R.id.tabs_kantong);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager){
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getChildFragmentManager());
        tabViewPagerAdapter.addFragment(new SimpanFragment(), "Kantong Sampah");
        tabViewPagerAdapter.addFragment(new ListOrderFragment(), "Daftar Order");
        viewPager.setAdapter(tabViewPagerAdapter);
    }
}
