package com.project.safewheels.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.safewheels.Entity.BikeAccessories;
import com.project.safewheels.R;

import java.util.List;

public class RepairAdaptor extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    RepairAdaptor.ViewHolder holder;
    List<BikeAccessories> baList;
    BikeAccessories ba;
    int index;
//    RepairAdaptor.MyOnClickListener mOnClickListener = new MyOnClickListener();


    public RepairAdaptor(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setList(List<BikeAccessories> balist) {
        this.baList = balist;
    }

    @Override
    public int getCount() {
        return baList.size();
    }

    @Override
    public BikeAccessories getItem(int position) {
        return baList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new RepairAdaptor.ViewHolder();
            convertView = mInflater.inflate(R.layout.resource_repair_section, null);

            holder.title = (TextView) convertView
                    .findViewById(R.id.section_title);
            holder.duration = (TextView) convertView
                    .findViewById(R.id.section_duration);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.section_icon);
            holder.performCheck = (TextView) convertView
                    .findViewById(R.id.section_action);
            convertView.setTag(holder);


        } else {
            holder = (RepairAdaptor.ViewHolder) convertView.getTag();
        }
        ba = (BikeAccessories) getItem(position);
        index = position;

        holder.title.setText(ba.getBaName());
        holder.duration.setText("Next check in about " + ba.getBaRepairDuration()+" days");
        holder.icon.setImageResource(ba.getBaImage());


        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView title;
        TextView duration;
        TextView performCheck;
    }
}
