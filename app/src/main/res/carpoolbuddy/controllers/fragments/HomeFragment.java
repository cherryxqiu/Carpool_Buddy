package com.example.carpoolbuddy.controllers.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.explore.VehicleProfileActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private Marker selectedMarker; private Marker additionalMarker;

    private Polyline routePolyline;

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }
        @Override
        public View getInfoContents(Marker marker) {
            System.out.println("getInfoContents");

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.titleTextView));
            tvTitle.setText(marker.getTitle());
            TextView tvTime = ((TextView)myContentsView.findViewById(R.id.timeTextView));
            tvTime.setText(marker.getSnippet());
            DocumentSnapshot document = (DocumentSnapshot) marker.getTag();
            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("onclick window");
                    Intent intent = new Intent(requireContext(), VehicleProfileActivity.class);
                    intent.putExtra("type", document.getString("vehicleType"));
                    intent.putExtra("vehicleId", document.getString("vehicleID"));
                    startActivity(intent);
                }
            });
            return myContentsView;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private ProgressDialog progressDialog;

    private FrameLayout map;
    private GoogleMap gMap;
    private Location currentLocation;
    private Marker marker;
    private FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    private SearchView searchView;
    private PlacesClient placesClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getCurrentLocation();
        map = view.findViewById(R.id.map);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();
        Places.initialize(view.getContext(), "AIzaSyAZ4dtpOHzJAe0DZMwQQMxinVvpDGNj64c");
         placesClient = Places.createClient(view.getContext());
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLocation();
        getCarObjectsFromFirestore();
        getBikeObjectsFromFirestore();
        getHelicopterObjectsFromFirestore();
        getSegwayObjectsFromFirestore();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = searchView.getQuery().toString();
                if (loc == null) {
                    Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
                        if (addressList.size() > 0) {
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            if (marker != null) {
                                marker.remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                            gMap.animateCamera(cameraUpdate);
                            marker = gMap.addMarker(markerOptions);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedClient.getLastLocation();

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(HomeFragment.this);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.addMarker(markerOptions);
        // Enable the user's current location on the map
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

            // Get the view of the My Location button
            View locationButton = ((View) getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // Set the desired size of the My Location button
            int desiredSize = 186; // Set the desired size in pixels

            // Modify the layout parameters of the button
            ViewGroup.LayoutParams layoutParams = locationButton.getLayoutParams();
            layoutParams.width = desiredSize;
            layoutParams.height = desiredSize;
            locationButton.setLayoutParams(layoutParams);

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    DocumentSnapshot document = (DocumentSnapshot) marker.getTag();
                    System.out.println("onclick window");
                    Intent intent = new Intent(requireContext(), VehicleProfileActivity.class);
                    intent.putExtra("type", document.getString("vehicleType"));
                    intent.putExtra("vehicleId", document.getString("vehicleID"));
                    startActivity(intent);
                }


            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Check if a different marker is selected
                    if (selectedMarker != null && !selectedMarker.equals(marker)) {
                        // Remove the polyline from the map
                        if (routePolyline != null) {
                            routePolyline.remove();
                            routePolyline = null;
                        }

                        // Remove the additional marker from the map
                        if (additionalMarker != null) {
                            additionalMarker.remove();
                            additionalMarker = null;
                        }
                    }

                    DocumentSnapshot document = (DocumentSnapshot) marker.getTag();
                    if (document != null) {

                        String pickupPlaceId = document.getString("pickUpLocation.placeId");
                        String dropOffPlaceId = document.getString("dropOffLocation.placeId");

                        getLatLngFromPlaceId(pickupPlaceId, new OnLatLngFetchedListener() {
                            @Override
                            public void onLatLngFetched(LatLng pickUpLatLng) {
                                getLatLngFromPlaceId(dropOffPlaceId, new OnLatLngFetchedListener() {
                                    @Override
                                    public void onLatLngFetched(LatLng dropOffLatLng) {
                                        String p = pickUpLatLng.toString().substring(10, pickUpLatLng.toString().length() - 1);
                                        String d = dropOffLatLng.toString().substring(10, dropOffLatLng.toString().length() - 1);
                                        List<LatLng> path = new ArrayList();
                                        GeoApiContext context = new GeoApiContext.Builder()
                                                .apiKey("AIzaSyBz4uNZFtWyVRm143jW3Fu-kV-UYXNTTOY")
                                                .build();
                                        System.out.println("p:"+p);
                                        System.out.println("d:"+d);

                                        DirectionsApiRequest req = DirectionsApi.getDirections(context, p, d);
                                        try {
                                            System.out.println("trying");

                                            DirectionsResult res = req.await();
                                            if (res.routes != null && res.routes.length > 0) {
                                                System.out.println("res.routes != null");

                                                DirectionsRoute route = res.routes[0];
                                                if (route.legs !=null) {
                                                    for(int i=0; i<route.legs.length; i++) {
                                                        DirectionsLeg leg = route.legs[i];
                                                        if (leg.steps != null) {
                                                            for (int j=0; j<leg.steps.length;j++){
                                                                DirectionsStep step = leg.steps[j];
                                                                if (step.steps != null && step.steps.length >0) {
                                                                    for (int k=0; k<step.steps.length;k++){
                                                                        DirectionsStep step1 = step.steps[k];
                                                                        EncodedPolyline points1 = step1.polyline;
                                                                        if (points1 != null) {
                                                                            System.out.println("points1 != null");

                                                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    System.out.println("else");

                                                                    EncodedPolyline points = step.polyline;
                                                                    if (points != null) {
                                                                        System.out.println("points != null");

                                                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                                        for (com.google.maps.model.LatLng coord : coords) {
                                                                            path.add(new LatLng(coord.lat, coord.lng));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch(Exception ex) {
                                            System.out.println("exception");
                                            System.out.println(ex.getLocalizedMessage());

                                            Log.e(TAG, ex.getLocalizedMessage());
                                        }
                                        if (path.size() > 0) {
                                            if (routePolyline != null) {
                                                routePolyline.remove();
                                            }
                                            PolylineOptions opts = new PolylineOptions().addAll(path).color(0xFF9362D9).width(13);
                                            routePolyline = googleMap.addPolyline(opts);
                                        }
                                        if (additionalMarker != null) {
                                            additionalMarker.remove();
                                        }
                                        additionalMarker = googleMap.addMarker(new MarkerOptions().position(dropOffLatLng));
                                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                                        double latitude1 = pickUpLatLng.latitude;
                                        double longitude1 = pickUpLatLng.longitude;
                                        double latitude2 = dropOffLatLng.latitude;
                                        double longitude2 = dropOffLatLng.longitude;

                                        double midpointLat = (latitude1 + latitude2) / 2.0;
                                        double midpointLng = (longitude1 + longitude2) / 2.0;

                                        LatLng midpointLatLng = new LatLng(midpointLat, midpointLng);

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpointLatLng, 14));                                    }
                                    @Override
                                    public void onLatLngFetchFailed() {
                                        Toast.makeText(getContext(),"fetch failed",Toast.LENGTH_SHORT);
                                    }
                                });
                            }

                            @Override
                            public void onLatLngFetchFailed() {
                            Toast.makeText(getContext(),"fetch failed",Toast.LENGTH_SHORT);
                            }
                        });

                    }
                    return false;
                }
            });

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Permission is granted, proceed to retrieve the current location
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        Task<Location> task = fusedClient.getLastLocation();

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    // TODO: Handle the retrieved location
                }
            }
        });

        task.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: Handle the failure to retrieve the location
            }
        });
    }

    private void getCarObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("vehicles")
                .document("cars")
                .collection("cars")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                        progressDialog.dismiss(); // Dismiss the progress dialog
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getBikeObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("bikes")
                .collection("bikes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getSegwayObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("segways")
                .collection("segways")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getHelicopterObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("helicopters")
                .collection("helicopters")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLatLngFromPlaceId(String placeId, QueryDocumentSnapshot document) {
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request).addOnCompleteListener((responseTask) -> {
            if (responseTask.isSuccessful()) {
                FetchPlaceResponse response = responseTask.getResult();
                Place place = response.getPlace();
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    Drawable pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.car_marker);
                    if(document.getString("vehicleType").equals("Helicopter")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.helicopter_marker);
                    }else if (document.getString("vehicleType").equals("Bike")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bike_marker);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.segway_marker);
                    }
                    int markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.25);
                    if (document.getString("vehicleType").equals("Bike")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.08);
                    }else if (document.getString("vehicleType").equals("Helicopter")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.2);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.15);
                    }
                    Bitmap bitmap = Bitmap.createBitmap(markerSize, markerSize, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    pngDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

                    pngDrawable.draw(canvas);
                    float rotationDegree = getRandomRotationDegree();
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(place.getName())
                            .rotation(rotationDegree)
                            .icon(markerIcon);
                    if (document.getString("vehicleType").equals("Bike")){
                        markerOptions.rotation(0);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        markerOptions.rotation(0);
                    }

                    Marker newMarker =  gMap.addMarker(markerOptions);
                    newMarker.setTitle(document.getString("pickUpLocation.address") + " to " + document.getString("dropOffLocation.address"));
                    newMarker.setSnippet("Departure time: "+document.getLong("time.hour") +":"+document.getLong("time.minute")+" "+document.getLong("time.day")+"/"
                            +document.getLong("time.month"));
                    newMarker.setTag(document);
                }
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private float getRandomRotationDegree() {
        return new Random().nextFloat() * 360;
    }
    public interface OnLatLngFetchedListener {
        void onLatLngFetched(LatLng latLng);
        void onLatLngFetchFailed();
    }
    private void getLatLngFromPlaceId(String placeId, OnLatLngFetchedListener listener) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request)
                .addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        listener.onLatLngFetched(latLng);
                    } else {
                        listener.onLatLngFetchFailed();
                    }
                })
                .addOnFailureListener((exception) -> {
                    exception.printStackTrace();
                    listener.onLatLngFetchFailed();
                });
    }
}