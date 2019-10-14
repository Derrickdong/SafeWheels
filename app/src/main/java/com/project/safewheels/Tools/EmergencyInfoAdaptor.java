package com.project.safewheels.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.safewheels.Entity.EmergencyListItem;
import com.project.safewheels.R;

import java.util.List;

/**
 * This a adaptor class that deal withe the Emergency contact information in the
 * emergency contact detail page
 */

public class EmergencyInfoAdaptor extends BaseAdapter {

    private Context context;
    private List<EmergencyListItem> list;
    private LayoutInflater layoutInflater;
    ImageView img;
    TextView tv_title;
    EditText et_intro;
    TextView tv_intro;

    public EmergencyInfoAdaptor(Context context, List<EmergencyListItem> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_listview, null);
        }

        img = (ImageView)convertView.findViewById(R.id.list_image);
        tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        tv_intro = (TextView)convertView.findViewById(R.id.tv_intro);

        img.setImageResource(list.get(position).getImage());
        tv_title.setText(list.get(position).getTitle());
        tv_intro.setText(list.get(position).getIntro());
        return convertView;
    }
}
