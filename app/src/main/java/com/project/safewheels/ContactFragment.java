package com.project.safewheels;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.safewheels.Entity.ListItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        try {
            info = readFromFile(getActivity().getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView = (ListView)vContact.findViewById(R.id.lv_infos);
        list = getList();
        listView.setAdapter(new ListAdapter(getContext(), list));
        listView.setOnItemClickListener(this);

        return vContact;
    }

    private List<ListItem> getList() {
        String[] infos = info.split(",");
        List<ListItem> newList = new ArrayList<>();
        ListItem name = new ListItem();
        name.setTitle("Name");
        name.setIntro(infos[0]);

        ListItem phone = new ListItem();
        phone.setTitle("Phone Number");
        phone.setIntro(infos[1]);

        ListItem email = new ListItem();
        email.setIntro(infos[2]);
        email.setTitle("Email Address");

        newList.add(name);
        newList.add(email);
        newList.add(phone);
        return newList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private String readFromFile(Context context) throws IOException {
        String ret;
//        File directory = context.getFilesDir();
//        File file = new File(directory, "emergency.txt");
        FileInputStream fileInputStream = context.openFileInput("emergency.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        while((ret = bufferedReader.readLine()) != null){
            stringBuffer.append(ret);
        }
        return stringBuffer.toString();
    }
}


