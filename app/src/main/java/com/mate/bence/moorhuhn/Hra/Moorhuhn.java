package com.mate.bence.moorhuhn.Hra;

class Moorhuhn {

    private float x;
    private float y;

    private int velkost;
    private int rychlost;

    private boolean start;
    private boolean original;
    private boolean mrtvy;

    Moorhuhn(float x, float y, int velkost, int rychlost, boolean start, boolean original, boolean mrtvy) {
        this.x = x;
        this.y = y;

        this.velkost = velkost;
        this.rychlost = rychlost;

        this.start = start;
        this.original = original;
        this.mrtvy = mrtvy;
    }

    float getX() {
        return this.x;
    }

    float getY() {
        return this.y;
    }

    int getVelkost() {
        return this.velkost;
    }

    boolean getStart() {
        return this.start;
    }

    boolean getOriginal() {
        return this.original;
    }

    boolean getMrtvy() {
        return this.mrtvy;
    }

    void setOriginal(boolean original) {
        this.original = original;
    }

    void setMrtvy(boolean mrtvy) {
        this.mrtvy = mrtvy;
    }

    void setRychlost(int rychlost) {
        this.rychlost = rychlost;
    }

    void spusti(int pozicia) {
        if (this.start) {
            if (!this.mrtvy) {
                this.x = (this.x + pozicia) + this.rychlost;
            } else {
                this.y = this.y + 25;
            }
        } else {
            if (!this.mrtvy) {
                this.x = (this.x - pozicia) - this.rychlost;
            } else {
                this.y = this.y + 25;
            }
        }
    }
}