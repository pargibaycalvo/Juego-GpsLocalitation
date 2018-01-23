package com.example.pargibaycalvo.gpslocalitation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    private Button Entrar, Salir;
    private MediaPlayer musicainicio;
    int MAX_VOLUME = 100;
    int soundVolume = 90;
    float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Entrar = (Button) findViewById(R.id.button4);
        Entrar.setOnClickListener(this);

        //musica de fondo para la app
        musicainicio = MediaPlayer.create(this, R.raw.inicio);
        musicainicio.setLooping(true);
        musicainicio.setVolume(volume, volume);
        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                musicainicio.start();
            }
        }, 1000);

    }

    @Override
    public void onClick(View v) {
        musicainicio.stop();
        switch (v.getId()){
            case R.id.button4:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
        }

    }
}
