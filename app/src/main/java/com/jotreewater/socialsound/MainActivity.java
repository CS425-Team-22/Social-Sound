package com.jotreewater.socialsound;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Track;

import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "TAGMain";

    // Firebase variables
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String previousTimestamp = "";

    // Spotify variables
    private static final String CLIENT_ID = "2d220991873e4b458063f1677be857b3";
    private static final String REDIRECT_URI = "http://com.jotreewater.socialsound/callback";
    private static final int REQUEST_CODE = 1337;
    public static SpotifyAppRemote mainSpotifyAppRemote;

    // New location variables
    private static int FINE_LOCATION_REQUEST = 99;
    private static int BACKGROUND_LOCATION_REQUEST = 100;
    LocationService locationService = new LocationService();
    Intent ServiceIntent;
    double lat, lon;
    String location_string = "[0,0]";

    // Auth variables
    public String username = "Username";
    public String profilePicture = "";
    String otherUsername = "null";
    String otherProfilePicture = "null";
    boolean anonymous = false;

    // Spotify variables
    public String trackName ="";
    public String trackImage;
    public String trackArtist;
    ErrorCallback mErrorCallback;

    // Main Activity variables
    Button buttonProfile;
    Button buttonSounds;
    Button buttonPlayer;

    String sound_id;
    double keyLat2;
    double keyLon2;

    Context context = this;

    // Happens first, happens once
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Main Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttonProfile
        buttonProfile = findViewById(R.id.buttonProfile);

        if (auth == null) {
            buttonProfile.setVisibility(View.GONE);
        }

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth != null) {
                    Log.d(TAG, "buttonProfile pressed");
                    ProfileFragment profileFragment = new ProfileFragment();
                    Fragment previous_fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                    getSupportFragmentManager().beginTransaction().remove(previous_fragment).commitNow();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, profileFragment).commitNow();
                }
            }
        });

        //buttonSounds
        buttonSounds = findViewById(R.id.buttonSounds);

        if (auth == null) {
            buttonSounds.setVisibility(View.GONE);
        }

        buttonSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth != null) {
                    Log.d(TAG, "buttonSounds pressed");
                    SoundsFragment soundsFragment = new SoundsFragment();
                    Fragment previous_fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                    getSupportFragmentManager().beginTransaction().remove(previous_fragment).commitNow();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, soundsFragment).commitNow();
                }
            }
        });

        // buttonPlayer
        buttonPlayer = findViewById(R.id.buttonPlayer);

        if (auth == null) {
            buttonPlayer.setVisibility(View.GONE);
        }

        buttonPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth != null) {
                    Log.d(TAG, "buttonPlayer pressed");
                    PlayerFragment playerFragment = new PlayerFragment();
                    Fragment previous_fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                    getSupportFragmentManager().beginTransaction().remove(previous_fragment).commitNow();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, playerFragment).commitNow();
                }
            }
        });


        // Check if the user needs to log in

        if (auth == null) {
            Log.d(TAG, "No UID found, starting LoginFragment");
            FragmentManager loginFragmentManager = getSupportFragmentManager();
            LoginFragment loginFragment = new LoginFragment();
            loginFragmentManager.beginTransaction().add(R.id.fragmentContainerView, loginFragment).commitNow();
        }
    }

    public void takeData(FirebaseAuth login_auth) {
        auth = login_auth;
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        reference.child("UserIdentification").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Get username
                username = (String) snapshot.child("username").getValue();

                // Get profilePicture
                profilePicture = (String) snapshot.child("profilePicture").getValue();

                // New Location Service
                startLocationService();

                // Spotify
                initializeSpotifyService();

                // Firebase Player Service
                initializeFirebasePlayerService();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initializeSpotifyService() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true).build();

        SpotifyAppRemote.disconnect(mainSpotifyAppRemote);

        Log.d(TAG,"Initalizing Spotify");

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mainSpotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "Spotify Connected");

                //reference.child("Users").child(auth.getUid()).child("track").setValue("track$$track$$track$$track$$track");
                reference.child("Locations").child(user.getUid()).child("lat").setValue(locationService.lat);
                reference.child("Locations").child(user.getUid()).child("lon").setValue(locationService.lon);

                // Switch to Profile Fragment
                //progressBar.setVisibility(View.GONE);
                ProfileFragment profileFragment = new ProfileFragment();
                Fragment previous_fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                getSupportFragmentManager().beginTransaction().remove(previous_fragment).commitNow();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, profileFragment).commitNow();
                // Reveal navbar
                buttonProfile.setVisibility(View.VISIBLE);
                buttonSounds.setVisibility(View.VISIBLE);
                buttonPlayer.setVisibility(View.VISIBLE);

                mainSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        String timestamp = Calendar.getInstance().getTime().toString();
                        String imageUriStringConversion = track.imageUri.toString();
                        imageUriStringConversion = imageUriStringConversion.substring(8, imageUriStringConversion.length() - 2);
                        if(!anonymous)
                        {
                            // Push track to Firebase
                            String appendedTrack = track.name + "$$" + imageUriStringConversion + "$$" + track.artist.name + "$$" + track.album.name + "$$" + track.uri.toString();
                            Log.d(TAG,"Appended Track: " + appendedTrack);
                            reference.child("Users").child(user.getUid()).child("track").setValue(appendedTrack);
                        }
                        location_string = "[" + lat + "," + lon + "]";
                        reference.child("Locations").child(user.getUid()).child("lat").setValue(locationService.lat);
                        reference.child("Locations").child(user.getUid()).child("lon").setValue(locationService.lon);
                        // For Profile
                        trackName = track.name;
                        trackImage = imageUriStringConversion;
                        trackArtist = track.artist.name;
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d(TAG, "Spotify failed to connect: " + error);
                initializeSpotifyService();
            }
        });
    }

    public void initializeFirebasePlayerService() {
        reference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println(snapshot.getChildren());
                String appendedString = "track$$track$$tack$$track$$track";
                try {
                    appendedString = snapshot.child("track").getValue().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StringTokenizer tokens = new StringTokenizer(appendedString, "$$");
                String trackName = tokens.nextToken();
                String trackImage = tokens.nextToken();
                String trackArtist = tokens.nextToken();
                String trackAlbum = tokens.nextToken();
                String trackUri = tokens.nextToken();

                // Location Variables
                float[] distance = new float[1];
                String key;
                key = snapshot.getKey();

                lat = locationService.lat;
                lon = locationService.lon;
                Log.d(TAG,"NEW lon: " + lon);
                Log.d(TAG,"NEW lat: " + lat);

                reference.child("Locations").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        keyLat2 = Double.parseDouble(snapshot.child("lat").getValue().toString());
                        keyLon2 = Double.parseDouble(snapshot.child("lon").getValue().toString());
                        Location.distanceBetween(lat, lon, keyLat2, keyLon2, distance); // returns distance in meters

                        String timestamp = Calendar.getInstance().getTime().toString();

                        if (distance[0] < 50 && !(timestamp.equals(previousTimestamp)) && (auth.getUid() != key) && !trackName.equals("track")) {
                            Log.d(TAG, "Swappin");
                            Log.d(TAG, "Succeed Distance: " + distance[0]);

                            sound_id = reference.child("Sounds").child(auth.getUid()).push().getKey();
                            Log.d(TAG, "snapshot trackname: " + snapshot.getValue().toString());
                            reference.child("Sounds").child(auth.getUid()).child(sound_id).child("trackName").setValue(trackName);
                            reference.child("Sounds").child(auth.getUid()).child(sound_id).child("trackArtist").setValue(trackArtist);
                            reference.child("Sounds").child(auth.getUid()).child(sound_id).child("trackAlbum").setValue(trackAlbum);
                            reference.child("Sounds").child(auth.getUid()).child(sound_id).child("trackImage").setValue(trackImage);
                            reference.child("Sounds").child(auth.getUid()).child(sound_id).child("trackUri").setValue(trackUri);

                            // User ID variables
                            reference.child("UserIdentification").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    otherUsername = snapshot.child("username").getValue().toString();
                                    otherProfilePicture = snapshot.child("profilePicture").getValue().toString();

                                    reference.child("Sounds").child(auth.getUid()).child(sound_id).child("username").setValue(otherUsername);
                                    reference.child("Sounds").child(auth.getUid()).child(sound_id).child("profilePicture").setValue(otherProfilePicture);
                                    reference.child("Sounds").child(auth.getUid()).child(sound_id).child("key").setValue(key);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            previousTimestamp = timestamp;
                        } else {
                            if (!(timestamp.equals(previousTimestamp)) && (auth.getUid() != key)) {
                                Log.d(TAG, "Fail Distance: " + distance[0]);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Happens whenever the activity is opened
    @Override
    protected void onStart() {
        Log.d(TAG, "Main Started");
        super.onStart();
    }

    // Happens whenever the activity is closed
    @Override
    protected void onStop() {
        Log.d(TAG, "Main Stopped");
        super.onStop();
    }

    // Happens when configuration changes
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Main Destroyed");
        stopServiceFunc();
        SpotifyAppRemote.disconnect(mainSpotifyAppRemote);
    }

    private void startLocationService()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Background permission");
                    alertDialog.setMessage("-Allow only while using the app- permission has been granted. If you want\n" +
                            "        to get location updates in the background even when not using the app then grant\n" +
                            "        -Allow all the time- permission");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Start service anyway",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startServiceFunc();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Grant background Permission",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    requestBackgroundLocationPermission();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();


                }else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    startServiceFunc();
                }
            }else{
                startServiceFunc();
            }

        }else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("ACCESS_FINE_LOCATION");
                alertDialog.setMessage("Location permission required");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                requestFineLocationPermission();
                                dialog.dismiss();
                            }
                        });


                alertDialog.show();

            } else {
                requestFineLocationPermission();
            }
        }
    }
    private void startServiceFunc(){
        locationService = new LocationService();
        ServiceIntent = new Intent(this, locationService.getClass());
        if (!isServiceRunning()) {
            startService(ServiceIntent);
            setServiceRunning(true);
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Service Already Started", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopServiceFunc(){
        locationService = new LocationService();
        ServiceIntent = new Intent(this, locationService.getClass());
        if (isServiceRunning()) {
            stopService(ServiceIntent);
            setServiceRunning(false);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
            //saveLocation(); // explore it by your self
        } else {
            Toast.makeText(this, "Location Service was already Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                BACKGROUND_LOCATION_REQUEST);
    }

    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, FINE_LOCATION_REQUEST);
    }
    private boolean serviceRunning = false;

    public boolean isServiceRunning() {
        return serviceRunning;
    }

    public void setServiceRunning(boolean serviceRunning) {
        this.serviceRunning = serviceRunning;
    }

    private void isDuplicate() {

    }

}