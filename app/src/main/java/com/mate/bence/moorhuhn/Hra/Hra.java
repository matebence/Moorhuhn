package com.mate.bence.moorhuhn.Hra;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mate.bence.moorhuhn.Prostredie.ProstredieHry;
import com.mate.bence.moorhuhn.R;

import java.util.ArrayList;

public class Hra extends View {

    private static final String TAG = ProstredieHry.class.getName();

    public static final int DLZKA_HRY = 18000000;
    private Context context;

    private Paint kresli;
    private Bitmap pozadie;

    private int slowMove = nastavHodnoty(20, 40);
    private boolean slowMoveStatus = false;

    private ArrayList<Moorhuhn> pocetMoorhunov;
    private float[] suradniceKoniec = new float[2];
    private float maxX, maxY;
    private float pozadieX, pozadieY;

    private int pocetNabojov = 8;
    private int pocetBodov = 0;
    private int aktualnyStavObrazkov = -1;
    private int cas = DLZKA_HRY;

    private boolean nabijanie = false;
    private boolean koniec = false;

    private int[] moorhuhnDoprava = {
            R.drawable.moorhuhn_lieta_doprava01, R.drawable.moorhuhn_lieta_doprava02, R.drawable.moorhuhn_lieta_doprava03,
            R.drawable.moorhuhn_lieta_doprava04, R.drawable.moorhuhn_lieta_doprava04, R.drawable.moorhuhn_lieta_doprava05,
            R.drawable.moorhuhn_lieta_doprava06, R.drawable.moorhuhn_lieta_doprava07, R.drawable.moorhuhn_lieta_doprava08,
            R.drawable.moorhuhn_lieta_doprava09, R.drawable.moorhuhn_lieta_doprava10, R.drawable.moorhuhn_lieta_doprava11,
            R.drawable.moorhuhn_lieta_doprava12, R.drawable.moorhuhn_lieta_doprava13};
    private int[] naboje = {
            R.drawable.naboj01, R.drawable.naboj02, R.drawable.naboj03, R.drawable.naboj04, R.drawable.naboj05,
            R.drawable.naboj06, R.drawable.naboj07, R.drawable.naboj08, R.drawable.naboj09, R.drawable.naboj10,
            R.drawable.naboj11, R.drawable.naboj12, R.drawable.naboj13, R.drawable.naboj14, R.drawable.naboj15,
            R.drawable.naboj16, R.drawable.naboj17, R.drawable.naboj18, R.drawable.naboj19, R.drawable.naboj20,
            R.drawable.naboj21};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Hra(Context context) {
        super(context);
        this.context = context;
        this.kresli = new Paint();
        this.pocetMoorhunov = new ArrayList<>();
        NastavPozadie();
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(pozadie, pozadieX, pozadieY, kresli);
        znazorniPocetBodov(canvas);
        znazorniNaboje(canvas, nabijanie);
        zostavajuciCas(canvas);
        Moorhuhn moorhuhn;
        for (int i = 0; i < pocetMoorhunov.size(); i++) {
            moorhuhn = (pocetMoorhunov.get(i));
            if (moorhuhn.getMrtvy()) {
                znazorniMrtvehoMoorhuna(canvas, moorhuhn);
            } else {
                znazorniMoorhuna(canvas, moorhuhn);
            }
        }
    }

    public void pridavajMoorhunov() {
        Moorhuhn moorhuhn;
        if (pocetMoorhunov.size() < 10) {
            if (Math.random() > 0.9) {
                int rozmedzieY = nastavHodnoty(250, (int) maxY - 250);
                int rychlost = nastavHodnoty(10, 60);
                if (nastavHodnoty(0, 6) > 3) {
                    pocetMoorhunov.add(new Moorhuhn(0, rozmedzieY, nastavHodnoty(130, 250), true, false, false, rychlost));
                } else {
                    pocetMoorhunov.add(new Moorhuhn(maxX, rozmedzieY, nastavHodnoty(130, 250), false, false, false, rychlost));
                }
            }
        }
        if (pocetMoorhunov.size() > 0) {
            for (int i = pocetMoorhunov.size() - 1; i >= 0; i--) {
                moorhuhn = (pocetMoorhunov.get(i));
                moorhuhn.Pohni(0);
                if (moorhuhn.getX() > (maxX + (maxX / 2))) {
                    pocetMoorhunov.remove(i);
                } else if (moorhuhn.getX() < 0) {
                    pocetMoorhunov.remove(i);
                } else if (moorhuhn.getY() > maxY) {
                    pocetMoorhunov.remove(i);
                }
            }
        }
    }

