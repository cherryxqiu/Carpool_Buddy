package com.example.carpoolbuddy.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.explore.VehicleProfileActivity;
import com.example.carpoolbuddy.controllers.fragments.ExploreFragment;
import com.example.carpoolbuddy.controllers.fragments.HomeFragment;
import com.example.carpoolbuddy.controllers.fragments.ProfileFragment;
import com.example.carpoolbuddy.controllers.fragments.RidesFragment;
import com.example.carpoolbuddy.controllers.rides.AddVehicleActivity;
import com.example.carpoolbuddy.databinding.ActivityMainBinding;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    private GoogleMap myMap;
    private SearchView mapSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up firebase auth and firestore instances
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());

//        mapSearchView = findViewById(R.id.mapSearch);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
//
//        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//            @Override
//            public boolean onQueryTextSubmit(String s){
//                String location = mapSearchView.getQuery().toString();
//                List<Address> addressList = null;
//                if(location !=null){
//                    Geocoder geocoder = new Geocoder (MainActivity.this);
//                    try{
//                        addressList = geocoder.getFromLocationName(location, 1);
//                    }catch (IOException e){
//
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s){
//                return false;
//                }
//        });
//
//
//        mapFragment.getMapAsync((OnMapReadyCallback) MainActivity.this);


        setContentView(binding.getRoot());
        replaceFragment(new ExploreFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new ExploreFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.rides:
                    replaceFragment(new RidesFragment());
                    break;
                case R.id.explore:
                    replaceFragment(new HomeFragment());
                    break;
            }
            return true;
        });


    }


    private void replaceFragment(Fragment fragment){
        System.out.println(fragment.getClass().toString());
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//
//        myMap = googleMap;
//
//        LatLng sydney = new LatLng(-34, 151);
//        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        MarkerOptions options = new MarkerOptions().position(sydney).title("Sydney");
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        myMap.addMarker(options);
//
//    }

    private void getDataAndDisplay(){
        //get data from firebase
        CollectionReference carsCollectionRef = firestore.collection("vehicles/cars/cars");
        List<Vehicle>  vehicleList = new ArrayList<>();
        carsCollectionRef.get().addOnCompleteListener(task -> {
            System.out.println("Getting vehicles...");
            if (task.isSuccessful()) {
                System.out.println("Task is successful");
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    System.out.println("Query Snapshot is not null");
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        System.out.println("document: " + document);
                        Vehicle vehicle = document.toObject(Vehicle.class);
                        System.out.println("vehicle: " + vehicle.getOwner());
                        if(vehicle.getCapacity()>0) vehicleList.add(vehicle);
                        System.out.println(vehicleList.size());
                    }
                }


                //display data
                System.out.println("Displaying data, data size: "+vehicleList.size());
                TableLayout tableLayout = findViewById(R.id.user_button);
                deleteAllRowsExceptHeader(tableLayout);
                for (final Vehicle vehicle : vehicleList) {
                    // Create a new row
                    final TableRow newRow = new TableRow(this);

                    // Create owner TextView
                    TextView ownerTextView = new TextView(this);
//                    ownerTextView.setText(vehicle.getOwner());
                    ownerTextView.setPadding(8, 8, 8, 8);
                    ownerTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
                    ownerTextView.setTextColor(Color.WHITE);
                    ownerTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Set layout weight

                    // Create capacity TextView
                    TextView capacityTextView = new TextView(this);
                    capacityTextView.setText(Integer.toString(vehicle.getCapacity()) + " people");
                    capacityTextView.setPadding(8, 8, 8, 8);
                    capacityTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
                    capacityTextView.setTextColor(Color.WHITE);
                    capacityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Set layout weight

                    // Add views to the new row
                    newRow.addView(ownerTextView);
                    newRow.addView(capacityTextView);

                    // Set an onClick listener for the row
                    newRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Switch to VehicleProfileActivity and pass the vehicle ID
                            Intent intent = new Intent(MainActivity.this, VehicleProfileActivity.class);
                            intent.putExtra("vehicleId", vehicle.getVehicleID());
                            startActivity(intent);
                        }
                    });


                    // Add the new row to the table layout
                    tableLayout.addView(newRow);

                    // Create a divider drawable
                    Drawable dividerDrawable = getResources().getDrawable(R.drawable.divider_line);

                    // Set the divider drawable for the table layout
                    tableLayout.setDividerDrawable(dividerDrawable);

                    // Show dividers between rows
                    tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
                }
            } else {
                System.err.println("Error getting documents: " + task.getException());
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void reload(View v)
    {
        TableLayout tableLayout = findViewById(R.id.user_button);
        getDataAndDisplay();
    }

    public void AddVehicle(View v){
        Intent intent = new Intent(this, AddVehicleActivity.class);
        startActivity(intent);
    }
    public void VehicleProfile(View v){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        startActivity(intent);
    }
    public void AppInfo(View v){
        System.out.println("app info!");
        Intent intent = new Intent(this, AppInfoActivity.class);
        startActivity(intent);
    }


    private void deleteAllRowsExceptHeader(TableLayout tableLayout) {
        int childCount = tableLayout.getChildCount();
        for (int i = childCount-1; i >= 0; i--) {
            View childView = tableLayout.getChildAt(i);
            tableLayout.removeView(childView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fragmentToLoad")) {
            String fragmentToLoad = intent.getStringExtra("fragmentToLoad");
            if (fragmentToLoad.equals("profile")) {
                // Load the profile fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new ProfileFragment())
                        .commit();


            }
        }
    }

}
