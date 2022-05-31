/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import view.MainViewRunner;

/**
 *
 * @author Jake Yeo
 */
public class UIHelper {

    public static Rectangle getDialogPaneClip(DialogPane dp) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(dp.widthProperty());
        clip.heightProperty().bind(dp.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        return clip;
    }

    public static Alert getCustomAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        //Styling
        a.getDialogPane().getStylesheets().add("/css/customDialogPanes.css");
        a.initStyle(StageStyle.TRANSPARENT);
        ImageView errorIcon = new ImageView(new Image(MainViewRunner.class.getResourceAsStream("/images/error.png")));
        errorIcon.setFitHeight(48);
        errorIcon.setFitWidth(48);
        a.getDialogPane().setGraphic(errorIcon);
        a.getDialogPane().setClip(UIHelper.getDialogPaneClip(a.getDialogPane()));
        a.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        a.setContentText(message);
        return a;
    }
}
