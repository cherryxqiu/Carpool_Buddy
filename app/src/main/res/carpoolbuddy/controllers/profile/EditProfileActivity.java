package com.example.carpoolbuddy.controllers.profile;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private EditText emailField;
    private String userId;
    private EditText phoneField;
    private EditText usernameField;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        emailField = findViewById(R.id.edit_email);
        phoneField = findViewById(R.id.edit_phone);
        usernameField = findViewById(R.id.edit_name);
        getUser();

        ImageView circleImageView = findViewById(R.id.circleImageView);
        circleImageView.setOnClickListener(this);
    }

    private void getUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        ProgressDialog progressDialog = new ProgressDialog(this);

        // get user info, set in the TextView
        TextView name = findViewById(R.id.edit_name);
        TextView email = findViewById(R.id.edit_email);
        TextView phone = findViewById(R.id.edit_phone);

        userId = user.getUid();

        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user1 = documentSnapshot.toObject(User.class);
                name.setText(user1.getName());
                email.setText(user1.getEmail());
                phone.setText(user1.getPhone());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        });
        ImageView profileImageView = findViewById(R.id.circleImageView);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageReference.child("profile_images")
                .child(userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(null);

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressDialog.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressDialog.dismiss();
                            return false;
                        }
                    })
                    .into(profileImageView);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    public void edit(View view) {
        String emailString = emailField.getText().toString();
        String phoneString = phoneField.getText().toString();
        String usernameString = usernameField.getText().toString();

        // Check validity
        if (usernameString.equals("") || phoneString.equals("") || emailString.equals("")) {
            Toast.makeText(this, "Please check your username, email, and password are valid.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageUri != null) {
            uploadImage(emailString, usernameString, phoneString);
        } else {
            updateFirebase(emailString, usernameString, phoneString, null);
        }
    }

    private void uploadImage(String email, String username, String phone) {
        StorageReference imageRef = storageRef.child(userId + ".jpg");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updateFirebase(email, username, phone, imageUrl);
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void updateFirebase(String email, String username, String phone, String imageUrl) {
        DocumentReference userRef = firestore.collection("users").document(userId);

        if (imageUrl != null) {
            userRef.update("name", username, "email", email, "phone", phone, "imageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        back(null);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            userRef.update("name", username, "email", email, "phone",phone)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        back(null);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    public void back(View view) {
//        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
//        startActivity(intent);
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.circleImageView) {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView circleImageView = findViewById(R.id.circleImageView);
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}