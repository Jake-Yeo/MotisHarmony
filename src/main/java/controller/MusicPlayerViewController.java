/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import model.MusicPlayerManager;
import model.YoutubeDownloader;

/**
 *
 * @author 1100007967
 */
public class MusicPlayerViewController {

    @FXML
    private AnchorPane downloadPageMainAnchor;
    @FXML
    private AnchorPane anchorPaneHoldingSlidingMenu;
    @FXML
    private Button playButton;

    @FXML
    private void playMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.playMusic();
    }

}
