/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author 1100007967
 */
public class SleepAlarm extends AlarmClock implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private static SleepAlarm sleepAlarmCurrentlyUsing;
    static private Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> {
                System.out.println("checking");
                if (Calendar.getInstance().compareTo(sleepAlarmCurrentlyUsing.getTimeToGoOff()) > 0) {
                    sleepAlarmCurrentlyUsing.stopAlarmCheck();
                    try {
                        MusicPlayerManager.getMpmCurrentlyUsing().getMediaPlayer().stop();
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    System.out.println("Sleeping");
                }
            }
    ));
    private int hoursTillSleep;
    private int minutesTillSleep;

    public SleepAlarm(int hoursTillSleep, int minutesTillSleep) {
        this.hoursTillSleep = hoursTillSleep;
        this.minutesTillSleep = minutesTillSleep;
    }

    public void setTimeForSleepAlarmToGoOff() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.get(Calendar.HOUR_OF_DAY);
//setHour();
    }

    @Override
    public Timeline getTimeline() {
        return timeline;
    }

}
