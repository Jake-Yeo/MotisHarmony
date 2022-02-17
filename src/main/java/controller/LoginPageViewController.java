/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import view.SceneChanger;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Account;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class LoginPageViewController implements Initializable {

    private int animationNum = 0;
    private FadeTransition fadeTransition = new FadeTransition();
    @FXML
    private AnchorPane loginSignupMainAnchorPane;
    @FXML
    private ImageView loginPageImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordPasswordField;
    @FXML
    private AnchorPane infoAnchorPane;
    @FXML
    private Button signupButton;
    @FXML
    private Button loginButton;

    private SceneChanger sceneController = new SceneChanger();
    @FXML
    private Text motisHarmonyText;
    @FXML
    private Text welcomeText;
    @FXML
    private Text taglineText;
    @FXML
    private Text pointOneText;
    @FXML
    private Text pointTwoText;
    @FXML
    private Text pointThreeText;

    private void playFadeAnimation() throws InterruptedException {
        //model.MusicPlayerManager.playMusic();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            if (animationNum == 0) {
                fadeTransition.setDuration(Duration.seconds(1));
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.setCycleCount(1);
                fadeTransition.setNode(motisHarmonyText);
                fadeTransition.play();
                animationNum++;
                System.out.println(animationNum);
            } else if (animationNum == 1) {
                fadeTransition.setNode(welcomeText);
                fadeTransition.play();
                animationNum++;
            } else if (animationNum == 2) {
                fadeTransition.setNode(taglineText);
                fadeTransition.play();
                animationNum++;
            } else if (animationNum == 3) {
                fadeTransition.setNode(pointOneText);
                fadeTransition.play();
                animationNum++;
            } else if (animationNum == 4) {
                fadeTransition.setNode(pointTwoText);
                fadeTransition.play();
                animationNum++;
            } else if (animationNum == 5) {
                fadeTransition.setNode(pointThreeText);
                fadeTransition.play();
                animationNum++;
            }
        }));
        timeline.setCycleCount(7);
        timeline.play();
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        Account.login(usernameTextField.getText(), passwordPasswordField.getText());
        //sceneController.switchToDownloadPageView();
    }

    public void signup(ActionEvent event) throws IOException, Exception {
        Account.signup(usernameTextField.getText(), passwordPasswordField.getText());
        //sceneController.switchToDownloadPageView();
    }

    @FXML
    public void mouseEnterSignupButton() {
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: #791c48;");
    }

    @FXML
    public void mouseExitSignupButton() {
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: transparent;");
    }

    @FXML
    public void mouseEnterLoginButton() {
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: #791c48;");
    }

    @FXML
    public void mouseExitLoginButton() {
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: transparent;");
    }

    @FXML
    public void mousePressedLoginButton() {
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: #791c48; -fx-background-color: #791c48; -fx-background-radius: 1em");
    }

    @FXML
    public void mouseReleasedLoginButton() {
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: transparent; -fx-background-color: transparent; -fx-background-radius: 1em");
    }

    @FXML
    public void mousePressedSignupButton() {
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: #791c48; -fx-background-color: #791c48; -fx-background-radius: 1em");
    }

    @FXML
    public void mouseReleasedSignupButton() {
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff7d; -fx-border-radius: 1em; -fx-border-color: transparent; -fx-background-color: transparent; -fx-background-radius: 1em");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loginSignupMainAnchorPane.setBackground(Background.EMPTY);//This will make the main anchor pane of the login page transparent for aethetics
        loginPageImageView.setImage(new Image(getClass().getResourceAsStream("/images/loginPage.png")));
        Rectangle clip = new Rectangle(
                loginPageImageView.getFitWidth(), loginPageImageView.getFitHeight()
        );
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        loginPageImageView.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = loginPageImageView.snapshot(parameters, null);

        // remove the rounding clip so that our effect can show through.
        loginPageImageView.setClip(null);
        loginPageImageView.setImage(image);

        clip = new Rectangle();
        clip.widthProperty().bind(infoAnchorPane.widthProperty());
        clip.heightProperty().bind(infoAnchorPane.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        infoAnchorPane.setClip(clip);
        motisHarmonyText.setOpacity(0);
        welcomeText.setOpacity(0);
        taglineText.setOpacity(0);
        pointOneText.setOpacity(0);
        pointTwoText.setOpacity(0);
        pointThreeText.setOpacity(0);
        try {
            playFadeAnimation();
        } catch (InterruptedException ex) {
            Logger.getLogger(LoginPageViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
