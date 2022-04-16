/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jake Yeo
 */
public class AlarmClock {

    private static AlarmClock alarmCurrentlyUsing;

    private int hour;
    private int minute;
    private String amOrPm;
    private long timeToGoOff;

    public AlarmClock(int hour, int min, String amOrPm) {
        this.hour = hour;
        this.minute = min;
        this.amOrPm = amOrPm;
    }

    public static AlarmClock getAlarmCurrentlyUsing() {
        return alarmCurrentlyUsing;
    }

    public static void setAlarmCurrentlyUsing(AlarmClock ac) {
        alarmCurrentlyUsing = ac;
    }

    public void checkAlarm() {
        Calendar cal = Calendar.getInstance();
        System.out.println("current date: " + cal.getTime());
        cal.add(Calendar.MINUTE, 7);
        System.out.println("7 days later: " + cal.getTime());
        
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getAmOrPm() {
        return amOrPm;
    }

    public void setAmOrPm(String amOrPm) {
        this.amOrPm = amOrPm;
    }
}
