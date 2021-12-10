package com.example.social_sound;

import android.os.Bundle;

import com.example.social_sound.ui.create_profile.CreateFragment;
import com.example.social_sound.ui.login.LoginFragment;
import com.example.social_sound.ui.playlist.PlaylistFragment;
import com.example.social_sound.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.social_sound.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProfileFragment profile_fragment;
    private CreateFragment create_fragment;
    private LoginFragment login_fragment;
    private PlaylistFragment playlist_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //*****************************************************************************************
        //  Ernesto Bernardo MainActivity.java
        //  MainActivity.java and functionality created with Android Studio Template
        //  X_fragment variables and displayX() and closeX() functions created by Ernesto Bernardo
        //*****************************************************************************************
        profile_fragment = new ProfileFragment();
        create_fragment = new CreateFragment();
        login_fragment = new LoginFragment();
        playlist_fragment = new PlaylistFragment();
    }

    public void displayProfile(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, profile_fragment);
        ft.commit();
    }

    public void closeProfile(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(profile_fragment);
        ft.commit();
    }

    public void displayCreate(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, create_fragment);
        ft.commit();
    }

    public void closeCreate(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(create_fragment);
        ft.commit();
    }

    public void displayLogin(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, login_fragment);
        ft.commit();
    }

    public void closeLogin(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(login_fragment);
        ft.commit();
    }

    public void displayPlaylist(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, playlist_fragment);
        ft.commit();
    }

    public void closePlaylist(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(playlist_fragment);
        ft.commit();
    }
}