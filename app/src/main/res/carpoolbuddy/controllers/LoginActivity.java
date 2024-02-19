package com.example.carpoolbuddy.controllers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText emailField;
    private EditText passwordField;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private int RC_SIGN_IN = 40;
    private String emailString;
    private String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        emailField = findViewById(R.id.edit_email);
        passwordField = findViewById(R.id.edit_password);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Google account");
        progressDialog.setMessage("creating account");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void SignIn(View w) {
         emailString = emailField.getText().toString();
         passwordString = passwordField.getText().toString();

        System.out.println("sign in" + emailString);

        //check validity
        if (passwordString.equals("") || emailString.equals("")) {
            Toast.makeText(LoginActivity.this, "Sign in failed. Please check your email is valid and password length is at least 6.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", emailString);
        intent.putExtra("password", passwordString);
        intent.putExtra("type", "login");
        startActivity(intent);
    }

    public void SignInWithGoogle(View w) {
        emailString = emailField.getText().toString();
        passwordString = passwordField.getText().toString();

        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", emailString);
        intent.putExtra("password", passwordString);
        intent.putExtra("type", "login_google");
        startActivity(intent);
    }

    public void Return(View w){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    public void ToSignUp(View w){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void ForgotPassword(View view) {
        emailString = emailField.getText().toString();
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("email", emailString);
        startActivity(intent);
    }


}