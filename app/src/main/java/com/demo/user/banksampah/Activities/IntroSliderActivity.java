package com.demo.user.banksampah.Activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.user.banksampah.R;

public class IntroSliderActivity extends AppCompatActivity {

    protected ViewPager mSlideViewPager;
    protected LinearLayout mDotLayout;
    protected TextView[] mDots;
    protected SliderAdapter slideAdapter;
    protected Button btnNext, btnPrevious, btnStart;
    protected int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        btnNext = findViewById(R.id.nextButton);
        btnPrevious = findViewById(R.id.prevButton);
        btnStart = findViewById(R.id.startButton);

        slideAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(slideAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage+1);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(IntroSliderActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });

    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for(int i = 0; i<mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if(i==0){
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.INVISIBLE);

                btnNext.setText("Next");

            }else if(i == mDots.length-1){
                Log.e("tag", String.valueOf(mDots.length-1));

                btnNext.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);

                //btnNext.setText("Start");
                btnPrevious.setText("Back");

                /*Intent login = new Intent(IntroSliderActivity.this, LoginActivity.class);
                startActivity(login);*/

            }else{
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);

                btnStart.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.VISIBLE);

                btnNext.setText("Next");
                btnPrevious.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
