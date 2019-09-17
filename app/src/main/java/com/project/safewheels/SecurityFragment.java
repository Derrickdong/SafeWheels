package com.project.safewheels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.BikeAccessories;
import com.project.safewheels.Tools.RepairAdaptor;

import java.util.ArrayList;
import java.util.List;


public class SecurityFragment extends Fragment {

    View vSecutiry;
    private List<BikeAccessories> baList = new ArrayList<BikeAccessories>();
    private ListView performCheckListView;
    private RepairAdaptor repairAdaptor;
    private Context context;
    private SharedPreferences rph;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vSecutiry = inflater.inflate(R.layout.fragment_security, container, false);

        context = getContext();
        rph = context.getSharedPreferences("safeWheels", Context.MODE_PRIVATE);
        performCheckListView = (ListView) vSecutiry.findViewById(R.id.perform_check_list_view);
        BikeAccessories breaks = new BikeAccessories("Breaks(front and rear)", rph.getString("Breaks(front and rear)", "0"), R.drawable.caliper, R.drawable.caliper_detail);
        BikeAccessories tires = new BikeAccessories("Tires(front and rear)", rph.getString("Tires(front and rear)", "0"), R.drawable.wheel, R.drawable.pump_detail);
        BikeAccessories chain = new BikeAccessories("Chain", rph.getString("Chain", "0"), R.drawable.chain, R.drawable.chain_detail);
        BikeAccessories crank = new BikeAccessories("Crank & Gear", rph.getString("Crank & Gear", "0"),  R.drawable.wheel, R.drawable.flashlight_detail);
        BikeAccessories flashlight = new BikeAccessories("FlashLight(front and rear)", rph.getString("FlashLight(front and rear)", "0"),  R.drawable.wheel, R.drawable.flashlight_detail);
        baList.add(breaks);
        baList.add(tires);
        baList.add(chain);
        baList.add(crank);
        baList.add(flashlight);


        repairAdaptor = new RepairAdaptor(context);
        repairAdaptor.setList(baList);
        performCheckListView.setAdapter(repairAdaptor);
        performCheckListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long l){
                BikeAccessories ba = (BikeAccessories)adapter.getItemAtPosition(position);

                Intent intent = new Intent(context, RepairDetailActivity.class);
                intent.putExtra("baName", ba.getBaName() + "");
                intent.putExtra("baImage", ba.getDetailImage());
                startActivity(intent);
            }
        });

        return vSecutiry;
    }
}
