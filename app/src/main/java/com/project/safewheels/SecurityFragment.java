package com.project.safewheels;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class SecurityFragment extends Fragment {

    View vSecutiry;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vSecutiry = inflater.inflate(R.layout.fragment_security, container, false);
        return vSecutiry;
    }
}
