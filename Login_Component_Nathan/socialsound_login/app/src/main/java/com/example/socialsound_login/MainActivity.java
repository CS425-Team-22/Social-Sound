package com.example.socialsound_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Patterns;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.register_button);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.pass_reset_button);
        forgotPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email_entry_register);
        editTextPassword = (EditText) findViewById(R.id.password_entry_register);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button: //user clicks "Register
                startActivity(new Intent(this, Register_User.class));
                break;
            case R.id.pass_reset_button: //user clicks forgot password
                startActivity(new Intent(this, PasswordReset.class));
                break;
            case R.id.login_button: //user clicks login
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid e-mail address!");
        }
        if(password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //check email verification
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified())
                    {
                        //log user in
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to login!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}