package com.example.social_sound.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        //*****************************************************************************************
        //  Ernesto Bernardo NotificationsViewModel.java
        //  File and functionality created with Android Studio Template
        //  Only string inside setValue() changed by Ernesto Bernardo
        //*****************************************************************************************
        mText.setValue("Search for users or songs by name");
    }

    public LiveData<String> getText() {
        return mText;
    }
}