package com.example.carpoolbuddy.controllers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText emailField;
    private EditText passwordField;

    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private int RC_SIGN_IN = 40;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //Set up firebase auth and firestore instances
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Set up for Google authentication
        progressDialog = new ProgressDialog(AuthActivity.this);
        progressDialog.setTitle("Google account");
        progressDialog.setMessage("creating account");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    public void SignIn(View w){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void SignUp(View w){
        System.out.println("signup---");

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void firebaseAuthWithGoogle(View w){
        System.out.println("sign in with google");
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult");
        System.out.println(resultCode+" result code");
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                System.out.println("onActivityResult succeed");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e){
                System.out.println("!!!"+ e);
                Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                throw new RuntimeException();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        System.out.println("firebaseAuth");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("firebaseAuth succeed");
                            Log.d("GOOGLE SIGN IN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GOOGLE SIGN IN", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }




}
