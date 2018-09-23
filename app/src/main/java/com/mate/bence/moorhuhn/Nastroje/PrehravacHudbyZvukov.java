package com.mate.bence.moorhuhn.Nastroje;

import android.content.Context;
import android.media.MediaPlayer;

public class PrehravacHudbyZvukov {

    private static final String TAG = PrehravacHudbyZvukov.class.getName();

    private Context context;
    private int hudba;

    private static MediaPlayer prehravacHudby = null;
    private static MediaPlayer prehravacZvukov = null;

    static public void spusti(Context context, int id) {
        new PrehravacHudbyZvukov(context, id);
    }

    static public void zastav() {
        PrehravacHudbyZvukov.prehravacHudby.stop();
        PrehravacHudbyZvukov.prehravacZvukov.stop();
    }

    private PrehravacHudbyZvukov(Context context, int id) {
        this.context = context;
        this.hudba = id;

        PrehravacHudbyZvukov.prehravacHudby = MediaPlayer.create(this.context, this.hudba);
        PrehravacHudbyZvukov.prehravacHudby.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                PrehravacHudbyZvukov.prehravacHudby.start();
            }
        });
        vytvorDalsi();
    }

    private MediaPlayer.OnCompletionListener koniecZvukovehoSuboru = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            PrehravacHudbyZvukov.prehravacHudby = PrehravacHudbyZvukov.prehravacZvukov;
            vytvorDalsi();
        }
    };

    private void vytvorDalsi() {
        PrehravacHudbyZvukov.prehravacZvukov = MediaPlayer.create(this.context, this.hudba);
        PrehravacHudbyZvukov.prehravacHudby.setNextMediaPlayer(PrehravacHudbyZvukov.prehravacZvukov);
        PrehravacHudbyZvukov.prehravacHudby.setOnCompletionListener(this.koniecZvukovehoSuboru);
    }
}