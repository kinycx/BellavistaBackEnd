package com.bro.bellavistabackend;

import android.Manifest;
import android.content.Context;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Objects;

public class MapFragment extends Fragment {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLatRef = mRootRef.child("trackfood1").child("latitude");
    DatabaseReference mLongRef = mRootRef.child("trackfood1").child("longitude");


    FloatingActionButton mPositionButton;
    FloatingActionButton mStop;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment

        mStop = view.findViewById(R.id.stop);
        mPositionButton = view.findViewById(R.id.currentLocation);

        getCurrentLocation();
        return view;
    }

    private void getCurrentLocation() {
        //check permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //posizione GPS
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.google_map);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            Task<Location> task = fusedLocationClient.getLastLocation();
            //
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    //mappa asincrona
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(googleMap -> {
                        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(41.205637138900705,16.58963978290558) , 12) );
                        //floating GPS button
                        mPositionButton.setOnClickListener(v -> {
                            googleMap.clear();
                            Snackbar.make(v, "Settata la posizione corrente", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .title("sono qui");
                            //icona bitmap
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions);
                            mLatRef.setValue(latLng.latitude);
                            mLongRef.setValue(latLng.longitude);
                        });

                        mStop.setOnClickListener(v -> {
                            googleMap.clear();
                            Snackbar.make(v, "Fermata la condivisione sul sito", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            mLatRef.setValue(0);
                            mLongRef.setValue(0);
                        });

                        //fine location GPS
                        //click su mappa
                        googleMap.setOnMapClickListener(latLng1 -> {
                            //quando clicchi sulla mappa
                            Snackbar.make(requireView(), "Posizione settata manualmente", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            MarkerOptions markerOptions1 = new MarkerOptions();
                            //icona bitmap
                            //markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.));
                            //posizione del marker
                            markerOptions1.position(latLng1);
                            //titolo del marker
                            markerOptions1.title(latLng1.latitude + " : " + latLng1.longitude);
                            //rimuovi i marker
                            googleMap.clear();
                            //animazione
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                            //aggiungi marker
                            googleMap.addMarker(markerOptions1);

                            //googleMap.setMyLocationEnabled(true);

                            mLatRef.setValue(latLng1.latitude);
                            mLongRef.setValue(latLng1.longitude);
                        });
                    });//
                }//
            });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 44: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
            }
        }
    }

}