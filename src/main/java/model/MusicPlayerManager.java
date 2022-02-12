/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import javafx.scene.media.Media;
import java.util.Random;
import java.util.Scanner;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    public static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash

    public static void playMusic() throws IOException {
        String[] musicPaths = new String(Files.readAllBytes(PathsManager.getLoggedInUserSongsTxtPath())).split("/n");
        System.out.println(Arrays.toString(musicPaths));
        Random randomNumGen = new Random(); 
        int indexOfNextSongToPlay = randomNumGen.nextInt(musicPaths.length);
        File file = new File(musicPaths[indexOfNextSongToPlay].trim());//replace with correct path when testing
        Media media = new Media(file.toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        System.out.println("finished playling");
        //playMusic();
    }
}
