package com.jotreewater.socialsound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;

public class LikesFragment extends Fragment {
    private final String TAG = "TAGLikes";
    Context likesFragmentContext;
    RecyclerView recyclerViewLikes;
    Button buttonLikesBack;

    // Arraylist for storing data
    private ArrayList<Sound> soundArrayList;
    private ArrayList<String> keylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        likesFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_likes, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundArrayList = new ArrayList<>();
        keylist = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"Likes Created");
        MainActivity mainActivity = (MainActivity) getActivity();

        // Handle Back press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        recyclerViewLikes = view.findViewById(R.id.recyclerViewUserLikes);

        SoundAdapter soundAdapter = new SoundAdapter(this.getContext(), soundArrayList, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerViewLikes.setLayoutManager(linearLayoutManager);
        recyclerViewLikes.setAdapter(soundAdapter);

        //Firebase
        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soundArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
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

        // Gestures
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG,"Deleted");
                Toast.makeText(likesFragmentContext, "Removed", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(keylist.get(position)).removeValue();
                keylist.clear();
            }
        }).attachToRecyclerView(recyclerViewLikes);

        // Gestures
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG,"Deleted");
                Toast.makeText(likesFragmentContext, "Removed", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(keylist.get(position)).removeValue();
                keylist.clear();
            }
        }).attachToRecyclerView(recyclerViewLikes);

        soundAdapter.setOnItemClickListener(new SoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "Position: " + position);
                playUri(soundArrayList.get(position).getTrackUri().toString());
            }
        });

        // buttonLikesBack
        buttonLikesBack = mainActivity.findViewById(R.id.buttonUserBack);
        buttonLikesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"buttonLikesBack pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        });

    }

    private void playUri(String uri) {
        MainActivity.mainSpotifyAppRemote.getPlayerApi().play(uri);
    }
}
