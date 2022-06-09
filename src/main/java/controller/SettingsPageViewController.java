/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import model.Accounts;
import model.AccountsDataManager;
import model.AlarmClock;
import model.Encryption;
import model.MusicPlayerManager;
import model.SleepTimer;
import view.UIHelper;
import model.YoutubeDownloader;
import view.MainViewRunner;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class SettingsPageViewController implements Initializable {

    @FXML
    private AnchorPane settingsViewMainAnchorPane;
    @FXML
    private Button logoutButton;
    @FXML
    private RadioButton saveDownloadQueueRadioButton;
    @FXML
    private RadioButton saveSongPositionRadioButton;
    @FXML
    private RadioButton savePlayPreference;
    @FXML
    private RadioButton stayLoggedInRadioButton;
    @FXML
    private RadioButton displaySongOnClickRadioButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private ImageView settingsPageBackgroundImageView;
    @FXML
    private Button infoButton;
    @FXML
    private Button headphoneActionButton;
    @FXML
    private ChoiceBox<String> headphonesActionChoiceBox;
    @FXML
    private Slider audioBalanceSlider;
    @FXML
    private Button closeAllChromeDriversButton;
    @FXML
    private RadioButton soundVisualizerRadioButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        headphonesActionChoiceBox.getItems().add("Next Song");
        headphonesActionChoiceBox.getItems().add("Pause/Play");
        headphonesActionChoiceBox.getItems().add("Do Nothing");
        headphonesActionChoiceBox.setVisible(false);
        headphonesActionChoiceBox.getSelectionModel().select(Accounts.getLoggedInAccount().getSettingsObject().getHeadphoneAction());
        headphonesActionChoiceBox.setOnAction(e -> {//I tried to do this via scene builder but it didn't work for some reason
            try {
                updateHeadphoneAction();
            } catch (Exception ex) {
                Logger.getLogger(SettingsPageViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        audioBalanceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                AccountsDataManager.setAudioBalance(audioBalanceSlider.getValue());
                if (MusicPlayerManager.getMpmCurrentlyUsing().getMediaPlayer() != null) {
                    MusicPlayerManager.getMpmCurrentlyUsing().getMediaPlayer().setBalance(audioBalanceSlider.getValue());
                }
            }
        });

        settingsPageBackgroundImageView.setImage(new Image("/images/settingsPageBackground.png"));
        logoutButton.getStylesheets().add("/css/settingsPageCustomButtons.css");
        deleteAccountButton.getStylesheets().add("/css/settingsPageCustomButtons.css");
        headphoneActionButton.getStylesheets().add("/css/settingsPageCustomHeadphoneActionButton.css");
        closeAllChromeDriversButton.getStylesheets().add("/css/settingsPageCustomHeadphoneActionButton.css");
        infoButton.getStylesheets().add("/css/settingsPageCustomButtons.css");
        audioBalanceSlider.getStylesheets().add("/css/customSeekSlider.css");//poorly named css file, still works though

        audioBalanceSlider.setMax(1);
        audioBalanceSlider.setMin(-1);
        audioBalanceSlider.setValue(Accounts.getLoggedInAccount().getSettingsObject().getAudioBalance());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(settingsViewMainAnchorPane.widthProperty());
        clip.heightProperty().bind(settingsViewMainAnchorPane.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        settingsViewMainAnchorPane.setClip(clip);

        saveDownloadQueueRadioButton.getStylesheets().add("/css/customRadioButton.css");
        saveSongPositionRadioButton.getStylesheets().add("/css/customRadioButton.css");
        savePlayPreference.getStylesheets().add("/css/customRadioButton.css");
        stayLoggedInRadioButton.getStylesheets().add("/css/customRadioButton.css");
        displaySongOnClickRadioButton.getStylesheets().add("/css/customRadioButton.css");
        soundVisualizerRadioButton.getStylesheets().add("/css/customRadioButton.css");

        saveDownloadQueueRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getSaveDownloadQueue());
        saveSongPositionRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getSaveSongPosition());
        savePlayPreference.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getSavePlayPreference());
        stayLoggedInRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getStayLoggedIn());
        displaySongOnClickRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getAutoDisplayNextSong());
        soundVisualizerRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getEnableSoundVisualizer());
    }

    @FXML
    private void showHeadphoneActionChoiceBox(ActionEvent event) {
        headphonesActionChoiceBox.show();
    }

    @FXML
    private void closeAllChromeDrivers() throws IOException {
        Alert warnDialog = UIHelper.getCustomWarning("Warning! Clicking on yes will also close any Chrome Browsers you yourself have opened! Are you sure you want to continue?");
        warnDialog.getButtonTypes().add(ButtonType.YES);
        warnDialog.getButtonTypes().add(ButtonType.NO);
        warnDialog.getButtonTypes().remove(ButtonType.CANCEL);
        warnDialog.getButtonTypes().remove(ButtonType.OK);

        Optional<ButtonType> buttonClicked = warnDialog.showAndWait();
        if (buttonClicked.get() == ButtonType.YES) {
            if (YoutubeDownloader.getYtdCurrentlyUsing().getStatus().getValue() != null && YoutubeDownloader.getYtdCurrentlyUsing().getStatus().getValue().equals("Finished Downloading Queue")) {
                YoutubeDownloader.closeAndQuitAllChromeDrivers();
            } else if (YoutubeDownloader.getYtdCurrentlyUsing().getStatus().getValue() == null) {
                UIHelper.getCustomAlert("The does program not have any Chrome Drivers for you to close!").show();
            } else {
                UIHelper.getCustomAlert("You need to have an empty Download Queue to use this!").show();
            }
        } else if (buttonClicked.get() == ButtonType.NO) {
            return;
        }
    }

    @FXML
    private void logout(ActionEvent event) throws Exception {
        //Stop the sleep timer from checking the time
        SleepTimer.getTimerCurrentlyUsing().stopTimerCheck();
        //We make sure that the sleepTimer is no longer enabled since there's no point in having a timer that starts when the app starts
        Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer().setEnableTimer(false);
        AccountsDataManager.saveAllSettings();
        MusicPlayerManager.getMpmCurrentlyUsing().stopDisposeMediaPlayer();
        AccountsDataManager adm = new AccountsDataManager();
        adm.setPathOfAccToAutoLogIn(null);
        YoutubeDownloader.getYtdCurrentlyUsing().setStopDownloading(true);
        YoutubeDownloader.getYtdCurrentlyUsing().setStopAllDownloadingProcesses(true);
        Accounts.setLoggedInAccount(null);
        //Stop the alarm clock from checking the time
        AlarmClock.getAlarmCurrentlyUsing().stopAlarmCheck();
        //Makes the sliding bar menu animate correctly
        MainViewRunner.setSlideBarRanOnce(false);
        YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList().clear();
        //initialize login page before switching
        MainViewRunner.getSceneChanger().addScreen("LoginPage", FXMLLoader.load(getClass().getResource("/fxml/LoginPageView.fxml")));
        MainViewRunner.getSceneChanger().switchToLoginPageView();
    }

    @FXML
    private void updateEnableSoundVisualizer(ActionEvent event) throws Exception {
        AccountsDataManager.setEnableSoundVisualizer(soundVisualizerRadioButton.isSelected());
        MusicPlayerManager.getMpmCurrentlyUsing().changeEnableSoundVisualizerUpdater();
    }

    @FXML
    private void updateSaveDownloadQueue(ActionEvent event) throws Exception {
        AccountsDataManager.setSaveDownloadQueue(saveDownloadQueueRadioButton.isSelected());
    }

    @FXML
    private void updateSaveSongPosition(ActionEvent event) throws Exception {
        AccountsDataManager.setSaveSongPosition(saveSongPositionRadioButton.isSelected());
    }

    @FXML
    private void updateSavePlayPreference(ActionEvent event) throws Exception {
        AccountsDataManager.setSavePlayPreference(savePlayPreference.isSelected());
    }

    @FXML
    private void updateStayLoggedIn(ActionEvent event) throws Exception {
        AccountsDataManager.setStayLoggedIn(stayLoggedInRadioButton.isSelected());
    }

    @FXML
    private void updateDisplaySongOnClick(ActionEvent event) throws Exception {
        AccountsDataManager.setUpdateDisplaySongOnClick(displaySongOnClickRadioButton.isSelected());
    }

    private void updateHeadphoneAction() throws Exception {
        System.out.println(headphonesActionChoiceBox.getSelectionModel().getSelectedItem());
        if (headphonesActionChoiceBox.getSelectionModel().getSelectedItem() != null) {
            AccountsDataManager.setHeadphoneAction(headphonesActionChoiceBox.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void showDeletionAccountDialogBox(ActionEvent event) throws IOException, Exception {
        //This creates a dialog popup to allow the user to delete their account
        Encryption encryption = new Encryption(Accounts.getLoggedInAccount().getKey());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/AccountDeletionDialog.fxml"));
        DialogPane accountDeletionDialog = fxmlLoader.load();
        AccountDeletionDialogController addcController = fxmlLoader.getController();
        accountDeletionDialog.getStylesheets().add("/css/customDialogPanes.css");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(accountDeletionDialog);
        dialog.setTitle("Delete Account");
        accountDeletionDialog.getScene().getWindow().centerOnScreen();
        dialog.initStyle(StageStyle.TRANSPARENT);
        accountDeletionDialog.setClip(UIHelper.getDialogPaneClip(accountDeletionDialog));
        dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);

        Optional<ButtonType> buttonClicked = dialog.showAndWait();
        if (buttonClicked.get() == ButtonType.CANCEL) {

        } else if (buttonClicked.get() == ButtonType.FINISH) {
            if (encryption.sha256Hash(encryption.encrypt(addcController.getPasswordText())).equals(Accounts.getLoggedInAccount().getPassword())) {
                AccountsDataManager.deleteCurrentAccount();
                MusicPlayerManager.getMpmCurrentlyUsing().stopDisposeMediaPlayer();
                AccountsDataManager adm = new AccountsDataManager();
                adm.setPathOfAccToAutoLogIn(null);
                YoutubeDownloader.getYtdCurrentlyUsing().setStopDownloading(true);
                YoutubeDownloader.getYtdCurrentlyUsing().setStopAllDownloadingProcesses(true);
                Accounts.setLoggedInAccount(null);
                //Stop the alarm clock from checking the time
                AlarmClock.getAlarmCurrentlyUsing().stopAlarmCheck();
                //Makes the sliding bar menu animate correctly
                MainViewRunner.setSlideBarRanOnce(false);
                YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList().clear();
                //initialize login page before switching
                MainViewRunner.getSceneChanger().addScreen("LoginPage", FXMLLoader.load(getClass().getResource("/fxml/LoginPageView.fxml")));
                MainViewRunner.getSceneChanger().switchToLoginPageView();
            } else {
                addcController.clearPasswordField();
                UIHelper.getCustomAlert("The password you entered is wrong!").show();
            }
        }
    }

    @FXML
    private void showInfoDialogBox(ActionEvent event) throws IOException, Exception {
        //This creates a dialog popup to allow the user to delete their account
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainViewRunner.class.getResource("/fxml/InfoDialog.fxml"));
        DialogPane infoDialog = fxmlLoader.load();
        InfoDialogController infoController = fxmlLoader.getController();
        infoDialog.getStylesheets().add("/css/customDialogPanes.css");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(infoDialog);
        dialog.setTitle("Info");
        infoDialog.getScene().getWindow().centerOnScreen();
        dialog.initStyle(StageStyle.TRANSPARENT);
        infoDialog.setClip(UIHelper.getDialogPaneClip(infoDialog));
        dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        Optional<ButtonType> buttonClicked = dialog.showAndWait();
    }

}
