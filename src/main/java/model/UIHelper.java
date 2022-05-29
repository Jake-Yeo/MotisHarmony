/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

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
}
