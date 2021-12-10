package com.example.social_sound.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        //*****************************************************************************************
        //  Ernesto Bernardo HomeViewModel.java
        //  File and functionality created with Android Studio Template
        //  Only string inside setValue() changed by Ernesto Bernardo
        //*****************************************************************************************
        mText.setValue("Your Sound Space");
    }

    public LiveData<String> getText() {
        return mText;
    }
}