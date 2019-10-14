package com.project.safewheels.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project.safewheels.Entity.Weather;
import com.project.safewheels.R;

import java.util.ArrayList;

/**
 * This is a tool class that deal with the look of weathers
 */

public class WeatherAdaptor extends ArrayAdapter {
    public WeatherAdaptor(@NonNull Context context, ArrayList<Weather> weatherArrayList) {
        super(context, 0, weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather weather = (Weather) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_list_item, parent, false);
        }
        TextView dateTextView = convertView.findViewById(R.id.tvdatetime);
        TextView tempTextView = convertView.findViewById(R.id.tvtemperature);
        TextView descTextView = convertView.findViewById(R.id.tvdescription);
        ImageView iconImageView = convertView.findViewById(R.id.ivIcon);
        //TextView linkTextView = convertView.findViewById(R.id.tvLink);

        String date = weather.getDateTime();
        dateTextView.setText(date);
        String temp = weather.getTemperature() + " \u2103";
        tempTextView.setText(temp);
        descTextView.setText(weather.getDescription());
        iconImageView.setImageResource(getContext()
                .getResources()
                .getIdentifier("a"+weather.getIconNumber(), "drawable", getContext().getPackageName()));
        //linkTextView.setText(weather.getLink());

        return convertView;
    }
}
