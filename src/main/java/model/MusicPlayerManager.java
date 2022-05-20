/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import javafx.scene.media.Media;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    private static MusicPlayerManager mpmCurrentlyUsing;
    private boolean paused = false;
    private int indexForOrderedPlay = 0;
    private Duration backupCurrentDuration = null;
    private boolean musicPlayerInitalized = false;
    private boolean playSongInLoop = false;
    private boolean isSeeking = false;
    private int volume;
    private String playType = "Ordered Play";
    private String songSortType;
    private String playlistCurrentlyViewing;
    private SongDataObject songObjectBeingPlayed;
    private MediaPlayer mediaPlayer;
    private ObservableList<SongDataObject> currentSongList = FXCollections.observableArrayList();
    //The variable below is really just being used to update the GUI when the alarm goes off
    private SimpleBooleanProperty uiUpdater = new SimpleBooleanProperty(true);
    private ObservableList<SongDataObject> playlistSongsPlaying = FXCollections.observableArrayList();
    private LinkedList<SongDataObject> songHistory = new LinkedList<>();
    private int posInSongHistory = 0;
    private String currentPlaylistPlayling;
    private double sliderVolume = Accounts.getLoggedInAccount().getSettingsObject().getPrefVolume();

    public static void setMpmCurrentlyUsing(MusicPlayerManager mpm) {
        mpmCurrentlyUsing = mpm;
    }

    public static MusicPlayerManager getMpmCurrentlyUsing() {
        return mpmCurrentlyUsing;
    }

    public SimpleBooleanProperty getUiUpdater() {
        return uiUpdater;
    }

    public void setIsSeeking(boolean seeking) {
        isSeeking = seeking;
    }

    public boolean getIsSeeking() {
        return isSeeking;
    }

    public void setSongSortType(String sortType) {
        songSortType = sortType;
    }

    public void setPlaylistCurrentlyViewing(String playlistName) {
        playlistCurrentlyViewing = playlistName;
    }

    public void updateCurrentSongListWithSearchQuery(String searchQuery) {
        searchQuery = searchQuery.trim().toLowerCase();
        ObservableList<SongDataObject> currentSongListToUpdateTo = FXCollections.observableArrayList();
        LinkedList<SongDataObject> listToSearchThrough = Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(getPlaylistCurrentlyViewing());
        for (SongDataObject sdo : listToSearchThrough) {
            if (sdo.getTitle().toLowerCase().contains(searchQuery) || sdo.getChannelName().toLowerCase().contains(searchQuery) || sdo.getVideoID().contains(searchQuery)) {
                currentSongListToUpdateTo.add(sdo);
            }
        }
        currentSongList.clear();
        currentSongList.addAll(currentSongListToUpdateTo);
    }

    public String getPlaylistCurrentlyViewing() {
        return playlistCurrentlyViewing;
    }

    public void setSongObjectBeingPlayed(SongDataObject sdo) throws Exception {
        songObjectBeingPlayed = sdo;
        if (Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition()) {
            AccountsDataManager.setLastSongPlayed(sdo);
        }
    }

    public void setBackUpCurrentDuration(Duration currentDuration) {
        backupCurrentDuration = currentDuration;
    }

    public Duration getBackUpCurrentDuration() {
        return backupCurrentDuration;
    }

    public SongDataObject getSongObjectBeingPlayed() {
        return songObjectBeingPlayed;
    }

    public boolean getPlaySongInLoop() {
        return playSongInLoop;
    }

    public void setPlaySongInLoop(boolean tf) {
        playSongInLoop = tf;
    }

    public double getSliderVolume() {
        return sliderVolume;
    }

    public void setSliderVolume(double volume) {
        sliderVolume = volume;
    }

    public LinkedList<SongDataObject> getSongHistory() {
        return songHistory;
    }

    public String[] getArrayOfSongInfoInCurrentSongList() {
        String[] arrayOfSongNames = new String[getCurrentSongList().size()];
        for (int i = 0; i < arrayOfSongNames.length; i++) {
            arrayOfSongNames[i] = getCurrentSongList().get(i).getTitle() + "\nBy: " + getCurrentSongList().get(i).getChannelName();
        }
        return arrayOfSongNames;
    }

    public int getPosInSongHistory() {
        return posInSongHistory;
    }

    public void setPosInSongHistory(int value) {
        posInSongHistory = value;
    }

    public SongDataObject[] getArrayOfSdoFromCurrentSongListViaIndicies(ObservableList<Integer> indicies) {
        SongDataObject[] sdoGotten = new SongDataObject[indicies.size()];
        for (int i = 0; i < sdoGotten.length; i++) {
            sdoGotten[i] = currentSongList.get(indicies.get(i));
        }
        return sdoGotten;
    }

    public void playThisPlaylist(String playlistName) throws Exception {
        //This will set which songs from which playlist to play next after the song which is currently playing has finsihed
        if (getCurrentPlaylistPlayling().equals(playlistName)) {
            return;
        }
        getSongHistory().clear();
        setCurrentPlaylistPlayling(playlistName);
        setIndexForOrderedPlay(0);
        syncPlaylistSongsPlaylingWithSelectedPlaylist(playlistName);
    }

    public void setCurrentPlaylistPlayling(String playlistName) throws Exception {
        if (playlistName != null) {
            currentPlaylistPlayling = playlistName;
            if (Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition()) {
                AccountsDataManager.setLastPlaylistPlayed(playlistName);
            }
        }
    }

    public String getCurrentPlaylistPlayling() {
        return currentPlaylistPlayling;
    }

    public void syncPlaylistSongsPlaylingWithSelectedPlaylist(String playlistName) {
        playlistSongsPlaying.clear();
        ObservableList<SongDataObject> songsToAddToPlaylistSongPlaying = FXCollections.observableArrayList();
        songsToAddToPlaylistSongPlaying.addAll(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName));
        sortCurrentSongList(songSortType, songsToAddToPlaylistSongPlaying);
        playlistSongsPlaying.addAll(songsToAddToPlaylistSongPlaying);
    }

    public void syncPlaylistSongsPlaylingWithCurentSongsList() {
        playlistSongsPlaying.clear();
        ObservableList<SongDataObject> songsToAddToPlaylistSongPlaying = FXCollections.observableArrayList();
        songsToAddToPlaylistSongPlaying.addAll(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(currentPlaylistPlayling));
        sortCurrentSongList(songSortType, songsToAddToPlaylistSongPlaying);
        playlistSongsPlaying.addAll(songsToAddToPlaylistSongPlaying);

    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String type) {
        playType = type;
    }

    public void updatePlayTypeAtEndOfMedia() {
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

    public boolean isThisPlaylistEmpty(String playlistName) {
        return Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName).isEmpty();
    }

    public void smartPlay() throws IOException, Exception {
        if (!isThisPlaylistEmpty(getCurrentPlaylistPlayling())) {
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
    }

    public void loopPlay() {
        File file = new File(getSongObjectBeingPlayed().getPathToWavFile());
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        stopDisposeMediaPlayer();
        mediaPlayer = new MediaPlayer(media);
        updatePlayTypeAtEndOfMedia();
        mediaPlayer.play();
        System.out.println("finished playling");
    }

    public void randomPlay() throws IOException, Exception {
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
        stopDisposeMediaPlayer();
        mediaPlayer = new MediaPlayer(media);
        updatePlayTypeAtEndOfMedia();
        //Updates the GUI when the MediaPlayer is Null
        uiUpdater.set(!uiUpdater.get());
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }

    public void setIndexForOrderedPlay(int index) {
        indexForOrderedPlay = index;
    }

    public int getIndexForOrderedPlay() {
        return indexForOrderedPlay;
    }

    public void orderedPlay() throws IOException, Exception {
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
        stopDisposeMediaPlayer();
        mediaPlayer = new MediaPlayer(media);
        indexForOrderedPlay++;
        updatePlayTypeAtEndOfMedia();
        //Updates the GUI when the MediaPlayer is Null
        uiUpdater.set(!uiUpdater.get());
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }

    //the bySelection boolean will indicate if the user used the double click or context menu to play the song
    public void playSong(SongDataObject songToPlay, boolean bySelection) throws Exception {
        setSongObjectBeingPlayed(songToPlay);
        if (bySelection) {
            //This if statement will make sure that the end of the linked list is not removed if you play songs by selection multiple times
            if (posInSongHistory != songHistory.size() - 1) {
                //Here we must remove the end of the linked list from the users position in the songHistory to the size of the linked list
                System.out.println("Size of song history is: " + songHistory.size() + " Position in song history is: " + posInSongHistory);
                int sizeOfSongHistoryBeforeChanged = songHistory.size();
                for (int i = 0; i < sizeOfSongHistoryBeforeChanged - posInSongHistory - 1; i++) {
                    songHistory.remove(songHistory.size() - 1);
                }
                songHistory.add(songToPlay);
                posInSongHistory = songHistory.size() - 1;
            } else {
                //If the user played a song by selection multiple times then there is no reason to remove the end of the songHistory, we can just append the song they requested to play to the end of the songHistory
                songHistory.add(songToPlay);
                posInSongHistory = songHistory.size() - 1;
            }
        }
        //If we don't play the song via selection, we don't have to add that song to the songHistory since it's already been added
        File file = new File(songToPlay.getPathToWavFile());//replace with correct path when testing
        System.out.println("song playing: " + file.toPath().toString());
        Media media = new Media(file.toURI().toASCIIString());
        stopDisposeMediaPlayer();
        mediaPlayer = new MediaPlayer(media);
        updatePlayTypeAtEndOfMedia();
        setMusicPlayerInitialized(true);
        mediaPlayer.play();
    }

    public InvalidationListener backupDurationTracker = new InvalidationListener() {
        public void invalidated(Observable ov) {
            //Here we just print the current time of the song
            backupCurrentDuration = mediaPlayer.getCurrentTime();
        }
    };

    public InvalidationListener getBackupDurationIlTracker() {
        return backupDurationTracker;
    }

    //The mediaplayer freezes when disconnecting any bluetooth device so we fix that here.
    public void resetPlayerOnError() {
        File file = new File(songObjectBeingPlayed.getPathToWavFile());
        Media media = new Media(file.toURI().toASCIIString());
        stopDisposeMediaPlayer();
        mediaPlayer.stop();
        mediaPlayer.currentTimeProperty().removeListener(backupDurationTracker);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(() -> {
            resetPlayerOnError();
        });
        mediaPlayer.setOnHalted(() -> {
            resetPlayerOnError();
        });
        mediaPlayer.currentTimeProperty().addListener(backupDurationTracker);//This will help us print the current time of the song
        mediaPlayer.setStartTime(backupCurrentDuration);
    }

    public void stopDisposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void nextOrPrevSong() throws IOException, Exception {
        stopDisposeMediaPlayer();
        smartPlay();
    }

    public void pauseSong() {
        mediaPlayer.pause();
        paused = true;
    }

    public void resumeSong() {
        mediaPlayer.play();
        paused = false;
    }

    public void setPaused(boolean tf) {
        paused = tf;
    }

    public ObservableList<SongDataObject> getCurrentSongList() {
        return currentSongList;
    }

    public ObservableList<SongDataObject> getPlaylistSongsPlaying() {
        return playlistSongsPlaying;
    }

    public void updateSongList(LinkedList<SongDataObject> sdota) {
        getCurrentSongList().clear();
        getCurrentSongList().addAll(sdota);
    }

    public void sortCurrentSongList(String sortType, ObservableList<SongDataObject> listToSort) {
        songSortType = sortType;
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public double getVolume() {
        return sliderVolume;
    }

    public boolean isSongPaused() {
        return paused;
    }

    public boolean isMusicPlayerInitialized() {
        return musicPlayerInitalized;
    }

    public void setMusicPlayerInitialized(boolean tf) {
        musicPlayerInitalized = tf;
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    public void seekTo(Duration duration) {
        mediaPlayer.seek(duration);
    }

    public double getTotalDurationInSeconds() {
        return mediaPlayer.getTotalDuration().toSeconds();
    }

    public double getCurrentTimeInSeconds() {
        return mediaPlayer.getCurrentTime().toSeconds();
    }
}
