package com.example.pargibaycalvo.gpslocalitation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    //Declaraciones, musica de fondo y tiempo de respuesta en salir de la app
    int MAX_VOLUME = 100;
    int soundVolume = 90;
    float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
    public static final int INTERVALO = 2000; //2 segundos para salir
    public long tiempoPrimerClick;

    //Declaraciones varias del programa
    private MediaPlayer musicafondo, guerra1, guerra2, panda, cata, legion;
    private GoogleMap mMap;
    private Marker marcador;
    private LatLng latLng, latLng1, latLng2, castelao, coordenadas;
    private LatLng latLngAl, latLngAl1, latLngAl2, latLngAl3, latLngAl4, latLngAl5, latLngAl6, latLngAl7;
    private TextView lblLatitud, lblLongitud, lblPanda, lblCata, lblLegion;
    double lat, lon;
    private static final int LOCATION_REQUEST_CODE = 1;

    //Declaraciones varias para la detección de coordenadas GPS
    private static final String TAG = "gpslog";
    private LocationManager mLocMgr;
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 1; // 1 metro
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000; // 1 sg

    //Declaraciones de distancias entre puntos tanto lideres como puntos de asalto
    private Location location1panda, location2cata, location3leg;
    private Location location1ali, location2ali, location3ali, location4ali, location5ali, location6ali, location7ali, location8ali;
    double distancia1ali, distancia2ali, distancia3ali, distancia4ali, distancia5ali, distancia6ali, distancia7ali, distancia8ali;
    float distancia1, distancia2, distancia3;
    float metroscerca = 20;
    float metroslejos = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lblLatitud = (TextView) findViewById(R.id.text1);
        lblLongitud = (TextView) findViewById(R.id.text2);
        lblPanda = (TextView) findViewById(R.id.text3);
        lblCata = (TextView) findViewById(R.id.text4);
        lblLegion = (TextView) findViewById(R.id.text5);

        //Permisos para poder localizarte vía GPS
        mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        }else{
            Log.i(TAG, "Permisos necesarios OK!.");
            mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper());
        }
        lblLatitud.setText("Lat ");
        lblLongitud.setText("Long ");

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

        //musica para las distancias
        guerra1 = MediaPlayer.create(this, R.raw.guerra1);
        guerra2 = MediaPlayer.create(this, R.raw.guerra2);
        panda = MediaPlayer.create(this, R.raw.panda);
        cata = MediaPlayer.create(this, R.raw.cata);
        legion = MediaPlayer.create(this, R.raw.legion);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        reinosConquistar();
        posicionAlianza();

        // Puntero por defecto con permisos de administrador ORGRICASTELAO
        castelao = new LatLng(42.236572, -8.714315);
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
    }

    //--------------------------------------------------------------------------------------------------------//
    //Marcadores principales
    private void reinosConquistar(){

        //Punto 1 PANDARIA
        latLng = new LatLng(42.237439, -8.714226);//La Fayette
        int radius = 10;

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#AF4046FF"));
        Circle circle = mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Mision 1. Encuentra a Sha de la Ira")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pandariam)));
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        location1panda = new Location("panda");
        location1panda.setLatitude(42.237439);
        location1panda.setLongitude(-8.714226);
//--------------------------------------------------------------------------------------------------------//

        //Punto 2 CATACLYSM
        latLng1 = new LatLng(42.237706, -8.715687);//GaliPizza
        int radius1 = 10;

        CircleOptions circleOptions1 = new CircleOptions()
                .center(latLng1)
                .radius(radius1)
                .strokeColor(Color.parseColor("#FF0000"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#BBFF404A"));
        Circle circle1 = mMap.addCircle(circleOptions1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Mision 2. Habla con Gamon para llegar a Legion")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cataclysm)));
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));

        location2cata = new Location("cataclysm");
        location2cata.setLatitude(42.237706);
        location2cata.setLongitude(-8.715687);
//--------------------------------------------------------------------------------------------------------//

        //Punto 3 LEGION
        latLng2 = new LatLng(42.238956, -8.716143);//Parada Bus Arenal
        int radius2 = 10;

        CircleOptions circleOptions2 = new CircleOptions()
                .center(latLng2)
                .radius(radius2)
                .strokeColor(Color.parseColor("#3ADF00"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#AF249607"));
        Circle circle2 = mMap.addCircle(circleOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Mision 3. Elimina a Titan de Argus")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.legion)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
        mMap.setOnInfoWindowClickListener(this);

        location3leg = new Location("legion");
        location3leg.setLatitude(42.238956);
        location3leg.setLongitude(-8.716143);
    }

    //--------------------------------------------------------------------------------------------------------//
    //Puntos de ataque Alianza
    private void posicionAlianza() {

        latLngAl = new LatLng(42.236961, -8.714323);//Ataque de la alianza PuertaEdificio
        double radius = 10;

        CircleOptions circleOptions = new CircleOptions()
                .center(latLngAl)
                .radius(radius)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle = mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl)
                .title("Campamento de la Alianza")
                .snippet("Enemigo Conserje")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl));
        mMap.setOnInfoWindowClickListener(this);

        location1ali = new Location("puertaprincipal");
        location1ali.setLatitude(42.236961);
        location1ali.setLongitude(-8.714323);
//--------------------------------------------------------------------------------------------------------//

        latLngAl1 = new LatLng(42.236942, -8.712684);//Ataque de la alianza Telepizza
        int radius1 = 10;

        CircleOptions circleOptions1 = new CircleOptions()
                .center(latLngAl1)
                .radius(radius1)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle1 = mMap.addCircle(circleOptions1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl1, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl1)
                .title("Campamento de la Alianza")
                .snippet("Enemigo Telepizza")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl1));
        mMap.setOnInfoWindowClickListener(this);

        location2ali = new Location("telepizza");
        location2ali.setLatitude(42.236942);
        location2ali.setLongitude(-8.712684);
