package com.project.safewheels;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.project.safewheels.Entity.EmergencyListItem;
import com.project.safewheels.Entity.Favorite;
import com.project.safewheels.Entity.FavoriteAddresses;
import com.project.safewheels.Tools.EmergencyInfoAdaptor;
import com.project.safewheels.Tools.FavoriteInfoAdaptor;
import com.project.safewheels.Tools.ReadAndWrite;

import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment implements AdapterView.OnItemClickListener {

    View vEmergency;
    ListView lv_emergency;
    ListView lv_favorite;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int yourChoice = 0;
    FavoriteInfoAdaptor favoriteInfoAdaptor;
    private List<EmergencyListItem> list;
    private FavoriteAddresses addresses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vEmergency = inflater.inflate(R.layout.fragment_emergency, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();

        lv_emergency = (ListView)vEmergency.findViewById(R.id.lv_emergency);
        list = getEmergencyList();
        lv_emergency.setAdapter(new EmergencyInfoAdaptor(getContext(), list));
        lv_emergency.setOnItemClickListener(this);

        lv_favorite = (ListView) vEmergency.findViewById(R.id.lv_favorite);
        final List<Favorite> favoriteAddresses = getFavoriteAddressList();
        if (favoriteAddresses != null){
            favoriteInfoAdaptor = new FavoriteInfoAdaptor(getContext(), favoriteAddresses);
            lv_favorite.setAdapter(favoriteInfoAdaptor);
        }
        lv_favorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position+1 == favoriteAddresses.size()){
                    if (favoriteAddresses.size() == 1){
                        Toast.makeText(getActivity(), "Please add a address into my favorite first", Toast.LENGTH_SHORT).show();
                    }else if (favoriteAddresses.size() <= 2){
                        showUpdateDialog(favoriteAddresses);
                    }else {
                        showSingleOptionDialog(favoriteAddresses);
                    }
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("navigate", true);
                    intent.putExtra("placePosition", position);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                }
            }
        });

        return vEmergency;
    }

    private void showUpdateDialog(final List<Favorite> favoriteAddresses){
        final EditText editText = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle("Please insert a new name for the Place")
                .setView(editText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoriteAddresses.get(yourChoice).setName(editText.getText().toString());
                        FavoriteAddresses favoriteAddresses1 = new FavoriteAddresses(favoriteAddresses);
                        favoriteAddresses1.remove("update");
                        saveFavorite(favoriteAddresses1);
                        Favorite favorite = new Favorite("update", "", null);
                        favorite.setName("Update favourite addresses' name");
                        favoriteAddresses.add(favorite);
                        favoriteInfoAdaptor.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showSingleOptionDialog(final List<Favorite> favoriteAddresses){
        String[] names = new String[favoriteAddresses.size()];
        for (Favorite favorite:favoriteAddresses){
            names[favoriteAddresses.indexOf(favorite)] = favorite.getName();
        }

        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(getActivity());
        singleChoiceDialog.setTitle("Which one do you want to update?");
        singleChoiceDialog.setSingleChoiceItems(names, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            showUpdateDialog(favoriteAddresses);
                        }
                    }
                });
        singleChoiceDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        singleChoiceDialog.show();
    }

    private void saveFavorite(FavoriteAddresses addresses) {
        Gson gson = new Gson();
        String json = gson.toJson(addresses);
        editor.putString("addresses" ,json);
        editor.apply();
    }


    private List<EmergencyListItem> getEmergencyList() {
        String info = ReadAndWrite.readFromFile(getActivity().getApplicationContext(), 1);
        if (info.isEmpty()){
            Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
            startActivity(intent);
        }
        String[] infos = info.split(",");
        List<EmergencyListItem> newList = new ArrayList<>();
        EmergencyListItem name = new EmergencyListItem();
        name.setImage(getResources().getIdentifier("couple", "drawable", getActivity().getPackageName()));
        name.setTitle("Name");
        name.setIntro(infos[0]);

        EmergencyListItem phone = new EmergencyListItem();
        phone.setTitle("Phone Number");
        phone.setImage(getResources().getIdentifier("mobile", "drawable", getActivity().getPackageName()));
        phone.setIntro(infos[1]);

        EmergencyListItem email = new EmergencyListItem();
        email.setIntro(infos[2]);
        email.setImage(getResources().getIdentifier("email", "drawable", getActivity().getPackageName()));
        email.setTitle("Email Address");

        EmergencyListItem update = new EmergencyListItem();
        update.setTitle("Update your Emergency contact detail");
        update.setImage(getResources().getIdentifier("resume", "drawable", getActivity().getPackageName()));

        newList.add(name);
        newList.add(email);
        newList.add(phone);
        newList.add(update);
        return newList;
    }

    private List<Favorite> getFavoriteAddressList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("addresses", "");
        FavoriteAddresses favoriteAddresses = gson.fromJson(json, FavoriteAddresses.class);
        Favorite favorite = new Favorite("update", "", null);
        favorite.setName("Update favourite addresses' name");
        favoriteAddresses.add(favorite);
        return favoriteAddresses.getAddressList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 3){
            Intent intent = new Intent(getActivity(), UpgradeEmergencyContact.class);
            startActivity(intent);
        }
    }
}
