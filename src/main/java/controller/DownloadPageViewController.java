/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;
import model.ErrorDataObject;
import model.SongDataObject;
import model.YoutubeDownloaderManager;
import model.YoutubeVideoPageParser;
import ws.schild.jave.EncoderException;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class DownloadPageViewController implements Initializable {

    private String youtubeUrlToGetInfoFrom = "";
    private Image thumbnailImage;
    private static boolean firstLinkFromDownloadQueueIsDownloading = false;
    private static boolean stopDownloading = false;
    private String youtubeLinkTextFieldContent = "";
    @FXML
    private TextField youtubeLinkField;
    @FXML
    private AnchorPane downloadPageMainAnchor;
    @FXML
    private ListView<String> listViewDownloadManager;
    @FXML
    private Button downloadButton;
    @FXML
    private Button deleteSelectedLinkButton;
    @FXML
    private Button clearQueueButton;
    @FXML
    private ImageView downloadQueueImageView;
    @FXML
    private Button deleteErrorListButton;
    @FXML
    private AnchorPane anchorPaneHoldingSlidingMenu;
    @FXML
    private ListView<String> downloadErrorList;
    @FXML
    private ListView<String> videoInfoList;
    @FXML
    private ImageView thumbnailImageView;
    @FXML
    private AnchorPane thumbnailAnchorPane;

    public DownloadPageViewController() {

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        updateErrorListViewWithJavafxThread(false);
        updateDownloadQueueListViewWithJavafxThread(false);
        downloadQueueImageView.setImage(new Image(getClass().getResourceAsStream("/images/MotisHarmonyTriangleBackground.png")));
        downloadPageMainAnchor.setBackground(Background.EMPTY);//This will make the main anchor pane of the login page transparent for aethetics
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(downloadPageMainAnchor.widthProperty());
        clip.heightProperty().bind(downloadPageMainAnchor.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        //listViewDownloadManager.getSelectionModel().getSelectedIndex();//This will get the index of the selected item
        listViewDownloadManager.setBackground(Background.EMPTY);
        listViewDownloadManager.getStylesheets().add("/css/customScrollBar.css");
        listViewDownloadManager.getStylesheets().add("/css/customListView.css");
        downloadErrorList.getStylesheets().add("/css/customScrollBar.css");
        downloadErrorList.getStylesheets().add("/css/customListView.css");
        videoInfoList.getStylesheets().add("/css/customScrollBar.css");
        videoInfoList.getStylesheets().add("/css/customListView.css");
        downloadPageMainAnchor.setClip(clip);
        clip = new Rectangle();
        clip.widthProperty().bind(thumbnailAnchorPane.widthProperty());
        clip.heightProperty().bind(thumbnailAnchorPane.heightProperty());
        clip.setArcWidth(30);//this sets the rounded corners
        clip.setArcHeight(30);
        thumbnailAnchorPane.setClip(clip);
        YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().addListener(new ListChangeListener<SongDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends SongDataObject> arg0) {
                updateDownloadQueueListViewWithJavafxThread(true);
                System.out.println("listener ran");
            }
        });
        YoutubeDownloaderManager.getErrorList().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends String> arg0) {
                updateErrorListViewWithJavafxThread(true);
                System.out.println("listener ran");
            }
        });
    }

    @FXML
    public void mouseEnterClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: #d87ccc; -fx-background-radius: 0 0 30px 0; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    ////////////////////////
    @FXML
    public void mouseEnterDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: #d87ccc; -fx-background-radius: 0 0 0 30px; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    ////////////////////////
    @FXML
    public void mouseEnterDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: #d87ccc; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #d87ccc; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    //////////////////////
    @FXML
    public void mouseEnterDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color: #d07ccc;");//#841858
    }

    @FXML
    public void mouseExitDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color:  #bc0c54;");
    }

    @FXML
    public void mousePressedDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color: #d07ccc; -fx-background-color: #d07ccc; -fx-text-fill: #58143c; -fx-background-radius:   30px 0 0 0");
    }

    @FXML
    public void mouseReleasedDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color:  #bc0c54; -fx-background-color: null; -fx-background-radius:   30px 0 0 0");
    }

    @FXML
    public void mouseEnterYoutubeLinkField() {
        youtubeLinkField.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-prompt-text-fill: #ffffff7d; -fx-text-fill: #ee4540; -fx-background-radius:  0 30px 0 0; -fx-border-color: #d07ccc; -fx-border-radius:  0 30px 0 0;");
    }

    @FXML
    public void mouseExitYoutubeLinkField() {
        youtubeLinkField.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-prompt-text-fill: #ffffff7d; -fx-text-fill: #ee4540; -fx-background-radius:  0 30px 0 0; -fx-border-color: #bc0c54; -fx-border-radius:  0 30px 0 0;");
    }

    @FXML
    private void clearDownloadErrorList(ActionEvent event) throws IOException {
        YoutubeDownloaderManager.getErrorList().clear();
    }

    public static void setFirstLinkFromDownloadQueueIsDownloading(boolean tf) {
        firstLinkFromDownloadQueueIsDownloading = tf;
    }

    public static boolean getStopDownloading() {
        return stopDownloading;
    }

    public static void setStopDownloading(boolean tf) {
        stopDownloading = tf;
    }

    /**
     * @param withJavafxThread use true to add with the javafx thread
     */
    @FXML
    public void updateErrorListViewWithJavafxThread(boolean withJavafxThread) {
        if (withJavafxThread) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    downloadErrorList.getItems().clear();
                    downloadErrorList.getItems().addAll(YoutubeDownloaderManager.getErrorList());
                }
            });
        } else {
            downloadErrorList.getItems().clear();
            downloadErrorList.getItems().addAll(YoutubeDownloaderManager.getErrorList());
        }
    }

    public void addErrorToErrorList(String error) {
        YoutubeDownloaderManager.getErrorList().add(0, error);
    }

    /**
     * @param withJavafxThread use true to update with the javafx thread
     */
    @FXML
    public void updateDownloadQueueListViewWithJavafxThread(boolean withJavafxThread) {
        if (withJavafxThread) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ObservableList<SongDataObject> listOfUrlDataObjects = YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList();
                    String[] listOfTitlesToShow = new String[listOfUrlDataObjects.size()];
                    for (int i = 0; i < listOfTitlesToShow.length; i++) {
                        listOfTitlesToShow[i] = listOfUrlDataObjects.get(i).getTitle();
                    }
                    listViewDownloadManager.getItems().clear();
                    listViewDownloadManager.getItems().addAll(listOfTitlesToShow);
                }
            });
        } else {
            ObservableList<SongDataObject> listOfUrlDataObjects = YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList();
            String[] listOfTitlesToShow = new String[listOfUrlDataObjects.size()];
            for (int i = 0; i < listOfTitlesToShow.length; i++) {
                listOfTitlesToShow[i] = listOfUrlDataObjects.get(i).getTitle();
            }
            listViewDownloadManager.getItems().clear();
            listViewDownloadManager.getItems().addAll(listOfTitlesToShow);
        }
    }

    @FXML
    private void clearQueueManager(ActionEvent event) throws IOException {
        setStopDownloading(true);
        YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().clear();
        updateDownloadQueueListViewWithJavafxThread(false);
    }

    @FXML
    private void deleteSelectedLinkFromQueueManager(ActionEvent event) throws IOException {
        if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {
            setStopDownloading(true);
            YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().remove(listViewDownloadManager.getSelectionModel().getSelectedIndex());
            updateDownloadQueueListViewWithJavafxThread(false);
        } else {
            addErrorToErrorList("Error! Select something before you delete!");
        }
    }

    @FXML
    private void displaySelectedVideoInfo() {
        if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {//Dont run the code if the user does not select anything
            if (!youtubeUrlToGetInfoFrom.equals(listViewDownloadManager.getSelectionModel().getSelectedItem())) { //Dont run the code if the user is trying to load the same info for the same url
                videoInfoList.getItems().clear();
                int urlDataObjectIndexToGet = listViewDownloadManager.getSelectionModel().getSelectedIndex();//Must get the selected item url first for the if statement below to work
                new Thread(//using thread so that this does not freeze gui, do not modify any Javafx components in this thread, all edits must be done on the Javafx 
                        new Runnable() {
                    public void run() {
                        SongDataObject youtubeData = YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().get(urlDataObjectIndexToGet);
                        thumbnailImage = new Image(youtubeData.getThumbnailUrl());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (videoInfoList.getItems().size() != 3) {
                                    videoInfoList.getItems().add("Music Title: " + youtubeData.getTitle());
                                    videoInfoList.getItems().add("Channel Name: " + youtubeData.getChannelName());
                                    videoInfoList.getItems().add("Music Duration: " + youtubeData.getVideoDuration());
                                    videoInfoList.getItems().add("Video Url: " + youtubeData.getVideoUrl());
                                }
                                double w = 0;
                                double h = 0;

                                double ratioX = thumbnailImageView.getFitWidth() / thumbnailImage.getWidth();
                                double ratioY = thumbnailImageView.getFitHeight() / thumbnailImage.getHeight();

                                double reducCoeff = 0;
                                if (ratioX >= ratioY) {
                                    reducCoeff = ratioY;
                                } else {
                                    reducCoeff = ratioX;
                                }

                                w = thumbnailImage.getWidth() * reducCoeff;
                                h = thumbnailImage.getHeight() * reducCoeff;

                                thumbnailImageView.setX((thumbnailImageView.getFitWidth() - w) / 2);
                                thumbnailImageView.setY((thumbnailImageView.getFitHeight() - h) / 2);

                                thumbnailImageView.setImage(thumbnailImage);
                                thumbnailAnchorPane.setStyle("-fx-background-color: black; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px; -fx-background-radius: 0 0 30px 30px;");
                            }
                        });
                    }
                }).start();
            }
        }
    }

    @FXML
    private void downloadVideoOrPlaylist(ActionEvent event) throws IOException {//we may have to prevent button spamming
        youtubeLinkTextFieldContent = youtubeLinkField.getText();
        youtubeLinkField.clear();
        new Thread(
                new Runnable() {
            public void run() {
                YoutubeDownloaderManager.addYoutubeLinkToDownloadQueueAndStartDownload(youtubeLinkTextFieldContent);
            }
        }).start();
    }
}
