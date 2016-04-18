package com.zovsky.labyrinth;

import java.util.ArrayList;

public class Creature {

    private int mL;
    private int mV;
    private int mU;
    private String mElixir;
    private ArrayList<String> mThings = new ArrayList<String>();
    private int mGold;
    private int mFood;
    private ArrayList<Integer> mKeys = new ArrayList<Integer>();

    public int getmL() {
        return mL;
    }

    public void setmL(int mL) {
        this.mL = mL;
    }

    public int getmV() {
        return mV;
    }

    public void setmV(int mV) {
        this.mV = mV;
    }

    public int getmU() {
        return mU;
    }

    public void setmU(int mU) {
        this.mU = mU;
    }

    public String getmElixir() {
        return mElixir;
    }

    public void setmElixir(String mElixir) {
        this.mElixir = mElixir;
    }

    public int getmGold() {
        return mGold;
    }

    public void setmGold(int mGold) {
        this.mGold = mGold;
    }

    public int getmFood() {
        return mFood;
    }

    public void setmFood(int mFood) {
        this.mFood = mFood;
    }

    public ArrayList<Integer> getmKeys() {
        return mKeys;
    }

    public Creature() {
        mL = 0;
        mV = 0;
        mU = 0;
        mGold = 0;
        mFood = 0;
        mKeys.clear();
        mThings.clear();
        mElixir = "";
    }





}


