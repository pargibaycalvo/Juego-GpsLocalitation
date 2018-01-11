package com.example.pargibaycalvo.gpslocalitation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    int MAX_VOLUME = 100; //volumen máximo de referencia
    int soundVolume = 90; //volumen que queremos poner
    float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
    public static final int INTERVALO = 2000; //2 segundos para salir
    public long tiempoPrimerClick;

    private MediaPlayer musicafondo;
    private GoogleMap mMap;
    private Marker marker;
    private Marker marcador;
    private LatLng latLng;
    private LatLng latLng1;
    private LatLng latLng2;
    private LatLng castelao;
    private LatLng coordenadas;
    double lat = 0.0;
    double lon = 0.0;
    private static final int LOCATION_REQUEST_CODE = 1;

    private LatLng p1,p2,p3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //musica de fondo para la app
        musicafondo = MediaPlayer.create(this, R.raw.orgricaste);
        musicafondo.setLooping(true);
        musicafondo.setVolume(volume, volume);
        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                musicafondo.start();
            }
        }, 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();

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


    //Posicion Actual del Usuario (conectarse vía GPS)
    private void localizacionActual(double lat, double lon){
        coordenadas= new LatLng(lat,lon);
        CameraUpdate miUbi= CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if(marcador!=null)marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Tu posicion actual")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
        mMap.animateCamera(miUbi);
    }

    private void actualizarUbicacion(Location localitation){
        if(localitation!=null){
            lat=localitation.getLatitude();
            lon=localitation.getLongitude();
            localizacionActual(lat,lon);
        }
    }

    LocationListener locListener = new LocationListener(){


        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);

    }

    private void localizacionPistas() {
        final MediaPlayer pandaria = MediaPlayer.create(this, R.raw.panda);
        final MediaPlayer cataclysm = MediaPlayer.create(this, R.raw.cata);
        final MediaPlayer legion = MediaPlayer.create(this, R.raw.leg);

        if (coordenadas == latLng) {
            musicafondo.stop();
            pandaria.start();
        } else if (coordenadas == latLng1) {
            musicafondo.stop();
            cataclysm.start();
        } else if (coordenadas == latLng2) {
            musicafondo.stop();
            legion.start();
        }
    }

    //confirmacion al salir de la app con sonido
    @Override
    public void onBackPressed(){

        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(this, "Por la Horda", Toast.LENGTH_SHORT).show();
            musicafondo.stop();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }




}
