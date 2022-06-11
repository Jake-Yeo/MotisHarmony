/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import model.AccountsDataManager;
import view.MainViewRunner;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class ExitButtonController implements Initializable {//if you want to change the colors of the exit button without changing it throughout the whole app, then use import instead of include

    @FXML
    private Button exitButton;
    @FXML
    private Button minimizeButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void mouseExitExitMainButton(MouseEvent event) {
        exitButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ee4540; -fx-border-radius: 2em 2em 0 0; -fx-background-radius: 2em 2em 0 0; -fx-padding: 0;");
    }

    @FXML
    private void mouseEnterExitMainButton(MouseEvent event) {
        exitButton.setStyle("-fx-background-color: #510a32; -fx-border-color: #ee4540; -fx-border-radius: 2em 2em 0 0; -fx-background-radius: 2em 2em 0 0; -fx-padding: 0;");
    }

    @FXML
    private void mouseExitExitMinimizeButton(MouseEvent event) {
        minimizeButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ee4540; -fx-border-radius: 0 0 2em 2em; -fx-background-radius: 0 0 2em 2em;; -fx-padding: 0;");
    }

    @FXML
    private void mouseEnterMinimizeButton(MouseEvent event) {
        minimizeButton.setStyle("-fx-background-color: #510a32; -fx-border-color: #ee4540; -fx-border-radius: 0 0 2em 2em; -fx-background-radius: 0 0 2em 2em;; -fx-padding: 0;");
    }

    @FXML
    private void exitAppMainButton(ActionEvent event) throws Exception {
        AccountsDataManager.saveAllSettings();//Saves all account data so far into an .acc file
        //System.out.println("Exited!");
        System.exit(0);
    }

    @FXML
    private void minimizeAppMainButton(ActionEvent event) {
        MainViewRunner.getStage().setIconified(true);
    }

}
