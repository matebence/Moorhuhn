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

import com.mate.bence.moorhuhn.Prostredie.HlavneMenu;
import com.mate.bence.moorhuhn.Prostredie.ProstredieHry;
import com.mate.bence.moorhuhn.R;

import java.util.ArrayList;

public class Hra extends View {

    private static final String TAG = ProstredieHry.class.getName();
    public static final int DLZKA_HRY = 18000000;

    private Context context;
    private Paint pozadie;
    private Bitmap prostredie;

    private int hodnotaSpomalenieHry = nastavHodnoty(20, 40);
    private boolean spomalenieHry = false;

    private ArrayList<Moorhuhn> moorhuhn;
    private float[] suradniceHry = new float[2];
    private float maxSuradnicaX, maxSuradnicaY;
    private float suradnicaProstrediaX, suradnicProstrediaY;

    private int pocetNabojov = 8;
    private int pocetBodov = 0;

    private boolean nabijanie = false;
    private boolean koniec = false;

    private int aktualnyObrazok = -1;
    private int uplinutyCas = DLZKA_HRY;

    private int[] obrazokMoorhuna = {
            R.drawable.moorhuhn_lieta_01, R.drawable.moorhuhn_lieta_02, R.drawable.moorhuhn_lieta_03,
            R.drawable.moorhuhn_lieta_04, R.drawable.moorhuhn_lieta_04, R.drawable.moorhuhn_lieta_05,
            R.drawable.moorhuhn_lieta_06, R.drawable.moorhuhn_lieta_07, R.drawable.moorhuhn_lieta_08,
            R.drawable.moorhuhn_lieta_09, R.drawable.moorhuhn_lieta_10, R.drawable.moorhuhn_lieta_11,
            R.drawable.moorhuhn_lieta_12, R.drawable.moorhuhn_lieta_13};

    private int[] obrazokNaboja = {
            R.drawable.naboj_01, R.drawable.naboj_02, R.drawable.naboj_03, R.drawable.naboj_04, R.drawable.naboj_05,
            R.drawable.naboj_06, R.drawable.naboj_07, R.drawable.naboj_08, R.drawable.naboj_09, R.drawable.naboj_10,
            R.drawable.naboj_11, R.drawable.naboj_12, R.drawable.naboj_13, R.drawable.naboj_14, R.drawable.naboj_15,
            R.drawable.naboj_16, R.drawable.naboj_17, R.drawable.naboj_18, R.drawable.naboj_19, R.drawable.naboj_20,
            R.drawable.naboj_21};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Hra(Context context) {
        super(context);

        this.context = context;
        this.pozadie = new Paint();
        this.moorhuhn = new ArrayList<>();

        nastavProstredie();
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.prostredie, this.suradnicaProstrediaX, this.suradnicProstrediaY, this.pozadie);

        znazorniPocetBodov(canvas);
        znazorniNaboje(canvas, this.nabijanie);
        zostavajuciCas(canvas);

        Moorhuhn moorhuhn;
        for (int i = 0; i < this.moorhuhn.size(); i++) {
            moorhuhn = (this.moorhuhn.get(i));
            if (moorhuhn.getMrtvy()) {
                znazorniMrtvehoMoorhuna(canvas, moorhuhn);
            } else {
                znazorniMoorhuna(canvas, moorhuhn);
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

            if (!this.koniec) {
                if (this.pocetNabojov > 0) {
                    this.pocetNabojov--;
                    prehrajZvuk(new int[]{R.raw.strela});

                    for (int i = this.moorhuhn.size() - 1; i >= 0; i--) {
                        moorhuhn = (this.moorhuhn.get(i));
                        if (vykonajAkciu(poziciaTouchX, poziciaTouchY, moorhuhn.getX() + (moorhuhn.getVelkost() / 2), moorhuhn.getY() + (moorhuhn.getVelkost() / 2))) {
                            bodovySystem(moorhuhn.getVelkost());
                            moorhuhn.setMrtvy(true);
                            prehrajZvuk(new int[]{R.raw.trefa});
                        }
                    }
                } else {
                    prehrajZvuk(new int[]{R.raw.prazdne});
                }

            } else if (vykonajAkciu(poziciaTouchX, poziciaTouchY, this.suradniceHry[0], this.suradniceHry[1])) {
                this.koniec = false;
                this.spomalenieHry = false;

                this.uplinutyCas = DLZKA_HRY;
                this.pocetBodov = 0;
            }
            invalidate();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void nastavProstredie() {
        Bitmap obrazok = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.prostredie));
        ziskajSirku();

        this.prostredie = Bitmap.createScaledBitmap(obrazok, (int) (this.maxSuradnicaX + (this.maxSuradnicaX / 2)), (int) (this.maxSuradnicaY + (this.maxSuradnicaY / 2)), false);
        this.suradnicaProstrediaX = (-1) * (this.maxSuradnicaX / 4);
        this.suradnicProstrediaY = (-1) * (this.maxSuradnicaY / 4);
    }

