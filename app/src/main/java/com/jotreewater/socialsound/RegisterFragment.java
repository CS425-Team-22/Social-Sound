package com.jotreewater.socialsound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

@SuppressWarnings("ALL")
public class RegisterFragment extends Fragment {

    private final String TAG = "TAGRegister";
    Context registerFragmentContext = null;

    public Button buttonRegister2, buttonRegisterBack;
    public TextInputEditText editTextRegisterEmail, editTextRegisterPassword, editTextRegisterUsername, editTextRegisterConfirmPassword;
    public ImageView imageViewRegisterProfilePicture;

    boolean imageControl = false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "Register Attached");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Register Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Register ViewCreated");
        super.onViewCreated(view, savedInstanceState);

        buttonRegister2 = getActivity().findViewById(R.id.buttonRegister2);
        buttonRegisterBack = getActivity().findViewById(R.id.buttonRegisterBack);
        editTextRegisterEmail = getActivity().findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = getActivity().findViewById(R.id.editTextRegisterPassword);
        editTextRegisterUsername = getActivity().findViewById(R.id.editTextRegisterUsername);
        editTextRegisterConfirmPassword = getActivity().findViewById(R.id.editTextConfirmPassword);
        imageViewRegisterProfilePicture = getActivity().findViewById(R.id.imageViewRegisterProfilePicture);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // buttonRegister2

        buttonRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "buttonRegister2 pressed");

                String email = editTextRegisterEmail.getText().toString();
                String password = editTextRegisterPassword.getText().toString();
                String username = editTextRegisterUsername.getText().toString();
                String confirmPassword = editTextRegisterConfirmPassword.getText().toString();

                Log.d(TAG, "Password Length: " + password.length());

                // no email was entered
                if(email.isEmpty()) {
                    editTextRegisterEmail.setError("Please enter an email address.");
                    editTextRegisterEmail.requestFocus();
                    return;
                }
                // invalid email address
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextRegisterEmail.setError("Please enter a valid email address.");
                    editTextRegisterEmail.requestFocus();
                    return;
                }
                // no password
                if (password.isEmpty()) {
                    editTextRegisterPassword.setError("Please enter your password.");
                    editTextRegisterPassword.requestFocus();
                    return;
                }
                // no password confirm
                if (confirmPassword.isEmpty()) {
                    editTextRegisterPassword.setError("Please confirm your password.");
                    editTextRegisterConfirmPassword.requestFocus();
                    return;
                }

                if (!confirmPassword.equals(password)) {
                    editTextRegisterPassword.setError("Passwords do not match.");
                    editTextRegisterConfirmPassword.requestFocus();
                    return;
                }
                // no username
                if (username.isEmpty()) {
                    editTextRegisterUsername.setError("Please enter your username.");
                    editTextRegisterUsername.requestFocus();
                    return;
                }
                register(email,password,username);
            }
        });

        // backButtonBehavior
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, LoginFragment.class, null).commitNow();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // buttonBack
        buttonRegisterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, LoginFragment.class, null).commitNow();
            }
        });

        // imageView onclick
        imageViewRegisterProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Image pressed");
                imageChooser();
            }
        });


    }

    public void register(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    reference.child("UserIdentification").child(auth.getUid()).child("username").setValue(username);
                    reference.child("Likes").child(auth.getUid()).child("likes").setValue("0");
                    if (imageControl) {
                        Log.d(TAG, "Image Control: " + imageControl);
                        UUID randomID = UUID.randomUUID();
                        String imageName = "images/" + randomID + ".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "Uploaded: " + randomID);
                                StorageReference storageReference = firebaseStorage.getReference(imageName);
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        reference.child("UserIdentification").child(auth.getUid()).child("profilePicture").setValue(filepath);
                                        Log.d(TAG, "Downloaded to database");
                                    }
                                });
                            }
                        });
                    } else {
                        reference.child("UserIdentification").child(auth.getUid()).child("profilePicture").setValue("null");
                    }

                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, LoginFragment.class, null).commitNow();
                } else {
                    Log.d(TAG, task.getException().toString());
                    Toast.makeText(registerFragmentContext, "Registration failed, perhaps this account already exists?", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            Picasso.get().load(imageUri).into(imageViewRegisterProfilePicture);
            imageControl = true;
        } else {
            Log.d(TAG, "resultCode: " + resultCode);
            imageControl = false;
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Register Started");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Register Resumed");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Register Paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Register Stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Register Destroyed");
        super.onDestroy();
    }
}
