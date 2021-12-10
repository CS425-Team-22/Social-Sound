package com.example.socialsound_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField;
    private Button sendEmail;
    private TextView cancel;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        emailField = (EditText) findViewById(R.id.emailResetField);

        sendEmail = (Button) findViewById(R.id.resetPasswordButton);
        sendEmail.setOnClickListener(this);

        cancel = (TextView) findViewById(R.id.cancelResetPasswordButton);
        cancel.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPasswordButton:
                resetPassword();
                if (resetPassword()) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.cancelResetPasswordButton:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    private boolean resetPassword() { //returns true if the password was sent
        String email = emailField.getText().toString().trim();
        try {
            mAuth.sendPasswordResetEmail(email);
        } catch (Error e) {
            System.out.println("Error generating email link: " + e.getMessage());
            Toast.makeText(PasswordReset.this, "There was an error.", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(PasswordReset.this, "Password reset sent to the given email.", Toast.LENGTH_LONG).show();
        return true;
    }
}