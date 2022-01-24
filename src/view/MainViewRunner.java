/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author Jake Yeo
 */
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class MainViewRunner extends Application {

    public static Stage stageToReturn;

    public static void launchPanel(String[] args) {
        launch(args);
    }

    private void setStage(Stage stage) {
        stageToReturn = stage;
    }

    public static Stage getStage() {//allows you to get the stage from the controller
        return stageToReturn;
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Stage stage = new Stage();

        Parent root = FXMLLoader.load(getClass().getResource("LoginPageView.fxml"));//The file must be in the package this line is in!! if not you must do /"package name here"/"fxml file name here"
        Scene scene = new Scene(root, Color.TRANSPARENT);
        stage.setTitle("MotisHarmony");
        //stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("/MotisHarmonyIcon.png")));
        stage.initStyle(StageStyle.TRANSPARENT);

        ArrayList<String> array = new ArrayList<>();
        stage.setScene(scene);
        setStage(stage);
        stage.show();
    }
}
