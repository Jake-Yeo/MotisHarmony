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

    public static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash

    public static void playMusic() throws IOException {
        ArrayList<String> musicPaths = Account.getLoggedInAccount().getListOfSongPaths();
        //String[] musicPaths = new String(Files.readAllBytes(PathsManager.getLoggedInUserSongsTxtPath())).split(System.lineSeparator());
        //System.out.println(Arrays.toString(musicPaths));
        Random randomNumGen = new Random();
        System.out.println(musicPaths.size());
        int indexOfNextSongToPlay = randomNumGen.nextInt(musicPaths.size());
        File file = new File(musicPaths.get(indexOfNextSongToPlay));//replace with correct path when testing
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
}
