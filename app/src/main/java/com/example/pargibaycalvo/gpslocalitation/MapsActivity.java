package com.example.pargibaycalvo.gpslocalitation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    //Declaraciones, musica de fondo y tiempo de respuesta en salir de la app
    int MAX_VOLUME = 100;
    int soundVolume = 50;
    float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
    public static final int INTERVALO = 2000;
    public long tiempoPrimerClick;

    //Declaraciones varias del programa
    private MediaPlayer musicafondo, guerra1, guerra2, panda, cata, legion, lich;
    private GoogleMap mMap;
    private Marker marcador;
    private LatLng latLng, latLng1, latLng2, latLng3, castelao, coordenadas;
    private LatLng latLngAl, latLngAl1, latLngAl2, latLngAl3, latLngAl4, latLngAl5, latLngAl6, latLngAl7;
    private TextView lblLatitud, lblLongitud, lblPanda, lblCata, lblLegion, lblLichkng, txtqr, crono;
    private Button qr;
    double lat, lon;
    private static final int LOCATION_REQUEST_CODE = 1;

    //Declaraciones varias para la detección de coordenadas GPS
    private static final String TAG = "gpslog";
    private LocationManager mLocMgr;
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 1;
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000;

    //Declaraciones de distancias entre puntos tanto lideres como puntos de asalto
    private Location location1panda, location2cata, location3leg, location4kng;
    private Location location1ali, location2ali, location3ali, location4ali, location5ali, location6ali, location7ali, location8ali;
    float distancia1, distancia2, distancia3, distancia4;
    float dist1ali, dist2ali, dist3ali, dist4ali, dist5ali, dist6ali, dist7ali, dist8ali;
    float metroscerca = 20;
    float metroslejos = 10;

    //Declaraciones Marcadores
    private Marker mpanda, mlegion, mcata, mlich;
    private Circle cir1, cir2, cir3, cir4;

    //Declaraciones para funcionamiento del lector QR
    private  final static int codigo = 0;
    private Intent intentactual;
    int victoria = 0;
    int qrlegion = 0, qrpanda = 0, qrcata = 0, qrlich = 0;

    //Declaraciones cuenta atras
    private static final String FORMAT = "%02d:%02d:%02d";
    Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Declaraciones de cuadros de textos y boton
        lblLatitud = (TextView) findViewById(R.id.text1);
        lblLongitud = (TextView) findViewById(R.id.text2);
        lblPanda = (TextView) findViewById(R.id.text3);
        lblCata = (TextView) findViewById(R.id.text4);
        lblLegion = (TextView) findViewById(R.id.text5);
        lblLichkng = (TextView) findViewById(R.id.text6);
        txtqr = (TextView) findViewById(R.id.textQR);
        crono = (TextView) findViewById(R.id.textCrono);
        qr = (Button) findViewById(R.id.button2);

        intentactual = this.getIntent();

        //Permisos para poder localizarte vía GPS
        mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        } else {
            Log.i(TAG, "Permisos necesarios OK!.");
            mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper());
        }
        lblLatitud.setText("Lat ");
        lblLongitud.setText("Long ");

        //musica de fondo para la app
        musicafondo = MediaPlayer.create(this, R.raw.orgricaste);
        musicafondo.setLooping(true);
        musicafondo.setVolume(volume, volume);
        new Timer().schedule(new TimerTask() {
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
        lich = MediaPlayer.create(this, R.raw.lich);

        contexto = this.getApplicationContext();

        //cuenta atras para finalizar el juego tiempo de 45min
        //al finalizar si no ganas salta una nueva ventana con gif
        new CountDownTimer(2706900, 1000) {

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {

                crono.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                crono.setText("HA GANADO LA LEGION!");
                    Intent intent = new Intent(getApplicationContext(), WastedActivity.class);
                    startActivity(intent);
            }

        }.start();

        View noReset = getWindow().getDecorView();
        int iuOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        noReset.setSystemUiVisibility(iuOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //ejecucion de metodos
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

        //funcion al pulsar el boton de qr
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SimpleScanner.class);
                if (intentactual.getExtras() != null) {
                    intent.putExtra("contador",intentactual.getExtras().getInt("contador"));
                }else{
                    intent.putExtra("contador",0);
                }
                startActivityForResult(intent,codigo);
            }
        });

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
        cir1 = mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        mpanda = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Mision 1. Encuentra a Sha de la Ira")
                .snippet("Objetivo: Entranarte")
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
        cir2 = mMap.addCircle(circleOptions1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));

        mcata = mMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Mision 2. Habla con Gamon para llegar a Legion")
                .snippet("Objetivo: Proporcionarte Armamento")
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
        cir3 = mMap.addCircle(circleOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 17));

        mlegion = mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Mision 3. Elimina a Titan de Argus")
                .snippet("Fundador: pargibay 6150")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.legion)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
        mMap.setOnInfoWindowClickListener(this);

        location3leg = new Location("legion");
        location3leg.setLatitude(42.238956);
        location3leg.setLongitude(-8.716143);
