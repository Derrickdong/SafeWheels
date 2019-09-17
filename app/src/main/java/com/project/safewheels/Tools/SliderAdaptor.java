package com.project.safewheels.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.project.safewheels.R;


public class SliderAdaptor extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdaptor (Context context) {
        this.context = context;
    }


    public int [] slide_images ={
            R.drawable.accident_zone,
            R.drawable.road_work,
            R.drawable.road_maintainance

    };

    public String[] slide_headings = {
            "Accident Zones",
            "Road Works Undergoing",
            "Road Maintenance Scheduled"
    };

    public String[] slide_description = {
            "It indicates accident Zones with previous history of bike accidents so be careful while driving in this Zones  ",
            "It indicates the locations where road constructions are happening currently",
            "It indicates the locations where road works are scheduled or any road maintenance undergoing",
    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_description[position]);

        container.addView(view);
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}