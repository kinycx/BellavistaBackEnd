package com.bro.bellavistabackend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapFragment extends Fragment {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLatRef = mRootRef.child("trackfood1").child("latitude");
    DatabaseReference mLongRef = mRootRef.child("trackfood1").child("longitude");

    boolean isPermissionGranted = true;
    GoogleMap mGoogleMap;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment

        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.google_map);

            TextView textView = (TextView) view.findViewById(R.id.set);
            textView.setText("placeholder"); //set text for text view

            //mappa asincrona
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //quando la mappa e' caricata
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                        @Override
                        public void onMapClick(LatLng latLng) {
                            //quando clicchi sulla mappa
                            MarkerOptions markerOptions = new MarkerOptions();
                            //posizione del marker
                            markerOptions.position(latLng);
                            //titolo del marker
                            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                            //rimuovi i marker
                            googleMap.clear();
                            //animazione
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            //aggiungi marker
                            googleMap.addMarker(markerOptions);

                            //googleMap.setMyLocationEnabled(true);
                            //
                            textView.setText(latLng.latitude + " : " + latLng.longitude);
                            //
                            mLatRef.setValue(latLng.latitude);
                            mLongRef.setValue(latLng.longitude);
                        }
                    });
                }
            });
        }
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(MapFragment.this)
                //        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


    private void checkPermission(){
        //TODO
    }
}