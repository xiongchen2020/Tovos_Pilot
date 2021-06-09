package com.tovos.uav.sample.welecome;

public class WelecomeAnimationBean {

    private int nowx = 0;
    private int movex = 0;
    private int nowy = 0;
    private int movey = 0;

    public WelecomeAnimationBean(int nowx, int movex, int nowy, int movey) {
        this.nowx = nowx;
        this.movex = movex;
        this.nowy = nowy;
        this.movey = movey;
    }

    public int getNowx() {
        return nowx;
    }


    public int getMovex() {
        return movex;
    }

    public int getNowy() {
        return nowy;
    }


    public int getMovey() {
        return movey;
    }

}
