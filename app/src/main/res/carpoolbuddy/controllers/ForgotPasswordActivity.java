package com.example.carpoolbuddy.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        emailField = findViewById(R.id.edit_email);
        String email  = getIntent().getStringExtra("email");
        System.out.println("email: "+email);
        if(email != null && !email.equals("")) emailField.setText(email);
    }

    public void sent(View w) {
        String emailString = emailField.getText().toString();
        if (TextUtils.isEmpty(emailString)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(emailString)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email sent successfully
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to send password reset email
                        Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void Return(View w){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}