    public void posuvajProstredieHry(double uhol, double kles, double stup, int posun) {
        if (uhol > 10 && kles > 0 && stup < 0) {

            this.suradnicaProstrediaX += 50;
            if (this.suradnicaProstrediaX > 0) {
                this.suradnicaProstrediaX = 0;
            } else {
                doPocitajPosun(posun);
            }
        } else if (uhol < -10 && kles > 0 && stup < 0) {

            this.suradnicaProstrediaX -= 50;
            if (this.suradnicaProstrediaX < (-1 * (this.maxSuradnicaX / 2))) {
                this.suradnicaProstrediaX = (-1 * (this.maxSuradnicaX / 2));
            } else {
                doPocitajPosun((-1) * posun);
            }
        }
    }

    private void doPocitajPosun(int hodnota) {
        Moorhuhn moorhuhn;

        if (this.moorhuhn.size() > 0) {
            for (int i = this.moorhuhn.size() - 1; i >= 0; i--) {
                moorhuhn = (this.moorhuhn.get(i));
                if (moorhuhn.getStart()) {
                    moorhuhn.spusti(hodnota);
                } else {
                    moorhuhn.spusti((-1) * hodnota);
                }
            }
        }
    }

    public void pridavajMoorhunov() {
        Moorhuhn moorhuhn;

        if (this.moorhuhn.size() < 10) {

            if (Math.random() > 0.9) {
                int rozmedzieY = nastavHodnoty(250, (int) this.maxSuradnicaY - 250);
                int rychlost = nastavHodnoty(10, 60);
                if (nastavHodnoty(0, 6) > 3) {
                    this.moorhuhn.add(new Moorhuhn(0, rozmedzieY, nastavHodnoty(130, 250), rychlost, true, false, false));
                } else {
                    this.moorhuhn.add(new Moorhuhn(this.maxSuradnicaX, rozmedzieY, nastavHodnoty(130, 250), rychlost, false, false, false));
                }
            }
        }

        if (this.moorhuhn.size() > 0) {
            for (int i = this.moorhuhn.size() - 1; i >= 0; i--) {
                moorhuhn = (this.moorhuhn.get(i));
                moorhuhn.spusti(0);
                if (moorhuhn.getX() > (this.maxSuradnicaX + (this.maxSuradnicaX / 2))) {
                    this.moorhuhn.remove(i);
                } else if (moorhuhn.getX() < 0) {
                    this.moorhuhn.remove(i);
                } else if (moorhuhn.getY() > this.maxSuradnicaY) {
                    this.moorhuhn.remove(i);
                }
            }
        }
    }

