package com.example.carpoolbuddy.carpoolbuddy.controllers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.User;
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

public class LoadingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String type;
    private String user_type;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private int RC_SIGN_IN = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        type = getIntent().getStringExtra("type");
        user_type = getIntent().getStringExtra("user_type");

        //Set up for Google authentication
        progressDialog = new ProgressDialog(LoadingActivity.this);
        progressDialog.setTitle("Google account");
        progressDialog.setMessage("creating account");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(type.equals("login")){
            SignIn();
        }else if (type.equals("login_google")){
            firebaseAuthWithGoogle();
        }else  if (type.equals("signup")){
            SignUp();
        }else if(type.equals("signup_google")){
            firebaseAuthWithGoogle();
        }
    }
    private void SignIn() {
        String emailString = getIntent().getStringExtra("email");
        String passwordString = getIntent().getStringExtra("password");

        assert emailString != null;
        assert passwordString != null;
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(emailString + " " + passwordString);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            toLogin(null);
                        }
                    }
                });

    }

    private void SignUp() {
        String emailString = getIntent().getStringExtra("email");
        String passwordString = getIntent().getStringExtra("password");
        String usernameString = getIntent().getStringExtra("username");

        //google create the user
        mAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("SIGN UP","sign up succeeded");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateFirebase(emailString,usernameString);
                    updateUI(user);
                }else{
                   toLoginFromSU(null);
                }
            }
        });
    }


    private void firebaseAuthWithGoogle(){
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
                Toast.makeText(LoadingActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
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
                            if(type.equals("signup_google")){
                                String email = user.getEmail();
                                if (email != null && email.endsWith("@student.cis.edu.hk")) {
                                    user_type = "S";
                                }else{user_type = "P/T";}
                                updateFirebase(email,user.getDisplayName());
                             }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GOOGLE SIGN IN", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoadingActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        }
                    }
                });
    }

    private void updateFirebase(String email, String username){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        User user = new User(currentUser.getUid(), username, email,  user_type);
        firestore.collection("users").document(user.getUid()).set(user);
    }

    private void toLoginFromSU(FirebaseUser currentUser){
        Toast.makeText(com.example.carpoolbuddy.controllers.LoadingActivity.this, "Sign up failed. Please make sure that gmail is valid and password length is at least 6 chars", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    private void toLogin(FirebaseUser currentUser){
        System.out.println("toLogin");

        Toast.makeText(LoadingActivity.this, "Password or email invalid.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }


}