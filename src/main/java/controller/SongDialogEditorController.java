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
import model.SongDataObject;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class SongDialogEditorController implements Initializable {

    @FXML
    private TextField songTextField;
    @FXML
    private TextField artistTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void setDialogSongDisplay(SongDataObject sdoToDisplay) {
        songTextField.setText(sdoToDisplay.getTitle());
        artistTextField.setText(sdoToDisplay.getChannelName());
    }

    @FXML
    public void applyDataChangesToSongDataObject(SongDataObject sdoToUpdate) {
        sdoToUpdate.setVideoTitle(songTextField.getText());
        sdoToUpdate.setChannelName(artistTextField.getText());
    }

}
