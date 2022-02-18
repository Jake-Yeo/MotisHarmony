/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import model.MusicPlayerManager;
import model.YoutubeDownloader;

/**
 *
 * @author 1100007967
 */
public class MusicPlayerViewController implements Initializable {

    @FXML
    private AnchorPane downloadPageMainAnchor;
    @FXML
    private AnchorPane anchorPaneHoldingSlidingMenu;
    @FXML
    private Button playButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Slider seekSlider;
    @FXML
    private Slider volumeSlider;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void playMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.playMusic();
        volumeSlider.setOnMouseClicked((MouseEvent mouseEvent) -> {
            MusicPlayerManager.getMediaPlayer().setVolume(volumeSlider.getValue());
        });
        seekSlider.setOnMousePressed((MouseEvent mouseEvent) -> {
            MusicPlayerManager.getMediaPlayer().seek(Duration.seconds(seekSlider.getValue()));
        });
        seekSlider.setOnMouseReleased((MouseEvent mouseEvent) -> {
        });
        MusicPlayerManager.getMediaPlayer().totalDurationProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            seekSlider.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> MusicPlayerManager.getMediaPlayer().getTotalDuration().toSeconds(),
                    MusicPlayerManager.getMediaPlayer().totalDurationProperty()));

            seekSlider.setValue(newValue.toSeconds());

        });
    }

    @FXML
    private void pauseMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.pauseSong();
    }

    @FXML
    private void resumeMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.resumeSong();
    }

    @FXML
    private void nextSong(ActionEvent event) throws IOException {
        seekSlider.setValue(0);
        MusicPlayerManager.nextSong();
    }
}
