package com.jotreewater.socialsound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginFragment extends Fragment {

    private final String TAG = "TAGLogin";

    // View Variables
    public TextInputEditText editTextEmail, editTextPassword;
    public Button buttonLogin;
    public Button buttonRegister;
    public TextView forgotPassword;
    public CheckBox rememberMe;
    public ProgressBar progressBar;

    FirebaseAuth auth;

    // Toast context variable
    Context loginFragmentContext;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "Login Attached");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Login Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginFragmentContext = container.getContext();
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Login ViewCreated");
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();

        //spinny thingy when logging in :)))
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        editTextEmail = getActivity().findViewById(R.id.editTextEmail);
        editTextPassword = getActivity().findViewById(R.id.editTextPassword);
        buttonLogin = getActivity().findViewById(R.id.buttonLogin);
        buttonRegister = getActivity().findViewById(R.id.buttonRegister);
        forgotPassword = getActivity().findViewById(R.id.forgotPassword);
        rememberMe = getActivity().findViewById(R.id.rememberMe);

        //init paper
        Paper.init(loginFragmentContext);

        String userEmailKey = Paper.book().read(Prevalent.userEmail);
        String userPasswordKey = Paper.book().read(Prevalent.userPassword);

        if(userEmailKey != null && userPasswordKey != null) {
            if (!userEmailKey.isEmpty() && !userPasswordKey.isEmpty()) {
                //automatically login
                buttonLogin.setVisibility(View.GONE);
                buttonRegister.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.GONE);
                editTextEmail.setVisibility(View.GONE);
                editTextPassword.setVisibility(View.GONE);
                rememberMe.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(loginFragmentContext, "Already logged in.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "=== Attempting to login automatically. User Profile: " + userEmailKey + " ===");
                signin(userEmailKey, userPasswordKey);
            }
        }

        //"Forgot Password?" checkbox
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Forgot Password pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,ForgotPasswordFragment.class,null).commitNow();
            }
        });

        // buttonLogin
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "=== Login Button pressed. ===");
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                //no email address
                if(email.isEmpty()) {
                    editTextEmail.setError("Please enter your email address.");
                    editTextEmail.requestFocus();
                    return;
                }

                //invalid email address
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Please enter a valid email address.");
                    editTextEmail.requestFocus();
                    return;
                }

                //no password
                if (password.isEmpty()) {
                    editTextPassword.setError("Please enter your password.");
                    editTextPassword.requestFocus();
                    return;
                }

                //if it reaches here, the user has a valid email and password entered
                Log.d(TAG, "=== Information validated. Logging in... ===");
                signin(email,password);
            }

        });
        // buttonRegister
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "buttonRegister pressed");
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,RegisterFragment.class,null).commitNow();
            }
        });
    }

    // FirebaseAuth
    public void signin(String email, String password) {
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Password: " + password);
        //show spin wheel
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    Log.d(TAG, "=== User information validated. ===");
                    //remember user/password if box is checked
                    if(rememberMe.isChecked()) {
                        Log.d(TAG, "=== Remember Me was pressed. ===");
                        Paper.book().write(Prevalent.userEmail, email);
                        Paper.book().write(Prevalent.userPassword, password);
                    }

                    //create new user with email and password info
                    Log.d(TAG, "=== New user status created. ===");
                    User userData = new User(email, password);


                    Toast.makeText(loginFragmentContext,"Successful login",Toast.LENGTH_SHORT).show();

                    //set current user
                    Log.d(TAG, "=== New user status set. ===");
                    Prevalent.currentUser = userData;

                    // Takes the user to the main activity //
                    MainActivity mainActivity = (MainActivity) getActivity();

                    mainActivity.takeData(auth);
                    //.....................................//
                } else {
                    Toast.makeText(loginFragmentContext,"Login failed, please try again.",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    buttonLogin.setVisibility(View.VISIBLE);
                    buttonRegister.setVisibility(View.VISIBLE);
                    forgotPassword.setVisibility(View.VISIBLE);
                    editTextEmail.setVisibility(View.VISIBLE);
                    editTextPassword.setVisibility(View.VISIBLE);
                    rememberMe.setVisibility(View.VISIBLE);
                    Paper.book().destroy(); //remove remember me so the user isn't forcibly logged in again

                }
            }
        });
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Login Started");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Login Resumed");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Login Paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Login Stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Login Destroyed");
        super.onDestroy();
    }
}