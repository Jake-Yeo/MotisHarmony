/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jake Yeo
 */
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

public class PlaylistMap implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;

    private HashMap<String, ArrayList<SongDataObject>> playlistMap = new HashMap<>();//Using a HashMap we can easily give playlists names

    public void createPlaylist(String name) throws Exception {
        this.playlistMap.put(name, new ArrayList<SongDataObject>());
    }

    public void addSongToPlaylist(String playlistName, ArrayList<SongDataObject> listOfSongs) throws Exception {
        this.playlistMap.get(playlistName).addAll(listOfSongs);
    }

    public void deletePlaylist(String playlistName) throws Exception {
        this.playlistMap.remove(playlistName);
    }

    public void removeSongFromPlaylist(String playlistName, SongDataObject[] sdoArray) throws Exception {
        List sdoToRemove = Arrays.asList(sdoArray);
        this.playlistMap.get(playlistName).removeAll(sdoToRemove);
    }

    public void addSongToPlaylist(String playlistName, SongDataObject song) throws Exception {
        this.playlistMap.get(playlistName).add(song);
    }

    public String[] getArrayOfPlaylistNames() {
        return this.playlistMap.keySet().toArray(new String[this.playlistMap.keySet().size()]);
    }

    public HashMap<String, ArrayList<SongDataObject>> getMapOfPlaylists() {
        return this.playlistMap;
    }
}
