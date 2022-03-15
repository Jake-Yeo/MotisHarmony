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
import java.util.Comparator;
import javafx.scene.media.Media;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    private static boolean paused = false;
    private static int indexForOrderedPlay = 0;
    private static boolean musicPlayerInitalized = false;
    private static int volume;
    private static String playType = "Ordered Play";
    private static SongDataObject songObjectBeingPlayed;
    private static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash
    private static ObservableList<SongDataObject> currentSongList = FXCollections.observableArrayList();

    public static String getPlayType() {
        return playType;
    }

    public static void setPlayType(String type) {
        playType = type;
    }

    public static void updatePlayTypeAtEndOfMedia() {
        if (playType.equals("Random Play")) {
            mediaPlayer.setOnEndOfMedia(() -> {
                try {
                    randomPlay();
                } catch (IOException ex) {
                    Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else if (playType.equals("Ordered Play")) {
            mediaPlayer.setOnEndOfMedia(() -> {
                try {
                    orderedPlay();
                } catch (IOException ex) {
                    Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public static void play() throws IOException {
        if (playType.equals("Random Play")) {
            randomPlay();
        } else if (playType.equals("Ordered Play")) {
            orderedPlay();
        }
    }

    public static void randomPlay() throws IOException {
        ObservableList<SongDataObject> songDataObjects = currentSongList;
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
        updatePlayTypeAtEndOfMedia();
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }

    public static void setIndexForOrderedPlay(int index) {
        indexForOrderedPlay = index;
    }

    public static void orderedPlay() throws IOException {
        if (indexForOrderedPlay > currentSongList.size() - 1) {
            indexForOrderedPlay = 0;
        }
        ObservableList<SongDataObject> songDataObjects = currentSongList;
        songObjectBeingPlayed = songDataObjects.get(indexForOrderedPlay);
        File file = new File(songDataObjects.get(indexForOrderedPlay).getPathToWavFile());//replace with correct path when testing
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        indexForOrderedPlay++;
        updatePlayTypeAtEndOfMedia();
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }

    public static void playSong(SongDataObject songToPlay) {
        songObjectBeingPlayed = songToPlay;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        File file = new File(songToPlay.getPathToWavFile());//replace with correct path when testing
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            try {
                randomPlay();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        mediaPlayer.play();
    }

    public static void nextSong() throws IOException {
        mediaPlayer.stop();
        play();
    }

    public static void pauseSong() {
        mediaPlayer.pause();
        paused = true;
    }

    public static void resumeSong() {
        mediaPlayer.play();
        paused = false;
    }

    public static void setPaused(boolean tf) {
        paused = tf;
    }

    public static ObservableList<SongDataObject> getCurrentSongList() {
        return currentSongList;
    }

    public static void updateSongList(ArrayList<SongDataObject> sdota) {
        MusicPlayerManager.getCurrentSongList().clear();
        MusicPlayerManager.getCurrentSongList().addAll(sdota);
    }

    public static void sortCurrentSongList(String sortType) {
        if (sortType.equals("A-Z")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getTitle().compareTo(secondSdo.getTitle()) < 0) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getTitle().compareTo(secondSdo.getTitle()) > 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        } else if (sortType.equals("Z-A")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getTitle().compareTo(secondSdo.getTitle()) > 0) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getTitle().compareTo(secondSdo.getTitle()) < 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        } else if (sortType.equals("A-Z By Artist")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getChannelName().compareTo(secondSdo.getChannelName()) < 0) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getChannelName().compareTo(secondSdo.getChannelName()) > 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();

        } else if (sortType.equals("Z-A By Artist")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getChannelName().compareTo(secondSdo.getChannelName()) > 0) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getChannelName().compareTo(secondSdo.getChannelName()) < 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        } else if (sortType.equals("Oldest Added")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getOrderAdded() < secondSdo.getOrderAdded()) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getOrderAdded() > secondSdo.getOrderAdded()) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        } else if (sortType.equals("Newest Added")) {
            FXCollections.sort(MusicPlayerManager.getCurrentSongList(), new Comparator() {
                @Override
                public int compare(Object sdo1, Object sdo2) {
                    SongDataObject firstSdo = (SongDataObject) sdo1;
                    SongDataObject secondSdo = (SongDataObject) sdo2;
                    int returnValue;
                    if (firstSdo.getOrderAdded() > secondSdo.getOrderAdded()) {
                        returnValue = 0;
                    } else {
                        if (firstSdo.getOrderAdded() < secondSdo.getOrderAdded()) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        }
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

    public static boolean isSongPaused() {
        return paused;
    }

    public static boolean isMusicPlayerInitialized() {
        return musicPlayerInitalized;
    }

    public static void setMusicPlayerInitialized(boolean tf) {
        musicPlayerInitalized = tf;
    }

    public static void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    public static void seekTo(Duration duration) {
        mediaPlayer.seek(duration);
    }

    public static double getTotalDurationInSeconds() {
        return mediaPlayer.getTotalDuration().toSeconds();
    }

    public static double getCurrentTimeInSeconds() {
        return mediaPlayer.getCurrentTime().toSeconds();
    }
}
