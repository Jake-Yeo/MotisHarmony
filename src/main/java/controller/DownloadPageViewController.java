/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Accounts;
import model.AccountsDataManager;
import model.ErrorDataObject;
import model.SongDataObject;
import model.YoutubeDownloader;
import model.YoutubeVideoPageParser;
import ws.schild.jave.EncoderException;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class DownloadPageViewController implements Initializable {

    private YoutubeDownloader ytd;
    //we will keep track of the sdo which has been selected so that no bugs occur when we request info from the model which constantly changes
    private SongDataObject sdoSelected;
    private ContextMenu errorListContextMenu = new ContextMenu();
    private ContextMenu downloadManagerContextMenu = new ContextMenu();
    private String youtubeUrlToGetInfoFrom = "";
    private Image thumbnailImage;
    private static boolean firstLinkFromDownloadQueueIsDownloading = false;
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
    @FXML
    private Text downloadPercentageText;
    @FXML
    private Text conversionPercentageText;
    @FXML
    private Label songNameDownloadingLabel;
    @FXML
    private Text gettingPlaylistPercentageText;

    private void retryDownloadOption() {
        new Thread(
                new Runnable() {
            public void run() {
                try {
                    ytd.downloadSongsFromDownloadQueue();
                } catch (IOException ex) {
                    Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (EncoderException ex) {
                    Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private void copyErrorMessageOption() {
        String stringToCopy = downloadErrorList.getSelectionModel().getSelectedItem();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyProblemUrlOption() {
        String stringToCopy = ytd.getErrorList().get(downloadErrorList.getSelectionModel().getSelectedIndex()).getProblemUrl();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyUrlOption() {
        String stringToCopy = sdoSelected.getVideoUrl();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyArtistNameOption() {
        String stringToCopy = sdoSelected.getChannelName();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    private void copyTitleNameOption() {
        String stringToCopy = sdoSelected.getTitle();
        if (!stringToCopy.isBlank()) {
            StringSelection stringSelection = new StringSelection(stringToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

    public void initContextMenus() {
        MenuItem retryDownload = new MenuItem("Retry Download");
        retryDownload.setOnAction(e -> {
            retryDownloadOption();
        });
        MenuItem copyYoutubeUrl = new MenuItem("Copy Youtube Url");
        copyYoutubeUrl.setOnAction(e -> copyUrlOption());
        MenuItem copyTitleName = new MenuItem("Copy Title Name");
        copyTitleName.setOnAction(e -> copyTitleNameOption());
        MenuItem copyArtistName = new MenuItem("Copy Artist Name");
        copyArtistName.setOnAction(e -> copyArtistNameOption());
        SeparatorMenuItem seperator = new SeparatorMenuItem();
        downloadManagerContextMenu.getItems().addAll(copyYoutubeUrl, copyTitleName, copyArtistName, seperator, retryDownload);

        MenuItem copyProblemUrl = new MenuItem("Copy Problem Url");
        copyProblemUrl.setOnAction(e -> copyProblemUrlOption());
        MenuItem copyErrorMessage = new MenuItem("Copy Error Message");
        copyErrorMessage.setOnAction(e -> copyErrorMessageOption());
        errorListContextMenu.getItems().addAll(copyProblemUrl, copyErrorMessage);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ytd = new YoutubeDownloader();
        YoutubeDownloader.setYtdCurrentlyUsing(ytd);

        ytd.setStopDownloading(false);
        ytd.setStopAllDownloadingProcesses(false);
        updateErrorListViewWithJavafxThread(false);
        updateDownloadQueueListViewWithJavafxThread(false);
        downloadQueueImageView.setImage(new Image(getClass().getResourceAsStream("/images/MotisHarmonyTriangleBackground.png")));
        downloadPageMainAnchor.setBackground(Background.EMPTY);//This will make the main anchor pane of the login page transparent for aethetics
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(downloadPageMainAnchor.widthProperty());
        clip.heightProperty().bind(downloadPageMainAnchor.heightProperty());
        clip.setArcWidth(50);//this sets the rounded corners
        clip.setArcHeight(50);
        downloadPageMainAnchor.setClip(clip);
        //listViewDownloadManager.getSelectionModel().getSelectedIndex();//This will get the index of the selected item
        listViewDownloadManager.setBackground(Background.EMPTY);
        listViewDownloadManager.getStylesheets().add("/css/customScrollBar.css");
        listViewDownloadManager.getStylesheets().add("/css/customListView.css");
        downloadErrorList.getStylesheets().add("/css/customScrollBar.css");
        downloadErrorList.getStylesheets().add("/css/customListView.css");
        videoInfoList.getStylesheets().add("/css/customScrollBar.css");
        videoInfoList.getStylesheets().add("/css/customListView.css");
        if (Accounts.getLoggedInAccount().getSettingsObject().getSaveDownloadQueue() && !Accounts.getLoggedInAccount().getSongsInQueueList().isEmpty() && Accounts.getLoggedInAccount().getSettingsObject().getSaveDownloadQueue()) {
            ytd.getYoutubeUrlDownloadQueueList().addAll(Accounts.getLoggedInAccount().getSongsInQueueList());
            updateDownloadQueueListViewWithJavafxThread(true);
            new Thread(//using thread so that this does not freeze gui, do not modify any Javafx components in this thread, all edits must be done on the Javafx.
                    new Runnable() {
                public void run() {
                    try {
                        ytd.downloadSongsFromDownloadQueue();
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (EncoderException ex) {
                        Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }

        ytd.getYoutubeUrlDownloadQueueList().addListener(new ListChangeListener<SongDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends SongDataObject> arg0) {
                updateDownloadQueueListViewWithJavafxThread(true);
                if (Accounts.getLoggedInAccount() != null && Accounts.getLoggedInAccount().getSettingsObject().getSaveDownloadQueue()) {
                    try {
                        AccountsDataManager.updateSongsInQueueList(ytd.getYoutubeUrlDownloadQueueList());
                    } catch (Exception ex) {
                        Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("listener ran");
            }
        });
        ytd.getSongNameDownloading().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldString, String newString) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        songNameDownloadingLabel.setText("Downloading: " + newString);
                    }
                });
            }
        });

        //This will automatically update the text for the download percentage
        ytd.getDownloadPercentage().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldNum, Number newNum) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        downloadPercentageText.setText("Download Percentage: " + Math.round(newNum.doubleValue() * 100) + "%");
                    }
                });
            }
        });

        //This will automatically update the text for the conversion percentage
        ytd.getConversionPercentage().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldNum, Number newNum) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        conversionPercentageText.setText("Conversion Percent: " + Math.round(newNum.doubleValue() * 100) + "%");
                    }
                });
            }
        });
        //This will automatically update the text for the playlist getter percentage
        ytd.getPlaylistGettingPercentage().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldNum, Number newNum) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (newNum.doubleValue() >= 0) {
                            gettingPlaylistPercentageText.setText("Getting Playlist: " + Math.round(newNum.doubleValue() * 100) + "%");
                        } else if (newNum.doubleValue() < 0) {
                            gettingPlaylistPercentageText.setText("Failed");
                        } else {
                            gettingPlaylistPercentageText.setText("Finished Getting Playlists");
                        }
                    }
                });
            }
        });

        ytd.getErrorList().addListener(new ListChangeListener<ErrorDataObject>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ErrorDataObject> change) {
                updateErrorListViewWithJavafxThread(true);
                System.out.println("listener ran");
            }

        });
        initContextMenus();
    }

    @FXML
    public void mouseEnterClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #f04369; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: #f04369; -fx-background-radius: 0 0 30px 0; -fx-border-color: #f04369; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedClearQueueButton() {
        clearQueueButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 0; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 0; -fx-border-width: 5px;");
    }

    ////////////////////////
    @FXML
    public void mouseEnterDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #f04369; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: #f04369; -fx-background-radius: 0 0 0 30px; -fx-border-color: #f04369; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedDeleteSelectedLinkButton() {
        deleteSelectedLinkButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 0 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 0 30px; -fx-border-width: 5px;");
    }

    ////////////////////////
    @FXML
    public void mouseEnterDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #f04369; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseExitDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mousePressedDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: #f04369; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #f04369; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    @FXML
    public void mouseReleasedDeleteErrorListButton() {
        deleteErrorListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 0 30px 30px; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px;");
    }

    //////////////////////
    @FXML
    public void mouseEnterDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color: #f04369;");//#841858
    }

    @FXML
    public void mouseExitDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color:  #bc0c54;");
    }

    @FXML
    public void mousePressedDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color: #f04369; -fx-background-color: #f04369; -fx-text-fill: #58143c; -fx-background-radius:   30px 0 0 0");
    }

    @FXML
    public void mouseReleasedDownloadButton() {
        downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4540; -fx-border-width: 5px; -fx-border-radius:   30px 0 0 0; -fx-border-color:  #bc0c54; -fx-background-color: null; -fx-background-radius:   30px 0 0 0");
    }

    @FXML
    public void mouseEnterYoutubeLinkField() {
        youtubeLinkField.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-prompt-text-fill: #ffffff7d; -fx-text-fill: #ee4540; -fx-background-radius:  0 30px 0 0; -fx-border-color: #f04369; -fx-border-radius:  0 30px 0 0;");
    }

    @FXML
    public void mouseExitYoutubeLinkField() {
        youtubeLinkField.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-prompt-text-fill: #ffffff7d; -fx-text-fill: #ee4540; -fx-background-radius:  0 30px 0 0; -fx-border-color: #bc0c54; -fx-border-radius:  0 30px 0 0;");
    }

    @FXML
    private void clearDownloadErrorList(ActionEvent event) throws IOException {
        ytd.getErrorList().clear();
    }

    public static void setFirstLinkFromDownloadQueueIsDownloading(boolean tf) {
        firstLinkFromDownloadQueueIsDownloading = tf;
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
                    try {
                        downloadErrorList.getItems().addAll(ErrorDataObject.getListOfErrorMessages(ytd.getErrorList()));
                    } catch (java.util.ConcurrentModificationException e) {
                        System.out.println("Stop modifying the view so fast");
                    }
                }
            });
        } else {
            downloadErrorList.getItems().clear();
            downloadErrorList.getItems().addAll(ErrorDataObject.getListOfErrorMessages(ytd.getErrorList()));
        }
    }

    public void addErrorToErrorList(ErrorDataObject error) {
        ytd.getErrorList().add(0, error);
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
                    ObservableList<SongDataObject> listOfUrlDataObjects = ytd.getYoutubeUrlDownloadQueueList();
                    String[] listOfTitlesToShow = new String[listOfUrlDataObjects.size()];
                    for (int i = 0; i < listOfTitlesToShow.length; i++) {
                        listOfTitlesToShow[i] = listOfUrlDataObjects.get(i).getTitle();
                    }
                    //The
                    String selectedString = listViewDownloadManager.getSelectionModel().getSelectedItem();
                    listViewDownloadManager.getItems().clear();
                    listViewDownloadManager.getItems().addAll(listOfTitlesToShow);
                    listViewDownloadManager.getSelectionModel().select(selectedString);
                }
            });
        } else {
            ObservableList<SongDataObject> listOfUrlDataObjects = ytd.getYoutubeUrlDownloadQueueList();
            String[] listOfTitlesToShow = new String[listOfUrlDataObjects.size()];
            for (int i = 0; i < listOfTitlesToShow.length; i++) {
                listOfTitlesToShow[i] = listOfUrlDataObjects.get(i).getTitle();
            }
            String selectedString = listViewDownloadManager.getSelectionModel().getSelectedItem();
            listViewDownloadManager.getItems().clear();
            listViewDownloadManager.getItems().addAll(listOfTitlesToShow);
            listViewDownloadManager.getSelectionModel().select(selectedString);
        }
    }

    @FXML
    private void clearQueueManager(ActionEvent event) throws IOException {
        ytd.getYoutubeUrlDownloadQueueList().clear();
        ytd.setStopDownloading(true);
        ytd.setRemoveFirstLink(false);
        if (YoutubeDownloader.getYtdCurrentlyUsing().isConvertingAudio()) {
            ytd.getAudioEncoderUsing().abortEncoding();
        }
        updateDownloadQueueListViewWithJavafxThread(false);
    }

    @FXML
    private void deleteSelectedLinkFromQueueManager(ActionEvent event) throws IOException {
        int selectedIndex = listViewDownloadManager.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            //We must abort the encoding if the audio selected is currently being encoded
            if (YoutubeDownloader.getYtdCurrentlyUsing().isConvertingAudio() && selectedIndex == 0) {
                ytd.setRemoveFirstLink(false);
                ytd.getAudioEncoderUsing().abortEncoding();
                ytd.getYoutubeUrlDownloadQueueList().remove(selectedIndex);
                updateDownloadQueueListViewWithJavafxThread(false);
            } else {
                //If the audio was not being encoded, we check to see if it is downloading. If it is we stop the download.
                if (selectedIndex == 0) {
                    ytd.setRemoveFirstLink(false);
                    ytd.setStopDownloading(true);
                }
                ytd.getYoutubeUrlDownloadQueueList().remove(listViewDownloadManager.getSelectionModel().getSelectedIndex());
                updateDownloadQueueListViewWithJavafxThread(false);
            }
        } else {
            addErrorToErrorList(new ErrorDataObject(true, "Error! Select something before you delete!"));
        }
    }

    @FXML
    private void showErrorListContextMenu(MouseEvent e) {
        if (downloadErrorList.getSelectionModel().getSelectedIndex() != -1) {
            if (e.getButton() == MouseButton.SECONDARY) {
                errorListContextMenu.show(downloadErrorList, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
            } else {
                errorListContextMenu.hide();
            }
        }
    }

    @FXML
    private void displaySelectedVideoInfo(MouseEvent e) {
        if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {
            sdoSelected = ytd.getYoutubeUrlDownloadQueueList().get(listViewDownloadManager.getSelectionModel().getSelectedIndex());
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println("worked");
                downloadManagerContextMenu.show(listViewDownloadManager, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
            } else {
                downloadManagerContextMenu.hide();
                if (listViewDownloadManager.getSelectionModel().getSelectedIndex() != -1) {//Dont run the code if the user does not select anything.
                    if (!youtubeUrlToGetInfoFrom.equals(listViewDownloadManager.getSelectionModel().getSelectedItem())) { //Dont run the code if the user is trying to load the same info for the same url
                        videoInfoList.getItems().clear();
                        int urlDataObjectIndexToGet = listViewDownloadManager.getSelectionModel().getSelectedIndex();//Must get the selected item url first for the if statement below to work
                        new Thread(//using thread so that this does not freeze gui, do not modify any Javafx components in this thread, all edits must be done on the Javafx.
                                new Runnable() {
                            public void run() {
                                SongDataObject youtubeData = ytd.getYoutubeUrlDownloadQueueList().get(urlDataObjectIndexToGet);
                                thumbnailImage = new Image(youtubeData.getThumbnailUrl());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (videoInfoList.getItems().size() != 3) {
                                            videoInfoList.getItems().add("Music Title: " + youtubeData.getTitle());
                                            System.out.println(youtubeData.getTitle());
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
                                        thumbnailAnchorPane.setStyle("-fx-background-color: black; -fx-border-color: #4c154a; -fx-border-radius: 0 0 30px 30px; -fx-border-width: 5px; -fx-background-radius: 0 0 33px 33px;");
                                    }
                                });
                            }
                        }).start();
                    }
                }
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
                YoutubeDownloader.getYtdCurrentlyUsing().addYoutubeLinkToDownloadQueueAndStartDownload(youtubeLinkTextFieldContent);
            }
        }).start();
    }
}
