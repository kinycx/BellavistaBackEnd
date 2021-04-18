package com.bro.bellavistabackend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class MapFragment extends Fragment {

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLatRef = mRootRef.child("trackfood1").child("latitude");
    DatabaseReference mLongRef = mRootRef.child("trackfood1").child("longitude");
    private FusedLocationProviderClient fusedLocationClient;


    GoogleMap mGoogleMap;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        TextView textView = (TextView) view.findViewById(R.id.set);
        textView.setText("placeholder"); //set text for text view

        Context context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        //check permission
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //posizione GPS

            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        //mappa asincrona
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                        .title("culo");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                googleMap.addMarker(markerOptions);
                                //fine location GPS

                                //click su mappa
                                googleMap.setOnMapClickListener(latLng1 -> {
                                    //quando clicchi sulla mappa
                                    MarkerOptions markerOptions1 = new MarkerOptions();
                                    //posizione del marker
                                    markerOptions1.position(latLng1);
                                    //titolo del marker
                                    markerOptions1.title(latLng1.latitude + " : " + latLng1.longitude);
                                    //rimuovi i marker
                                    googleMap.clear();
                                    //animazione
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));
                                    //aggiungi marker
                                    googleMap.addMarker(markerOptions1);

                                    //googleMap.setMyLocationEnabled(true);
                                    //
                                    textView.setText(latLng1.latitude + " : " + latLng1.longitude);
                                    //
                                    mLatRef.setValue(latLng1.latitude);
                                    mLongRef.setValue(latLng1.longitude);
                                });
                            }
                        });
                    }
                }
            });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }
}
