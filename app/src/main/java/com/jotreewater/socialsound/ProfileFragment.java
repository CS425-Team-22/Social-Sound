package com.jotreewater.socialsound;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageIdentifier;
import com.spotify.protocol.types.ImageUri;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import io.paperdb.Paper;

public class ProfileFragment extends Fragment {
    private final String TAG = "TAGProfile";
    Context profileFragmentContext;

    TextView textViewUsername, textViewLocation, textViewTrackName, textViewTrackArtist, textViewLikes;
    ImageView imageViewTrackImage, imageViewProfilePicture;
    Button buttonLogout, buttonSettings, buttonLikedSongs;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    String trackName;
    String trackImage;
    String trackArtist;

    Double lat, lon;
    String location_string = "[0.0,0.0]";

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "Profile Attached");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Profile Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profileFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Profile ViewCreated");
        super.onViewCreated(view, savedInstanceState);

        // get variables and functions from main activity
        MainActivity mainActivity = (MainActivity) getActivity();

        getTrack();

        // textViewUsername
        textViewUsername = getActivity().findViewById(R.id.textViewUsername);
        Log.d(TAG, "Username: " + mainActivity.username);
        textViewUsername.setText(mainActivity.username);
        reference.child("Likes").child(mainActivity.user.getUid()).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewUsername.setText(mainActivity.username + " (" + snapshot.getValue().toString() + " likes)");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // textViewTrackName
        textViewTrackName = getActivity().findViewById(R.id.textViewTrackName);


        // imageViewTrackImage
        imageViewTrackImage = getActivity().findViewById(R.id.imageViewTrackImage);
        imageViewTrackImage.setVisibility(View.INVISIBLE);


        // buttonLogout

        buttonLogout = getActivity().findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy(); //remove remember me so the user isn't forcibly logged in again
                mainActivity.buttonPlayer.setVisibility(View.GONE);
                mainActivity.buttonProfile.setVisibility(View.GONE);
                mainActivity.buttonSounds.setVisibility(View.GONE);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, LoginFragment.class, null).commitNow();
                mainActivity.auth = null;
                SpotifyAppRemote.disconnect(mainActivity.mainSpotifyAppRemote);
                mainActivity.stopServiceFunc();
            }
        });

        // textViewLocation
        textViewLocation = getActivity().findViewById(R.id.textViewLocation);

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "textViewLocation clicked");
                lat = mainActivity.locationService.lat;
                lon = mainActivity.locationService.lon;
                reference.child("Locations").child(mainActivity.user.getUid()).child("lat").setValue(mainActivity.locationService.lat);
                reference.child("Locations").child(mainActivity.user.getUid()).child("lon").setValue(mainActivity.locationService.lon);
            }
        });

        mainActivity.reference.child("Locations").child(mainActivity.auth.getUid()).child("lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lat = Double.parseDouble(snapshot.getValue().toString());
                Log.d(TAG,"Lat: " + snapshot.getValue().toString());
                reference.child("Locations").child(mainActivity.auth.getUid()).child("lon").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lon = Double.parseDouble(snapshot.getValue().toString());
                        Log.d(TAG,"Lon: " + snapshot.getValue().toString());
                        String location_string = "[" + lat + "," + lon + "]";
                        textViewLocation.setText(location_string);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // buttonSettings
        buttonSettings = getActivity().findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "buttonSettings Pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, SettingsFragment.class, null).commitNow();
            }
        });

        // imageViewProfilePicture
        imageViewProfilePicture = getActivity().findViewById(R.id.imageViewProfilePicture);
        //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/joseph-13648.appspot.com/o/images%2F75b00248-9cd6-49bc-aabb-9b890682531f.jpg?alt=media&token=2f18e7f3-d8a1-4da2-9510-ce4447f50532").into(imageViewProfilePicture);
        reference.child("UserIdentification").child(mainActivity.auth.getUid()).child("profilePicture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Snapshot: " + snapshot);
                if (snapshot.getValue() != null) {
                    Picasso.get().load(snapshot.getValue().toString()).into(imageViewProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //textViewTrackArtist
        textViewTrackArtist = getActivity().findViewById(R.id.textViewTrackArtist);

        //buttonLikedSongs
        buttonLikedSongs = getActivity().findViewById(R.id.buttonLikedSongs);
        buttonLikedSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"buttonLikedSongs pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, LikesFragment.class, null).commitNow();
            }
        });
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Profile Started");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Profile Resumed");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Profile Paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Profile Stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Profile Destroyed");
        super.onDestroy();
    }

    void getTrack()
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        reference.child("Users").child(mainActivity.auth.getUid()).child("track").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String appendedString = "track$$track$$track$$track$$track";
                try{
                    appendedString = snapshot.getValue().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StringTokenizer tokens = new StringTokenizer(appendedString, "$$");
                trackName = tokens.nextToken();
                textViewTrackName.setText(trackName);
                trackImage = tokens.nextToken();

                if (trackImage != null) {
                    Log.d(TAG, "TrackImage URI: " + trackImage);
                    ImageUri imageUri = new ImageUri(trackImage);
                    Log.d(TAG, "ImageURI: " + imageUri.toString());
                    if (mainActivity.mainSpotifyAppRemote != null) {
                        Log.d(TAG, "Spotify success :)");
                        mainActivity.mainSpotifyAppRemote.getImagesApi().getImage(imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                            @Override
                            public void onResult(Bitmap data) {
                                imageViewTrackImage.setImageBitmap(data);
                                imageViewTrackImage.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Log.d(TAG, "Spotify failed :(");
                        reference.child("Users").child(mainActivity.user.getUid()).child("trackImage").setValue("Connecting");
                    }
                }

                trackArtist = tokens.nextToken();
                textViewTrackArtist.setText("by " + trackArtist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
