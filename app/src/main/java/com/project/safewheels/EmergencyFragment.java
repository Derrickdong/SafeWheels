package com.project.safewheels;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.ListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment implements AdapterView.OnItemClickListener {

    View vEmergency;
    ListView listView;
    private List<ListItem> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vEmergency = inflater.inflate(R.layout.fragment_emergency, container, false);

        listView = (ListView)vEmergency.findViewById(R.id.lv_emergency);
        list = getList();
        listView.setAdapter(new ListAdapter(getContext(), list));

        listView.setOnItemClickListener(this);
        return vEmergency;
    }

    private List<ListItem> getList() {
        List<ListItem> newList = new ArrayList<>();
        ListItem favorite = new ListItem();
        favorite.setTitle("My Favorite");
        favorite.setIntro("");

        ListItem contact = new ListItem();
        contact.setTitle("Emergency Contact");
        newList.add(contact);
        newList.add(favorite);
        return newList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                if (readFromFile(getActivity().getApplicationContext())){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactFragment()).commit();
                }else {
                    Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
                    startActivity(intent);
                }
                break;
            case 1:

                break;
        }
    }


    private Boolean readFromFile(Context context) {
        File directory = context.getFilesDir();
        File file = new File(directory, "emergency.txt");
        if (file.exists()){
            return true;
        }
        return false;
    }
}
