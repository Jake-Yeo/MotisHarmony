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
    private static boolean playSongInLoop = false;
    private static int volume;
    private static String playType = "Ordered Play";
    private static SongDataObject songObjectBeingPlayed;
    private static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash
    private static ObservableList<SongDataObject> currentSongList = FXCollections.observableArrayList();
    private static ObservableList<SongDataObject> playlistSongsPlaying = FXCollections.observableArrayList();
    private static ArrayList<SongDataObject> songHistory = new ArrayList<>();
    private static int posInSongHistory = 0;
    private static String currentPlaylistPlayling;
    private static double sliderVolume = Accounts.getLoggedInAccount().getSettingsObject().getPrefVolume();

    public static void setSongObjectBeingPlayed(SongDataObject sdo) throws Exception {
        songObjectBeingPlayed = sdo;
        if (Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition()) {
            AccountsDataManager.setLastSongPlayed(sdo);
        }
    }

    public static SongDataObject getSongObjectBeingPlayed() {
        return songObjectBeingPlayed;
    }

    public static boolean getPlaySongInLoop() {
        return playSongInLoop;
    }

    public static void setPlaySongInLoop(boolean tf) {
        playSongInLoop = tf;
    }

    public static double getSliderVolume() {
        return sliderVolume;
    }

    public static void setSliderVolume(double volume) {
        sliderVolume = volume;
    }

    public static ArrayList<SongDataObject> getSongHistory() {
        return songHistory;
    }

    public static String[] getArrayOfSongInfoInCurrentSongList() {
        String[] arrayOfSongNames = new String[getCurrentSongList().size()];
        for (int i = 0; i < arrayOfSongNames.length; i++) {
            arrayOfSongNames[i] = MusicPlayerManager.getCurrentSongList().get(i).getTitle() + "\nBy: " + MusicPlayerManager.getCurrentSongList().get(i).getChannelName();
        }
        return arrayOfSongNames;
    }

    public static int getPosInSongHistory() {
        return posInSongHistory;
    }

    public static void setPosInSongHistory(int value) {
        posInSongHistory = value;
    }

    public static SongDataObject[] getArrayOfSdoFromCurrentSongListViaIndicies(ObservableList<Integer> indicies) {
        SongDataObject[] sdoGotten = new SongDataObject[indicies.size()];
        for (int i = 0; i < sdoGotten.length; i++) {
            sdoGotten[i] = currentSongList.get(indicies.get(i));
        }
        return sdoGotten;
    }

    public static void playThisPlaylist(String playlistName) throws Exception {
        //This will set which songs from which playlist to play next after the song which is currently playing has finsihed
        if (MusicPlayerManager.getCurrentPlaylistPlayling().equals(playlistName)) {
            return;
        }
        MusicPlayerManager.getSongHistory().clear();
        MusicPlayerManager.setCurrentPlaylistPlayling(playlistName);
        MusicPlayerManager.setIndexForOrderedPlay(0);
        MusicPlayerManager.syncPlaylistSongsPlaylingWithCurentSongsList();
    }

    public static void setCurrentPlaylistPlayling(String playlistName) throws Exception {
        if (playlistName != null) {
            currentPlaylistPlayling = playlistName;
            if (Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition()) {
                AccountsDataManager.setLastPlaylistPlayed(playlistName);
            }
        }
    }

    public static String getCurrentPlaylistPlayling() {
        return currentPlaylistPlayling;
    }

    public static void syncPlaylistSongsPlaylingWithCurentSongsList() {
        playlistSongsPlaying.clear();
        for (int i = 0; i < currentSongList.size(); i++) {
            playlistSongsPlaying.add(currentSongList.get(i));
        }
    }

    public static String getPlayType() {
        return playType;
    }

    public static void setPlayType(String type) {
        playType = type;
    }

    public static void updatePlayTypeAtEndOfMedia() {
        System.out.println(playSongInLoop);
        if (!playSongInLoop) {
            if (playType.equals("Random Play")) {
                mediaPlayer.setOnEndOfMedia(() -> {
                    try {
                        randomPlay();
                    } catch (IOException ex) {
                        Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } else if (playType.equals("Ordered Play")) {
                mediaPlayer.setOnEndOfMedia(() -> {
                    try {
                        orderedPlay();
                    } catch (IOException ex) {
                        Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(MusicPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } else {
            mediaPlayer.setOnEndOfMedia(() -> {
                loopPlay();
            });
        }
    }

    public static void smartPlay() throws IOException, Exception {
        if (!musicPlayerInitalized) {
            if (playType.equals("Random Play")) {
                randomPlay();
            } else if (playType.equals("Ordered Play")) {
                orderedPlay();
            }
        } else if (playSongInLoop) {
            loopPlay();
        } else {
            if (playType.equals("Random Play")) {
                randomPlay();
            } else if (playType.equals("Ordered Play")) {
                orderedPlay();
            }
        }

    }

    public static void loopPlay() {
        File file = new File(getSongObjectBeingPlayed().getPathToWavFile());
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        updatePlayTypeAtEndOfMedia();
        mediaPlayer.play();
        System.out.println("finished playling");
    }

    public static void randomPlay() throws IOException, Exception {
        ObservableList<SongDataObject> songDataObjects = playlistSongsPlaying;
        //String[] musicPaths = new String(Files.readAllBytes(PathsManager.getLoggedInUserSongsTxtPath())).split(System.lineSeparator());
        //System.out.println(Arrays.toString(musicPaths));
        Random randomNumGen = new Random();
        System.out.println(songDataObjects.size());
        int indexOfNextSongToPlay = randomNumGen.nextInt(songDataObjects.size());
        setSongObjectBeingPlayed(songDataObjects.get(indexOfNextSongToPlay));
        songHistory.add(getSongObjectBeingPlayed());
        posInSongHistory = songHistory.size() - 1;
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

    public static int getIndexForOrderedPlay() {
        return indexForOrderedPlay;
    }

    public static void orderedPlay() throws IOException, Exception {
        songHistory.clear();
        if (indexForOrderedPlay > playlistSongsPlaying.size() - 1) {
            indexForOrderedPlay = 0;
        }
        if (indexForOrderedPlay < 0) {
            indexForOrderedPlay = playlistSongsPlaying.size() - 1;
        }
        ObservableList<SongDataObject> songDataObjects = playlistSongsPlaying;
        setSongObjectBeingPlayed(songDataObjects.get(indexForOrderedPlay));
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

    public static void playSong(SongDataObject songToPlay) throws Exception {
        setSongObjectBeingPlayed(songToPlay);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        File file = new File(songToPlay.getPathToWavFile());//replace with correct path when testing
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        updatePlayTypeAtEndOfMedia();
        setMusicPlayerInitialized(true);
        mediaPlayer.play();
    }

    public static void nextOrPrevSong() throws IOException, Exception {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        smartPlay();
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

    public static ObservableList<SongDataObject> getPlaylistSongsPlaying() {
        return playlistSongsPlaying;
    }

    public static void updateSongList(ArrayList<SongDataObject> sdota) {
        MusicPlayerManager.getCurrentSongList().clear();
        MusicPlayerManager.getCurrentSongList().addAll(sdota);
    }

    public static void sortCurrentSongList(String sortType, ObservableList<SongDataObject> listToSort) {
        if (sortType.equals("A-Z")) {
            FXCollections.sort(listToSort, new Comparator() {
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
            FXCollections.sort(listToSort, new Comparator() {
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
            FXCollections.sort(listToSort, new Comparator() {
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
            FXCollections.sort(listToSort, new Comparator() {
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
            FXCollections.sort(listToSort, new Comparator() {
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
            FXCollections.sort(listToSort, new Comparator() {
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
        return sliderVolume;
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
