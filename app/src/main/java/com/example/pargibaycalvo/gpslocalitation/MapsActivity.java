package com.example.pargibaycalvo.gpslocalitation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Marker marker;
    private LatLng latLng;
    private LatLng latLng1;
    private LatLng latLng2;
    private LatLng castelao;
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

        // Puntero por defecto con permisos de administrador ORGRICASTELAO
        castelao = new LatLng(42.23661386151706, -8.714480996131897);
        mMap.addMarker(new MarkerOptions()
                .position(castelao)
                .title("Orgricastelao (DanielCastelao)")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.horda)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(castelao));
        mMap.setOnInfoWindowClickListener(this);

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

        // Marcadores 3 pistas
        mMap.addMarker(new MarkerOptions().position(new LatLng(42.23661386151706, -8.714480996131897)));

        //Punto 1 PANDARIA
        latLng = new LatLng(42.237439526686515, -8.714226186275482);//La Fayette
        int radius = 10;

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle = mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Mision 1. Encuentra a Sha de la Ira")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pandariam)));
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Punto 2 CATACLYSM
        latLng1 = new LatLng(42.237706320945556, -8.715687990188599);//GaliPizza
        int radius1 = 10;

        CircleOptions circleOptions1 = new CircleOptions()
                .center(latLng1)
                .radius(radius1)
                .strokeColor(Color.parseColor("#FF0000"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle1 = mMap.addCircle(circleOptions1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Mision 2. Habla con Gamon para llegar a Legion")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cataclysm)));
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));

        //Punto 3 LEGION
        latLng2 = new LatLng(42.238956026405795, -8.71614396572113);//Parada Bus Arenal
        int radius2 = 10;

        CircleOptions circleOptions2 = new CircleOptions()
                .center(latLng2)
                .radius(radius2)
                .strokeColor(Color.parseColor("#3ADF00"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle2 = mMap.addCircle(circleOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Mision 3. Elimina a Titan de Argus")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.legion)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
        mMap.setOnInfoWindowClickListener(this);

    }

    //Ventana de información 
    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.equals(latLng)) {
            WowDialogFragment.newInstance(marker.getTitle(),
                    getString(R.string.pandaria_full_snippet))
                    .show(getSupportFragmentManager(), null);
        }
        else if (marker.equals(latLng1)){
            WowDialogFragment.newInstance(marker.getTitle(),
                    getString(R.string.cataclysm_full_snippet))
                    .show(getSupportFragmentManager(), null);
        }
        else if (marker.equals(latLng2)){
            WowDialogFragment.newInstance(marker.getTitle(),
                    getString(R.string.legion_full_snippet))
                    .show(getSupportFragmentManager(), null);
        }
        else if (marker.equals(castelao)){
            WowDialogFragment.newInstance(marker.getTitle(),
                    getString(R.string.orgricastelao_full_snippet))
                    .show(getSupportFragmentManager(), null);
        }
        else{
            System.out.println("Error");
        }
    }
}
