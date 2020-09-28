package com.kazimir.pedometer;

public class DayData {

    //date format YYYYMMDD
    private String date;
    private int steps;

    public DayData() {

    }

    public DayData(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }


    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getDistance(int stepLength) {
        float distance = this.steps * stepLength;
        return (distance / 100000);
    }


}
