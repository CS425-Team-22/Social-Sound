package com.jotreewater.socialsound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class SettingsFragment extends Fragment {
    private final String TAG = "TAGSettings";

    Context settingsFragmentContext;

    Button buttonBack, buttonSave;
    TextInputEditText editTextChangeUsername;
    public ImageView imageViewSettingsProfilePicture;
    Uri imageUri;
    CheckBox checkBoxAnonymous;

    boolean imageControl = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Settings Created");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "View Created");
        // get variables and functions from main activity
        MainActivity mainActivity = (MainActivity) getActivity();


        //editText
        editTextChangeUsername = getActivity().findViewById(R.id.editTextChangeUsername);

        // buttonBack
        buttonBack = getActivity().findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Back Button Pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        });

        // imageViewSettingsProfilePicture
        imageViewSettingsProfilePicture = getActivity().findViewById(R.id.imageViewSettingsProfilePicture);
        imageViewSettingsProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Image pressed");
                imageChooser();
            }
        });

        // checkBoxAnonymous
        checkBoxAnonymous = getActivity().findViewById(R.id.checkBoxAnonymous);
        if(mainActivity.anonymous == true)
        {
            checkBoxAnonymous.setChecked(true);
        }

        //buttonSave
        buttonSave = getActivity().findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Save button pressed");
                String newUsername = editTextChangeUsername.getText().toString();
                if (!newUsername.equals("")) {
                    mainActivity.reference.child("UserIdentification").child(mainActivity.auth.getUid()).child("username").setValue(newUsername);
                }
                if (imageControl) {
                    Log.d(TAG, "Image Control: " + imageControl);
                    UUID randomID = UUID.randomUUID();
                    String imageName = "images/" + randomID + ".jpg";
                    mainActivity.storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "Uploaded: " + randomID);
                            StorageReference storageReference = mainActivity.firebaseStorage.getReference(imageName);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filepath = uri.toString();
                                    mainActivity.reference.child("UserIdentification").child(mainActivity.auth.getUid()).child("profilePicture").setValue(filepath);
                                    Log.d(TAG, "Downloaded to database");
                                }
                            });
                        }
                    });
                }
                if(checkBoxAnonymous.isChecked())
                {
                    Log.d(TAG,"Anonymous mode enabled");
                    mainActivity.anonymous = true;
                }else
                {
                    Log.d(TAG,"Anonymous mode disabled");
                    mainActivity.anonymous = false;
                }
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        });

        // backButtonBehavior

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, ProfileFragment.class, null).commitNow();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && resultCode == getActivity().RESULT_OK) {
            Log.d(TAG, "resultCode: " + resultCode);
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewSettingsProfilePicture);
            imageControl = true;
        } else {
            Log.d(TAG, "resultCode: " + resultCode);
            imageControl = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Settings Destroyed");
    }

}
