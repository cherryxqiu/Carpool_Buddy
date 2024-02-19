package com.example.carpoolbuddy.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.AuthActivity;
import com.example.carpoolbuddy.controllers.profile.EditProfileActivity;
import com.example.carpoolbuddy.controllers.profile.HelpActivity;
import com.example.carpoolbuddy.controllers.profile.MessageMainActivity;
import com.example.carpoolbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> logout());

        TextView editButton = view.findViewById(R.id.edit);
        ImageView editIcon = view.findViewById(R.id.edit2);
        editButton.setOnClickListener(v -> openEditProfileActivity());
        editIcon.setOnClickListener(v -> openEditProfileActivity());

        TextView messageButton = view.findViewById(R.id.message);
        ImageView messageIcon = view.findViewById(R.id.message2);
        messageButton.setOnClickListener(v -> openMessageMainActivity());
        messageIcon.setOnClickListener(v -> openMessageMainActivity());

        TextView helpButton = view.findViewById(R.id.help);
        ImageView helpIcon = view.findViewById(R.id.help2);
        helpButton.setOnClickListener(v -> openHelpMainActivity());
        helpIcon.setOnClickListener(v -> openHelpMainActivity());

        getUserData(view);

        return view;
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }

    private void openMessageMainActivity() {
        Intent intent = new Intent(getActivity(), MessageMainActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }
    private void openHelpMainActivity() {
        Intent intent = new Intent(getActivity(), HelpActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }

    @SuppressLint("SetTextI18n")
    private void getUserData(View view) {
        if (isAdded() && requireContext() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();

            TextView nameTextView = view.findViewById(R.id.name);
            TextView emailTextView = view.findViewById(R.id.email);
            TextView phoneTextView = view.findViewById(R.id.vehicle_phone);
            TextView ridesTextView = view.findViewById(R.id.rides_num);
            TextView tripsTextView = view.findViewById(R.id.trips_num);
            TextView ratingTextView = view.findViewById(R.id.rating);

            ImageView profileImageView = view.findViewById(R.id.circleImageView);

            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading user information...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            DocumentReference userRef = firestore.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (isAdded() && requireContext() != null) {
                    if (documentSnapshot.exists()) {
                        User user1 = documentSnapshot.toObject(User.class);
                        nameTextView.setText(user1.getName());
                        emailTextView.setText(user1.getEmail());
                        phoneTextView.setText(user1.getPhone());
                        ratingTextView.setText(user1.getRating()+"");
                        ridesTextView.setText(user1.getRatingNum()+"");

                        StorageReference profileImageRef = storageReference.child("profile_images")
                                .child(userId + ".jpg");

                        progressDialog.setMessage("Loading profile image...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(null);

                            Glide.with(requireContext())
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
                            if (isAdded() && requireContext() != null) {
                                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                if (isAdded() && requireContext() != null) {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            CollectionReference reservationsCollectionRef = firestore.collection("reservations").document(userId).collection(userId);
            reservationsCollectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int numberOfRecords = task.getResult().size();
                    tripsTextView.setText(numberOfRecords+"");
                } else {
                    Toast.makeText(view.getContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });





        }
    }
}