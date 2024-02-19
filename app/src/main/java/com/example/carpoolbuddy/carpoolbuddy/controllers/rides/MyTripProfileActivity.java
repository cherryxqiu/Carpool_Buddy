package com.example.carpoolbuddy.carpoolbuddy.controllers.rides;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyTripProfileActivity extends AppCompatActivity {
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_profile);

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
                    Vehicle vehicle = document.toObject(Vehicle.class);
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
//                        if(vehicle.isEnd()){
//                            button.setText("Delete Trip");
//                            instructionField.setText("The ride owner has ended the ride. After you delete this trip, you'll not be able to see it.");
//                        }
                        addImage(vehicle);

                    }
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                Toast.makeText(this, "Load ride info failed", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void cancel(View w){
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
                        int currentCapacity = document.getLong("capacity").intValue();
                        int newCapacity = currentCapacity+c;

                        documentRef.update("open", true, "capacity", newCapacity)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> subTask) {
                                        if (subTask.isSuccessful()) {
                                            delReservation();
                                            Toast.makeText(MyTripProfileActivity.this, "Cancel reservation successful. Please make sure to let the vehicle owner know!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MyTripProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(MyTripProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyTripProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delReservation() {
        // Replace "userid" and "vehicleid" with the actual user ID and vehicle ID
        String userId = mAuth.getCurrentUser().getUid();

        String reservationPath = "reservations/" + userId + "/" + userId + "/" + vehicleId;
        DocumentReference documentRef = firestore.document(reservationPath);
        documentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Reservation document deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting reservation document", e);
                    }
                });


        String recordPath = "records/" + vehicleId + "/" + vehicleId + "/" + userId;
        DocumentReference rdocumentRef = firestore.document(recordPath);
        rdocumentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Reservation document deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting reservation document", e);
                    }
                });
    }

}
