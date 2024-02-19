package com.example.carpoolbuddy.controllers.rides;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EndedTripActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private TextView plField;
    private TextView dlField;
    private TextView nameField;
    private TextView phoneField;
    private TextView priceField;
    private TextView hField;
    private String type;
    private TextView dField;
    private TextView caField;
    private TextView typeField;

    private TextView instructionField;
    private Button button;

    private FirebaseAuth mAuth;
    private int c;

    private String vehicleId;
    private Vehicle vehicle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ended_trip_profile);

        // Retrieve the vehicleId from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            vehicleId = intent.getStringExtra("vehicleId");
            type = intent.getStringExtra("type");
        }
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        caField = findViewById(R.id.c4);
        nameField = findViewById(R.id.vehicle_name4);
        phoneField = findViewById(R.id.vehicle_phone4);
        priceField = findViewById(R.id.vehicle_price4);
        plField = findViewById(R.id.pl4);
        dlField = findViewById(R.id.dl4);
        hField = findViewById(R.id.hour4);
        dField = findViewById(R.id.date4);
        typeField = findViewById(R.id.type4);
        instructionField = findViewById(R.id.instruction);
        button = findViewById(R.id.button);
        String userId = mAuth.getCurrentUser().getUid();
        String path = "reservations/"+userId+"/"+userId;

        DocumentReference vehicleRef = firestore.collection(path).document(vehicleId);
        vehicleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                     vehicle = document.toObject(Vehicle.class);

                    if (vehicle != null) {
                        nameField.setText(vehicle.getOwner().getName());
                        phoneField.setText(vehicle.getOwner().getPhone());
                        hField.setText(vehicle.getTime().getHour() + ":" + vehicle.getTime().getMinute());
                        dField.setText(vehicle.getTime().getDay() + "/" + vehicle.getTime().getMonth() + "/" + vehicle.getTime().getYear());
                        plField.setText(vehicle.getPickUpLocation().getAddress());
                        dlField.setText(vehicle.getDropOffLocation().getAddress());
                        caField.setText(vehicle.getCapacity() + " seats");
                        c = vehicle.getCapacity();
                        priceField.setText(Double.toString(vehicle.getPrice()) + " HKD");
                        typeField.setText(vehicle.getVehicleType());

                    }
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                Toast.makeText(this, "Load ride info failed", Toast.LENGTH_SHORT).show();
            }
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

    public void deleteAndRate(View view) {
        // Replace "userid" and "vehicleid" with the actual user ID and vehicle ID
        String userId = mAuth.getCurrentUser().getUid();
        String ownerId = vehicle.getOwner().getUid();
        System.out.println("ownerId");

        String reservationPath = "reservations/" + userId + "/" + userId + "/" + vehicleId;

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        float rating = ratingBar.getRating();
        System.out.println();

        DocumentReference documentRef = firestore.document(reservationPath);
        documentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DocumentReference userRef = firestore.collection("users").document(ownerId);

                        userRef.update("ratingNum", FieldValue.increment(1))
                                .addOnSuccessListener(bVoid -> {
                                    userRef.update("ratingTotal", FieldValue.increment(rating))
                                            .addOnSuccessListener(cVoid -> {
                                                userRef.get().addOnSuccessListener(documentSnapshot -> {
                                                    int ratingNum = documentSnapshot.getLong("ratingNum").intValue();
                                                    float ratingTotal = documentSnapshot.getLong("ratingTotal").floatValue();
                                                    Map<String, Object> data = new HashMap<>();
                                                    System.out.println(ratingTotal / ratingNum);

                                                    userRef.update("rating", ratingTotal / ratingNum);
                                                    Toast.makeText(EndedTripActivity.this, "Trip record deleted successfully. Experience rated successfully.", Toast.LENGTH_SHORT).show();
                                                    back(null);
                                                });
                                            });
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Something went wrong, please refresh and try again.", e);
                    }
                });


    }


}
