/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Accounts;
import model.AccountsDataManager;
import model.AlarmClock;
import model.MusicPlayerManager;
import model.PlaylistMap;
import model.SettingsObject;
import model.SleepTimer;
import model.SongDataObject;
import model.YoutubeDownloader;
import view.MainViewRunner;

/**
 *
 * @author 1100007967
 */
public class MusicPlayerViewController implements Initializable, PropertyChangeListener {

    private ContextMenu songListContextMenu = new ContextMenu();
    private ContextMenu playlistListContextMenu = new ContextMenu();
    private MusicPlayerManager mpm;
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
    private Button nextButton;
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
    private Text currentTimeText;
    @FXML
    private Text totalTimeText;
    @FXML
    private Button addPlaylistButton;
    @FXML
    private TextField playlistNameTextField;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button playButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button loopButton;
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> sortPlaylistChoiceBox;
    @FXML
    private TextField searchTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mpm = new MusicPlayerManager();
        searchTextField.setText("");
        MusicPlayerManager.setMpmCurrentlyUsing(mpm);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(downloadPageMainAnchor.widthProperty());
        clip.heightProperty().bind(downloadPageMainAnchor.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        downloadPageMainAnchor.setClip(clip);

        if (mpm.getSongObjectBeingPlayed() != null) {
            updateInfoDisplays();
        }

        if (mpm.getMediaPlayer() != null) {
            volumeSlider.setValue(mpm.getVolume());
            seekSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> mpm.getMediaPlayer().getTotalDuration().toSeconds(), mpm.getMediaPlayer().totalDurationProperty()));
            init();
        } else {
            volumeSlider.setValue(Accounts.getLoggedInAccount().getSettingsObject().getPrefVolume());
        }
        volumeSlider.setOnMouseReleased(e -> {
            try {
                mpm.setSliderVolume(volumeSlider.getValue());
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        seekSlider.getStylesheets().add("/css/customSlider.css");
        try {
            updatePlaylistList();
        } catch (Exception ex) {
            Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        setUpContextMenus();
        comboBox.setVisible(false);
        sortChoiceBox.setVisible(false);
        sortPlaylistChoiceBox.setVisible(false);
        comboBox.setVisibleRowCount(16);
        comboBox.setMaxWidth(200);
        comboBox.getStylesheets().add("/css/comboBox.css");
        //Here we add the options for the types of sort available to the user for the currentSongList
        sortChoiceBox.getItems().add("A-Z");
        sortChoiceBox.getItems().add("Z-A");
        sortChoiceBox.getItems().add("A-Z By Artist");
        sortChoiceBox.getItems().add("Z-A By Artist");
        sortChoiceBox.getItems().add("Oldest Added");
        sortChoiceBox.getItems().add("Newest Added");
        //Here we automatically sort the currentSongList based on the users preference
        sortChoiceBox.getSelectionModel().select(Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference());
        mpm.setSongSortType(Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference());
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            //This will sort the currentSongList when the sort context menu value is changed
            try {
                if (newValue != null) {
                    sortModelCurrentSongList(newValue);
                    try {
                        AccountsDataManager.updateCurrentSongListSortType(newValue);
                    } catch (Exception ex) {
                        Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //This if statment will only set up the MusicPlayer for if the user wanted their song position saved if and only if the user enabled this option, and if both the last playlist and song played are not null
        //This if statement will also check to make sure the user did not delete the song which is to be automatically played, if it was deleted then the Music player will not save the users song position
        if (Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition() && Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed() != null && Accounts.getLoggedInAccount().getSettingsObject().getLastSongPlayed() != null && Files.exists(Paths.get(Accounts.getLoggedInAccount().getSettingsObject().getLastSongPlayed().getPathToWavFile()))) {
            if (!mpm.isMusicPlayerInitialized()) {
                try {
                    //Code below ensure that the user is playing the last playlist they played
                    mpm.setCurrentPlaylistPlayling(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed());
                    mpm.playThisPlaylist(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed());
                    mpm.updateSongList(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed()));
                    mpm.syncPlaylistSongsPlaylingWithCurentSongsList();
                    mpm.sortCurrentSongList(Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference(), mpm.getPlaylistSongsPlaying());
                } catch (Exception ex) {
                    Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Code below ensures that the song which will play when the play button is hit is the song which was last played by the user
            try {
                mpm.playSong(Accounts.getLoggedInAccount().getSettingsObject().getLastSongPlayed());
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Code below ensure that the user is playing the last playlist they played
            playlistList.getSelectionModel().select(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed());
            mpm.setPlaylistCurrentlyViewing(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed());
            mpm.pauseSong();
            playButton.setStyle("-fx-padding: -2 0 0 3; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
            playButton.setText("▶");
            init();
            updateInfoDisplays();
        } else {
            //If the user does not want to save their song posititon, then the program will just set the playlist to default
            try {
                AccountsDataManager.setLastPlaylistPlayed("All Songs");
                mpm.setCurrentPlaylistPlayling("All Songs");
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            playlistList.getSelectionModel().select("All Songs");
            mpm.setPlaylistCurrentlyViewing("All Songs");
        }
        mpm.updateSongList(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(Accounts.getLoggedInAccount().getSettingsObject().getLastPlaylistPlayed()));//This will set the currentSongList with all the songs which have been downloaded so far. This ensures that no errors occur when the user presses play without picking a playlist

        updateViewCurrentSongList();//Since we change the model but do not have a change listener we must manually change the view during initialization
        mpm.syncPlaylistSongsPlaylingWithCurentSongsList();//Since we change the model but do not have a change listener we must manually sync the model during initialization
        mpm.getCurrentSongList().addListener(new ListChangeListener<SongDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends SongDataObject> arg0) {
                //we update the model here, then we can change the view if we were to reorder the model as well
                updateViewCurrentSongList();
                if (mpm.getCurrentPlaylistPlayling().equals(playlistList.getSelectionModel().getSelectedItem())) {
                    //If the view of the same playlist which is currently playing is changed, then those changes will be synced with the model so the user knows which song will play next when using ordered play
                    mpm.syncPlaylistSongsPlaylingWithCurentSongsList();
                }
                System.out.println("view listener ran");
            }
        });

        songList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        playButton.textOverrunProperty().set(OverrunStyle.CLIP);
        System.out.println("SongList sort " + Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference());
        //Here we automatically sort the currentSongList based on the users preference
        sortChoiceBox.getSelectionModel().select(Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference());

        //We add the types of sorts available to the user for the playlist
        sortPlaylistChoiceBox.getItems().add("A-Z");
        sortPlaylistChoiceBox.getItems().add("Z-A");
        sortPlaylistChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                if (newValue != null) {
                    //updatePlaylistList() automatically sorts the list
                    updatePlaylistList();
                    System.out.println("Save thingsjfoiajweoifj");
                    try {
                        AccountsDataManager.updateCurrentPlaylistListSortType(newValue);
                    } catch (Exception ex) {
                        Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        System.out.println("PlaylistList sort" + Accounts.getLoggedInAccount().getSettingsObject().getPlaylistListSortPreference());
        sortPlaylistChoiceBox.getSelectionModel().select(Accounts.getLoggedInAccount().getSettingsObject().getPlaylistListSortPreference());
//playlistList.getItems().add(new PlaylistDataObject().getMapOfPlaylists().keySet().);

        //The listener below will update the currentSongList whenever a song is completed downloading. This will allow the music player to automatically add songs to its model which it can then play
        YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList().addListener(new ListChangeListener<SongDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends SongDataObject> arg0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            updateModelCurrentSongList();
                        } catch (Exception ex) {
                            Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });

        if (Accounts.getLoggedInAccount().getSettingsObject().getSavePlayPreference()) {

            if (Accounts.getLoggedInAccount().getSettingsObject().getPlayType().equals("Random Play")) {
                mpm.setPlayType("Random Play");
                shuffleButton.setStyle("-fx-padding: 0 0 0 0; -fx-background-color: transparent; -fx-border-color: #d07ccc; -fx-border-width: 3px; -fx-border-radius: 50px;");
                shuffleButton.setTextFill(Paint.valueOf("#d07ccc"));
            } else if (Accounts.getLoggedInAccount().getSettingsObject().getPlayType().equals("Ordered Play")) {
                mpm.setPlayType("Ordered Play");
                shuffleButton.setStyle("-fx-padding: 0 0 0 0; -fx-background-color: transparent; -fx-border-color: #f04444; -fx-border-width: 3px; -fx-border-radius: 50px;");
                shuffleButton.setTextFill(Paint.valueOf("#f04444"));
            }
            mpm.setPlaySongInLoop(Accounts.getLoggedInAccount().getSettingsObject().getPlaySongInLoop());
        }

        MainViewRunner.getStage().setOnCloseRequest(windowEvent -> {
            mpm.getMediaPlayer().stop();
        });
        //Only execute this loop if there is not song object which has been saved
        if (mpm.getSongObjectBeingPlayed() == null && !Accounts.getLoggedInAccount().getListOfSongDataObjects().isEmpty()) {
            //We play intialize and update the info displays below so that if the user decides to delete the current song which is to be saved, then the Alarm clock will not run into any errors as the program will automatically pick a song.
            try {
                mpm.smartPlay();
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            init();
            updateInfoDisplays();
            mpm.pauseSong();
        }
//This block of code below will automatically sort the songList on startup.
        try {
            sortModelCurrentSongList(Accounts.getLoggedInAccount().getSettingsObject().getSongListSortPreference());
        } catch (Exception ex) {
            Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //This block of code will set up the Sleepalarm
        SleepTimer.setTimerCurrentlyUsing(Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer());
        //Should probably just make AlarmClock equal to the accAlarmClock;
        //AlarmClock.setAlarmCurrentlyUsing(new AlarmClock(accAlarmClock.getHour(), accAlarmClock.getMinute(), accAlarmClock.getAmOrPm()));
        AlarmClock.setAlarmCurrentlyUsing(Accounts.getLoggedInAccount().getSettingsObject().getAlarmClock());
        if (Accounts.getLoggedInAccount().getSettingsObject().getAlarmClock().getEnableAlarm()) {
            try {
                AlarmClock.getAlarmCurrentlyUsing().startAlarmCheck();
            } catch (ParseException ex) {
                Logger.getLogger(AlarmClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        mpm.getUiUpdater().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                init();
                updateInfoDisplays();
                mpm.pauseSong();
                mpm.resumeSong();
            }
        });

    }

    @FXML
    private void searchTextFieldWhenTyped(KeyEvent e) {
        //Search everytime the text input is changed
        System.out.println("Searching!!");
        mpm.updateCurrentSongListWithSearchQuery(searchTextField.getText());
    }

    @FXML
    private void shuffleButtonOnAction() {
        //The code below immitates what spotify does when a shuffle button is pressed
        if (mpm.getPlayType().equals("Ordered Play")) {
            mpm.getSongHistory().clear();//Just in case
            mpm.setPlayType("Random Play");
            shuffleButton.setStyle("-fx-padding: 0 0 0 0; -fx-background-color: transparent; -fx-border-color: #d07ccc; -fx-border-width: 3px; -fx-border-radius: 50px;");
            shuffleButton.setTextFill(Paint.valueOf("#d07ccc"));
        } else if (mpm.getPlayType().equals("Random Play")) {
            mpm.setPlayType("Ordered Play");
            shuffleButton.setStyle("-fx-padding: 0 0 0 0; -fx-background-color: transparent; -fx-border-color: #f04444; -fx-border-width: 3px; -fx-border-radius: 50px;");
            shuffleButton.setTextFill(Paint.valueOf("#f04444"));
        }
        if (Accounts.getLoggedInAccount().getSettingsObject().getSavePlayPreference()) {
            try {
                AccountsDataManager.setPlayType(mpm.getPlayType());
            } catch (Exception ex) {
                Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @FXML
    private void loopButtonOnAction() {
        //The code below immitates what spotify does when a loop button button is pressed
        if (mpm.getPlaySongInLoop()) {
            mpm.setPlaySongInLoop(false);
        } else {
            mpm.setPlaySongInLoop(true);
        }
        if (Accounts.getLoggedInAccount().getSettingsObject().getSavePlayPreference()) {
            try {
                AccountsDataManager.setPlaySongInLoop(mpm.getPlaySongInLoop());
            } catch (Exception ex) {
                Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void sortModelCurrentSongList(String sortType) throws Exception {
        //we sort the view of the current playlist selected
        mpm.sortCurrentSongList(sortType, mpm.getCurrentSongList());
        //If the current playlist selected is the same as the current playlist playing then we update the next song to play to match the view of the current playlist selected
        if (mpm.getCurrentPlaylistPlayling().equals(playlistList.getSelectionModel().getSelectedItem())) {
            mpm.setIndexForOrderedPlay(mpm.getPlaylistSongsPlaying().indexOf(mpm.getSongObjectBeingPlayed()) + 1);
        }
    }

    private void sortPlaylistList(String sortType) throws Exception {
        //we sort the view of the current playlist selected
        if (sortType.equals("A-Z")) {
            FXCollections.sort(playlistList.getItems(), new Comparator() {
                @Override
                public int compare(Object string1, Object string2) {
                    String firstString = (String) string1;
                    String secondString = (String) string2;
                    int returnValue;
                    if (firstString.compareTo(secondString) < 0) {
                        returnValue = 0;
                    } else {
                        if (firstString.compareTo(secondString) > 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
            System.out.println(sortType);
            //updateViewCurrentSongList();
        } else if (sortType.equals("Z-A")) {
            FXCollections.sort(playlistList.getItems(), new Comparator() {
                @Override
                public int compare(Object string1, Object string2) {
                    String firstString = (String) string1;
                    String secondString = (String) string2;
                    int returnValue;
                    if (firstString.compareTo(secondString) > 0) {
                        returnValue = 0;
                    } else {
                        if (firstString.compareTo(secondString) < 0) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                    return returnValue;
                }
            });
        }
    }

    @FXML
    private void onComboBoxClicked(ActionEvent e) throws Exception {
        if (comboBox.getSelectionModel().selectedIndexProperty().get() >= 0) {
            if (songList.getSelectionModel().getSelectedIndices().size() == 1) {
                System.out.println(comboBox.getSelectionModel().getSelectedItem());
                AccountsDataManager.addSongToPlaylist(comboBox.getSelectionModel().getSelectedItem(), mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndex()));
                //The if statment below ensures that if the user were to remove a song from a playlist, and then add that song back, and then not refresh that playlist, then if they are currently playing that playlist, that song which was added will be played if the user is using ordered or random play
                if (mpm.getCurrentPlaylistPlayling().equals(comboBox.getSelectionModel().getSelectedItem())) {
                    mpm.getPlaylistSongsPlaying().clear();
                    mpm.getPlaylistSongsPlaying().addAll(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistList.getSelectionModel().getSelectedItem()));
                    if (sortChoiceBox.getValue() != null) {
                        //automatically sort the PlaylistSongsPlaying
                        mpm.sortCurrentSongList(sortChoiceBox.getValue(), mpm.getPlaylistSongsPlaying());
                    }
                    mpm.setIndexForOrderedPlay(mpm.getPlaylistSongsPlaying().indexOf(mpm.getSongObjectBeingPlayed()) + 1);
                }
            } else {
                LinkedList<SongDataObject> sdoToAddToPlaylist = new LinkedList<>();
                for (int i = 0; i < songList.getSelectionModel().getSelectedIndices().size(); i++) {
                    sdoToAddToPlaylist.add(mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndices().get(i)));
                }
                AccountsDataManager.addSongToPlaylist(comboBox.getSelectionModel().getSelectedItem(), sdoToAddToPlaylist);
                //The if statment below ensures that if the user were to remove a song from a playlist, and then add that song back, and then not refresh that playlist, then if they are currently playing that playlist, that song which was added will be played if the user is using ordered or random play
                if (mpm.getCurrentPlaylistPlayling().equals(comboBox.getSelectionModel().getSelectedItem())) {
                    mpm.getPlaylistSongsPlaying().clear();
                    mpm.getPlaylistSongsPlaying().addAll(Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistList.getSelectionModel().getSelectedItem()));
                    if (sortChoiceBox.getValue() != null) {
                        //automatically sort the PlaylistSongsPlaying
                        mpm.sortCurrentSongList(sortChoiceBox.getValue(), mpm.getPlaylistSongsPlaying());
                    }
                    mpm.setIndexForOrderedPlay(mpm.getPlaylistSongsPlaying().indexOf(mpm.getSongObjectBeingPlayed()) + 1);
                }
            }
        }
        System.out.println(comboBox.getSelectionModel().selectedIndexProperty().get());
    }

    @FXML
    private void createNewPlaylist(ActionEvent event) throws Exception {
        //Code to create a new playlist
        if (!playlistNameTextField.getText().trim().isBlank()) {
            AccountsDataManager.createPlaylist(playlistNameTextField.getText().trim());
            updatePlaylistList();
            playlistNameTextField.clear();
        }
    }

    private void onFirstMusicPlayerPlay() throws IOException, Exception {
        //This code below initalizes the MusicPlayer
        mpm.smartPlay();
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();
        mpm.setMusicPlayerInitialized(true);
        mpm.setPaused(false);
        playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
        playButton.setText("⏸︎");
    }

    @FXML
    private void playMusic(ActionEvent event) throws IOException, Exception {
        if (!mpm.isThisPlaylistEmpty(mpm.getCurrentPlaylistPlayling())) {
            //The code below immitates what spotify does when a playbutton is pushed
            if (!mpm.isMusicPlayerInitialized()) {
                onFirstMusicPlayerPlay();
            } else if (!mpm.isSongPaused()) {
                System.out.println("Paused Song");
                mpm.pauseSong();
                playButton.setStyle("-fx-padding: -2 0 0 3; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
                playButton.setText("▶");
                System.out.println(mpm.getMediaPlayer().getStatus());
            } else {
                System.out.println("Resumed Song");
                mpm.resumeSong();
                playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
                playButton.setText("⏸︎");
                System.out.println(mpm.getMediaPlayer().getStatus());
            }
        }
    }

    public void setUpPlayButton() {
        seekSlider.setValue(0);
        playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
        playButton.setText("⏸︎");
        mpm.setPaused(false);
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();
    }

    @FXML
    private void previousSong() throws IOException, Exception {
        if (!mpm.isThisPlaylistEmpty(mpm.getCurrentPlaylistPlayling())) {
            //The code below immitates what spotify does when a previous song button is pressed
            if (!mpm.isMusicPlayerInitialized()) {
                //This line basically initializes the MusicPlayer so that it is not null and can be used
                onFirstMusicPlayerPlay();
            }
            if (!mpm.getPlaySongInLoop()) {
                if (mpm.getPlayType().equals("Ordered Play")) {
                    //This will clear the songHistory(), remember it is only used to keep track of songs when shuffling
                    mpm.getSongHistory().clear();
                    //The code below will let the MusicPlayerManager know to play the next song
                    mpm.setIndexForOrderedPlay(mpm.getPlaylistSongsPlaying().indexOf(mpm.getSongObjectBeingPlayed()) - 1);
                    mpm.nextOrPrevSong();
                    //This code will set up the play button and audio info displays
                    setUpPlayButton();
                } else if (mpm.getPlayType().equals("Random Play")) {
                    //We get the index of the next song to keep track of which song to play next in the song history
                    int indexOfPrevSong = mpm.getPosInSongHistory() - 1;
                    //If there is no song left to play in the song history then do nothing because we are getting the previous song, there is no previous song left
                    if (indexOfPrevSong < 0) {
                        return;
                    } else {
                        //If there is a previous song to play then we play it
                        mpm.playSong(mpm.getSongHistory().get(indexOfPrevSong));
                        //We keep track of the position we are on in the songHistory
                        mpm.setPosInSongHistory(mpm.getPosInSongHistory() - 1);
                        setUpPlayButton();
                    }
                }
            } else {
                //Since the looping code in in the manager, we just need to run code from the manager and update the UI
                mpm.nextOrPrevSong();
                init();//initalize again because a new MediaPlayer is made
                updateInfoDisplays();
            }
        }
    }

    @FXML
    private void nextSong(ActionEvent event) throws IOException, Exception {
        if (!mpm.isThisPlaylistEmpty(mpm.getCurrentPlaylistPlayling())) {
            //The code below immitates what spotify does when a next song button is pressed
            if (!mpm.isMusicPlayerInitialized()) {
                //This line basically initializes the MusicPlayer so that it is not null and can be used
                onFirstMusicPlayerPlay();
            }
            goToNextSong();
        }

    }

    private void goToNextSong() throws Exception {
        if (!mpm.getPlaySongInLoop()) {
            if (mpm.getPlayType().equals("Ordered Play")) {
                //This will clear the songHistory(), remember it is only used to keep track of songs when shuffling
                mpm.getSongHistory().clear();
                //The code below will let the mpm know to play the next song
                mpm.setIndexForOrderedPlay(mpm.getPlaylistSongsPlaying().indexOf(mpm.getSongObjectBeingPlayed()) + 1);

                mpm.nextOrPrevSong();
                //This code will set up the play button and audio info displays
                setUpPlayButton();
            } else if (mpm.getPlayType().equals("Random Play")) {
                //We get the index of the next song to keep track of which song to play next in the song history
                int indexOfNextSong = mpm.getPosInSongHistory() + 1;
                //If there is no next song left to play in the song history, then we play a random song and add that to our song history
                if (indexOfNextSong > mpm.getSongHistory().size() - 1) {
                    System.out.println("got random");
                    mpm.nextOrPrevSong();
                    setUpPlayButton();
                    return;
                } else {
                    //If there is a next song to play in our songHistory then we use the indexOfNextSong to find the next song to play
                    mpm.playSong(mpm.getSongHistory().get(indexOfNextSong));
                    //We increment the position in the songHistory to keep track of wether or not we have to get a random song or not
                    mpm.setPosInSongHistory(mpm.getPosInSongHistory() + 1);
                    setUpPlayButton();
                }
            }
        } else {
            //Since the looping code in in the manager, we just need to run code from the manager and update the UI
            mpm.nextOrPrevSong();
            init();//initalize again because a new MediaPlayer is made
            updateInfoDisplays();
        }
    }

    private void updateInfoDisplays() {
        //This code will update the UI with data of the current song playing
        songInfoViewList.getItems().clear();
        songInfoText.setText("Song name: " + mpm.getSongObjectBeingPlayed().getTitle() + "Song creator: " + mpm.getSongObjectBeingPlayed().getChannelName());
        songInfoViewList.getItems().add("Song name: " + mpm.getSongObjectBeingPlayed().getTitle());
        songInfoViewList.getItems().add("Song creator: " + mpm.getSongObjectBeingPlayed().getChannelName());
        songInfoViewList.getItems().add("Song duration: " + mpm.getSongObjectBeingPlayed().getVideoDuration());
        thumbnailImageView.setImage(new Image(mpm.getSongObjectBeingPlayed().getPathToThumbnail()));
    }

    private String getCurrentTimeStringFormatted(int currentseconds, int totalSeconds) {
        //This will get the current position of the song along with its total time EXx: 1:43/5:00
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
        mpm.getMediaPlayer().setOnEndOfMedia(new Runnable() {//this will tell the music player what to do when the song ends. Since a new media player is created each time, we must call the init() method again to set and initialize the media player again
            public void run() {
                try {
                    mpm.smartPlay();
                    init();
                    updateInfoDisplays();
                } catch (IOException ex) {
                    Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        mpm.getMediaPlayer().setOnPlaying(() -> {
            mpm.setPaused(false);
            playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
            playButton.setText("⏸︎");
            init();
            updateInfoDisplays();
            System.out.println("Setting the pause button to play");
        });

        mpm.getMediaPlayer().setOnPaused(() -> {
            //The if statement ensures that the mediaPlayer is not paused when seeking. We pause when we seek if not weird audio is generated
            if (!mpm.getIsSeeking()) {
                mpm.setPaused(true);
                playButton.setStyle("-fx-padding: -2 0 0 3; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
                playButton.setText("▶");
                //probably don't need the init() method below
                init();
                updateInfoDisplays();
                System.out.println("Setting the pause button to paused");
            }
        });

        mpm.getMediaPlayer().setOnError(new Runnable() {//this will tell the music player what to do when the song ends. Since a new media player is created each time, we must call the init() method again to set and initialize the media player again
            public void run() {
                mpm.resetPlayerOnError();
                init();
                updateInfoDisplays();
                mpm.getMediaPlayer().setOnPlaying(() -> {
                    mpm.getMediaPlayer().setStartTime(Duration.ZERO);
                    mpm.getMediaPlayer().setOnPlaying(null);
                });
                mpm.getMediaPlayer().play();
            }
        });

        mpm.getMediaPlayer().setOnHalted(() -> {
            mpm.resetPlayerOnError();
            init();
            updateInfoDisplays();
            mpm.getMediaPlayer().setOnPlaying(() -> {
                mpm.getMediaPlayer().setStartTime(Duration.ZERO);
                mpm.getMediaPlayer().setOnPlaying(null);
            });
            mpm.getMediaPlayer().play();
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                mpm.setVolume(volumeSlider.getValue());
                mpm.setSliderVolume(volumeSlider.getValue());
            }
        });

        seekSlider.setOnMousePressed((MouseEvent mouseEvent) -> {//This handles the seeking of the song
            mpm.setIsSeeking(true);
            mpm.getMediaPlayer().pause();//Pause the song so there is no weird audio. However do not change the boolean isPaused value so we can choose wether or not to resume the song or not after we finish seeking
        });

        seekSlider.setOnMouseReleased((MouseEvent mouseEvent) -> {//This handles the seeking of the song
            mpm.seekTo(Duration.seconds(seekSlider.getValue()));//Set where to resume the song
            System.out.println(seekSlider.getValue() + " seek slider");
            //Here we keep a backup of the current duration of the song just incase the mediaPlayer crashes, which it does everytime you disconnect a bluetooth headset for some reason
            mpm.setBackUpCurrentDuration(mpm.getMediaPlayer().getCurrentTime());
            if (!mpm.isSongPaused()) {
                mpm.resumeSong();//Resume the song once the user releases their mouse key and if they want it to resume
            }
            mpm.setIsSeeking(false);
        });

        mpm.getMediaPlayer().setOnReady(new Runnable() {//This will set the volume of the song, and the max value of the seekSlider once the media player has finished analyzing and reading the song.
            public void run() {
                mpm.setVolume(volumeSlider.getValue());//Sets the volume
                mpm.setSliderVolume(volumeSlider.getValue());
                seekSlider.setMax(mpm.getMediaPlayer().getTotalDuration().toSeconds());
                //seekSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> mpm.getMediaPlayer().getTotalDuration().toSeconds(), mpm.getMediaPlayer().totalDurationProperty()));//Sets the max values of the seekSlider to the duration of the song that is to be played
            }
        });

        mpm.getMediaPlayer().currentTimeProperty().addListener(new InvalidationListener() {//This will automatically update the seekSlider to match the current position of the song
            public void invalidated(Observable ov) {
                seekSlider.setValue(mpm.getCurrentTimeInSeconds());
                timeText.setText(getCurrentTimeStringFormatted((int) Math.floor(mpm.getCurrentTimeInSeconds()), (int) Math.floor(mpm.getTotalDurationInSeconds())));
            }
        });

        //Here we keep a backup of the current duration of the song just incase the mediaPlayer crashes, which it does everytime you disconnect a bluetooth headset for some reason
        mpm.getMediaPlayer().currentTimeProperty().addListener(mpm.getBackupDurationIlTracker());

    }

    public void contextMenuPlaySongOption() throws Exception {
        if (!mpm.getCurrentPlaylistPlayling().equals(playlistList.getSelectionModel().getSelectedItem())) {
            mpm.getSongHistory().clear();
        }
        mpm.setCurrentPlaylistPlayling(playlistList.getSelectionModel().getSelectedItem());
        mpm.syncPlaylistSongsPlaylingWithCurentSongsList();
        //Code above will set which songs from which playlist to play next after the song which is currently playing has finsihed
        mpm.playSong(mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndex()));
        mpm.setIndexForOrderedPlay(songList.getSelectionModel().getSelectedIndex() + 1);
        mpm.setPaused(false);
        if (!mpm.isMusicPlayerInitialized()) {
            mpm.setMusicPlayerInitialized(true);
        }
        playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
        playButton.setText("⏸︎");
        init();//initalize again because a new MediaPlayer is made
        updateInfoDisplays();
    }

    public void contextMenuAddToPlaylistOption() {
        updatePlaylistToAddToChoiceBox();
        comboBox.show();
    }

    public void contextMenuDeletePlaylistOption() throws Exception {
        String selectedItem = playlistList.getSelectionModel().getSelectedItem();

        AccountsDataManager.deletePlaylist(selectedItem);
        if (selectedItem.equals(mpm.getPlaylistCurrentlyViewing())) {
            mpm.setPlaylistCurrentlyViewing("All Songs");
            playlistList.getSelectionModel().select("All Songs");
            updateModelCurrentSongList();
        }
        updatePlaylistList();
        mpm.updateSongList(Accounts.getLoggedInAccount().getListOfSongDataObjects());
        playlistList.getSelectionModel().select(mpm.getPlaylistCurrentlyViewing());
        updateModelCurrentSongList();
    }

    public void updatePlaylistToAddToChoiceBox() {
        comboBox.getItems().clear();
        PlaylistMap map = Accounts.getLoggedInAccount().getPlaylistDataObject();
        comboBox.getItems().addAll(map.getArrayOfPlaylistNames());
    }

    public void deleteSongFromPlaylistOption() throws Exception {
        if (playlistList.getSelectionModel().getSelectedItem() != null) {
            AccountsDataManager.removeSongFromPlaylist(playlistList.getSelectionModel().getSelectedItem(), mpm.getArrayOfSdoFromCurrentSongListViaIndicies(songList.selectionModelProperty().get().getSelectedIndices()));
        } else {
            //This else statement should only run when the user does not click on any playlists during startup
            AccountsDataManager.removeSongFromPlaylist(mpm.getCurrentPlaylistPlayling(), mpm.getArrayOfSdoFromCurrentSongListViaIndicies(songList.selectionModelProperty().get().getSelectedIndices()));

        }
        updateModelCurrentSongList();
    }

    public void playPlaylistOption() throws IOException, Exception {
        if (!mpm.isThisPlaylistEmpty(playlistList.getSelectionModel().getSelectedItem())) {
            System.out.println("playling playlist");
            if (mpm.isMusicPlayerInitialized()) {
                mpm.setIndexForOrderedPlay(0);
                mpm.playThisPlaylist(playlistList.getSelectionModel().getSelectedItem());
                mpm.nextOrPrevSong();
                init();//initalize again because a new MediaPlayer is made
                updateInfoDisplays();
                mpm.setMusicPlayerInitialized(true);
                mpm.setPaused(false);
                playButton.setStyle("-fx-padding: -4 0 3 1; -fx-background-radius: 50px; -fx-border-radius: 50px; -fx-border-width: 3px; -fx-background-color: transparent; -fx-border-color: #f04444;");
                playButton.setText("⏸︎");
            } else {
                mpm.setIndexForOrderedPlay(0);
                mpm.playThisPlaylist(playlistList.getSelectionModel().getSelectedItem());
                onFirstMusicPlayerPlay();
            }
        }
    }

    public void deleteSongFromAccountOption() throws IOException, Exception {
        AccountsDataManager.deleteSong(mpm.getArrayOfSdoFromCurrentSongListViaIndicies(songList.selectionModelProperty().get().getSelectedIndices()));
        updateModelCurrentSongList();
    }

    public void editSongDataOption() throws IOException {
        //This creates a dialog popup to allow the user to edit the data of a SongDataObject
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/SongDialogEditor.fxml"));
        DialogPane songDialogEditor = fxmlLoader.load();
        SongDialogEditorController sdeController = fxmlLoader.getController();
        int indexOfSongToDisplay = songList.selectionModelProperty().get().getSelectedIndex();
        SongDataObject sdoToEdit = mpm.getCurrentSongList().get(indexOfSongToDisplay);
        sdeController.setDialogSongDisplay(sdoToEdit);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(songDialogEditor);
        dialog.setTitle("hi");
        dialog.setX(MouseInfo.getPointerInfo().getLocation().getX());
        dialog.setY(MouseInfo.getPointerInfo().getLocation().getY());
        Optional<ButtonType> buttonClicked = dialog.showAndWait();
        if (buttonClicked.get() == ButtonType.APPLY) {
            sdeController.applyDataChangesToSongDataObject(sdoToEdit);
            updateViewCurrentSongList();
        } else if (buttonClicked.get() == ButtonType.CANCEL) {
            return;
        }
    }

    public void editPlaylistNameOption() throws IOException, Exception {
        //This creates a dialog popup to allow the user to edit the name of a playlist
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/PlaylistDialogEditor.fxml"));
        DialogPane playlistDialogEditor = fxmlLoader.load();
        PlaylistDialogEditorController pdeController = fxmlLoader.getController();
        String playlistSong = playlistList.getSelectionModel().getSelectedItem();
        pdeController.setPlaylistToEdit(playlistSong);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(playlistDialogEditor);
        dialog.setTitle("hi");
        dialog.setX(MouseInfo.getPointerInfo().getLocation().getX());
        dialog.setY(MouseInfo.getPointerInfo().getLocation().getY());
        Optional<ButtonType> buttonClicked = dialog.showAndWait();
        if (buttonClicked.get() == ButtonType.APPLY) {
            pdeController.updatePlaylistName(playlistSong);
            //We update the playlistList so that the new name of the playlist shows up
            updatePlaylistList();
        } else if (buttonClicked.get() == ButtonType.CANCEL) {
            return;
        }
    }

    public void showSleepAlarmDialog() throws IOException, ParseException {
        if (SleepTimer.getTimerCurrentlyUsing() != null) {
            SleepTimer.getTimerCurrentlyUsing().stopTimerCheck();
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/SleepAlarmDialogEditor.fxml"));
        DialogPane songDialogEditor = fxmlLoader.load();
        SleepAlarmDialogEditorController sadeController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(songDialogEditor);
        dialog.setTitle("hi");
        dialog.setX(MouseInfo.getPointerInfo().getLocation().getX());
        dialog.setY(MouseInfo.getPointerInfo().getLocation().getY());
        Optional<ButtonType> buttonClicked = dialog.showAndWait();

        if (buttonClicked.get() == ButtonType.APPLY) {
            AccountsDataManager.saveSleepTimerSettings();
            if (SleepTimer.getTimerCurrentlyUsing().getEnableTimer()) {
                SleepTimer.getTimerCurrentlyUsing().startTimerCheck();
                System.out.println("Starting chek ");
            } else {
                SleepTimer.getTimerCurrentlyUsing().stopTimerCheck();
                System.out.println("Stopping chek ");
            }
        } else if (buttonClicked.get() == ButtonType.CANCEL) {
            SleepTimer setttingSleepalarm = Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer();
            SleepTimer.getTimerCurrentlyUsing().setHour(setttingSleepalarm.getHour());
            SleepTimer.getTimerCurrentlyUsing().setMinute(setttingSleepalarm.getMinute());
            SleepTimer.getTimerCurrentlyUsing().setEnableTimer(setttingSleepalarm.getEnableTimer());
        }
    }

    public void showAlarmClockDialog() throws IOException, ParseException {
        //This creates a dialog popup to allow the user to edit the data of a SongDataObject
        if (AlarmClock.getAlarmCurrentlyUsing() != null) {
            AlarmClock.getAlarmCurrentlyUsing().stopAlarmCheck();
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/AlarmClockDialogEditor.fxml"));
        DialogPane songDialogEditor = fxmlLoader.load();
        AlarmClockDialogEditorController acdeController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(songDialogEditor);
        dialog.setTitle("hi");
        dialog.setX(MouseInfo.getPointerInfo().getLocation().getX());
        dialog.setY(MouseInfo.getPointerInfo().getLocation().getY());
        Optional<ButtonType> buttonClicked = dialog.showAndWait();
        if (buttonClicked.get() == ButtonType.APPLY && !Accounts.getLoggedInAccount().getListOfSongDataObjects().isEmpty()) {
            AccountsDataManager.saveAlarmClockSettings();
            if (AlarmClock.getAlarmCurrentlyUsing().getEnableAlarm()) {
                AlarmClock.getAlarmCurrentlyUsing().startAlarmCheck();
            } else {
                AlarmClock.getAlarmCurrentlyUsing().stopAlarmCheck();
            }
        } else if (buttonClicked.get() == ButtonType.CANCEL) {
            SettingsObject setObj = Accounts.getLoggedInAccount().getSettingsObject();
            AlarmClock.getAlarmCurrentlyUsing().setAmOrPm(setObj.getAlarmClock().getAmOrPm());
            AlarmClock.getAlarmCurrentlyUsing().setHour(setObj.getAlarmClock().getHour());
            AlarmClock.getAlarmCurrentlyUsing().setMinute(setObj.getAlarmClock().getMinute());
            if (AlarmClock.getAlarmCurrentlyUsing().getEnableAlarm()) {
                AlarmClock.getAlarmCurrentlyUsing().startAlarmCheck();
            } else {
                AlarmClock.getAlarmCurrentlyUsing().stopAlarmCheck();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setContentText("You need to have downloaded at least one song before using this feature! Changes not saved");
            a.show();
        }
    }

    public void sortPlaylistOption() throws Exception {
        sortPlaylistChoiceBox.show();
    }

    private void copyUrlOption() {
        String stringToCopy = mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndex()).getVideoUrl();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyArtistNameOption() {
        String stringToCopy = mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndex()).getChannelName();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyTitleNameOption() {
        String stringToCopy = mpm.getCurrentSongList().get(songList.getSelectionModel().getSelectedIndex()).getTitle();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    public void setUpContextMenus() {
        MenuItem sortSongList = new MenuItem("Sort Song List");
        sortSongList.setOnAction(e -> sortChoiceBox.show());
        MenuItem editSongData = new MenuItem("Edit Song Data");
        editSongData.setOnAction(e -> {
            try {
                editSongDataOption();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem playSong = new MenuItem("Play Song");
        playSong.setOnAction(e -> {
            try {
                contextMenuPlaySongOption();
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem addToPlaylist = new MenuItem("Add To Playlist");
        addToPlaylist.setOnAction(e -> contextMenuAddToPlaylistOption());
        MenuItem deleteFromPlaylist = new MenuItem("Delete From Playlist");
        deleteFromPlaylist.setOnAction(e -> {
            try {
                deleteSongFromPlaylistOption();
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem deleteSong = new MenuItem("Delete From Account");
        deleteSong.setOnAction(e -> {
            try {
                deleteSongFromAccountOption();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem copyYoutubeUrl = new MenuItem("Copy Youtube Url");
        copyYoutubeUrl.setOnAction(e -> copyUrlOption());
        MenuItem copyTitleName = new MenuItem("Copy Title Name");
        copyTitleName.setOnAction(e -> copyTitleNameOption());
        MenuItem copyArtistName = new MenuItem("Copy Artist Name");
        copyArtistName.setOnAction(e -> copyArtistNameOption());
        SeparatorMenuItem seperator0 = new SeparatorMenuItem();
        SeparatorMenuItem seperator1 = new SeparatorMenuItem();
        SeparatorMenuItem seperator2 = new SeparatorMenuItem();
        songListContextMenu.getItems().addAll(playSong, seperator0, addToPlaylist, sortSongList, editSongData, seperator1, copyYoutubeUrl, copyTitleName, copyArtistName, seperator2, deleteFromPlaylist, deleteSong);
        MenuItem sortPlaylist = new MenuItem("Sort Playlist");
        sortPlaylist.setOnAction(e -> sortPlaylistChoiceBox.show());
        MenuItem editPlaylistName = new MenuItem("Edit Playlist Name");
        editPlaylistName.setOnAction(e -> {
            try {
                editPlaylistNameOption();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem deletePlaylist = new MenuItem("Delete Playlist");
        deletePlaylist.setOnAction(e -> {
            try {
                contextMenuDeletePlaylistOption();
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        MenuItem playPlaylist = new MenuItem("Play Playlist");
        playPlaylist.setOnAction(e -> {
            try {
                playPlaylistOption();
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        playlistListContextMenu.getItems().addAll(sortPlaylist, editPlaylistName, playPlaylist, deletePlaylist);
    }

    @FXML
    public void showSongListContextMenu(MouseEvent e) {
        if (songList.getSelectionModel().getSelectedIndex() != -1 || !songList.getSelectionModel().getSelectedIndices().isEmpty()) {
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println("worked");
                songListContextMenu.show(songList, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
            } else {
                songListContextMenu.hide();
            }
        }
    }

    @FXML
    public void showPlaylistListContextMenu(MouseEvent e) throws Exception {
        if (playlistList.getSelectionModel().getSelectedIndex() != -1) {
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println("worked");
                //updateModelCurrentSongList();
                playlistListContextMenu.show(playlistList, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
            } else {
                playlistListContextMenu.hide();
                mpm.setPlaylistCurrentlyViewing(playlistList.getSelectionModel().getSelectedItem());
                updateModelCurrentSongList();
            }
        }
    }

    private void updatePlaylistList() throws Exception {
        //Since all selections are wiped when updating, we must set what the selected item is once we finish updating.
        String itemSelected = playlistList.getSelectionModel().getSelectedItem();
        PlaylistMap map = Accounts.getLoggedInAccount().getPlaylistDataObject();
        playlistList.getItems().clear();
        playlistList.getItems().addAll(map.getArrayOfPlaylistNames());
        if (sortPlaylistChoiceBox.getSelectionModel().getSelectedItem() != null) {
            sortPlaylistList(sortPlaylistChoiceBox.getSelectionModel().getSelectedItem());
        }
        //Make sure that All Songs is always at the very top of the list
        playlistList.getItems().remove("All Songs");
        playlistList.getItems().add(0, "All Songs");
        playlistList.getSelectionModel().select(itemSelected);
    }

    private void updateModelCurrentSongList() throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            PlaylistMap map = Accounts.getLoggedInAccount().getPlaylistDataObject();
            int selectedIndex = playlistList.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String keyValue = playlistList.getItems().get(selectedIndex);
                System.out.println(keyValue);
                LinkedList<SongDataObject> songDataObjectsToAdd = map.getMapOfPlaylists().get(keyValue);
                mpm.updateSongList(songDataObjectsToAdd);//Updates the currentSongList. SongListView automatically updates
                if (sortChoiceBox.getValue() != null) {
                    //automatically sort the ModelSongList
                    sortModelCurrentSongList(sortChoiceBox.getValue());
                }
            }
        }
    }

    private void updateViewCurrentSongList() {
        //we clear the list and then put the new list of song names in
        System.out.println("updating current song list");
        ObservableList<String> stringsToAdd = FXCollections.observableArrayList();
        //We don't use clear if not the users selections will disappear
        for (String s : mpm.getArrayOfSongInfoInCurrentSongList()) {
            stringsToAdd.add(s);
        }

        for (String s : songList.getItems()) {
            stringsToAdd.remove(s);
        }

        songList.getItems().addAll(stringsToAdd);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            updatePlaylistList();
        } catch (Exception ex) {
            Logger.getLogger(MusicPlayerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
