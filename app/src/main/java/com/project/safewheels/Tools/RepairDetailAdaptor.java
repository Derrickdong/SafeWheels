package com.project.safewheels.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.safewheels.R;

import java.util.List;

/**
 * This is a tool class that deal with the looks of repair details
 */

public class RepairDetailAdaptor extends BaseAdapter {
    Context context;
    LayoutInflater mInflater;
    RepairDetailAdaptor.ViewHolder holder;
    List<String> steps;
    String step;


    public RepairDetailAdaptor(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setList(List<String> steps) {
        this.steps = steps;
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public Object getItem(int position) {
        return steps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new RepairDetailAdaptor.ViewHolder();
            convertView = mInflater.inflate(R.layout.resource_repair_detail, null);

            holder.stepDetail = (TextView) convertView
                    .findViewById(R.id.step_detail);
            convertView.setTag(holder);
        } else {
            holder = (RepairDetailAdaptor.ViewHolder) convertView.getTag();
        }
        step = getItem(position).toString();
        holder.stepDetail.setText(step);

        return convertView;
    }


    class ViewHolder {
        TextView stepDetail;
    }

}
