package com.project.safewheels;

import androidx.fragment.app.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;


public class SecurityFragment extends Fragment {

    View vSecutiry;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vSecutiry = inflater.inflate(R.layout.fragment_security, container, false);
        return vSecutiry;
    }
}
