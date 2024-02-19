package com.example.carpoolbuddy.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.carpoolbuddy.controllers.rides.AddVehicleActivity;
import com.example.carpoolbuddy.controllers.rides.EndedTripActivity;
import com.example.carpoolbuddy.controllers.rides.MyTripProfileActivity;
import com.example.carpoolbuddy.controllers.rides.MyVehicleProfileActivity;
import com.example.carpoolbuddy.models.User;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseUser curuser;
    private String userId;
    private User user;
    private ArrayList<Vehicle> vehicles;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesFragment newInstance(String param1, String param2) {
        RidesFragment fragment = new RidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rides, container, false);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        curuser = mAuth.getCurrentUser();
        userId = curuser.getUid();
        ImageView addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> toAdd());

        Button ridesButton = view.findViewById(R.id.myvehicles);
        ridesButton.setOnClickListener(v -> showMyRides(view));

        Button tripsButton = view.findViewById(R.id.mytrips);
        tripsButton.setOnClickListener(v -> showMyTrips(view));
        showMyTrips(view);
        return view;
    }

    private void toAdd() {
        Intent intent = new Intent(getActivity(), AddVehicleActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }

    private void showMyRides(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.linear);
        linearLayout.removeAllViews();
        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CollectionReference carsCollectionRef = firestore.collection("vehicles").document("cars").collection("cars");
        carsCollectionRef.get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Dismiss the progress dialog after data retrieval
            if (task.isSuccessful()) {
                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    if (vehicle.getOwner().getUid().equals(userId) && !vehicle.isEnd()) {
                        vehicles.add(vehicle);
                    }
                }
                renderLayoutRows(view, vehicles,false);
            } else {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        CollectionReference b = firestore.collection("vehicles").document("bikes").collection("bikes");
        b.get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Dismiss the progress dialog after data retrieval
            if (task.isSuccessful()) {
                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    if (vehicle.getOwner().getUid().equals(userId) && !vehicle.isEnd()) {
                        vehicles.add(vehicle);
                    }
                }
                renderLayoutRows(view, vehicles,false);
            } else {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        CollectionReference h = firestore.collection("vehicles").document("helicopters").collection("helicopters");
        h.get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Dismiss the progress dialog after data retrieval

            if (task.isSuccessful()) {
                List<Vehicle> vehicles = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);

                    if (vehicle.getOwner().getUid().equals(userId) && !vehicle.isEnd()) {
                        vehicles.add(vehicle);
                    }
                }

                renderLayoutRows(view, vehicles, false);
            } else {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        CollectionReference s = firestore.collection("vehicles").document("segways").collection("segways");
        s.get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Dismiss the progress dialog after data retrieval
            if (task.isSuccessful()) {
                List<Vehicle> vehicles = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);

                    if (vehicle.getOwner().getUid().equals(userId) && !vehicle.isEnd()) {
                        vehicles.add(vehicle);
                    }
                }
                renderLayoutRows(view, vehicles,false);
            } else {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMyTrips(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.linear);
        linearLayout.removeAllViews();
        System.out.println("showMyTrips...");
        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CollectionReference carsCollectionRef = firestore.collection("reservations").document(userId).collection(userId);
        carsCollectionRef.get().addOnCompleteListener(task -> {
            System.out.println("getting"+userId);

            progressDialog.dismiss(); // Dismiss the progress dialog after data retrieval
            if (task.isSuccessful()) {
                System.out.println("successful");

                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);

                    if(vehicle.isEndByUser()) {
                        System.out.println("is ended by user");
                        continue;
                    }

                    firestore.collection("vehicles")
                            .document(vehicle.getVehicleType().toLowerCase()+"s")
                            .collection(vehicle.getVehicleType().toLowerCase()+"s")
                            .document(vehicle.getVehicleID())
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    System.out.println("retriving end info");
                                    DocumentSnapshot document2 = task2.getResult();
                                    System.out.println(vehicle.getVehicleID()+" "+vehicle.getVehicleType()+ "  " +Boolean.TRUE.equals(document2.getBoolean("end")));

                                    if (document2 != null && document2.exists()) {
                                        boolean isEnded = Boolean.TRUE.equals(document2.getBoolean("end"));
                                        if (isEnded) {
                                            vehicle.setEnd(true);
                                            System.out.println("is ended");
                                        }
                                    }
                                }
                            });
                    vehicles.add(vehicle);

                }
                renderLayoutRows(view, vehicles, true);
            } else {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void renderLayoutRows(View view, List<Vehicle> vehicles, boolean trip) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        LinearLayout linearLayout = view.findViewById(R.id.linear);

        for (Vehicle vehicle : vehicles) {
            View rowView = inflater.inflate(R.layout.vehicle_row_user, null);
            TextView ownerTextView = rowView.findViewById(R.id.owner);
            TextView locationTextView = rowView.findViewById(R.id.location);
            TextView infoTextView = rowView.findViewById(R.id.info);
            TextView endTextView = rowView.findViewById(R.id.endByOwner);
            System.out.println(vehicle.isEnd());

            if(vehicle.isEnd()) {
                System.out.println("set text to ended");
                endTextView.setText("Ended by Owner!    ");
            }

            locationTextView.setText(vehicle.getPickUpLocation().getAddress() + " to "+vehicle.getDropOffLocation().getAddress());
            firestore.collection("users").document(vehicle.getOwner().getUid()).get().addOnSuccessListener(documentSnapshot -> {
                float rating = documentSnapshot.getLong("rating").floatValue();
                ownerTextView.setText(vehicle.getOwner().getName() + " | rating: " + rating);
            });
            infoTextView.setText(vehicle.getPrice() + " HKD | " + vehicle.getTime().toString() + " | " + vehicle.getCapacity() + " seats");

            // Load the image using the vehicle ID
            ImageView imageView = rowView.findViewById(R.id.image);
            String imageName = vehicle.getVehicleID()+".png";
            System.out.println(imageName);
            StorageReference imageRef = storageReference.child("vehicles")
                    .child(imageName);
            System.out.println("ref:"+imageRef);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                System.out.println("URL:"+uri);

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
                Drawable d = getResources().getDrawable(R.drawable.rectangle_grey);
                imageView.setImageDrawable(d);
                System.out.println(e+" error");
//                            Toast.makeText(this, "Failed to load profile images", Toast.LENGTH_SHORT).show();
            });


            // Set onClickListener to the row view
            if(vehicle.isEnd()){
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start another activity and pass the vehicleId as extra
                        Intent intent = new Intent(view.getContext(), MyVehicleProfileActivity.class);
                        if(trip) intent = new Intent(view.getContext(), EndedTripActivity.class);
                        intent.putExtra("vehicleId", vehicle.getVehicleID());
                        intent.putExtra("type", vehicle.getVehicleType());

                        startActivity(intent);
                    }
                });
            }else{
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start another activity and pass the vehicleId as extra
                     Intent intent = new Intent(view.getContext(), MyVehicleProfileActivity.class);
                    if(trip) intent = new Intent(view.getContext(), MyTripProfileActivity.class);
                    intent.putExtra("vehicleId", vehicle.getVehicleID());
                    intent.putExtra("type", vehicle.getVehicleType());

                    startActivity(intent);
                }
            });
            }

            linearLayout.addView(rowView);
        }
    }





}