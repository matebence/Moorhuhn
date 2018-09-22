package com.mate.bence.moorhuhn.Prostredie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mate.bence.moorhuhn.Nastroje.PrehravacHudbyZvukov;
import com.mate.bence.moorhuhn.R;

public class HlavneMenu extends AppCompatActivity {

    public static boolean zvuky = true;
    public static boolean hudba = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        naCelejObrazovke();
        init();
        vitajMoorhuhn();
        if (hudba) {
            PrehravacHudbyZvukov.spusti(HlavneMenu.this, R.raw.hudba);
        }
    }

    private void init() {
        Button hudba = findViewById(R.id.hudba_hry_moorhuhn);
        Button zvuk = findViewById(R.id.zvuky_hry_moorhuhn);
        Button spustiHru = findViewById(R.id.tlacidlo_spusti_hru);

        hudba.setOnClickListener(spravcaHudby);
        zvuk.setOnClickListener(spravcaZvuku);
        spustiHru.setOnClickListener(hra);
    }

    private View.OnClickListener spravcaHudby = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button obrazok = findViewById(R.id.hudba_hry_moorhuhn);
            if (hudba) {
                hudba = false;
                PrehravacHudbyZvukov.zastav();
                obrazok.setBackgroundResource(R.drawable.ic_hudba_vypnuta);
            } else {
                hudba = true;
                PrehravacHudbyZvukov.spusti(HlavneMenu.this, R.raw.hudba);
                obrazok.setBackgroundResource(R.drawable.ic_hudba_zapnuta);
            }
        }
    };

    private View.OnClickListener spravcaZvuku = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button obrazok = findViewById(R.id.zvuky_hry_moorhuhn);
            if (zvuky) {
                zvuky = false;
                obrazok.setBackgroundResource(R.drawable.ic_zvuky_vypnuta);
            } else {
                zvuky = true;
                obrazok.setBackgroundResource(R.drawable.ic_zvuky_zapnuta);
            }
        }
    };

    private View.OnClickListener hra = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrehravacHudbyZvukov.zastav();
            if (zvuky) {
                final MediaPlayer hraSpustena = MediaPlayer.create(HlavneMenu.this, R.raw.spusti);
                hraSpustena.start();
            }
            startActivity(new Intent(HlavneMenu.this, ProstredieHry.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    private void naCelejObrazovke() {
        @SuppressLint("InlinedApi")
        int atributy = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(atributy);
        setContentView(R.layout.hlavne_menu);
    }

    private void vitajMoorhuhn() {
        ImageView oknoObrazka = findViewById(R.id.animacia_moorhuhna);
        oknoObrazka.setBackgroundResource(R.drawable.ic_moorhun_vitaj_animacia);
        AnimationDrawable virajMoorhuhn = (AnimationDrawable) oknoObrazka.getBackground();
        virajMoorhuhn.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hudba) {
            PrehravacHudbyZvukov.zastav();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hudba) {
            PrehravacHudbyZvukov.spusti(HlavneMenu.this, R.raw.hudba);
        }
    }
}