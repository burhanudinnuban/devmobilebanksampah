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
import com.demo.user.banksampah.MemberFragment.ListMember.ListMember;
import com.demo.user.banksampah.MemberFragment.RequestMember.RequestMember;
import com.demo.user.banksampah.R;

public class MemberFragment extends Fragment {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;

    public MemberFragment(){
    }

    public static MemberFragment newInstance() {
        return new MemberFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_member, container, false);
        viewPager = rootView.findViewById(R.id.viewpager_content_member);

        tabLayout = rootView.findViewById(R.id.tabs_member);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager){
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getChildFragmentManager());
        tabViewPagerAdapter.addFragment(new ListMember(), "Daftar Member");
        tabViewPagerAdapter.addFragment(new RequestMember(), "Permintaan Daftar Member");
        viewPager.setAdapter(tabViewPagerAdapter);
    }
}
