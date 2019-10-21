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

/**
 * This is a tool class that shows the look of Slides in the instruction
 */

public class SliderAdaptor extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdaptor (Context context) {
        this.context = context;
    }


    public int [] slide_images ={
            R.drawable.logo_safewheels1,
            R.drawable.bicycle_screenshot,
            R.drawable.accident_screenshot,
            R.drawable.road_work,
            R.drawable.road_maintainance
    };

    public String[] slide_headings = {
            "",
            "Highlight of Bike Lanes",
            "Place to Look Out",
            "Road Under Maintenance",
            "Road Maintenance Scheduled"
    };

    public String[] slide_description = {
            "Your Personal Bicycle Safety Assistant",
            "Bicycle-friendly roads are highlighted in Green",
            "Potential troublesome road sections are highlighted in Orange color",
            "Roads may be under maintenance.\nPress the sign for more info on the map",
            "Maintenance may be scheduled.\nPress the sign for more info on the map",
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