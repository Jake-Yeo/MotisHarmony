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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.MainViewRunner;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class DragWindowButtonController implements Initializable {

    private double yOffSet = 0;
    private double xOffSet = 0;
    @FXML
    private Button dragWindowButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void mouseEnterDragWindowButton() {
        dragWindowButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0em 0em 10em 0em; -fx-border-color: #ee4540; -fx-border-radius: 0em 0em 10em 0em; -fx-border-width:  0 4 4 0;");
    }

    @FXML
    public void mouseExitDragWindowButton() {
        dragWindowButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0em 0em 10em 0em; -fx-border-color: #841439; -fx-border-radius: 0em 0em 10em 0em; -fx-border-width:  0 4 4 0;");
    }

    @FXML
    public void mouseDragDragWindowButton(MouseEvent mouse) {
        Stage stage = (Stage) ((Node) mouse.getSource()).getScene().getWindow();
        stage.setX(mouse.getScreenX() - xOffSet);
        stage.setY(mouse.getScreenY() - yOffSet);
        MainViewRunner.getStage().setX(mouse.getScreenX() - xOffSet);
        MainViewRunner.getStage().setY(mouse.getScreenY() - yOffSet);
        dragWindowButton.setStyle("-fx-background-color: #ee4540; -fx-background-radius: 0em 0em 10em 0em; -fx-border-color: #ee4540; -fx-border-radius: 0em 0em 10em 0em; -fx-border-width:  0 4 4 0;");
    }

    @FXML
    private void mousePressDragWindowButton(MouseEvent mouse) {
        yOffSet = mouse.getSceneY();
        xOffSet = mouse.getSceneY();
        dragWindowButton.setStyle("-fx-background-color: #ee4540; -fx-background-radius: 0em 0em 10em 0em; -fx-border-color: #ee4540; -fx-border-radius: 0em 0em 10em 0em; -fx-border-width:  0 4 4 0;");
    }

    @FXML
    public void mouseReleaseDragWindowButton() {
        dragWindowButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0em 0em 10em 0em; -fx-border-color: #ee4540; -fx-border-radius: 0em 0em 10em 0em; -fx-border-width:  0 4 4 0;");
    }

}
