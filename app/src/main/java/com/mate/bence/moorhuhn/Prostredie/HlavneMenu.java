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

public class HlavneMenu extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HlavneMenu.class.getName();

    public static boolean stavZvuku = true;
    public static boolean stavHudby = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spustiNaCelejObrazovke();
        init();

        if (HlavneMenu.stavHudby) {
            PrehravacHudbyZvukov.spusti(HlavneMenu.this, R.raw.hudba);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (HlavneMenu.stavHudby) {
            PrehravacHudbyZvukov.zastav();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HlavneMenu.stavHudby) {
            PrehravacHudbyZvukov.spusti(HlavneMenu.this, R.raw.hudba);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hudba_hry_moorhuhn:
                spravcaHudbyZvukov(R.id.hudba_hry_moorhuhn, R.drawable.ic_hudba_zapnuta, R.drawable.ic_hudba_vypnuta, R.raw.hudba, HlavneMenu.stavHudby);
                break;
            case R.id.zvuky_hry_moorhuhn:
                spravcaHudbyZvukov(R.id.zvuky_hry_moorhuhn, R.drawable.ic_zvuky_zapnuta, R.drawable.ic_zvuky_vypnuta, -1, HlavneMenu.stavZvuku);
                break;
            case R.id.tlacidlo_spusti_hru:
                spravcaHry();
                break;
        }
    }

    private void spravcaHudbyZvukov(int tlacidloID, int tlacidloZapnuta, int tlacidloVypnuta, int zvukovaStopa, boolean stavTlacidla){
        Button vlastnostiTlacidla = findViewById(tlacidloID);
        if (stavTlacidla) {
            if(zvukovaStopa != -1) {
                PrehravacHudbyZvukov.zastav();
                HlavneMenu.stavHudby = false;
            }else{
                HlavneMenu.stavZvuku = false;
            }
            vlastnostiTlacidla.setBackgroundResource(tlacidloVypnuta);
        } else {
            if(zvukovaStopa != -1){
                PrehravacHudbyZvukov.spusti(HlavneMenu.this, zvukovaStopa);
                HlavneMenu.stavHudby = true;
            }else{
                HlavneMenu.stavZvuku = true;
            }
            vlastnostiTlacidla.setBackgroundResource(tlacidloZapnuta);
        }
    }

    private void spravcaHry(){
        PrehravacHudbyZvukov.zastav();
        if (HlavneMenu.stavZvuku) {
            final MediaPlayer hra = MediaPlayer.create(HlavneMenu.this, R.raw.spusti);
            hra.start();
        }
        startActivity(new Intent(HlavneMenu.this, ProstredieHry.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void spustiNaCelejObrazovke() {
        @SuppressLint("InlinedApi")

        int vlastnosti = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(vlastnosti);
        setContentView(R.layout.hlavne_menu);
    }

    private void init() {
        Button tlacidloHudby = findViewById(R.id.hudba_hry_moorhuhn);
        tlacidloHudby.setOnClickListener(this);

        Button tlacidloZvuku = findViewById(R.id.zvuky_hry_moorhuhn);
        tlacidloZvuku.setOnClickListener(this);

        Button tlacidloSpustenieHry = findViewById(R.id.tlacidlo_spusti_hru);
        tlacidloSpustenieHry.setOnClickListener(this);

        animaciaMoorhuhna();
    }

    private void animaciaMoorhuhna() {
        ImageView obrazok = findViewById(R.id.animacia_moorhuhna);
        obrazok.setBackgroundResource(R.drawable.ic_moorhun_vitaj_animacia);

        AnimationDrawable moorhuhn = (AnimationDrawable) obrazok.getBackground();
        moorhuhn.start();
    }
}