package com.example.socialsound_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Patterns;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ProgressBar;

import android.content.Intent;
import android.widget.Toast;

public class Register_User extends AppCompatActivity implements View.OnClickListener {

    //button var
    private EditText editTextEmail, editTextPassword, editTextName;
    private ProgressBar progressBar;
    private Button back, register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        back = (Button) findViewById(R.id.go_back_button);
        back.setOnClickListener(this);

        register = (Button) findViewById(R.id.register_button_register);
        register.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email_entry_register);
        editTextPassword = (EditText) findViewById(R.id.password_entry_register);
        editTextName = (EditText) findViewById(R.id.name_entry);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back_button:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register_button_register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("E-mail is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email!
            editTextEmail.setError("Invalid e-mail address!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 3) {
            editTextPassword.setError("Password needs to be at least 3 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(email, name);
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register_User.this, "Registered successfully.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Register_User.this, "Failed to register.", Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(Register_User.this, "Failed to register.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

    }
}