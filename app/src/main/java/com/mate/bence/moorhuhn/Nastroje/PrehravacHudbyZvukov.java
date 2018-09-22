package com.mate.bence.moorhuhn.Nastroje;

import android.content.Context;
import android.media.MediaPlayer;


public class PrehravacHudbyZvukov {

    private Context context = null;
    private int hudba = 0;

    private static MediaPlayer mediaPlayer = null;
    private static MediaPlayer dalsiMediaPlayer = null;

    static public void spusti(Context context, int resId) {
        new PrehravacHudbyZvukov(context, resId);
    }

    static public void zastav() {
        mediaPlayer.stop();
        dalsiMediaPlayer.stop();
    }

    private PrehravacHudbyZvukov(Context context, int resId) {
        this.context = context;
        hudba = resId;
        mediaPlayer = MediaPlayer.create(this.context, hudba);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                PrehravacHudbyZvukov.mediaPlayer.start();
            }
        });
        vytvorDalsi();
    }

    private void vytvorDalsi() {
        dalsiMediaPlayer = MediaPlayer.create(context, hudba);
        mediaPlayer.setNextMediaPlayer(dalsiMediaPlayer);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            PrehravacHudbyZvukov.mediaPlayer = dalsiMediaPlayer;
            vytvorDalsi();
        }
    };
}