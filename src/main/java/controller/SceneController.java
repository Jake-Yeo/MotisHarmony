/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import view.MainViewRunner;

/**
 *
 * @author Jake Yeo
 */
public class SceneController {
    
    public SceneController() {
    }
    
    public void switchToDownloadPageView() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/DownloadPageView.fxml"));//if the fxml file is in another package you must put /"package name here"/"fxml file name here"
        Scene scene = new Scene(root, Color.TRANSPARENT);
        MainViewRunner.getStage().setScene(scene);
        MainViewRunner.getStage().show();
    }

    public void switchToLoginPageView() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginPageView.fxml"));//if the fxml file is in another package you must put /"package name here"/"fxml file name here"
        Scene scene = new Scene(root, Color.TRANSPARENT);
        MainViewRunner.getStage().setScene(scene);
        MainViewRunner.getStage().show();
    }

    public void switchToBrowserPageView() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/BrowserPageView.fxml"));//if the fxml file is in another package you must put /"package name here"/"fxml file name here"
        Scene scene = new Scene(root, Color.TRANSPARENT);
        MainViewRunner.getStage().setScene(scene);
        MainViewRunner.getStage().show();
    }
}