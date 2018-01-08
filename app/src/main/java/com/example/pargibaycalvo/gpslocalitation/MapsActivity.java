package com.example.pargibaycalvo.gpslocalitation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Puntero por defecto con permisos de administrador
        LatLng castelao = new LatLng(42.23661386151706, -8.714480996131897);
        mMap.addMarker(new MarkerOptions().position(castelao).title("Orgricastelao (DanielCastelao)").icon(BitmapDescriptorFactory.fromResource(R.drawable.horda)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(castelao));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // Solicitar permisos de usuario
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setMyLocationEnabled(true);

        // Marcadores 3 pistas
        mMap.addMarker(new MarkerOptions().position(new LatLng(42.23661386151706, -8.714480996131897)));

        LatLng latLng = new LatLng(42.237439526686515, -8.714226186275482);//La Fayette

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Mision 1. Encuentra a Sha de la Ira")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pandariam)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        LatLng latLng1 = new LatLng(42.237706320945556, -8.715687990188599);//GaliPizza

        googleMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Mision 2. Habla con Gamon para llegar a Legion")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cataclysm)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));

        LatLng latLng2 = new LatLng(42.238956026405795, -8.71614396572113);//Parada Bus Arenal

        googleMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Mision 3. Elimina a Titan de Argus")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.legion)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));

    }
}
