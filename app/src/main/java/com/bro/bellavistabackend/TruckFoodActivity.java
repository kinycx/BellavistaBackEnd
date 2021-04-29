package com.bro.bellavistabackend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.w3c.dom.ls.LSException;

import java.util.Objects;

public class TruckFoodActivity extends FragmentActivity implements OnMapReadyCallback {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLatRef = mRootRef.child("trackfood1").child("latitude");
    DatabaseReference mLongRef = mRootRef.child("trackfood1").child("longitude");

    FloatingActionButton mPositionButton;
    FloatingActionButton mStop;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context;
        mPositionButton = (FloatingActionButton) findViewById(R.id.currentLocation);
        mStop = (FloatingActionButton) findViewById(R.id.stop);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(TruckFoodActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        currentLocation = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        assert supportMapFragment != null;
                        supportMapFragment.getMapAsync(TruckFoodActivity.this);
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(TruckFoodActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        //floating GPS button
        mPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                        .title("sono qui");
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                googleMap.addMarker(markerOptions);
                Snackbar.make(v, "Settata la posizione corrente", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //icona bitmap
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.));

                mLatRef.setValue(latLng.latitude);
                mLongRef.setValue(latLng.longitude);
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                Snackbar.make(v, "Fermata la condivisione sul sito", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mLatRef.setValue(0);
                mLongRef.setValue(0);
            }
        });


        //fine location GPS
        //click su mappa

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                Snackbar.make(supportMapFragment.requireView(), "Posizione settata manualmente", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //quando clicchi sulla mappa
                MarkerOptions markerOptions1 = new MarkerOptions();
                //icona bitmap
                //markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.));
                //posizione del marker
                markerOptions1.position(latLng);
                //titolo del marker
                markerOptions1.title(latLng.latitude + " : " + latLng.longitude);
                //rimuovi i marker
                googleMap.clear();
                //animazione
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                //aggiungi marker
                googleMap.addMarker(markerOptions1);

                //googleMap.setMyLocationEnabled(true);

                mLatRef.setValue(latLng.latitude);
                mLongRef.setValue(latLng.longitude);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }
}