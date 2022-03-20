/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import view.SceneChanger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation.Status;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import view.MainViewRunner;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class SlidingMenuBarController implements Initializable {

    private SceneChanger sceneChanger = new SceneChanger();
    private boolean isSlidingMenuOpen = false;
    private TranslateTransition translateTransition = new TranslateTransition();
    private BrowserPageViewController browserPageViewcontroller = new BrowserPageViewController();
    @FXML
    private Button switchToSettingsButton;
    @FXML
    private Button switchToDownloadPageButton;
    @FXML
    private Button switchToBrowserButton;
    @FXML
    private Button menuOpenCloseButton;
    @FXML
    private Button switchToPlayerButton;
    @FXML
    private AnchorPane buttonBackgroundAnchorPane;
    @FXML
    private AnchorPane slidingMenuMainAnchorPane;

    @FXML
    private void menuOpenClose(ActionEvent event) {
        if (slidingMenuMainAnchorPane.getLayoutX() < -50) {//Since the menu is loaded open, we must set the x position to a negative number first, this will handle translations when the menu is first initallized.
            if (translateTransition.getStatus() != Status.RUNNING) {//prevents the user from spam clicking the menu button and glitching it out
                if (!isSlidingMenuOpen) {
                    translateTransition.setDuration(Duration.seconds(0.2));
                    translateTransition.setToX(196);
                    translateTransition.setNode(slidingMenuMainAnchorPane);
                    translateTransition.play();
                    isSlidingMenuOpen = !isSlidingMenuOpen;
                } else {
                    translateTransition.setDuration(Duration.seconds(0.2));
                    translateTransition.setToX(0);
                    translateTransition.setNode(slidingMenuMainAnchorPane);
                    translateTransition.play();
                    isSlidingMenuOpen = !isSlidingMenuOpen;
                }
            }
        } else {//This will handle translations when the menu is left open and not set to a closed position
            if (translateTransition.getStatus() != Status.RUNNING) {//prevents the user from spam clicking the menu button and glitching it out
                if (!isSlidingMenuOpen) {
                    translateTransition.setDuration(Duration.seconds(0.2));
                    translateTransition.setToX(-196);
                    translateTransition.setNode(slidingMenuMainAnchorPane);
                    translateTransition.play();
                    isSlidingMenuOpen = !isSlidingMenuOpen;
                } else {
                    translateTransition.setDuration(Duration.seconds(0.2));
                    translateTransition.setToX(0);
                    translateTransition.setNode(slidingMenuMainAnchorPane);
                    translateTransition.play();
                    isSlidingMenuOpen = !isSlidingMenuOpen;
                }
            }
        }
    }

    @FXML
    private void switchToPlayer(ActionEvent event) throws IOException {
        sceneChanger.switchToMusicPlayerPageView();
        System.out.println("player");
    }

    @FXML
    private void switchToBrowser(ActionEvent event) throws IOException {
        sceneChanger.switchToBrowserPageView();
        System.out.println("browser");
    }

    @FXML
    private void switchToDownloadPage(ActionEvent event) throws IOException {
        sceneChanger.switchToDownloadPageView();
        System.out.println("download");
    }

    @FXML
    private void switchToSettings(ActionEvent event
    ) {
        System.out.println("settings");
    }

    /////////////////////////////////
    @FXML
    public void mouseEnterMenuOpenCloseButton() {
        buttonBackgroundAnchorPane.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-border-width: 3px; -fx-padding: 0; -fx-border-radius: 0 20px 0 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-background-radius: 0 20px 0 0;");
    }

    @FXML
    public void mouseExitMenuOpenCloseButton() {
        buttonBackgroundAnchorPane.setStyle("-fx-border-color: transparent; -fx-border-width: 3px; -fx-padding: 0; -fx-border-radius: 0 20px 0 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-background-radius: 0 20px 0 0;");
    }

    @FXML
    public void mousePressedMenuOpenCloseButton() {
        buttonBackgroundAnchorPane.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-border-width: 3px; -fx-padding: 0; -fx-border-radius: 0 20px 0 0; -fx-background-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-text-fill: #ffffff7d; -fx-background-radius: 0 20px 0 0;");
    }

    @FXML
    public void mouseReleasedMenuOpenCloseButton() {
        buttonBackgroundAnchorPane.setStyle("-fx-border-color: transparent; -fx-border-width: 3px; -fx-padding: 0; -fx-border-radius: 0 20px 0 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-background-radius: 0 20px 0 0;");
    }

    /////////////////////////////////
    @FXML
    public void mouseEnterSwitchToPlayerButton() {
        switchToPlayerButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseExitSwitchToPlayerButton() {
        switchToPlayerButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mousePressedSwitchToPlayerButton() {
        switchToPlayerButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-text-fill:  #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseReleasedSwitchToPlayerButton() {
        switchToPlayerButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }
    /////////////////////////////////

    @FXML
    public void mouseEnterSwitchToBrowserButton() {
        switchToBrowserButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseExitSwitchToBrowserButton() {
        switchToBrowserButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mousePressedSwitchToBrowserButton() {
        switchToBrowserButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-text-fill:  #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseReleasedSwitchToBrowserButton() {
        switchToBrowserButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }
    /////////////////////////////////

    @FXML
    public void mouseEnterSwitchToDownloadPageButton() {
        switchToDownloadPageButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseExitSwitchToDownloadPageButton() {
        switchToDownloadPageButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mousePressedSwitchToDownloadPageButton() {
        switchToDownloadPageButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-text-fill:  #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseReleasedSwitchToDownloadPageButton() {
        switchToDownloadPageButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    /////////////////////////////////
    @FXML
    public void mouseEnterSwitchToSettingsButton() {
        switchToSettingsButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseExitSwitchToSettingsButton() {
        switchToSettingsButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mousePressedSwitchToSettingsButton() {
        switchToSettingsButton.setStyle("-fx-border-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ff3e37, #db194d, #ae0a58, #7c1257, #4c154a); -fx-text-fill:  #ffffff7d; -fx-border-width: 3px");
    }

    @FXML
    public void mouseReleasedSwitchToSettingsButton() {
        switchToSettingsButton.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-background-radius: 0; -fx-border-radius: 0; -fx-background-color: linear-gradient(to top, #ee4540, #c12c4e, #8e224e, #5b1d43, #2d142c); -fx-text-fill: #ffffff7d; -fx-border-width: 3px");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        // TODO
        browserPageViewcontroller.turnOffWebEngine();
        if (!MainViewRunner.getSlideBarRanOnce()) {
            slidingMenuMainAnchorPane.setLayoutX(-196);
            MainViewRunner.setSlideBarRanOnce(true);
        }

    }

}
