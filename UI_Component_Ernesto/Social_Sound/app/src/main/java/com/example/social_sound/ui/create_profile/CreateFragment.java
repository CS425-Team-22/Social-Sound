package com.example.social_sound.ui.create_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.social_sound.MainActivity;
import com.example.social_sound.R;

public class CreateFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState){
        //*****************************************************************************************
        //  Ernesto Bernardo CreateFragment.java
        //  File and functionality created from Android Studio file creation
        //  Button variable and setOnClickListener functions created by Ernesto Bernardo
        //*****************************************************************************************
        View view = inflater.inflate(R.layout.fragment_create, parent, false);
        Button btn1 = (Button) view.findViewById(R.id.create_account);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closeCreate();
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){

    }
}
