package com.project.safewheels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.BikeAccessories;
import com.project.safewheels.Tools.RepairAdaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class handles the look of the safety check page
 */

public class SafetyCheckFragment extends Fragment {

    View vSecutiry;
    private List<BikeAccessories> baList = new ArrayList<BikeAccessories>();
    private ListView performCheckListView;
    private RepairAdaptor repairAdaptor;
    private SharedPreferences rph;
    private int durationLast1 = 0;
    private int durationLast2 = 0;
    private int durationLast3 = 0;
    private int durationLast4 = 0;
    private int durationLast5 = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vSecutiry = inflater.inflate(R.layout.fragment_safecheck, container, false);

        rph = getContext().getSharedPreferences("safeWheels", Context.MODE_PRIVATE);
        durationLast1 = getDuration("Breaks(front and rear)");
        durationLast2 = getDuration("Tires(front and rear)");
        durationLast3 = getDuration("Chain");
        durationLast4 = getDuration("Crank & Gear");
        durationLast5 = getDuration("FlashLight(front and rear)");

        performCheckListView = (ListView) vSecutiry.findViewById(R.id.perform_check_list_view);
        BikeAccessories breaks = new BikeAccessories("Breaks(front and rear)", durationLast1 + "", R.drawable.caliper, R.drawable.caliper_detail);
        BikeAccessories tires = new BikeAccessories("Tires(front and rear)", durationLast2+"", R.drawable.wheel, R.drawable.pump_detail);
        BikeAccessories chain = new BikeAccessories("Chain", durationLast3+"", R.drawable.chain, R.drawable.chain_detail);
        BikeAccessories crank = new BikeAccessories("Crank & Gear", durationLast4+"",  R.drawable.lock, R.drawable.gears_detail);
        BikeAccessories flashlight = new BikeAccessories("FlashLight(front and rear)", durationLast5 + "",  R.drawable.flashlight, R.drawable.flashlight_detail);
        baList.add(breaks);
        baList.add(tires);
        baList.add(chain);
        baList.add(crank);
        baList.add(flashlight);


        repairAdaptor = new RepairAdaptor(getActivity().getApplicationContext());
        repairAdaptor.setList(baList);
        performCheckListView.setAdapter(repairAdaptor);
        performCheckListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long l){
                BikeAccessories ba = (BikeAccessories)adapter.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), RepairDetailActivity.class);
                intent.putExtra("baName", ba.getBaName() + "");
                intent.putExtra("baImage", ba.getDetailImage());
                startActivity(intent);
                repairAdaptor.notifyDataSetChanged();
            }
        });

        return vSecutiry;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getDuration(String name) {
        int durationLeft;
        if(!"".equals(rph.getString(name+"duration", ""))){
            Date date1 = new Date(rph.getString(name+"startDate", null));
            Date date2 = new Date();
            long timePast = date2.getTime() - date1.getTime();
            int timePastInDay = Math.toIntExact(timePast / 1000 / 60 / 60 / 24);
            int duration = Integer.parseInt(rph.getString(name+"duration", ""));
            durationLeft = duration - timePastInDay;
            return durationLeft;
        }
        return -10000;
    }
}
