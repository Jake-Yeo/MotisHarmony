/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.MusicPlayerManager;
import model.YoutubeDownloader;
import model.YoutubeVideoPageParser;

/**
 *
 * @author 1100007967
 */
public class MusicPlayerViewController implements Initializable {

    private ContextMenu contextMenu = new ContextMenu();
    @FXML
    private AnchorPane downloadPageMainAnchor;
    @FXML
    private Text songInfoText;
    @FXML
    private AnchorPane anchorPaneHoldingSlidingMenu;
    @FXML
    private ListView<String> playlistList;
    @FXML
    private ListView<String> songList;
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
    @FXML
    private Text timeText;
    @FXML
    private ListView<String> songInfoViewList;
    @FXML
    private ImageView thumbnailImageView;
    @FXML
    private AreaChart<String, Number> spektrum;
    private XYChart.Data[] series1Data;
    private static final int BANDS = 500;
    private static final double INTERVAL = 0.005;
    private static final double DROPDOWN = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(downloadPageMainAnchor.widthProperty());
        clip.heightProperty().bind(downloadPageMainAnchor.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        downloadPageMainAnchor.setClip(clip);
        
        if (MusicPlayerManager.getSongObjectBeingPlayed() != null) {
            updateInfoDisplays();
        }

        if (MusicPlayerManager.getMediaPlayer() != null) {
            volumeSlider.setValue(MusicPlayerManager.getVolume());
            seekSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> MusicPlayerManager.getMediaPlayer().getTotalDuration().toSeconds(), MusicPlayerManager.getMediaPlayer().totalDurationProperty()));
            init();
        } else {
            volumeSlider.setValue(1);
        }

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        XYChart.Data[] series1Data = new XYChart.Data[BANDS];
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 1), 0);
            series1.getData().add(series1Data[i]);
        }
        spektrum.getData().add(series1);
        spektrum.getYAxis().setAutoRanging(false);
        spektrum.getStylesheets().add("/css/fxplayer.css");
        ((NumberAxis)spektrum.getYAxis()).setUpperBound(70);
        seekSlider.getStylesheets().add("/css/customSlider.css");
    }

    @FXML
    private void playMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.playMusic();
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1Data = new XYChart.Data[BANDS + 2];
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 1), 0);
            //noinspection unchecked
            series1.getData().add(series1Data[i]);
        }
        spektrum.getData().add(series1);
        Node fill = series1.getNode().lookup(".chart-series-area-fill"); // only for AreaChart
        Node line = series1.getNode().lookup(".chart-series-area-line");

        fill.setStyle("-fx-fill: linear-gradient(to bottom, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c);");
        line.setStyle("-fx-stroke: transparent");

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
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();

    }

    private void updateInfoDisplays() {
        songInfoViewList.getItems().clear();
        songInfoText.setText("Song name: " + MusicPlayerManager.getSongObjectBeingPlayed().getTitle() + "Song creator: " + MusicPlayerManager.getSongObjectBeingPlayed().getChannelName());
        songInfoViewList.getItems().add("Song name: " + MusicPlayerManager.getSongObjectBeingPlayed().getTitle());
        songInfoViewList.getItems().add("Song creator: " + MusicPlayerManager.getSongObjectBeingPlayed().getChannelName());
        songInfoViewList.getItems().add("Song duration: " + MusicPlayerManager.getSongObjectBeingPlayed().getVideoDuration());
        thumbnailImageView.setImage(new Image(MusicPlayerManager.getSongObjectBeingPlayed().getPathToThumbnail()));
    }

    private String getCurrentTimeStringFormatted(int currentseconds, int totalSeconds) {
        boolean getTotalSecondsInHourFormat = false;
        String totalTime = getCurrentTimeString(totalSeconds, false);
        if (totalTime.length() > 5) {
            getTotalSecondsInHourFormat = true;
        }
        String currentSeconds = getCurrentTimeString(currentseconds, getTotalSecondsInHourFormat);
        return currentSeconds + "/" + totalTime;
    }

    private String getCurrentTimeString(int seconds, boolean inHourFormat) {
        String videoDuration = "";
        String stringDurationMinutes = "";
        int durationInSeconds = seconds;
        int durationMinutes = (int) Math.floor(durationInSeconds / 60);
        int durationHours = 0;
        String remaindingSeconds = "" + (durationInSeconds - durationMinutes * 60);
        if (remaindingSeconds.length() == 1) {
            remaindingSeconds = 0 + remaindingSeconds;
        }
        if (durationMinutes >= 60 || inHourFormat) { //This will convert the youtube duration from milliseconds, to a readable format.
            durationHours = (int) Math.floor(durationMinutes / 60);
            durationMinutes = durationMinutes - durationHours * 60;
            stringDurationMinutes = durationMinutes + "";
            if (stringDurationMinutes.length() == 1) {
                stringDurationMinutes = 0 + stringDurationMinutes;
            }
            if (durationHours == 0) {
                videoDuration = "0:" + stringDurationMinutes + ":" + remaindingSeconds;
            } else {
                videoDuration = durationHours + ":" + stringDurationMinutes + ":" + remaindingSeconds;
            }
        } else {
            videoDuration = durationMinutes + ":" + remaindingSeconds;
        }
        return videoDuration;
    }

    public void init() {
        MusicPlayerManager.getMediaPlayer().setOnEndOfMedia(new Runnable() {//this will tell the music player what to do when the song ends. Since a new media player is created each time, we must call the init() method again to set and initialize the media player again
            public void run() {
                try {
                    MusicPlayerManager.playMusic();
                    init();
                    updateInfoDisplays();
                } catch (IOException ex) {
                    Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                MusicPlayerManager.setVolume(volumeSlider.getValue());
            }
        });

        seekSlider.setOnMousePressed((MouseEvent mouseEvent) -> {//This handles the seeking of the song
            MusicPlayerManager.pauseSong();//Pause the song so there is no weird audio
        });

        seekSlider.setOnMouseReleased((MouseEvent mouseEvent) -> {//This handles the seeking of the song
            MusicPlayerManager.seekTo(Duration.seconds(seekSlider.getValue()));//Set where to resume the song
            MusicPlayerManager.resumeSong();//Resume the song once the user releases their mous key
        });

        MusicPlayerManager.getMediaPlayer().setOnReady(new Runnable() {//This will set the volume of the song, and the max value of the seekSlider once the media player has finished analyzing and reading the song.
            public void run() {
                MusicPlayerManager.setVolume(volumeSlider.getValue());//Sets the volume
                seekSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> MusicPlayerManager.getMediaPlayer().getTotalDuration().toSeconds(), MusicPlayerManager.getMediaPlayer().totalDurationProperty()));//Sets the max values of the seekSlider to the duration of the song that is to be played
                MusicPlayerManager.getMediaPlayer().setAudioSpectrumListener(new SpektrumListener());
                MusicPlayerManager.getMediaPlayer().setAudioSpectrumNumBands(BANDS);
                MusicPlayerManager.getMediaPlayer().setAudioSpectrumInterval(INTERVAL);
            }
        });

        MusicPlayerManager.getMediaPlayer().currentTimeProperty().addListener(new InvalidationListener() {//This will automatically update the seekSlider to match the current position of the song
            public void invalidated(Observable ov) {
                seekSlider.setValue(MusicPlayerManager.getCurrentTimeInSeconds());
                timeText.setText(getCurrentTimeStringFormatted((int) Math.floor(MusicPlayerManager.getCurrentTimeInSeconds()), (int) Math.floor(MusicPlayerManager.getTotalDurationInSeconds())));
            }
        });

    }

    private class SpektrumListener implements AudioSpectrumListener {

        float[] buffer = createFilledBuffer(BANDS, MusicPlayerManager.getMediaPlayer().getAudioSpectrumThreshold());

        @Override
        public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {

            for (int i = 0; i < magnitudes.length; i++) {
                if (magnitudes[i] >= buffer[i]) {
                    buffer[i] = magnitudes[i];
                    series1Data[i].setYValue(magnitudes[i] - MusicPlayerManager.getMediaPlayer().getAudioSpectrumThreshold());
                } else {
                    series1Data[i].setYValue(buffer[i] - MusicPlayerManager.getMediaPlayer().getAudioSpectrumThreshold());
                    buffer[i] -= 0.25;
                }
            }
        }
    }

    private float[] createFilledBuffer(int size, float fillValue) {
        float[] floats = new float[size];
        Arrays.fill(floats, fillValue);
        return floats;
    }

    private class PreparationWorker implements Runnable {

        public void run() {

            //videoView.setMediaPlayer(mediaplayer);
            MusicPlayerManager.getMediaPlayer().setAudioSpectrumListener(new SpektrumListener());
            MusicPlayerManager.getMediaPlayer().setAudioSpectrumNumBands(BANDS);
            MusicPlayerManager.getMediaPlayer().setAudioSpectrumInterval(INTERVAL);

        }
    }
    
        public void setUpContextMenu() {
        MenuItem downloadLink = new MenuItem("Download Video Audio");
        downloadLink.setOnAction(e -> System.out.println("Go Forward"));


        contextMenu.getItems().addAll(downloadLink);
    }
}
