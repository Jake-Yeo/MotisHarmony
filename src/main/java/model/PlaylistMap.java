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
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class PlaylistMap implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;

    private HashMap<String, ArrayList<SongDataObject>> playlistMap = new HashMap<>();//Using a HashMap we can easily give playlists names

    public void createPlaylist(String name) {
        this.playlistMap.put(name, new ArrayList<SongDataObject>());
    }

    public void addSongToPlaylist(String playlistName, ArrayList<SongDataObject> listOfSongs) {
        this.playlistMap.get(playlistName).addAll(listOfSongs);
    }

    public void addSongToPlaylist(String playlistName, SongDataObject song) {
        this.playlistMap.get(playlistName).add(song);
    }
    
    public HashMap<String, ArrayList<SongDataObject>> getMapOfPlaylists() {
        return this.playlistMap;
    }
    
    

}