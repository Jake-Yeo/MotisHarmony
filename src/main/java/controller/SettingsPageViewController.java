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
import javafx.scene.layout.AnchorPane;
import model.AccountsDataManager;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void logout(ActionEvent event) throws Exception {
        AccountsDataManager adm = new AccountsDataManager();
        adm.setPathOfAccToAutoLogIn(null);
        MainViewRunner.getSceneChanger().switchToLoginPageView();
    }
    
}
