package com.example.social_sound.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.social_sound.MainActivity;
import com.example.social_sound.R;

public class ProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState){
        //*****************************************************************************************
        //  Ernesto Bernardo ProfileFragment.java
        //  File and functionality created from Android Studio file creation
        //  Button variable and setOnClickListener functions created by Ernesto Bernardo
        //*****************************************************************************************
        View view = inflater.inflate(R.layout.fragment_profile, parent, false);
        Button btn1 = (Button) view.findViewById(R.id.logout);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closeProfile();
            }
        });
        return view;
    }


    public void onViewCreated(View view, Bundle savedInstanceState){

    }
}