//--------------------------------------------------------------------------------------------------------//

        latLngAl2 = new LatLng(42.23772, -8.712716);//Ataque de la alianza RotondaTV
        int radius2 = 10;

        CircleOptions circleOptions2 = new CircleOptions()
                .center(latLngAl2)
                .radius(radius2)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle2 = mMap.addCircle(circleOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl2, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl2)
                .title("Campamento de la Alianza")
                .snippet("Enemigo RotonTV")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl2));
        mMap.setOnInfoWindowClickListener(this);

        location3ali = new Location("rotondatv");
        location3ali.setLatitude(42.23772);
        location3ali.setLongitude(-8.712716);
//--------------------------------------------------------------------------------------------------------//

        latLngAl3 = new LatLng(42.237748, -8.714929);//Ataque de la alianza CaféSambor´s
        int radius3 = 10;

        CircleOptions circleOptions3 = new CircleOptions()
                .center(latLngAl3)
                .radius(radius3)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle3 = mMap.addCircle(circleOptions3);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl3, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl3)
                .title("Campamento de la Alianza")
                .snippet("Enemigo Sambors")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl3));
        mMap.setOnInfoWindowClickListener(this);

        location4ali = new Location("sambors");
        location4ali.setLatitude(42.237748);
        location4ali.setLongitude(-8.714929);
//--------------------------------------------------------------------------------------------------------//

        latLngAl4 = new LatLng(42.237945, -8.716356);//Ataque de la alianza CerveceríaLatería
        int radius4 = 10;

        CircleOptions circleOptions4 = new CircleOptions()
                .center(latLngAl4)
                .radius(radius4)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle4 = mMap.addCircle(circleOptions4);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl4)
                .title("Campamento de la Alianza")
                .snippet("Enemigo Latería")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl4));
        mMap.setOnInfoWindowClickListener(this);

        location5ali = new Location("lateria");
        location5ali.setLatitude(42.237945);
        location5ali.setLongitude(-8.716356);
//--------------------------------------------------------------------------------------------------------//

        latLngAl5 = new LatLng(42.238749, -8.714991);//Ataque de la alianza RealeSeguros
        int radius5 = 10;

        CircleOptions circleOptions5 = new CircleOptions()
                .center(latLngAl5)
                .radius(radius5)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle5 = mMap.addCircle(circleOptions5);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl5, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl5)
                .title("Campamento de la Alianza")
                .snippet("Enemigo Reale")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl5));
        mMap.setOnInfoWindowClickListener(this);

        location6ali = new Location("reale");
        location6ali.setLatitude(42.238749);
        location6ali.setLongitude(-8.714991);
//--------------------------------------------------------------------------------------------------------//

        latLngAl6 = new LatLng(42.237968, -8.714398);//Ataque de la alianza Misterphone
        int radius6 = 10;

        CircleOptions circleOptions6 = new CircleOptions()
                .center(latLngAl6)
                .radius(radius6)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle6 = mMap.addCircle(circleOptions6);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl6, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl6)
                .title("Campamento de la Alianza")
                .snippet("Enemigo MrPhone")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl6));
        mMap.setOnInfoWindowClickListener(this);

        location7ali = new Location("misterphone");
        location7ali.setLatitude(42.237968);
        location7ali.setLongitude(-8.714398);
