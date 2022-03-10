/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import model.Accounts;
import model.MusicPlayerManager;
import model.PlaylistMap;
import model.SongDataObject;
import model.YoutubeDownloader;
import model.YoutubeVideoPageParser;

/**
 *
 * @author 1100007967
 */
public class MusicPlayerViewController implements Initializable, PropertyChangeListener {

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
        seekSlider.getStylesheets().add("/css/customSlider.css");

        MusicPlayerManager.getCurrentSongList().addListener(new ListChangeListener<SongDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends SongDataObject> arg0) {
                //we update the model here, then we can change the view if we were to reorder the model as well
                updateViewCurrentSongList();
                System.out.println("listener ran");
            }
        });
        updatePlaylistList();
        //playlistList.getItems().add(new PlaylistDataObject().getMapOfPlaylists().keySet().);
    }

    @FXML
    private void playMusic(ActionEvent event) throws IOException {
        MusicPlayerManager.playMusic();
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();
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
            }
        });

        MusicPlayerManager.getMediaPlayer().currentTimeProperty().addListener(new InvalidationListener() {//This will automatically update the seekSlider to match the current position of the song
            public void invalidated(Observable ov) {
                seekSlider.setValue(MusicPlayerManager.getCurrentTimeInSeconds());
                timeText.setText(getCurrentTimeStringFormatted((int) Math.floor(MusicPlayerManager.getCurrentTimeInSeconds()), (int) Math.floor(MusicPlayerManager.getTotalDurationInSeconds())));
            }
        });

    }

    public void setUpContextMenu() {
        MenuItem downloadLink = new MenuItem("Download Video Audio");
        downloadLink.setOnAction(e -> System.out.println("Go Forward"));

        contextMenu.getItems().addAll(downloadLink);
    }

    private void updatePlaylistList() {
        PlaylistMap map = Accounts.getLoggedInAccount().getPlaylistDataObject();
        playlistList.getItems().addAll(map.getMapOfPlaylists().keySet().toArray(new String[map.getMapOfPlaylists().keySet().size()]));
    }
    @FXML
    private void updateModelCurrentSongList() {
        PlaylistMap map = Accounts.getLoggedInAccount().getPlaylistDataObject();
        int selectedIndex = playlistList.getSelectionModel().getSelectedIndex();
        String keyValue = playlistList.getItems().get(selectedIndex);
        System.out.println(keyValue);
        ArrayList<SongDataObject> songDataObjectsToAdd = map.getMapOfPlaylists().get(keyValue);
        MusicPlayerManager.getCurrentSongList().clear();
        MusicPlayerManager.getCurrentSongList().addAll(songDataObjectsToAdd);
    }

    private void updateViewCurrentSongList() {
        String[] arrayOfSongNames = new String[MusicPlayerManager.getCurrentSongList().size()];
        for (int i = 0; i < arrayOfSongNames.length; i++) {
            arrayOfSongNames[i] = MusicPlayerManager.getCurrentSongList().get(i).getTitle();
        }
        //we clear the list and then put the new list of song names in
        songList.getItems().clear();
        songList.getItems().addAll(arrayOfSongNames);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updatePlaylistList();
    }
}
