package com.example.social_sound.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        //*****************************************************************************************
        //  Ernesto Bernardo DashboardViewModel.java
        //  File and functionality created with Android Studio Template
        //  Only string inside setValue() changed by Ernesto Bernardo
        //*****************************************************************************************
        mText.setValue("Discover your Social Sound!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}