//--------------------------------------------------------------------------------------------------------//

        //Punto 4 KING
        latLng3 = new LatLng(42.238447, -8.717890);//Bar Puerto
        int radius3 = 10;

        CircleOptions circleOptions3 = new CircleOptions()
                .center(latLng3)
                .radius(radius3)
                .strokeColor(Color.parseColor("#3ADF00"))
                .strokeWidth(4)
                .fillColor(Color.parseColor("#A440FFEF"));
        cir4 = mMap.addCircle(circleOptions3);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng3, 17));

        mlich = mMap.addMarker(new MarkerOptions()
                .position(latLng3)
                .title("Mision 4. Habla con el Rey Exánime")
                .snippet("Objetivo: Reclutar ejército de muertos")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.lichking)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng3));
        mMap.setOnInfoWindowClickListener(this);

        location4kng = new Location("lich");
        location4kng.setLatitude(42.238447);
        location4kng.setLongitude(-8.717890);
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
    //Cada ciertos segundos actualiza la cámara y te lleva al punto de tu posicion
    private void localizacionActual(double lat, double lon){
        coordenadas= new LatLng(lat,lon);
        CameraUpdate miUbi= CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if(marcador!=null)marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Tú")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pj)));
        mMap.animateCamera(miUbi);
    }

    //Actualiza la ubicacion tuya cada cierto tiempo
    private void actualizarUbicacion(Location localitation){
        if(localitation!=null){
            lat= localitation.getLatitude();
            lon=localitation.getLongitude();
            localizacionActual(lat,lon);
        }

    }

    //Distancias entre tu posicion y la posicion de los reinos a conquistar
    //Cuando estes cerca de uno de los reinos hará un cambio de sonido
    private void distanciaPanda(Location localitation) {
        distancia1 = localitation.distanceTo(location1panda);
        lblPanda.setText(("Mts a Pandaria: " + distancia1));
        if (localitation.distanceTo(location1panda) <= metroscerca) {
            panda.start();
        } else if (localitation.distanceTo(location1panda) > metroslejos) {
            panda.stop();
        }
    }
    private void distanciaCata(Location localitation) {
        distancia2 = localitation.distanceTo(location2cata);
        lblCata.setText(("Mts a Cataclysm: " + distancia2));
        if (localitation.distanceTo(location2cata) <= metroscerca) {
            cata.start();
        } else if (localitation.distanceTo(location2cata) > metroslejos) {
            cata.stop();
        }
    }
    private void distanciaLeg(Location localitation) {
        distancia3 = localitation.distanceTo(location3leg);
        lblLegion.setText(("Mts a Legion: " + distancia3));
        if (localitation.distanceTo(location3leg) <= metroscerca) {
            legion.start();
            crono.setText("Encuentra el QR y habrás ganado la batalla. Por la HORDA!");
        } else if (localitation.distanceTo(location3leg) > metroslejos) {
            legion.stop();
        }
    }
    private void distanciaLich(Location localitation) {
        distancia4 = localitation.distanceTo(location4kng);
        lblLichkng.setText(("Mts a Lich: " + distancia4));
        if (localitation.distanceTo(location4kng) <= metroscerca) {
            lich.start();
        } else if (localitation.distanceTo(location4kng) > metroslejos) {
            lich.stop();
        }
    }

    //Distancias entre tu posicion y puntos de asalto de la Alianza
    //Estando cerca saltará un sonido de batalla
    private void distanciaAlianza1(Location localitation) {
        dist1ali = localitation.distanceTo(location1ali);
        if (localitation.distanceTo(location1ali) <= metroscerca) {
            guerra1.start();
        } else if (localitation.distanceTo(location1ali) > metroslejos) {
            guerra1.stop();
        }
    }
    private void distanciaAlianza2(Location localitation) {
        dist2ali = localitation.distanceTo(location2ali);
        if (localitation.distanceTo(location2ali) <= metroscerca) {
            guerra1.start();
        } else if (localitation.distanceTo(location2ali) > metroslejos) {
            guerra1.stop();
        }
    }
    private void distanciaAlianza3(Location localitation) {
        dist3ali = localitation.distanceTo(location3ali);
        if (localitation.distanceTo(location3ali) <= metroscerca) {
            guerra1.start();
        } else if (localitation.distanceTo(location3ali) > metroslejos) {
            guerra1.stop();
        }
    }
    private void distanciaAlianza4(Location localitation){
        dist4ali = localitation.distanceTo(location4ali);
        if(localitation.distanceTo(location4ali) <= metroscerca){
            guerra1.start();
        }else if(localitation.distanceTo(location4ali) > metroslejos){
            guerra1.stop();}
    }

    private void distanciaAlianza5(Location localitation) {
        dist5ali = localitation.distanceTo(location5ali);
        if (localitation.distanceTo(location5ali) <= metroscerca) {
            guerra2.start();
        } else if (localitation.distanceTo(location5ali) > metroslejos) {
            guerra2.stop();
        }
    }
    private void distanciaAlianza6(Location localitation) {
        dist6ali = localitation.distanceTo(location6ali);
        if (localitation.distanceTo(location6ali) <= metroscerca) {
            guerra2.start();
        } else if (localitation.distanceTo(location6ali) > metroslejos) {
            guerra2.stop();
        }
    }
    private void distanciaAlianza7(Location localitation) {
        dist7ali = localitation.distanceTo(location7ali);
        if (localitation.distanceTo(location7ali) <= metroscerca) {
            guerra2.start();
        } else if (localitation.distanceTo(location7ali) > metroslejos) {
            guerra2.stop();
        }
    }
    private void distanciaAlianza8(Location localitation) {
        dist8ali = localitation.distanceTo(location8ali);
        if(localitation.distanceTo(location8ali) <= metroscerca){
            guerra2.start();
        } else if (localitation.distanceTo(location8ali) > metroslejos){
            guerra2.stop();
        }
    }

    //Metodos para tener nuestra localizacion exacta
    LocationListener locListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            distanciaPanda(location);
            distanciaCata(location);
            distanciaLeg(location);
            distanciaLich(location);

            distanciaAlianza1(location);
            distanciaAlianza2(location);
            distanciaAlianza3(location);
            distanciaAlianza4(location);
            distanciaAlianza5(location);
            distanciaAlianza6(location);
            distanciaAlianza7(location);
            distanciaAlianza8(location);


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

    //Permisos para que nos pueda dar nuestra posicion
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

    //confirmacion al salir de la app
    @Override
    public void onBackPressed(){

        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(this, "Por la Horda", Toast.LENGTH_SHORT).show();
            musicafondo.stop();
            panda.stop();
            legion.stop();
            guerra2.stop();
            guerra1.stop();
            cata.stop();
            lich.stop();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    //resultado al leer los qr
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == codigo) {
                qrCapture(data.getExtras().getString("retorno"));
                intentactual.putExtra("contador",data.getExtras().getInt("contador"));
                Toast.makeText(this, "Has escaneado QR: "+intentactual.getExtras().getInt("contador"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //lectura de los qr específicos (no vale cualquier qr), ocultar los marker al leer los qr, si lees un qr repetido no cuenta
    //cuando leas los 4 qr juego finalizado con pantalla de victoria
    private void qrCapture(String retorno) {

        if (retorno.contains("1Objetivo")) {
            txtqr.setText(retorno);
            qrpanda++;
            victoria++;
            cir1.setVisible(false);
            mpanda.setVisible(false);
            if (qrpanda >=2 ){
                txtqr.setText("LOGRO YA CONOCIDO: "+retorno);
                victoria--;
            }
        } else if (retorno.contains("2Objetivo")) {
            txtqr.setText(retorno);
            qrcata++;
            victoria++;
            cir2.setVisible(false);
            mcata.setVisible(false);
            if (qrcata >=2 ){
                txtqr.setText("LOGRO YA CONOCIDO: "+retorno);
                victoria--;
            }
        } else if (retorno.contains("3Objetivo")) {
            txtqr.setText(retorno);
            qrlich++;
            victoria++;
            cir4.setVisible(false);
            mlich.setVisible(false);
            if (qrlich >=2 ){
                txtqr.setText("LOGRO YA CONOCIDO: "+retorno);
                victoria--;
            }
        } else if (retorno.contains("Titan")) {
            txtqr.setText(retorno);
            qrlegion++;
            victoria++;
            cir3.setVisible(false);
            mlegion.setVisible(false);
            if (qrlegion >=2 ){
                txtqr.setText("LOGRO YA CONOCIDO: "+retorno);
                victoria--;
            }
        } else {
            txtqr.setText("QR INCORRECTO");
        }

        if (victoria == 4) {
            Intent intent = new Intent(this.getApplicationContext(), WinActivity.class);
            startActivity(intent);
        }

    }
}
