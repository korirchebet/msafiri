package com.elitechinc.my.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.elitechinc.my.Classes.TrackingService;
import com.elitechinc.my.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivtechs.maplocationpicker.MapUtility;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    Button location, customlocation;
    private GoogleMap mMap;
    FirebaseAuth mAuth;
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private int REQUEST_CODE = 111;
    TextView txtget;
    FloatingActionButton add;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(view.getContext());
        //MapUtility.apiKey = getResources().getString(R.string.google_maps_api);
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();

        } else {
            ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                            googleMap.addMarker(markerOptions).showInfoWindow();
                            googleMap.setTrafficEnabled(true);
                            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setCompassEnabled(true);
                            googleMap.getUiSettings().setRotateGesturesEnabled(true);
                            googleMap.getUiSettings().setZoomGesturesEnabled(true);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 40));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference referencemain = FirebaseDatabase.getInstance().getReference("Hotspots");
        referencemain.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LatLng newLocation = new LatLng(
                        snapshot.child("latitude").getValue(Double.class),
                        snapshot.child("longitude").getValue(Double.class)
                );
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bromarker))
                        .title(snapshot.getKey()));
                marker.setTag(newLocation);
                hotspots();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //int position = (int) (marker.getTag());
                        if (marker.getTitle().equals("You are here")) {
                            Toast.makeText(getActivity(), "Your location", Toast.LENGTH_SHORT).show();
                        } else {
                            //Intent intent = new Intent(getActivity(), detailsActivity.class);
                            //intent.putExtra("key", marker.getTitle());
                            //startActivity(intent);
                        }

                        return false;
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

    }

    private void hotspots() {
       /* DatabaseReference referencemain = FirebaseDatabase.getInstance().getReference("Hotspots");
        referencemain.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LatLng newLocation = new LatLng(
                        snapshot.child("latitude").getValue(Double.class),
                        snapshot.child("longitude").getValue(Double.class)
                );
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bromarkered))
                        .title(snapshot.getKey()));
                marker.setTag(newLocation);
                hotspots();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
               // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 20));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //int position = (int) (marker.getTag());
                        if (marker.getTitle().equals("You are here")) {
                            Toast.makeText(getActivity(), "Your location", Toast.LENGTH_SHORT).show();
                        } else {
                            //Intent intent = new Intent(getActivity(), detailsActivity.class);
                            //intent.putExtra("key", marker.getTitle());
                            //startActivity(intent);
                        }

                        return false;
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
}