    private void znazorniMoorhuna(Canvas canvas, Moorhuhn moorhuhn) {
        @SuppressLint("DrawAllocation")
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), vratAktualnyObrazok(this.obrazokMoorhuna));
        Bitmap moorhuhnObrazok;

        if (moorhuhn.getStart()) {
            moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);
            canvas.drawBitmap(moorhuhnObrazok, moorhuhn.getX(), moorhuhn.getY(), this.pozadie);
        } else {
            Matrix zrkadlo = new Matrix();
            zrkadlo.preScale(-1, 1);
            moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);
            Bitmap dst = Bitmap.createBitmap(moorhuhnObrazok, 0, 0, moorhuhnObrazok.getWidth(), moorhuhnObrazok.getHeight(), zrkadlo, false);
            canvas.drawBitmap(dst, moorhuhn.getX(), moorhuhn.getY(), pozadie);
        }
    }

    private void znazorniMrtvehoMoorhuna(Canvas canvas, Moorhuhn moorhuhn) {
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.moorhuhn_mrtvy);
        Bitmap moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, moorhuhn.getVelkost(), moorhuhn.getVelkost(), false);

        canvas.drawBitmap(moorhuhnObrazok, moorhuhn.getX(), moorhuhn.getY(), pozadie);
    }

    public void rychlostMoorhunov(float hodnota) {
        if (this.pocetBodov > this.hodnotaSpomalenieHry) {
            if (!this.spomalenieHry) {
                Toast.makeText(this.context, "Možnosť spomalenie hry AKTÍVNA", Toast.LENGTH_LONG).show();
                this.spomalenieHry = true;
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

    private void zistiSmerZmenRychlost(boolean doprava, boolean dolava) {
        Moorhuhn moorhuhn;

        if (this.moorhuhn.size() > 0) {
            for (int i = this.moorhuhn.size() - 1; i >= 0; i--) {
                moorhuhn = this.moorhuhn.get(i);
                moorhuhn.setOriginal(true);
                if (moorhuhn.getStart()) {
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

    private void nastavSpatRychlost() {
        Moorhuhn moorhuhn;

        if (this.moorhuhn.size() > 0) {
            for (int i = this.moorhuhn.size() - 1; i >= 0; i--) {
                moorhuhn = this.moorhuhn.get(i);
                if (moorhuhn.getOriginal()) {
                    moorhuhn.setRychlost(nastavHodnoty(10, 60));
                }
            }
        }
    }

    private void znazorniNaboje(Canvas canvas, boolean nabijanie) {
        int velksot = 150;
        int posun = 0;
        Bitmap ziskajZdroj;

        if (nabijanie) {
            ziskajZdroj = BitmapFactory.decodeResource(getResources(), vratAktualnyObrazok(this.obrazokNaboja));
        } else {
            ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.naboj_01);
        }

        Bitmap naboj = Bitmap.createScaledBitmap(ziskajZdroj, velksot, velksot, false);
        for (int i = 0; i < this.pocetNabojov; i++) {
            canvas.drawBitmap(naboj, posun, this.maxSuradnicaY - velksot, this.pozadie);
            posun += 80;
        }
    }

    public void nabiZbran() {
        this.pocetNabojov = 8;
        this.nabijanie = true;

        prehrajZvuk(new int[]{R.raw.nabit});
    }

    private void znazorniPocetBodov(Canvas canvas) {
        this.pozadie.setTextSize(100);
        this.pozadie.setColor(Color.WHITE);

        canvas.drawText(Integer.toString(this.pocetBodov), 20, 90, pozadie);
    }

    private void zostavajuciCas(final Canvas canvas) {
        this.pozadie.setTextSize(100);
        this.pozadie.setColor(Color.WHITE);

        int velkost = 250;

        this.uplinutyCas -= 10000;
        int sekundy = (this.uplinutyCas / 100000);

        if (sekundy < 1) {
            this.pozadie.setTextSize(200);

            canvas.drawText("KONIEC HRY", (this.maxSuradnicaX / 2) - (int) (velkost * 2.3), this.maxSuradnicaY / 2, this.pozadie);
            dalsiPokus(canvas, velkost);

            if (!this.koniec) {
                prehrajZvuk(new int[]{R.raw.koniec});
            }

            this.koniec = true;
            this.moorhuhn.clear();
        } else {
            canvas.drawText(Integer.toString(sekundy) + "s", this.maxSuradnicaX - velkost, 90, this.pozadie);
        }
    }

    private void dalsiPokus(Canvas canvas, int velkost) {
        int velkostPisma = 150;
        Bitmap ziskajZdroj = BitmapFactory.decodeResource(getResources(), R.drawable.koniec_dalsi_pokus);
        Bitmap moorhuhnObrazok = Bitmap.createScaledBitmap(ziskajZdroj, velkost, velkost, false);

        canvas.drawBitmap(moorhuhnObrazok, (this.maxSuradnicaX / 2) - (velkost / 2), (this.maxSuradnicaY / 2) - (velkost / 2) + velkostPisma, this.pozadie);

        this.suradniceHry[0] = (this.maxSuradnicaX / 2);
        this.suradniceHry[1] = (this.maxSuradnicaY / 2) + (velkost / 2);
    }

    private void bodovySystem(int moorhuhn) {
        if (moorhuhn > 130 && moorhuhn < 150) {
            this.pocetBodov += 5;
        } else if (moorhuhn > 150 && moorhuhn < 170) {
            this.pocetBodov += 4;
        } else if (moorhuhn > 170 && moorhuhn < 190) {
            this.pocetBodov += 3;
        } else if (moorhuhn > 190 && moorhuhn < 220) {
            this.pocetBodov += 2;
        } else if (moorhuhn > 220 && moorhuhn < 250) {
            this.pocetBodov++;
        }
    }

    private void prehrajZvuk(int zvuky[]) {
        if (HlavneMenu.stavZvuku) {
            for (int zvuk : zvuky) {
                final MediaPlayer aktualnyZvuk = MediaPlayer.create(this.context, zvuk);
                aktualnyZvuk.start();
                aktualnyZvuk.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        aktualnyZvuk.release();
                    }
                });
            }
        }
    }

    private int vratAktualnyObrazok(int obrazky[]) {
        this.aktualnyObrazok++;
        if (this.aktualnyObrazok > obrazky.length - 1) {
            this.aktualnyObrazok = 0;
            this.nabijanie = false;
        }

        return obrazky[this.aktualnyObrazok];
    }

    private boolean vykonajAkciu(float aX, float aY, float bX, float bY) {
        return (Math.sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY))) < 30;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void ziskajSirku() {
        WindowManager sirka = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        assert sirka != null;
        Display display = sirka.getDefaultDisplay();
        Point velkost = new Point();

        display.getRealSize(velkost);
        this.maxSuradnicaX = velkost.x;
        display.getSize(velkost);
        this.maxSuradnicaY = velkost.y;
    }

    private int nastavHodnoty(int min, int max) {
        int pomer = (max - min) + 1;
        return (int) (Math.random() * pomer) + min;
    }
}