//--------------------------------------------------------------------------------------------------------//

        latLngAl7 = new LatLng(42.239073, -8.717032);//Ataque de la alianza MetropolGalicia
        int radius7 = 10;

        CircleOptions circleOptions7 = new CircleOptions()
                .center(latLngAl7)
                .radius(radius7)
                .strokeColor(Color.parseColor("#FFEAFF01"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#C80022F9"));
        Circle circle7 = mMap.addCircle(circleOptions7);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAl7, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLngAl7)
                .title("Campamento de la Alianza")
                .snippet("Enemigo MetroGaliza")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alianza1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAl7));
        mMap.setOnInfoWindowClickListener(this);

        location8ali = new Location("metropol");
        location8ali.setLatitude(42.239073);
        location8ali.setLongitude(-8.717032);
    }

    //--------------------------------------------------------------------------------------------------------//
    //Ventana de información (NO ACTIVA)
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
        //CameraUpdate miUbi= CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if(marcador!=null)marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Tú")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pj)));
        //mMap.animateCamera(miUbi);
    }

    private void actualizarUbicacion(Location localitation){
        if(localitation!=null){
            lat= localitation.getLatitude();
            lon=localitation.getLongitude();
            localizacionActual(lat,lon);
        }

    }

    //Distancias entre tu posicion y la posicion de los reinos a conquistar
    //Cuando estes cerca de uno de los reinos hará un cambio de sonido
    private void distanciaReinos(Location localitation) {
        distancia1 = localitation.distanceTo(location1panda);
        distancia2 = localitation.distanceTo(location2cata);
        distancia3 = localitation.distanceTo(location3leg);

        lblPanda.setText(("Mts a Pandaria: " + distancia1));
        lblCata.setText(("Mts a Cataclysm: " + distancia2));
        lblLegion.setText(("Mts a Legion: " + distancia3));

        if(localitation.distanceTo(location1panda)<= metroscerca){
            musicafondo.stop();
            panda.start();
        }
        else if(localitation.distanceTo(location1panda)>metroslejos){
            panda.stop();
            musicafondo.start();
        }
        if(localitation.distanceTo(location2cata)<= metroscerca){
            musicafondo.stop();
            cata.start();
        }
        else if(localitation.distanceTo(location2cata)>metroslejos){
            cata.stop();
            musicafondo.start();
        }
        if(localitation.distanceTo(location3leg)<= metroscerca){
            musicafondo.stop();
            legion.start();
        }
        else if(localitation.distanceTo(location3leg)>metroslejos){
            legion.stop();
            musicafondo.start();
        }
    }

    private void distanciaAlianza(Location localitation){
        distancia1ali = localitation.distanceTo(location1ali);
        distancia2ali = localitation.distanceTo(location2ali);
        distancia3ali = localitation.distanceTo(location3ali);
        distancia4ali = localitation.distanceTo(location4ali);
        distancia5ali = localitation.distanceTo(location5ali);
        distancia6ali = localitation.distanceTo(location6ali);
        distancia7ali = localitation.distanceTo(location7ali);
        distancia8ali = localitation.distanceTo(location8ali);

        if(localitation.distanceTo(location1ali) <= metroscerca){
            musicafondo.stop();
            guerra1.start();
        }else if(localitation.distanceTo(location1ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location2ali) <= metroscerca){
            musicafondo.stop();
            guerra2.start();
        }else if(localitation.distanceTo(location2ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location3ali) <= metroscerca){
            musicafondo.stop();
            guerra1.start();
        }else if(localitation.distanceTo(location3ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location4ali) <= metroscerca){
            musicafondo.stop();
            guerra2.start();
        }else if(localitation.distanceTo(location4ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location5ali) <= metroscerca){
            musicafondo.stop();
            guerra1.start();
        }else if(localitation.distanceTo(location5ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location6ali) <= metroscerca){
            musicafondo.stop();
            guerra2.start();
        }else if(localitation.distanceTo(location6ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location7ali) <= metroscerca){
            musicafondo.stop();
            guerra1.start();
        }else if(localitation.distanceTo(location7ali) > metroslejos){
            musicafondo.start();
        }
        if(localitation.distanceTo(location8ali) <= metroscerca){
            musicafondo.stop();
            guerra2.start();
        }else if(localitation.distanceTo(location8ali) > metroslejos){
            musicafondo.start();
        }


    }

    LocationListener locListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            localizacionAlianza(location);
            distanciaReinos(location);
            distanciaAlianza(location);
            Log.i(TAG, "Lat " + location.getLatitude() + " Long " + location.getLongitude());
            lblLatitud.setText(("Lat: " +   location.getLatitude()));
            lblLongitud.setText(("Long: " + location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onProviderDisabled()");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderDisabled()");

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled()");

        }
    };

    private void miUbicacion(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, locListener);

    }

    private void localizacionAlianza(Location localitation) {

        if(localitation.distanceTo(location1ali) <= metroscerca){
            guerra1.start();
        }
        else if(localitation.distanceTo(location1ali) >metroslejos){
            guerra1.stop();
        }

    }

    //confirmacion al salir de la app
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
