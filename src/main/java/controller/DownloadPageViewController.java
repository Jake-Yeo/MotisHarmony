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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;
import model.DataObject;
import model.YoutubeDownloaderManager;
import model.YoutubeVideoPageParser;
import ws.schild.jave.EncoderException;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class DownloadPageViewController implements Initializable {

    private String titleName = "";
    private String channelName = "";
    private String durationTime = "";
    private String youtubeUrlToGetInfoFrom = "";
    private Image thumbnailImage;
    private boolean firstLinkFromDownloadQueueIsDownloading = false;
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
        downloadErrorList.getItems().clear();
    }

    private void addErrorToErrorList(String error) {
        downloadErrorList.getItems().add(0, error);
    }

    @FXML
    private void clearQueueManager(ActionEvent event) throws IOException {
        if (firstLinkFromDownloadQueueIsDownloading == true) {
            listViewDownloadManager.getItems().remove(1, listViewDownloadManager.getItems().size());
            YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().clear();
            YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().addAll(listViewDownloadManager.getItems());
        } else {
            listViewDownloadManager.getItems().clear();
            YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().clear();
        }
    }

    @FXML
    private void deleteSelectedLinkFromQueueManager(ActionEvent event) throws IOException {
        if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {
            if (listViewDownloadManager.getSelectionModel().getSelectedIndex() == 0 && firstLinkFromDownloadQueueIsDownloading == true) {
                addErrorToErrorList("Error the song you selected is currently being downloaded and cannot be deleted!");
            } else {
                YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().remove(listViewDownloadManager.getSelectionModel().getSelectedIndex());
                updateDownloadQueueTextArea();
            }
        } else {
            addErrorToErrorList("Error! Select something before you delete!");
        }
    }

    private void updateDownloadQueueTextArea() {

        listViewDownloadManager.getItems().clear();
        listViewDownloadManager.getItems().addAll(YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList());
    }

    private void addErrorToErrorListWithJavafxThread(String error) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                addErrorToErrorList(error);
            }
        });
    }

    @FXML
    private void displaySelectedVideoInfo() {
        if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {//Dont run the code if the user does not select anything
            if (!youtubeUrlToGetInfoFrom.equals(listViewDownloadManager.getSelectionModel().getSelectedItem())) { //Dont run the code if the user is trying to load the same info for the same url
                videoInfoList.getItems().clear();
                youtubeUrlToGetInfoFrom = listViewDownloadManager.getSelectionModel().getSelectedItem();//Must get the selected item url first for the if statement below to work
                new Thread(//using thread so that this does not freeze gui, do not modify any Javafx components in this thread, all edits must be done on the Javafx 
                        new Runnable() {
                    public void run() {
                        try {
                            DataObject youtubeData = YoutubeVideoPageParser.getYoutubeVideoData(youtubeUrlToGetInfoFrom);
                            titleName = youtubeData.getTitle();
                            channelName = youtubeData.getChannelName();
                            durationTime = youtubeData.getVideoDuration();
                            thumbnailImage = new Image(youtubeData.getThumbnailUrl());
                        } catch (IOException ex) {
                            Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (videoInfoList.getItems().size() != 3) {
                                    videoInfoList.getItems().add("Music Title: " + titleName);
                                    videoInfoList.getItems().add("Channel Name: " + channelName);
                                    videoInfoList.getItems().add("Music Duration: " + durationTime);
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
        new Thread(//using thread so that this does not freeze gui, do not modify any Javafx components in this thread, all edits must be done on the Javafx 
                new Runnable() {
            public void run() {
                String youtubeLinkTextOriginal = youtubeLinkTextFieldContent;
                try {
                    DataObject errorData = YoutubeVideoPageParser.isUrlValid(youtubeLinkTextOriginal);
                    if (!errorData.getDidErrorOccur()) {
                        YoutubeDownloaderManager.addYoutubeLinkToDownloadQueue(youtubeLinkTextOriginal);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updateDownloadQueueTextArea();
                            }
                        });
                        if (!YoutubeDownloaderManager.isAppDownloadingFromDownloadQueue()) {
                            try {
                                downloadSongsFromDownloadQueue();
                            } catch (Exception e) {
                                Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    } else {
                        addErrorToErrorListWithJavafxThread(errorData.getErrorMessage());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    public void downloadSongsFromDownloadQueue() throws FileNotFoundException, IOException, EncoderException {//We put this method here so that we don't need a while loop to update the downloadQueueList
        YoutubeDownloaderManager.setIsChromeDriverActive(true); // this will make sure that the chrome driver isn't restarted multiple times in order to increase download speed.
        YoutubeDownloaderManager.setupChromeDriver();
        while (!YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().isEmpty()) {//The user may continue to add urls to the download queue list, so we continue to download untill the download queue is empty
            firstLinkFromDownloadQueueIsDownloading = true;
            if (YoutubeVideoPageParser.isYoutubeLinkAvailableToPublic(YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().get(0))) {//The youtube urls in the playlists are not checked, so we must check those here.
                YoutubeDownloaderManager.downloadYoutubeVideoUrl(YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().get(0));//Gets the first youtube url in the download queue list
            } else {
                addErrorToErrorListWithJavafxThread("WARNING! " + YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().get(0) + " is likely an age restricted video, please find link which is not age restricted!");
            }
            YoutubeDownloaderManager.getYoutubeUrlDownloadQueueList().remove(0);//Removes the youtube url from the list which was downloaded.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listViewDownloadManager.getItems().remove(0);
                }
            });
            firstLinkFromDownloadQueueIsDownloading = false;
        }
        YoutubeDownloaderManager.quitChromeDriver();
        YoutubeDownloaderManager.setIsChromeDriverActive(false);
    }

}
