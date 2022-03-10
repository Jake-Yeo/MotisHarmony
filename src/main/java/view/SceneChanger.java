/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;//This should actually be in the view package in order to follow proper MVC architecture

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import view.MainViewRunner;

/**
 *
 * @author Jake Yeo
 */
public class SceneChanger {

    private FXMLLoader loader = new FXMLLoader();

    public SceneChanger() {
    }

    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;

    public SceneChanger(Scene main) {
        this.main = main;
    }

    public void addScreen(String name, Pane pane) {
        screenMap.put(name, pane);
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
    }

    public void activate(String name) {
        main.setRoot(screenMap.get(name));
    }

    public static void switchToDownloadPageView() throws IOException {
        MainViewRunner.getSceneChanger().activate("DownloadPage");
    }

    public static void switchToLoginPageView() throws IOException {
        MainViewRunner.getSceneChanger().activate("LoginPage");
    }

    public static void switchToBrowserPageView() throws IOException {
        MainViewRunner.getSceneChanger().activate("BrowserPage");
    }

    public static void switchToMusicPlayerPageView() throws IOException {
        MainViewRunner.getSceneChanger().activate("MusicPlayerPage");
    }
}
