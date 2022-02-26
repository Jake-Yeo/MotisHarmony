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
import java.util.ArrayList;
import java.util.HashMap;

public class PlaylistDataObject implements Serializable {

    private HashMap<String, ArrayList<SongDataObject>> playlist = new HashMap<>();//Using a HashMap we can easily give playlists names
}
