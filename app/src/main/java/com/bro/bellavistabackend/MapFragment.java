package com.bro.bellavistabackend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment

        SupportMapFragment supportMapFragment =  (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //mappa asincrona
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //quando la mappa e' caricata
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
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

                    }
                });
            }
        });

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
}