package com.mate.bence.moorhuhn.Prostredie;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mate.bence.moorhuhn.Hra.Hra;
import com.mate.bence.moorhuhn.Hra.Prostredie;

public class ProstredieHry extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = ProstredieHry.class.getName();
    private final int POMER = 2150;

    private Hra hra;
    private Handler prostredieHry;

    private final int velkostPosunu = 50;
    private boolean nabyte = false;

    private SensorManager senzor;
    private Sensor accelorometer;
    private Sensor magnetometer;

    private float[] smerA = new float[16];
    private float[] smerB = new float[16];
    private float[] gravitacia = new float[3];
    private float[] pozicia = new float[3];
    private float[] orientacia = new float[3];

    private double uhol = 0;
    private double kles = 0;
    private double stup = 0;

    private long stavHodnout = 0;
    private float x, y, z;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spustiNaCelejObrazovke();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.senzor.registerListener(this, this.accelorometer, SensorManager.SENSOR_DELAY_FASTEST);
        this.senzor.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.senzor.unregisterListener(this, this.accelorometer);
        this.senzor.unregisterListener(this, this.magnetometer);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long cas = System.currentTimeMillis();
                if ((cas - this.stavHodnout) > 100) {

                    long aktualnaHodnota = (cas - this.stavHodnout);
                    this.stavHodnout = cas;
                    float hodnota = Math.abs(x + y + z - this.x - this.y - this.z) / aktualnaHodnota * 10000;

                    if (hodnota > this.POMER) {
                        this.nabyte = true;
                    }

                    this.x = x;
                    this.y = y;
                    this.z = z;
                }
                this.gravitacia = event.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                this.pozicia = event.values.clone();
                break;
        }
        if (this.gravitacia != null && this.pozicia != null) {
            if (SensorManager.getRotationMatrix(this.smerA, this.smerB, this.gravitacia, this.pozicia)) {
                SensorManager.getOrientation(this.smerA, this.orientacia);

                this.uhol = Math.toDegrees(this.orientacia[0]);
                this.kles = Math.toDegrees(this.orientacia[1]);
                this.stup = Math.toDegrees(this.orientacia[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void init() {
        this.senzor = (SensorManager) getSystemService(SENSOR_SERVICE);

        assert this.senzor != null;
        this.accelorometer = senzor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = senzor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        spustiGenerovanieHry();
        new Prostredie(this.prostredieHry).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void spustiNaCelejObrazovke() {
        @SuppressLint("InlinedApi")
        int vlastnosti = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(vlastnosti);

        this.hra = new Hra(this);
        setContentView(this.hra);
    }

    @SuppressLint("HandlerLeak")
    private void spustiGenerovanieHry() {
        this.prostredieHry = new Handler() {
            public void handleMessage(Message msg) {
                hra.posuvajProstredieHry(uhol, kles, stup, velkostPosunu);
                hra.pridavajMoorhunov();
                hra.rychlostMoorhunov(y);

                if (nabyte) {
                    nabyte = false;
                    hra.nabiZbran();
                }

                hra.invalidate();
                super.handleMessage(msg);
            }
        };
    }
}