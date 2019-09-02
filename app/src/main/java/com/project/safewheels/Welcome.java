package com.project.safewheels;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class Welcome extends Fragment {

    View vWelcome;
    Button btn_map;
    Button btn_check;
    Button btn_emer;
    FragmentManager fragmentManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vWelcome = inflater.inflate(R.layout.fragment_welcome, container, false);
        fragmentManager = getFragmentManager();

        btn_map = (Button)vWelcome.findViewById(R.id.btn_rideNow);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            }
        });
        btn_check = (Button)vWelcome.findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SecurityFragment()).commit();
            }
        });
        btn_emer = (Button)vWelcome.findViewById(R.id.btn_emer);
        btn_emer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Calling Emergency contact", Toast.LENGTH_SHORT).show();
            }
        });

        return vWelcome;
    }
}
