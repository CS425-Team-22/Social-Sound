package com.jotreewater.socialsound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;

public class UserLikesFragment extends Fragment {
    private static final String TAG = "TAGUserLikes";
    private RecyclerView recyclerViewUserLikes;
    Context userLikesFragmentContext;
    Button buttonUserBack;
    private ArrayList<Sound> soundArrayList;
    private ArrayList<String> keylist;
    TextView textViewUserUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState) {
        Log.d(TAG, "Sounds View Creating");

        userLikesFragmentContext = parent.getContext();

        View view = inflater.inflate(R.layout.fragment_user_likes, parent, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "UserLikes Created");

        soundArrayList = new ArrayList<>();
        keylist = new ArrayList<>();

        MainActivity mainActivity = (MainActivity) getActivity();

        // recyclerView
        recyclerViewUserLikes = view.findViewById(R.id.recyclerViewUserLikes);

        SoundAdapter soundAdapter = new SoundAdapter(this.getContext(), soundArrayList, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerViewUserLikes.setLayoutManager(linearLayoutManager);
        recyclerViewUserLikes.setAdapter(soundAdapter);

        Bundle bundle = getArguments();
        String username = bundle.getString("username");
        String key = bundle.getString("key");

        Log.d(TAG, "key: " + key);
        Log.d(TAG, "username: " + username);

        // textViewUserUsername
        textViewUserUsername = mainActivity.findViewById(R.id.textViewUserUsername);
        textViewUserUsername.setText(username + "'s Liked Sounds");


        // firebase
        mainActivity.reference.child("LikedSounds").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soundArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        soundArrayList.add(new Sound(dataSnapshot.child("trackName").getValue().toString(), dataSnapshot.child("trackArtist").getValue().toString(), dataSnapshot.child("trackAlbum").getValue().toString(), dataSnapshot.child("username").getValue().toString(), dataSnapshot.child("trackUri").getValue().toString(), new ImageUri(dataSnapshot.child("trackImage").getValue().toString()), dataSnapshot.child("profilePicture").getValue().toString(), dataSnapshot.child("key").getValue().toString()));
                        keylist.add(dataSnapshot.getKey());
                        soundAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.d(TAG, "Client Error: " + e.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // buttonUserBack
        buttonUserBack = mainActivity.findViewById(R.id.buttonUserBack);
        buttonUserBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "buttonUserBack pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, SoundsFragment.class, null).commitNow();
            }
        });

        // Handle Back press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Handle clicking card
        soundAdapter.setOnItemClickListener(new SoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "Position: " + position);
                playUri(soundArrayList.get(position).getTrackUri().toString());
            }
        });
    }

    private void playUri(String uri) {
        MainActivity.mainSpotifyAppRemote.getPlayerApi().play(uri);
    }
}
