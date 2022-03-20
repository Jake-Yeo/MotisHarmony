/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.MouseInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.YoutubeDownloader;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class BrowserPageViewController implements Initializable {

    private ContextMenu contextMenu = new ContextMenu();
    @FXML
    private WebView browserWebView;
    private WebEngine engine;
    @FXML
    private AnchorPane webViewMainAnchorPane;
    @FXML
    private Button downloadUrlButton;
    @FXML
    private ComboBox<String> bookMarkComboBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setUpContextMenu();
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(webViewMainAnchorPane.widthProperty());
        clip.heightProperty().bind(webViewMainAnchorPane.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        webViewMainAnchorPane.setClip(clip);
        //browserWebView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/538.19 (KHTML, like Gecko) JavaFX/8.0 Safari/538.19");
        browserWebView.setContextMenuEnabled(true);
        /*WebConsoleListener.setDefaultListener(new WebConsoleListener() { //This does not work and causes an error
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });
         */
        browserWebView.setContextMenuEnabled(false);
        browserWebView.getEngine().load("https://www.youtube.com/");//https://www.google.ca/videohp
        browserWebView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
    }

    @FXML
    public void downloadBrowserUrl(ActionEvent event) throws IOException {
        new Thread(
                new Runnable() {
            public void run() {
                YoutubeDownloader.addYoutubeLinkToDownloadQueueAndStartDownload(browserWebView.getEngine().getLocation());
            }
        }).start();
    }

    @FXML
    public void showContextMenu(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            contextMenu.show(browserWebView, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
        } else {
            contextMenu.hide();
        }
    }

    public void turnOffWebEngine() {
        //browserWebView.getEngine().load("https://www.youtube.com/");
    }

    @FXML
    public void goBack() {
        browserWebView.getEngine().executeScript("history.back()");
    }

    @FXML
    public void goForward() {
        browserWebView.getEngine().executeScript("history.forward()");
    }

    private void downloadVideoOrPlaylist() throws IOException {//we may have to prevent button spamming.
        new Thread(
                new Runnable() {
            public void run() {
                YoutubeDownloader.addYoutubeLinkToDownloadQueueAndStartDownload(browserWebView.getEngine().getLocation());
            }
        }).start();
    }

    public void setUpContextMenu() {
        MenuItem downloadLink = new MenuItem("Download Video Audio");
        downloadLink.setOnAction(e -> {
            try {
                downloadVideoOrPlaylist();
            } catch (IOException ex) {
                Logger.getLogger(BrowserPageViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        MenuItem reload = new MenuItem("Reload");
        reload.setOnAction(e -> browserWebView.getEngine().reload());

        MenuItem goBack = new MenuItem("Go Back");
        goBack.setOnAction(e -> goBack());

        MenuItem goForward = new MenuItem("Go Forward");
        goForward.setOnAction(e -> goForward());
        contextMenu.getItems().addAll(reload, goBack, goForward, downloadLink);
    }
}
