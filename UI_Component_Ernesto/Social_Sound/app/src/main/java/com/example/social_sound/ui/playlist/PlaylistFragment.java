package com.example.social_sound.ui.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.social_sound.MainActivity;
import com.example.social_sound.R;

public class PlaylistFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState){
        View view = inflater.inflate(R.layout.fragment_playlist, parent, false);
        //*****************************************************************************************
        //  Ernesto Bernardo PlaylistFragment.java
        //  File and functionality created from Android Studio file creation
        //  Button variable and setOnClickListener functions created by Ernesto Bernardo
        //*****************************************************************************************
        Button btn1 = (Button) view.findViewById(R.id.playlist1);
        Button btn2 = (Button) view.findViewById(R.id.playlist2);
        Button btn3 = (Button) view.findViewById(R.id.playlist3);
        Button btn4 = (Button) view.findViewById(R.id.playlist4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closePlaylist();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closePlaylist();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closePlaylist();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).closePlaylist();
            }
        });

        return view;
    }

    public void onViewCreated(android.view.View view, Bundle savedInstanceState){

    }
}
