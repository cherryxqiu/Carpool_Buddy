package com.example.carpoolbuddy.carpoolbuddy.controllers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.internal.zabe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private FirebaseFirestore firestore;
        private EditText emailField;

        private EditText passwordField;
        private EditText usernameField;

        GoogleSignInClient mGoogleSignInClient;
        private ProgressDialog progressDialog;
        private int RC_SIGN_IN = 40;
        private Spinner addTypeSpinner;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            System.out.println("signup---");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            emailField = findViewById(R.id.edit_email);
            passwordField = findViewById(R.id.edit_password);
            usernameField = findViewById(R.id.signup_username);

            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setTitle("Google account");
            progressDialog.setMessage("creating account");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build();
            zabe GoogleSignIn = null;
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        public void SignUp2(View w){
            CheckBox userTypeCheckbox = (CheckBox)findViewById(R.id.user_type);
            boolean isChecked = userTypeCheckbox.isChecked();
            String t;
            if (isChecked) {
                t = "S";
            } else {
                t = "T/P";
            }
            System.out.println("sign up");
            String emailString = emailField.getText().toString();
            String passwordString = passwordField.getText().toString();
            String usernameString = usernameField.getText().toString();

            //check validity
            if(usernameString.equals("")  || passwordString.equals("")  || emailString.equals("")){
                Toast.makeText(com.example.carpoolbuddy.controllers.SignUpActivity.this, "Sign in failed. Please check your username and email are valid and password length is at least 6.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra("email", emailString);
            intent.putExtra("password", passwordString);
            intent.putExtra("username", usernameString);
            intent.putExtra("user_type", t);
            intent.putExtra("type", "signup");
            startActivity(intent);

        }
        public <FirebaseUser> void updateUI(FirebaseUser currentUser){
            if(currentUser != null){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }


    public void Return(View w){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    public void SignInWithGoogleSU(View w) {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("type", "signup_google");
        startActivity(intent);
    }

    public void ToSignIn(View w){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}