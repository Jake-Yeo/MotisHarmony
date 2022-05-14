/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author 1100007967
 */
public class SleepTimer implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private static SleepTimer sleepTimerCurrentlyUsing;
    private int hoursTillSleep;
    private int minutesTillSleep;
    private boolean enableTimer = false;
    private Calendar timeToStopSong;
    static private Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> {
                System.out.println("checking");
                if (Calendar.getInstance().compareTo(sleepTimerCurrentlyUsing.timeToStopSong) > 0) {
                    sleepTimerCurrentlyUsing.stopTimerCheck();
                    //We must set wether or not the timer is enabled with the account settings so that the settings are saved no matter what!
                    Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer().setEnableTimer(false);
                    if (MusicPlayerManager.getMpmCurrentlyUsing().getMediaPlayer() != null) {
                        try {
                            MusicPlayerManager.getMpmCurrentlyUsing().getMediaPlayer().pause();
                        } catch (Exception i) {
                            i.printStackTrace();
                        }
                    }
                    System.out.println("Sleeping");
                }
            }
    ));

    public SleepTimer(int hoursTillSleep, int minsTillSleep) {
        this.hoursTillSleep = hoursTillSleep;
        this.minutesTillSleep = minsTillSleep;
    }

    public boolean getEnableTimer() {
        return enableTimer;
    }

    public void setEnableTimer(boolean tf) {
        enableTimer = tf;
    }

    public void setTimeForTimerToGoOff() {
        timeToStopSong = Calendar.getInstance();
        timeToStopSong.add(Calendar.HOUR_OF_DAY, this.hoursTillSleep);
        timeToStopSong.add(Calendar.MINUTE, this.minutesTillSleep);
//setHour();
    }

    public static void setTimerCurrentlyUsing(SleepTimer st) {
        sleepTimerCurrentlyUsing = st;
    }

    public static SleepTimer getTimerCurrentlyUsing() {
        return sleepTimerCurrentlyUsing;
    }

    public void setHour(int h) {
        hoursTillSleep = h;
    }

    public void stopTimerCheck() {
        getTimeline().stop();
    }

    public void startTimerCheck() throws ParseException {
        setTimeForTimerToGoOff();
        getTimeline().setCycleCount(Timeline.INDEFINITE);
        getTimeline().play();
    }

    public void setMinute(int m) {
        minutesTillSleep = m;
    }

    public int getMinute() {
        return minutesTillSleep;
    }

    public int getHour() {
        return hoursTillSleep;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

}
