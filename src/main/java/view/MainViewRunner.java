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
import java.net.URL;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import model.Accounts;
import model.AccountsDataManager;
import model.PathsManager;

public class MainViewRunner extends Application {

    public static Stage stageToReturn;
    public static SceneChanger sceneChanger;
    public static boolean slideBarLoaded = false;

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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginPageView.fxml"));//The file must be in the package this line is in!! if not you must do /"package name here"/"fxml file name here"
        Scene scene = new Scene(root, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/contextMenu.css").toExternalForm());
        stage.setTitle("MotisHarmony");
        stage.initStyle(StageStyle.TRANSPARENT);

        ArrayList<String> array = new ArrayList<>();
        SceneChanger screenController = new SceneChanger(scene);

        screenController.addScreen("LoginPage", FXMLLoader.load(getClass().getResource("/fxml/LoginPageView.fxml")));
        sceneChanger = screenController;
        setStage(stage);
        AccountsDataManager adm = new AccountsDataManager();
        System.out.println(adm.getAccPathToAutoLogIn());
        if (adm.getAccPathToAutoLogIn() != null) {
            Accounts.setLoggedInAccount(Accounts.deserializeAccountFromPath(adm.getAccPathToAutoLogIn()));
            PathsManager.setLoggedInUserDataPath(Accounts.getLoggedInAccount().getUsername());//We need to set up this path first to access the contents of the account the user is trying to log into.
            PathsManager.setUpPathsInsideUserDataPath();
            //Since these classes need an account to be created in order to initialize, we must set up the accounts first before adding these
            screenController.addScreen("DownloadPage", FXMLLoader.load(getClass().getResource("/fxml/DownloadPageView.fxml")));
            screenController.addScreen("BrowserPage", FXMLLoader.load(getClass().getResource("/fxml/BrowserPageView.fxml")));
            screenController.addScreen("MusicPlayerPage", FXMLLoader.load(getClass().getResource("/fxml/MusicPlayerView.fxml")));
            screenController.addScreen("SettingsPage", FXMLLoader.load(getClass().getResource("/fxml/SettingsPageView.fxml")));
            screenController.switchToDownloadPageView();//The Account signup was successful and we can now let the user use the application
        } else {
            screenController.switchToLoginPageView();
        }

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/MotisHarmonyIcon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static SceneChanger getSceneChanger() {
        return sceneChanger;
    }

    public static boolean getSlideBarRanOnce() {
        return slideBarLoaded;
    }

    public static void setSlideBarRanOnce(boolean tf) {
        slideBarLoaded = tf;
    }
}
