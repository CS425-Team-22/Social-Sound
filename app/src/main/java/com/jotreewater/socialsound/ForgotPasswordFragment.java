package com.jotreewater.socialsound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordFragment extends Fragment {

    private final String TAG = "TagForgot";
    Context forgotPasswordFragmentContext = null;

    public Button buttonSendEmail, buttonForgotBack;
    public TextInputEditText editTextSendEmail;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "Forgot Password Attached");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Forgot Password Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPasswordFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_forgotpassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ForgotPassword ViewCreated");
        super.onViewCreated(view, savedInstanceState);

        buttonSendEmail = getActivity().findViewById(R.id.buttonSendEmail);
        editTextSendEmail = getActivity().findViewById(R.id.editTextSendEmail);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send Email button pressed");
                if (resetPassword()) {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,LoginFragment.class,null).commitNow();
                }
            }
        });

        buttonForgotBack = getActivity().findViewById(R.id.buttonForgotBack);
        buttonForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,LoginFragment.class,null).commitNow();
            }
        });

        // back button functionality
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,LoginFragment.class,null).commitNow();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    private boolean resetPassword() {
        String email = editTextSendEmail.getText().toString().trim();

        // no email was entered
        if(email.isEmpty()) {
            editTextSendEmail.setError("Please enter an email address.");
            editTextSendEmail.requestFocus();
            return false;
        }
        // invalid email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextSendEmail.setError("Please enter a valid email address.");
            editTextSendEmail.requestFocus();
            return false;
        }

        try {
            // send a password reset email
            auth.sendPasswordResetEmail(email);
        } catch (Error e) {
            // unable to send
            Log.d(TAG, "Error generating email link: " + e.getMessage());
            Toast.makeText(forgotPasswordFragmentContext, "There was an error.", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(forgotPasswordFragmentContext, "Password reset sent to " + email, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Forgot Password Started");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Forgot Password Resumed");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Forgot Password Paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Forgot Password Stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Forgot Password Destroyed");
        super.onDestroy();
    }
}
