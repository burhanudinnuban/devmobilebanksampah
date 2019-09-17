package com.demo.user.banksampah.Services;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.user.banksampah.R;

public class SliderAdapter extends PagerAdapter {

    protected Context context;
    private LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.ic_page1,
            R.drawable.ic_page2,
            R.drawable.ic_page3
    };

    public String[] slide_headings = {
            "APLIKASI OLEH MULTI INTI DIGITAL LESTARI",
            "MENGAPA ERECYCLE?",
            "MEKANISME"
    };

    public String[] slide_desc = {
            "Aplikasi ramah lingkungan berbasis teknologi untuk mengurangi pembuangan sampah sembarangan",
            "Mudah, Perhitungan Tepat, Menjaga Lingkungan dan Menguntungkan",
            "Dengan proses melalui aplikasi di smartphone, sampah akan dijemput ke rumah lalu ditimbang secara digital, akurat, " +
                    "dan real time sampah tersebut akan diganti dengan reward point yang dapat ditukarkan."
    };

    @Override
    public int getCount(){
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slideImage);
        TextView slideHeading = view.findViewById(R.id.slideHeading);
        TextView slideDescription = view.findViewById(R.id.slideDesc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_desc[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((RelativeLayout)object);
    }
}
