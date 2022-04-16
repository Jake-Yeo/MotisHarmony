/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author Jake Yeo
 */
public class AlarmClock {

    private static AlarmClock alarmCurrentlyUsing;

    private int hour;
    private int minute;
    private String amOrPm;
    private Calendar timeToGoOff;

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

    public void setTimeForAlarmToGoOff() {
        DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy-HH-mm");
        Calendar currentDate = Calendar.getInstance();
        String[] stringMonthDayYearHourMinute = dateFormatter.format(currentDate.getTime()).split("-");
        int[] monthDayYearHourMinute = new int[stringMonthDayYearHourMinute.length];
        for (int i = 0; i < monthDayYearHourMinute.length; i++) {
            monthDayYearHourMinute[i] = Integer.parseInt(stringMonthDayYearHourMinute[i]);
        }
        Calendar futureAlarmDate = Calendar.getInstance();
        //Subtract one from the months because January starts from index 0
        if (amOrPm.equals("PM")) {
            futureAlarmDate.set(monthDayYearHourMinute[2], monthDayYearHourMinute[0] - 1, monthDayYearHourMinute[1], hour + 12, minute, 0);
        } else {
            futureAlarmDate.set(monthDayYearHourMinute[2], monthDayYearHourMinute[0] - 1, monthDayYearHourMinute[1], hour, minute, 0);
        }
        if (futureAlarmDate.getTimeInMillis() < currentDate.getTimeInMillis()) {
            futureAlarmDate.set(monthDayYearHourMinute[2], monthDayYearHourMinute[0] - 1, monthDayYearHourMinute[1] + 1);
        }
        timeToGoOff = futureAlarmDate;
        System.out.println(Arrays.toString(monthDayYearHourMinute));
        System.out.println(dateFormatter.format(currentDate.getTime()));
        System.out.println(dateFormatter.format(futureAlarmDate.getTime()));
    }

    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> {
                if (timeToGoOff.compareTo(Calendar.getInstance()) <= 0) {
                    stopAlarmCheck();
                    try {
                        MusicPlayerManager.getMpmCurrentlyUsing().smartPlay();
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    System.out.println("Alarm");
                }
            }
    ));

    public void stopAlarmCheck() {
        timeline.stop();
    }

    public void startAlarmCheck() throws ParseException {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
