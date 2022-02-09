/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import java.awt.MouseInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.YoutubeDownloaderManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;

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
        clip.setArcWidth(30);//this sets the rounded corners
        clip.setArcHeight(30);
        webViewMainAnchorPane.setClip(clip);
        browserWebView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/538.19 (KHTML, like Gecko) JavaFX/8.0 Safari/538.19");
        browserWebView.setContextMenuEnabled(true);
        /*WebConsoleListener.setDefaultListener(new WebConsoleListener() { //This does not work and causes an error
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });
         */
        browserWebView.getEngine().load("https://www.youtube.com/");//https://www.google.ca/videohp
    }

    @FXML
    public void downloadBrowserUrl(ActionEvent event) throws IOException {
        YoutubeDownloaderManager.addYoutubeLinkToDownloadQueueAndStartDownload(browserWebView.getEngine().getLocation());
    }

    @FXML
    public void showContextMenu(MouseEvent e) {
        //if (e.getButton() == MouseButton.SECONDARY) {
        //     contextMenu.show(browserWebView, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
        // } else {
        //      contextMenu.hide();
        //  }
    }

    public void setUpContextMenu() {
        MenuItem downloadLink = new MenuItem("Download Video Audio");
        downloadLink.setOnAction(e -> System.out.println("Go Forward"));

        MenuItem reload = new MenuItem("Reload");
        reload.setOnAction(e -> browserWebView.getEngine().reload());

        MenuItem goOneSiteBack = new MenuItem("Go Back");
        goOneSiteBack.setOnAction(e -> System.out.println("Go Back"));

        MenuItem goOneSiteForward = new MenuItem("Hide Images");
        goOneSiteForward.setOnAction(e -> System.out.println("Go Forward"));

        contextMenu.getItems().addAll(reload, goOneSiteBack, goOneSiteForward, downloadLink);
    }
}
