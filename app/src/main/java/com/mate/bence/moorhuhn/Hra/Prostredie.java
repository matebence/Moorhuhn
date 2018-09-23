package com.mate.bence.moorhuhn.Hra;

import android.os.Handler;
import android.util.Log;

public class Prostredie extends Thread {

    private static final String TAG = Prostredie.class.getName();
    private Handler prostredie;

    public Prostredie(Handler prostredie) {
        super();
        this.prostredie = prostredie;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(100);
            } catch (Exception e) {
                Log.v(TAG, "Pri generovanie prostredie hry doslo chybe");
            }
            this.prostredie.sendEmptyMessage(0);
        }
    }
}