/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.util.Duration;

/**
 *
 * @author Jake Yeo
 */
public class AlarmClock implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private static AlarmClock alarmCurrentlyUsing;
    private int hour;
    private int minute;
    private String amOrPm;
    private boolean enableAlarm = false;
    private Calendar timeToGoOff;
    static private Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> {
                System.out.println("checking");
                if (alarmCurrentlyUsing.timeToGoOff.compareTo(Calendar.getInstance()) <= 0) {
                    //alarmCurrentlyUsing.stopAlarmCheck();
                    try {
                        if (!Accounts.getLoggedInAccount().getListOfSongDataObjects().isEmpty()) {
                            MusicPlayerManager.getMpmCurrentlyUsing().smartPlay();
                        }
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    //This will ensure that the alarm continues to check the time even after it goes off, because the user may end up pausing the song
                    alarmCurrentlyUsing.setTimeForAlarmToGoOff();
                    System.out.println("Alarm");
                }
            }
    ));

    public AlarmClock() {

    }

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

    public Timeline getTimeline() {
        return timeline;
    }

    public Calendar getTimeToGoOff() {
        return timeToGoOff;
    }

    public boolean getEnableAlarm() {
        return enableAlarm;
    }

    public void setEnableAlarm(boolean tf) {
        enableAlarm = tf;
    }

    private void setTimeForAlarmToGoOff() {
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

    public void stopAlarmCheck() {
        getTimeline().stop();
    }

    public void startAlarmCheck() throws ParseException {
        setTimeForAlarmToGoOff();
        getTimeline().setCycleCount(Timeline.INDEFINITE);
        getTimeline().play();
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
