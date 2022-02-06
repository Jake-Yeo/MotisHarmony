/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Jake Yeo
 */
public class MusicPlayerManager {

    public static MediaPlayer mediaPlayer; //This NEEDS TO BE STATIC or else the mediaPlayer will hang during the middle of a long song because of the java garbage collection https://stackoverflow.com/questions/47835433/why-does-javafx-media-player-crash

    public static void playMusic() {
        File file = new File("C:\\Users\\Jake Yeo\\MotisHarmony\\accounts\\3dAudio\\downloadedMusic\\TheFatRatTheCallingfeatLauraBrehmDAUDIO[gle6xZTkwc0].wav");//replace with correct path when testing
        String path = file.toURI().toASCIIString();

        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
