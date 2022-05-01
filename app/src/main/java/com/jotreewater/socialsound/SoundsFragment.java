package com.jotreewater.socialsound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;

public class SoundsFragment extends Fragment {
    private RecyclerView recyclerViewSounds;

    private final String TAG = "TAGSounds";

    // Arraylist for storing data
    private ArrayList<Sound> soundArrayList;
    private ArrayList<String> keylist;

    // Likes Variables
    String imageUriStringConversion;

    Context soundsFragmentContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState) {

        Log.d(TAG, "Sounds View Creating");

        soundsFragmentContext = parent.getContext();

        View view = inflater.inflate(R.layout.fragment_sounds, parent, false);

        return view;
    }

    // Function to play a specific track given by the parameter URI
    private void playUri(String uri) {
        MainActivity.mainSpotifyAppRemote.getPlayerApi().play(uri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Sounds Attached");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Sounds Created");
        // here we have created new array list and added data to it.
        soundArrayList = new ArrayList<>();
        keylist = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Sounds View Created");
        MainActivity mainActivity = (MainActivity) getActivity();

        recyclerViewSounds = view.findViewById(R.id.recyclerViewUserLikes);

        // we are initializing our adapter class and passing our arraylist to it.
        SoundAdapter soundAdapter = new SoundAdapter(this.getContext(), soundArrayList, true);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layout manager and adapter to our recycler view.
        recyclerViewSounds.setLayoutManager(linearLayoutManager);
        recyclerViewSounds.setAdapter(soundAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //Toast.makeText(soundsFragmentContext, "Swipe right to like, left to delete", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG,"Liked");
                Toast.makeText(soundsFragmentContext, "Liked", Toast.LENGTH_SHORT).show();
                SoundAdapter.selectedItem = -1;
                int position = viewHolder.getAdapterPosition();
                mainActivity.reference.child("Sounds").child(mainActivity.auth.getUid()).child(keylist.get(position)).child("key").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d(TAG, "Key: " + snapshot.getValue().toString());
                        String Key = snapshot.getValue().toString();

                        // update likes

                        mainActivity.reference.child("Likes").child(snapshot.getValue().toString()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d(TAG,"snapshot data: " + snapshot);
                                String stringLikes = snapshot.getValue().toString();
                                Log.d(TAG,"Likes before: " + stringLikes);
                                Integer intLikes = Integer.parseInt(stringLikes) + 1;
                                stringLikes = intLikes.toString();
                                Log.d(TAG,"Likes after: " + stringLikes);
                                mainActivity.reference.child("Likes").child(Key).child("likes").setValue(stringLikes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // update liked sounds
                        String sound_id = mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).push().getKey();

                        Sound sound = soundArrayList.get(position);
                        String trackName = sound.getTrackName();
                        String trackArtist = sound.getTrackArtist();
                        String trackAlbum = sound.getTrackAlbum();
                        ImageUri trackImage = sound.getTrackImage();
                        imageUriStringConversion = trackImage.toString();
                        imageUriStringConversion = imageUriStringConversion.substring(8, imageUriStringConversion.length() - 2);
                        String trackUri = sound.getTrackUri();
                        String username = sound.getUsername();
                        String profilePicture = sound.getProfilePicture();
                        String key = sound.getKey();

                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("trackName").setValue(trackName);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("trackArtist").setValue(trackArtist);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("trackAlbum").setValue(trackAlbum);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("trackImage").setValue(imageUriStringConversion);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("trackUri").setValue(trackUri);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("username").setValue(username);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("profilePicture").setValue(profilePicture);
                        mainActivity.reference.child("LikedSounds").child(mainActivity.auth.getUid()).child(sound_id).child("key").setValue(key);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mainActivity.reference.child("Sounds").child(mainActivity.auth.getUid()).child(keylist.get(position)).removeValue();
                keylist.clear();
            }
        }).attachToRecyclerView(recyclerViewSounds);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //Toast.makeText(soundsFragmentContext, "Swipe left to delete", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG,"Deleted");
                Toast.makeText(soundsFragmentContext, "Removed", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                SoundAdapter.selectedItem = -1;
                mainActivity.reference.child("Sounds").child(mainActivity.auth.getUid()).child(keylist.get(position)).removeValue();
                keylist.clear();
            }
        }).attachToRecyclerView(recyclerViewSounds);

        soundAdapter.setOnItemClickListener(new SoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "Position: " + position);
                playUri(soundArrayList.get(position).getTrackUri().toString());
            }
        });

        // Firebase
        mainActivity.reference.child("Sounds").child(mainActivity.auth.getUid()).addValueEventListener(new ValueEventListener() {
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
                Log.d(TAG, "Firebase Error: " + error);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Sounds Started");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Sounds Resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Sounds Paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Sounds Stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Sounds Destroyed");
    }
}
