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

import com.project.safewheels.Entity.ListItem;
import com.project.safewheels.Tools.ListAdaptor;
import com.project.safewheels.Tools.ReadAndWrite;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener {

    View vContact;
    ListView listView;
    private List<ListItem> list;
    private String info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vContact = inflater.inflate(R.layout.fragment_contact, container, false);

        info = ReadAndWrite.readFromFile(getActivity().getApplicationContext());
        if (info.isEmpty()){
            Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
            startActivity(intent);
        }

        listView = (ListView)vContact.findViewById(R.id.lv_infos);
        list = getList();
        listView.setAdapter(new ListAdaptor(getContext(), list));
        listView.setOnItemClickListener(this);

        return vContact;
    }

    private List<ListItem> getList() {
            String[] infos = info.split(",");
            List<ListItem> newList = new ArrayList<>();
            ListItem name = new ListItem();
            name.setImage(getResources().getIdentifier("couple", "drawable", getActivity().getPackageName()));
            name.setTitle("Name");
            name.setIntro(infos[0]);

            ListItem phone = new ListItem();
            phone.setTitle("Phone Number");
            phone.setImage(getResources().getIdentifier("mobile", "drawable", getActivity().getPackageName()));
            phone.setIntro(infos[1]);

            ListItem email = new ListItem();
            email.setIntro(infos[2]);
            email.setImage(getResources().getIdentifier("email", "drawable", getActivity().getPackageName()));
            email.setTitle("Email Address");

            ListItem update = new ListItem();
            update.setTitle("Update your Emergency contact detail");
            update.setImage(getResources().getIdentifier("resume", "drawable", getActivity().getPackageName()));

            newList.add(name);
            newList.add(email);
            newList.add(phone);
            newList.add(update);
            return newList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 3){
            Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
            startActivity(intent);
        }
    }

}


