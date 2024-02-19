package com.example.carpoolbuddy.carpoolbuddy.controllers.rides;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyVehicleProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private LinearLayout currentUsers;

    private TextView plField;
    private TextView dlField;
    private TextView nameField;
    private TextView phoneField;
    private TextView priceField;
    private TextView hField;
    private String type;
    private TextView dField;

    private String vehicleId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle_profile);

        // Retrieve the vehicleId from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            vehicleId = intent.getStringExtra("vehicleId");
            type = intent.getStringExtra("type");
        }
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        nameField = findViewById(R.id.vehicle_name4);
        priceField = findViewById(R.id.vehicle_price4);
        plField = findViewById(R.id.pl4);
        dlField = findViewById(R.id.dl4);
        hField = findViewById(R.id.hour4);
        dField = findViewById(R.id.date4);
        currentUsers = findViewById(R.id.current_users);

        String path = "vehicles/cars/cars";
        switch (type) {
            case "Car":
                path = "vehicles/cars/cars";
                break;
            case "Bike":
                path = "vehicles/bikes/bikes";
                break;
            case "Helicopter":
                path = "vehicles/helicopters/helicopters";
                break;
            case "Segway":
                path = "vehicles/segways/segways";
                break;
        }

        DocumentReference vehicleRef = firestore.collection(path).document(vehicleId);
        vehicleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    if (vehicle != null) {
                        nameField.setText(vehicle.getVehicleType());
                        hField.setText(vehicle.getTime().getHour() + ":" + vehicle.getTime().getMinute());
                        dField.setText(vehicle.getTime().getDay() + "/" + vehicle.getTime().getMonth() + "/" + vehicle.getTime().getYear());
                        plField.setText(vehicle.getPickUpLocation().getAddress());
                        dlField.setText(vehicle.getDropOffLocation().getAddress());
                        priceField.setText(Double.toString(vehicle.getPrice()) + " HKD");
                        addImage(vehicle);
                        addCurrentUsers();
                    }
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                Toast.makeText(this, "Load ride info failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCurrentUsers() {
        CollectionReference carsCollectionRef = firestore.collection("records").document(vehicleId).collection(vehicleId);
        carsCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
                renderLayoutRows(users);
            } else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void renderLayoutRows(List<User> users) {
        LayoutInflater inflater = LayoutInflater.from(this);
        if(users.size()==0){
            View rowView = inflater.inflate(R.layout.current_riders_row, null);
            TextView nameTextView = rowView.findViewById(R.id.cu_name2);
            TextView phoneTextView = rowView.findViewById(R.id.cu_phone);
            nameTextView.setText("No riders");
            phoneTextView.setText("");
            currentUsers.addView(rowView);
            return;
        }
        for (User user : users) {
            View rowView = inflater.inflate(R.layout.current_riders_row, null);
            TextView nameTextView = rowView.findViewById(R.id.cu_name2);
            TextView phoneTextView = rowView.findViewById(R.id.cu_phone);
            nameTextView.setText(user.getName());
            phoneTextView.setText(user.getPhone());
            if(user.getPhone()==null || user.getPhone().equals("")){
                phoneTextView.setText(user.getEmail());
            }
            currentUsers.addView(rowView);
        }
    }

    private void addImage(Vehicle vehicle){
        // Load the image using the vehicle ID
        ImageView imageView = findViewById(R.id.vehicle_image4);
        String imageName = vehicle.getVehicleID()+".png";
        System.out.println(imageName);
        StorageReference imageRef = storageReference.child("vehicles")
                .child(imageName);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(null);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        }).addOnFailureListener(e -> {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.rectangle_grey);
            imageView.setImageDrawable(d);
            System.out.println(e+" error");
        });

    }
    public void back(View w) {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    public void end(View w){
        String collectionPath = "vehicles/cars/cars";
        switch (type) {
            case "Car":
                collectionPath = "vehicles/cars/cars";
                break;
            case "Bike":
                collectionPath = "vehicles/bikes/bikes";
                break;
            case "Helicopter":
                collectionPath = "vehicles/helicopters/helicopters";
                break;
            case "Segway":
                collectionPath = "vehicles/segways/segways";
                break;
        }
        DocumentReference documentRef = firestore.collection(collectionPath).document(vehicleId);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        documentRef.update("end", true,"open", false)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> subTask) {
                                        if (subTask.isSuccessful()) {
                                            Toast.makeText(MyVehicleProfileActivity.this, "Close ride successful, you and your riders will not be able to view the ride.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MyVehicleProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(MyVehicleProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyVehicleProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteAndRate(View view) {
    }
}
