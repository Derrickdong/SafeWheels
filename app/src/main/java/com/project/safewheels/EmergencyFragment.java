package com.project.safewheels;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.EmergencyListItem;
import com.project.safewheels.Tools.EmergencyInfoAdaptor;
import com.project.safewheels.Tools.ReadAndWrite;

import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment implements AdapterView.OnItemClickListener {

    View vEmergency;
    ListView listView;
    private List<EmergencyListItem> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vEmergency = inflater.inflate(R.layout.fragment_emergency, container, false);

        listView = (ListView)vEmergency.findViewById(R.id.lv_emergency);
        list = getList();
        listView.setAdapter(new EmergencyInfoAdaptor(getContext(), list));

        listView.setOnItemClickListener(this);
        return vEmergency;
    }

    private List<EmergencyListItem> getList() {
        List<EmergencyListItem> newList = new ArrayList<>();
//        EmergencyListItem favorite = new EmergencyListItem();
//        favorite.setTitle("My Favorite");
//        favorite.setImage(getResources().getIdentifier("like", "drawable", getActivity().getPackageName()));
//        favorite.setIntro("");

        EmergencyListItem contact = new EmergencyListItem();
        contact.setImage(getResources().getIdentifier("phone_book", "drawable", getActivity().getPackageName()));
        contact.setTitle("Emergency Contact");
        newList.add(contact);
//        newList.add(favorite);
        return newList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                String str = ReadAndWrite.readFromFile(getActivity().getApplicationContext());
                if (!str.isEmpty()){
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
}
