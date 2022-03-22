/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Jake Yeo
 */
public class SettingsObject implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private double prefVolume = 1;
    private String songListSortPreference = "A-Z";
    private String playlistListSortPreference = "A-Z";
    private boolean saveDownloadQueue = true;
    private boolean saveSongPosition = true;
    private String lastPlaylistPlayed = null;
    private SongDataObject lastSongPlayed = null;

    public SettingsObject() {
        System.out.println("playlistListSortPreference = " + playlistListSortPreference);
    }

    public double getPrefVolume() {
        return this.prefVolume;
    }

    public String getSongListSortPreference() {
        return this.songListSortPreference;
    }

    public String getPlaylistListSortPreference() {
        return this.playlistListSortPreference;
    }

    public boolean getSaveDownloadQueue() {
        return this.saveDownloadQueue;
    }

    public boolean getSaveSongPosition() {
        return this.saveSongPosition;
    }

    public void setPrefVolume(double prefVolume) {
        this.prefVolume = prefVolume;
    }

    public void setSongListSortPreference(String songListSortPreference) {
        this.songListSortPreference = songListSortPreference;
    }

    public void setPlaylistListSortPreference(String playlistListSortPreference) {
        this.playlistListSortPreference = playlistListSortPreference;
    }

    public void setSaveDownloadQueue(boolean saveDownloadQueue) {
        this.saveDownloadQueue = saveDownloadQueue;
    }

    public void setSaveSongPosition(boolean saveSongPosition) {
        this.saveSongPosition = saveSongPosition;
    }

    public void setLastPlaylistPlayed(String lastPlaylistPlayed) {
        this.lastPlaylistPlayed = lastPlaylistPlayed;
    }

    public void setLastSongPlayed(SongDataObject lastSongPlayed) {
        this.lastSongPlayed = lastSongPlayed;
    }

    public String getLastPlaylistPlayed() {
        return this.lastPlaylistPlayed;
    }

    public SongDataObject getLastSongPlayed() {
        return this.lastSongPlayed;
    }

}
