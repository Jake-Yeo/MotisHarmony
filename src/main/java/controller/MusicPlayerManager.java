/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    private static Media media;//This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash

    public static void playSong() {
        File file = new File("C:\\Users\\Jake Yeo\\OneDrive\\Desktop\\SongsDelete\\OneRepublicCountingStarsOfficialMusicVideo.mp3");//replace with correct path when testing
        String path = file.toURI().toASCIIString();
        media = new Media(path);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
