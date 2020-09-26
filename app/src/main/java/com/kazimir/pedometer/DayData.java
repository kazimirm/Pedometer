package com.kazimir.pedometer;

public class DayData {

    //date format YYYYMMDD
    private String date;
    private int steps;
    private float distance;
    private int STEP_SIZE = 70;

    public DayData() {

    }

    public DayData(String date, int steps) {
        this.date = date;
        this.steps = steps;
        this.distance = steps * (STEP_SIZE / 100000);
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
}
