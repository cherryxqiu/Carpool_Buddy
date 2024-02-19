package com.example.carpoolbuddy.carpoolbuddy.models;

public class CTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    public CTime(){

    }
    public CTime(int y, int m, int d, int h, int mi) {
        this.year = y;
        this.month=m;
        this.day=d;
        this.hour = h;
        this.minute = mi;

    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String toString() {
        return day+"/"+month+"/"+year+" "+hour+":"+minute;
    }
}
