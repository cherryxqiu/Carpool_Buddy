package com.example.carpoolbuddy.carpoolbuddy.controllers.rides;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.adapters.ThemedAutocompleteSupportFragment;
import com.example.carpoolbuddy.models.CLocation;
import com.example.carpoolbuddy.models.CTime;
import com.example.carpoolbuddy.models.User;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class AddVehicleActivity extends AppCompatActivity {
    private static final int RC_IMAGE_PICK = 123;
    private StorageReference storageReference;
    private Uri imageUri;
    private ImageView vehicleImage;
    private TextView uploadTextView;
    private FirebaseAuth mAuth;
    private User user;
    private FirebaseFirestore firestore;
    private EditText capacityField;
    private EditText timeField;

    private EditText priceField;
    private ImageView pickTimeBtn;
    private EditText selectedTimeTV;
    private Spinner typeField;
    private TextView pText;
    private TextView dText;

    private AutocompleteSupportFragment autocompleteFragment;

    private double price;
    private int capacity;
    private String type;
    private CLocation pl;
    private CLocation dl;

    private CTime time;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("vehicles");
        uploadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
        //get all input data
        capacityField = findViewById(R.id.capacity);
        priceField = findViewById(R.id.price);
        timeField = (EditText) findViewById(R.id.time);
        typeField = findViewById(R.id.spinner2);
        pText = findViewById(R.id.pText);
        dText = findViewById(R.id.dText);
        Setup();

    }



    private void Setup()
    {

        uploadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


        // auto complete location (pick up)
        Places.initialize(getApplicationContext(), "AIzaSyAZ4dtpOHzJAe0DZMwQQMxinVvpDGNj64c");
        AutocompleteSupportFragment autocompleteFragment = new ThemedAutocompleteSupportFragment();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.autocomplete_fragment, autocompleteFragment)
                .commit();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String address = place.getName();
                String placeId = place.getId();
                pl = new CLocation(address, placeId);
                CharSequence c = "        ";
                autocompleteFragment.setText(c);
                pText.setText("");

            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
        });
        CharSequence c = "        ";
        autocompleteFragment.setText(c);

        // auto complete location (drop off)
        AutocompleteSupportFragment autocompleteFragment2 = new ThemedAutocompleteSupportFragment();
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.autocomplete_fragment2, autocompleteFragment2)
                .commit();
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println("selected place name: "+place.getName());
                String address = place.getName();
                String placeId = place.getId();
                dl = new CLocation(address, placeId);
                CharSequence c = "        ";
                autocompleteFragment2.setText(c);
                dText.setText("");
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
        });
        autocompleteFragment2.setText(c);


        // Setup calendar time picker
        pickTimeBtn = findViewById(R.id.timepicker);
        selectedTimeTV = findViewById(R.id.time); // Assuming you have a TextView for displaying the selected time and day

        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a TimePickerDialog for selecting the time
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddVehicleActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time = new CTime(year,month,dayOfMonth,hour,minute);
                                // Update the selectedTimeTV with the selected time
                                String selectedDateTime = hourOfDay + ":" + minute + " " + dayOfMonth + "/" + (month + 1) + "/" + year;
                                selectedTimeTV.setText(selectedDateTime);
                            }
                        }, hour, minute, false);

                // Create a DatePickerDialog for selecting the day
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddVehicleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the selectedDateTimeTV with the selected time and day
                                String selectedDateTime = hour + ":" + minute + " " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                selectedTimeTV.setText(selectedDateTime);
                                // Show the TimePickerDialog after the date is selected
                                timePickerDialog.show();
                            }
                        }, year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

    }



    public void addNewVehicle(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to open a new vehicle? You will not be able to edit your ride for your riders' info accuracy");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User confirmed, proceed with adding the vehicle
            String priceText = priceField.getText().toString().trim();
            String capacityText = capacityField.getText().toString().trim();
            if (priceText.isEmpty() || capacityText.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            price = Double.parseDouble(String.valueOf(priceField.getText()));
            capacity = Integer.parseInt(String.valueOf(capacityField.getText()));

            if (!formValid()) return;
            type = "Car";
            type = typeField.getSelectedItem().toString();

            FirebaseUser curuser = mAuth.getCurrentUser();
            String userId = curuser.getUid();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            System.out.println(userId);
            DocumentReference userRef = firestore.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                System.out.println("getting user on success...");
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                }
                Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), user, capacity, price, type, pl, dl, time);
                String imageName = vehicle.getVehicleID()+".png";

                firestore.collection("users").document(userId).set(user);

                StorageReference imageRef = storageReference.child(imageName);
                UploadTask uploadTask = imageRef.putFile(imageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        switch (type) {
                            case "Car":
                                firestore.collection("vehicles")
                                        .document("cars")
                                        .collection("cars")
                                        .document(vehicle.getVehicleID())
                                        .set(vehicle);
                                break;
                            case "Bike":
                                firestore.collection("vehicles")
                                        .document("bikes")
                                        .collection("bikes")
                                        .document(vehicle.getVehicleID())
                                        .set(vehicle);
                                break;
                            case "Helicopter":
                                firestore.collection("vehicles")
                                        .document("helicopters")
                                        .collection("helicopters")
                                        .document(vehicle.getVehicleID())
                                        .set(vehicle);
                                break;
                            case "Segway":
                                firestore.collection("vehicles")
                                        .document("segways")
                                        .collection("segways")
                                        .document(vehicle.getVehicleID())
                                        .set(vehicle);
                                break;
                        }

                        progressDialog.dismiss();
                        Toast.makeText(AddVehicleActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> back(null), 2000);
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddVehicleActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {

                    progressDialog.dismiss();
                    Toast.makeText(AddVehicleActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
            });

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User canceled, do nothing or perform any desired action
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    private boolean formValid() {
        if (dl == null || pl == null || time == null || capacity == 0 || price == 0) {
//            System.out.println(dl.getAddress() + " 11");
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i(TAG, "Place: ${place.getName()}, ${place.getId()}");
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    public void back(View w){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_IMAGE_PICK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
        }
    }

}