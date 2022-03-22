/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import model.Accounts;
import model.AccountsDataManager;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        saveDownloadQueueRadioButton.setSelected(Accounts.getLoggedInAccount().getSettingsObject().getSaveDownloadQueue());
    }

    @FXML
    private void logout(ActionEvent event) throws Exception {
        AccountsDataManager adm = new AccountsDataManager();
        adm.setPathOfAccToAutoLogIn(null);
        YoutubeDownloader.setStopDownloading(true);
        YoutubeDownloader.setStopAllDownloadingProcesses(true);
        Accounts.setLoggedInAccount(null);
        YoutubeDownloader.getYoutubeUrlDownloadQueueList().clear();
        MainViewRunner.getSceneChanger().switchToLoginPageView();
    }

    @FXML
    private void updateSaveDownloadQueue(ActionEvent event) throws Exception {
        AccountsDataManager.setSaveDownloadQueue(saveDownloadQueueRadioButton.isSelected());
        try {
            AccountsDataManager.updateSongsInQueueList(YoutubeDownloader.getYoutubeUrlDownloadQueueList());
        } catch (Exception ex) {
            Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
