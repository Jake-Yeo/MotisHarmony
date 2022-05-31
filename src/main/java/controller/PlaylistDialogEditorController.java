/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import model.Accounts;
import model.AccountsDataManager;
import model.MusicPlayerManager;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class PlaylistDialogEditorController implements Initializable {

    @FXML
    private TextField playlistNameTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setPlaylistToEdit(String playlistName) {
        playlistNameTextField.setText(playlistName);
    }
    
    public String getPlaylistNameTextFieldText() {
        return playlistNameTextField.getText();
    }

    public void updatePlaylistName(String playlistName) throws Exception {
        MusicPlayerManager mpm = MusicPlayerManager.getMpmCurrentlyUsing();
        //Here we update the name of the current song playing if the playlist which is being renamed is the same as the playlist being played
        if (mpm.getCurrentPlaylistPlayling().equals(playlistName)) {
            mpm.setCurrentPlaylistPlayling(playlistNameTextField.getText());
        }
        AccountsDataManager.renamePlaylist(playlistName, playlistNameTextField.getText());
    }

}