    private void znazorniMoorhuna(Canvas canvas, Moorhuhn moorhuhn) {
        @SuppressLint("DrawAllocation")
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), vratAktualnyObrazok(moorhuhnDoprava));
        Bitmap moorhuhnObrazok;
        if (moorhuhn.getDoprava()) {
            moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);
            canvas.drawBitmap(moorhuhnObrazok, moorhuhn.getX(), moorhuhn.getY(), kresli);
        } else {
            Matrix zrkadlo = new Matrix();
            zrkadlo.preScale(-1, 1);
            moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);
            Bitmap dst = Bitmap.createBitmap(moorhuhnObrazok, 0, 0, moorhuhnObrazok.getWidth(), moorhuhnObrazok.getHeight(), zrkadlo, false);
            canvas.drawBitmap(dst, moorhuhn.getX(), moorhuhn.getY(), kresli);
        }
    }

    private void znazorniMrtvehoMoorhuna(Canvas canvas, Moorhuhn moorhuhn) {
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.moorhuhn_mrtvy);
        Bitmap moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);
        canvas.drawBitmap(moorhuhnObrazok, moorhuhn.getX(), moorhuhn.getY(), kresli);
    }

    private void znazorniNaboje(Canvas canvas, boolean nabijanie) {
        int velksot = 150;
        int posun = 0;
        Bitmap ziskajZdroj;
        if (nabijanie) {
            ziskajZdroj = BitmapFactory.decodeResource(getResources(), vratAktualnyObrazok(naboje));
        } else {
            ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.naboj01);
        }
        Bitmap naboj = Bitmap.createScaledBitmap(ziskajZdroj, velksot, velksot, false);
        for (int i = 0; i < pocetNabojov; i++) {
            canvas.drawBitmap(naboj, posun, maxY - velksot, kresli);
            posun += 80;
        }
    }

    private void znazorniPocetBodov(Canvas canvas) {
        kresli.setTextSize(100);
        kresli.setColor(Color.WHITE);
        canvas.drawText(Integer.toString(pocetBodov), 20, 90, kresli);
    }

    private void zostavajuciCas(final Canvas canvas) {
        kresli.setTextSize(100);
        kresli.setColor(Color.WHITE);
        int velkost = 250;

        cas -= 10000;
        int sekundy = (cas / 100000);

        if (sekundy < 1) {
            kresli.setTextSize(200);
            canvas.drawText("KONIEC HRY", (maxX / 2) - (int) (velkost * 2.3), maxY / 2, kresli);
            esteRaz(canvas, velkost);

            if (!koniec) {
                prehrajZvuk(new int[]{R.raw.koniec});
            }
            koniec = true;
            pocetMoorhunov.clear();
        } else {
            canvas.drawText(Integer.toString(sekundy) + "s", maxX - velkost, 90, kresli);
        }
    }

    private void esteRaz(Canvas canvas, int velkost) {
        int velkostPisma = 150;
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.este_raz);
        Bitmap moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, velkost, velkost, false);
        canvas.drawBitmap(moorhuhnObrazok, (maxX / 2) - (velkost / 2), (maxY / 2) - (velkost / 2) + velkostPisma, kresli);
        suradniceKoniec[0] = (maxX / 2);
        suradniceKoniec[1] = (maxY / 2) + (velkost / 2);
    }

    public void posuvajProstredieHry(double azimuth, double pitch, double roll, int posun) {
        if (azimuth > 10 && pitch > 0 && roll < 0) {
            pozadieX += 50;
            if (pozadieX > 0) {
                pozadieX = 0;
            } else {
                doPocitajPosun(posun);
            }
        } else if (azimuth < -10 && pitch > 0 && roll < 0) {
            pozadieX -= 50;
            if (pozadieX < (-1 * (maxX / 2))) {
                pozadieX = (-1 * (maxX / 2));
            } else {
                doPocitajPosun((-1) * posun);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void NastavPozadie() {
        Bitmap obrazok = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.pozadie));
        ziskajSirku();
        this.pozadie = Bitmap.createScaledBitmap(obrazok, (int) (maxX + (maxX / 2)), (int) (maxY + (maxY / 2)), false);
        pozadieX = (-1) * (maxX / 4);
        pozadieY = (-1) * (maxY / 4);
    }

    private void doPocitajPosun(int hodnota) {
        Moorhuhn moorhuhn;
        if (pocetMoorhunov.size() > 0) {
            for (int i = pocetMoorhunov.size() - 1; i >= 0; i--) {
                moorhuhn = (pocetMoorhunov.get(i));
                if (moorhuhn.getDoprava()) {
                    moorhuhn.Pohni(hodnota);
                } else {
                    moorhuhn.Pohni((-1) * hodnota);
                }
            }
        }
    }

    public void rychlostMoorhunov(float hodnota) {
        if (pocetBodov > slowMove) {
            if (!slowMoveStatus) {
                Toast.makeText(context, "Slow move bolo aktivovane", Toast.LENGTH_LONG).show();
                slowMoveStatus = true;
            }
            if (hodnota > 5) {
                zistiSmerZmenRychlost(true, false);
            } else if (hodnota < -5) {
                zistiSmerZmenRychlost(false, true);
            } else {
                nastavSpatRychlost();
            }
        }
    }

    private void nastavSpatRychlost() {
        Moorhuhn moorhuhn;
        if (pocetMoorhunov.size() > 0) {
            for (int i = pocetMoorhunov.size() - 1; i >= 0; i--) {
                moorhuhn = pocetMoorhunov.get(i);
                if (moorhuhn.getZmenene()) {
                    moorhuhn.setRychlost(nastavHodnoty(10, 60));
                }
            }
        }
    }

    private void zistiSmerZmenRychlost(boolean doprava, boolean dolava) {
        Moorhuhn moorhuhn;
        if (pocetMoorhunov.size() > 0) {
            for (int i = pocetMoorhunov.size() - 1; i >= 0; i--) {
                moorhuhn = pocetMoorhunov.get(i);
                moorhuhn.setZmenene(true);
                if (moorhuhn.getDoprava()) {
                    if (doprava && !dolava) {
                        moorhuhn.setRychlost(60);
                    } else {
                        moorhuhn.setRychlost(10);
                    }
                } else {
                    if (doprava && !dolava) {
                        moorhuhn.setRychlost(10);
                    } else {
                        moorhuhn.setRychlost(60);
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        Moorhuhn moorhuhn;
        int e = event.getAction();
        if (e == MotionEvent.ACTION_DOWN) {
            float poziciaTouchX = event.getX();
            float poziciaTouchY = event.getY();
            if (!koniec) {
                if (pocetNabojov > 0) {
                    pocetNabojov--;
                    prehrajZvuk(new int[]{R.raw.strela});
                    for (int i = pocetMoorhunov.size() - 1; i >= 0; i--) {
                        moorhuhn = (pocetMoorhunov.get(i));
                        if (vykonajAkciu(poziciaTouchX, poziciaTouchY, moorhuhn.getX() + (moorhuhn.getVelkost() / 2), moorhuhn.getY() + (moorhuhn.getVelkost() / 2))) {
                            bodovySystem(moorhuhn.getVelkost());
                            moorhuhn.setMrtvy(true);
                            prehrajZvuk(new int[]{R.raw.trefa});
                        }
                    }
                } else {
                    prehrajZvuk(new int[]{R.raw.prazdne});
                }

            } else if (koniec && vykonajAkciu(poziciaTouchX, poziciaTouchY, suradniceKoniec[0], suradniceKoniec[1])) {
                koniec = false;
                cas = DLZKA_HRY;
                pocetBodov = 0;
                slowMoveStatus = false;
            }
            invalidate();
        }
        return true;
    }

    private int vratAktualnyObrazok(int poleObrazkov[]) {
        aktualnyStavObrazkov++;
        if (aktualnyStavObrazkov > poleObrazkov.length - 1) {
            aktualnyStavObrazkov = 0;
            nabijanie = false;
        }
        return poleObrazkov[aktualnyStavObrazkov];
    }

    public void nabiZbran() {
        pocetNabojov = 8;
        nabijanie = true;
        prehrajZvuk(new int[]{R.raw.nabit});
    }

    private void bodovySystem(int velkost) {
        if (velkost > 130 && velkost < 150) {
            pocetBodov += 5;
        } else if (velkost > 150 && velkost < 170) {
            pocetBodov += 4;
        } else if (velkost > 170 && velkost < 190) {
            pocetBodov += 3;
        } else if (velkost > 190 && velkost < 220) {
            pocetBodov += 2;
        } else if (velkost > 220 && velkost < 250) {
            pocetBodov++;
        }
    }

    private boolean vykonajAkciu(float aX, float aY, float bX, float bY) {
        return (Math.sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY))) < 30;
    }

    private void prehrajZvuk(int zvuky[]) {
        if (HlavneMenuActivity.zvuky) {
            for (int zvuk : zvuky) {
                final MediaPlayer player = MediaPlayer.create(context, zvuk);
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        player.release();
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void ziskajSirku() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        Point velkost = new Point();
        display.getRealSize(velkost);
        maxX = velkost.x;
        display.getSize(velkost);
        maxY = velkost.y;
    }

    private int nastavHodnoty(int min, int max) {
        int medzi = (max - min) + 1;
        return (int) (Math.random() * medzi) + min;
    }
}