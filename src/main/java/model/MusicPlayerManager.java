/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.media.Media;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    private static int volume;
    private static SongDataObject songObjectBeingPlayed;
    public static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash

    public static void playMusic() throws IOException {
        ArrayList<SongDataObject> songDataObjects = Accounts.getLoggedInAccount().getListOfSongDataObjects();
        //String[] musicPaths = new String(Files.readAllBytes(PathsManager.getLoggedInUserSongsTxtPath())).split(System.lineSeparator());
        //System.out.println(Arrays.toString(musicPaths));
        Random randomNumGen = new Random();
        System.out.println(songDataObjects.size());
        int indexOfNextSongToPlay = randomNumGen.nextInt(songDataObjects.size());
        songObjectBeingPlayed = songDataObjects.get(indexOfNextSongToPlay);
        File file = new File(songDataObjects.get(indexOfNextSongToPlay).getPathToWavFile());//replace with correct path when testing
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            try {
                playMusic();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }

    public static void nextSong() throws IOException {
        mediaPlayer.stop();
        playMusic();
    }

    public static void pauseSong() {
        mediaPlayer.pause();
    }

    public static void resumeSong() {
        mediaPlayer.play();
    }

    public static double getCurrentSongDurationInSec() {
        return mediaPlayer.getTotalDuration().toSeconds();
    }
    
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public static double getVolume() {
        return mediaPlayer.getVolume();
    }
    
    public static SongDataObject getSongObjectBeingPlayed() {
        return songObjectBeingPlayed;
    }
}
