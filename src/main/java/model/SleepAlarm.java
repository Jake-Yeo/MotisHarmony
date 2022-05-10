/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
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
    private int hoursTillSleep;
    private int minutesTillSleep;
    private Calendar timeToStopSong;
    static private Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> {
                System.out.println("checking");
                if (Calendar.getInstance().compareTo(sleepAlarmCurrentlyUsing.timeToStopSong) > 0) {
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

    public SleepAlarm(int hoursTillSleep, int minsTillSleep ) {
        this.hoursTillSleep = hoursTillSleep;
        this.minutesTillSleep = minsTillSleep;
    }

    public void setTimeForSleepAlarmToGoOff(int hoursTillSleep, int minsTillSleep) {
        this.hoursTillSleep = hoursTillSleep;
        this.minutesTillSleep = minsTillSleep;
        timeToStopSong = Calendar.getInstance();
        timeToStopSong.add(Calendar.HOUR_OF_DAY,  this.hoursTillSleep);
        timeToStopSong.add(Calendar.MINUTE, this.minutesTillSleep);
//setHour();
    }

    @Override
    public Timeline getTimeline() {
        return timeline;
    }

}
