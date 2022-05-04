package com.jotreewater.socialsound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

public class PlayerFragment extends Fragment {
    private final String TAG = "TAGPlayer";

    // View Variables
    public AppCompatImageButton play;
    public AppCompatImageButton skip;
    public AppCompatImageButton rewind;
    public AppCompatImageButton seekForward;
    public AppCompatImageButton seekBack;
    public TextView trackInfo;
    public ImageView image;
    public MainActivity main;
    public SeekBar progressBar;
    seekbarThread thread = new seekbarThread();
    int position = 0;
    private Button openSpotify;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "Player Attached");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Player Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Player ViewCreated");
        super.onViewCreated(view, savedInstanceState);

        main = (MainActivity) getActivity();
        
        // Assign buttons to their correct views
        play = getActivity().findViewById(R.id.play_pause_button);
        skip = getActivity().findViewById(R.id.skip_next_button);
        rewind = getActivity().findViewById(R.id.skip_prev_button);
        seekForward = getActivity().findViewById(R.id.seek_forward_button);
        seekBack = getActivity().findViewById(R.id.seek_back_button);
        image = getActivity().findViewById(R.id.image);
        trackInfo = getActivity().findViewById(R.id.current_track_label);
        progressBar = getActivity().findViewById(R.id.seekBar);
        openSpotify = getActivity().findViewById(R.id.spotifyButton);

        // Create onClickListeners for each button that is displayed on the Player Fragment
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayPauseButtonClicked();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipThread skip = new skipThread(); // Starts a separate thread to track the progress bar for new song
                skip.start();
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewindThread rewind = new rewindThread();   // Starts thread to rewind progresss bar on rewind
                rewind.start();
            }
        });

        seekForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSeekForward();
                progressBar.setProgress(progressBar.getProgress()+15);
            }
        });

        seekBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSeekBack();
                progressBar.setProgress(progressBar.getProgress()-15);
            }
        });

        openSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//main.mainSpotifyAppRemote.getPlayerApi().pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                main.mainSpotifyAppRemote.
                        getPlayerApi().
                        seekTo(seekBar.getProgress()*1000);
                main.mainSpotifyAppRemote.
                        getPlayerApi().
                        resume();
            }
        });

        updatePlayPauseButton();
        updateCoverImage();
        showCurrentTrack();
        initializeProgressBar();
    }

    // Play button that switches between and image of the play button
    // then once paused, switches to the pause button
    public void onPlayPauseButtonClicked() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                int pauseID = main.getResources().getIdentifier("btn_pause", "drawable", main.getPackageName());
                                play.setImageResource(pauseID);

                                main.mainSpotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setErrorCallback(main.mErrorCallback);
                            } else {
                                int playID = main.getResources().getIdentifier("btn_play", "drawable", main.getPackageName());
                                play.setImageResource(playID);

                                main.mainSpotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setErrorCallback(main.mErrorCallback);
                            }
                        });
    }

    // Function to skip a song backwards when the skip button is pressed
    public void onSkipPreviousButtonClicked() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .skipPrevious();

        // Sleeps the program for 2.2 seconds so that the API call
        // to change the album cover can occur
        try {
            Thread.sleep(200);
            updateCoverImage();
            showCurrentTrack();
            updatePlayPauseButton();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Skips the current song being played
    public void onSkipNextButtonClicked() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .skipNext()
                .setErrorCallback(main.mErrorCallback);

        // Sleeps the program for 2.2 seconds so that the API call
        // to change the album cover can occur
        try {
            Thread.sleep(200);
            updateCoverImage();
            showCurrentTrack();
            updatePlayPauseButton();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Moves the song duration back by 15 seconds
    public void onSeekBack() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(-15000)
                .setErrorCallback(main.mErrorCallback);
    }

    // Moves the song duration forward by 15 seconds
    public void onSeekForward() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(15000)
                .setErrorCallback(main.mErrorCallback);
    }

    public void updatePlayPauseButton() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                int playID = main.getResources().getIdentifier("btn_play", "drawable", main.getPackageName());
                                play.setImageResource(playID);
                            } else {
                                int pauseID = main.getResources().getIdentifier("btn_pause", "drawable", main.getPackageName());
                                play.setImageResource(pauseID);
                            }
                        });
    }

    // Set the cover image to hte image of the current song playing
    public void updateCoverImage() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerstate -> {
                            if (playerstate != null) {
                                main.mainSpotifyAppRemote
                                        .getImagesApi()
                                        .getImage(playerstate.track.imageUri)
                                        .setResultCallback(
                                                bitmap -> image.setImageBitmap(bitmap)
                                        );
                            }
                        });

        main.mainSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerstate -> System.out.println(playerstate.track.imageUri)
                );
    }

    // Display the track name and artist of the current song playing
    public void showCurrentTrack() {
        main.mainSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerstate -> {
                            String track = playerstate.track.name;
                            String artist = playerstate.track.artist.name;
                            trackInfo.setText(String.format("Track: %s\nBy: %s", track, artist));
                        }
                );
    }

    // Starts the progress bar at 0 for a track and sets the bar max length to the duration of a track
    private void initializeProgressBar() {
        main.mainSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            if (playerState.track != null) {
                position = 0;
                progressBar.setMax((int) (playerState.track.duration / 1000));
                updateCoverImage();
                showCurrentTrack();
                thread.start();
            }
        });
    }

    // Thread class used to initialize and track progress bar throughout a song
    public class seekbarThread extends Thread {
        public void run() {
            while (position < progressBar.getMax())
            {
                main.mainSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(
                        player -> {
                            position = (int) (player.playbackPosition/1000);
                            progressBar.setProgress(position);
                        });

                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            progressBar.setProgress(0);
        }
    }

    // Thread class used to fix progress bar for a song change
    public class skipThread extends Thread {
        public void run() {
            onSkipNextButtonClicked();
        }
    }
    
    // Thread class used to fix progress bar for a song change
    public class rewindThread extends Thread {
        public void run() {
            onSkipPreviousButtonClicked();
        }
    }
}
