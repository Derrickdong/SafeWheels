package com.project.safewheels;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.ListItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener {

    View vContact;
    ListView listView;
    Button btn_update;
    private List<ListItem> list;
    private String info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vContact = inflater.inflate(R.layout.fragment_contact, container, false);

        info = readFromFile(getActivity().getApplicationContext());

        listView = (ListView)vContact.findViewById(R.id.lv_infos);
        list = getList();
        listView.setAdapter(new ListAdapter(getContext(), list));
        listView.setOnItemClickListener(this);

        btn_update = (Button)vContact.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
                startActivity(intent);
            }
        });

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

        newList.add(name);
        newList.add(email);
        newList.add(phone);
        return newList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private String readFromFile(Context context){
        String result = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput("emergency.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            if ((result = bufferedReader.readLine()) != null){
                return result;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


