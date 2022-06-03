/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.DownloadPageViewController;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ws.schild.jave.EncoderException;

/**
 *
 * @author Jake Yeo
 */
public class YoutubeDownloader {

    private static YoutubeDownloader ytdCurrentlyUsing;
    private AudioConverter ac = new AudioConverter();
    private WebDriver driver;
    private boolean isChromeDriverActive = false;
    private boolean removeFirstLink = true;
    private boolean wifiConnected = true;
    private boolean isConvertingAudio = false;
    private boolean stopDownloading = false;
    private boolean stopAllDownloadingProcesses = false;
    private SimpleStringProperty status = new SimpleStringProperty();
    private SimpleDoubleProperty downloadPercentage = new SimpleDoubleProperty();
    private SimpleDoubleProperty conversionPercentage = new SimpleDoubleProperty();
    private SimpleStringProperty songNameDownloading = new SimpleStringProperty();
    private SimpleDoubleProperty playlistGettingPercentage = new SimpleDoubleProperty();
    private boolean isPlaylistUrlGetterCurrentlyGettingUrls = false;
    private int BYTE_AMT_FOR_3_MIN_VID = 3402697;//We should download videos in three minute segments
    private final String YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER = "Age-restricted";
    private final String YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER = "ctier";//Ad audio links are the only links that contain cteir in them
    private final String YOUTUBE_AD_AUDIO_SOURCE_START_IDENTIFIER = "https:";
    private final String YOUTUBE_AD_AUDIO_SOURCE_END_IDENTIFIER = ",";
    private final String YOUTUBE_AUDIO_SOURCE_IDENTIFIER = "mime=audio";
    private final String YOUTUBE_AUDIO_SOURCE_START_IDENTIFIER = "https:";
    private final String YOUTUBE_AUDIO_SOURCE_END_IDENTIFIER = "range";
    private ObservableList<SongDataObject> youtubeUrlDownloadQueueList = FXCollections.observableArrayList();
    private ObservableList<ErrorDataObject> errorList = FXCollections.observableArrayList();
    private YoutubeVideoPageParser yvpp = new YoutubeVideoPageParser();

    public static YoutubeDownloader getYtdCurrentlyUsing() {
        return ytdCurrentlyUsing;
    }

    public static void setYtdCurrentlyUsing(YoutubeDownloader ytd) {
        ytdCurrentlyUsing = ytd;
    }

    private void setupChromeDriver() {
        //Set the Path of Executable Browser Driver
        //System.setProperty("webdriver.chrome.driver", "chromedriver.exe");//We probably don't need this anymore because we automatically get the webdriver below
        try {
            WebDriverManager.chromedriver().setup();//This will automatically update the chrome webdriver to the proper version
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");//start the chrome browser headless, can be changed if you want
            options.setCapability(ChromeOptions.CAPABILITY, options);
            options.addArguments("--mute-audio");
            driver = new ChromeDriver(options);
            try {
                final URL url = new URL("http://www.google.com");
                final URLConnection conn = url.openConnection();
                setWifiConnected(true);
            } catch (Exception e) {
                setWifiConnected(false);
            }
        } catch (Exception e) {
            setWifiConnected(false);
            errorList.add(new ErrorDataObject(true, "You aren't connected to wifi!"));
        }
    }

    public YoutubeVideoPageParser getYoutubeVideoPageParserUsing() {
        return yvpp;
    }

    public SimpleDoubleProperty getPlaylistGettingPercentage() {
        return playlistGettingPercentage;
    }

    public SimpleStringProperty getStatus() {
        return status;
    }

    public AudioConverter getAudioEncoderUsing() {
        return ac;
    }

    public boolean isConvertingAudio() {
        return isConvertingAudio;
    }

    public void setIsConvertingAudio(boolean tf) {
        isConvertingAudio = tf;
    }

    public void setRemoveFirstLink(boolean tf) {
        removeFirstLink = tf;
    }

    public SimpleDoubleProperty getDownloadPercentage() {
        return downloadPercentage;
    }

    public SimpleDoubleProperty getConversionPercentage() {
        return conversionPercentage;
    }

    public SimpleStringProperty getSongNameDownloading() {
        return songNameDownloading;
    }

    public boolean getWifiConnected() {
        return wifiConnected;
    }

    public void setWifiConnected(boolean tf) {
        wifiConnected = tf;
    }

    private void quitChromeDriver() {
        try {
            driver.quit();
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("taskkill /F /IM chromedriver.exe");
        } catch (Exception e) {
            System.err.println("Driver not properly initialized");
        }
    }

    public boolean getStopAllDownloadingProcesses() {
        return stopAllDownloadingProcesses;
    }

    public void setStopAllDownloadingProcesses(boolean tf) {
        stopAllDownloadingProcesses = tf;
    }

    private void addYoutubeUrlsToDownloadQueue(String youtubeUrl) throws IOException {//Since we allow the user to input as many playlists as they want to download, we need a way to manage and organize downloads so that we don't end up with corrupted audio downloads
        youtubeUrl = yvpp.getRegularYoutubeUrl(youtubeUrl);//makes sure that any variations of one youtube url will always be turned into one variation to allow for url comparison so that duplicated urls are not present withing the downloader queue
        String youtubeId = yvpp.getYoutubeVideoID(youtubeUrl);//Its easier to compare videos with ids rather than with urls
        if (!getYoutubeUrlDownloadQueueListVideoIds().contains(youtubeId) && !Accounts.getLoggedInAccount().getListOfSongVideoIds().contains(youtubeId)) {//Makes sure that a youtube url is not added to the download queue list multiple times
            SongDataObject sdoToAddToDownloadQueue = yvpp.getYoutubeVideoData(youtubeUrl);
            //We check here again because the user has the ability to add two links which lead to the same video, we must get the video data and check again
            if (!SongDataObject.toIDStringList(getYoutubeUrlDownloadQueueList()).contains(sdoToAddToDownloadQueue.getVideoID()) && !Accounts.getLoggedInAccount().getListOfSongVideoIds().contains(sdoToAddToDownloadQueue.getVideoID())) {
                youtubeUrlDownloadQueueList.add(sdoToAddToDownloadQueue);//adds the youtube url to the download queue
            } else {
                errorList.add(new ErrorDataObject(true, youtubeUrl + " has already been added to the download queue, or has already been downloaded", youtubeUrl));
            }
        } else {
            errorList.add(new ErrorDataObject(true, youtubeUrl + " has already been added to the download queue, or has already been downloaded", youtubeUrl));
        }
    }

    private boolean isAppDownloadingFromDownloadQueue() {
        return isChromeDriverActive;
    }

    private void setIsChromeDriverActive(boolean tf) {
        isChromeDriverActive = tf;
    }

    public ObservableList<SongDataObject> getYoutubeUrlDownloadQueueList() {
        return youtubeUrlDownloadQueueList;
    }

    public LinkedList<String> getYoutubeUrlDownloadQueueListVideoIds() {
        LinkedList<String> idList = new LinkedList<>();
        for (SongDataObject sdo : youtubeUrlDownloadQueueList) {
            idList.add(sdo.getVideoID());
        }
        return idList;
    }

    public ObservableList<ErrorDataObject> getErrorList() {
        return errorList;
    }

    private void addSongsFromPlaylistToDownloadQueue(String youtubePlaylistLink) {
        if (yvpp.isGettingPlaylist() == false) {
            isPlaylistUrlGetterCurrentlyGettingUrls = true;
            LinkedList<SongDataObject> youtubePlaylistUrls = null;
            try {
                youtubePlaylistUrls = yvpp.getPlaylistYoutubeUrls(youtubePlaylistLink);
            } catch (java.net.SocketTimeoutException i) {
                errorList.add(new ErrorDataObject(true, "Failed to get playlist since you are disconnected from wifi", youtubePlaylistLink));
                return;
            } catch (IOException e) {
                errorList.add(new ErrorDataObject(true, "You're currently IP banned from youtube and cannot download songs at this time", youtubePlaylistLink));
                return;
            }
            if (youtubePlaylistUrls == null) {
                errorList.add(new ErrorDataObject(true, "Sorry we cannot download the url you entered at this time.", youtubePlaylistLink));
                return;
            }
            LinkedList<SongDataObject> sdosToRemoveFromYoutubePlaylistUrls = new LinkedList<>();
            LinkedList<SongDataObject> youtubePlaylistUrlsNoDupe = new LinkedList<>();
            //There is a youtube bug where two of the same videos are added to one playlist, this will completely break the program, we prevent this by removing duplicate videos which were in the youtube playlist
            LinkedList<String> sdoIdArray = SongDataObject.getYoutubeIdList(youtubePlaylistUrls);
            LinkedList<String> sdoCompareToArray = new LinkedList<>();
            for (int i = 0; i < youtubePlaylistUrls.size(); i++) {
                if (!sdoCompareToArray.contains(sdoIdArray.get(i))) {
                    youtubePlaylistUrlsNoDupe.add(youtubePlaylistUrls.get(i));
                    sdoCompareToArray.add(youtubePlaylistUrls.get(i).getVideoID());
                }
            }

            //This for loop will get a list of songs which have already been downloaded, or are already in the download manager
            for (int i = 0; i < youtubePlaylistUrlsNoDupe.size(); i++) {
                if (getYoutubeUrlDownloadQueueListVideoIds().contains(youtubePlaylistUrlsNoDupe.get(i).getVideoID()) || Accounts.getLoggedInAccount().getListOfSongVideoIds().contains(youtubePlaylistUrls.get(i).getVideoID())) {
                    sdosToRemoveFromYoutubePlaylistUrls.add(youtubePlaylistUrlsNoDupe.get(i));
                }
            }

            //We remove the list we got from the above for loop and remove it from the youtubePlaylistUrls list
            youtubePlaylistUrlsNoDupe.removeAll(sdosToRemoveFromYoutubePlaylistUrls);
            youtubeUrlDownloadQueueList.addAll(youtubePlaylistUrlsNoDupe);//Adds the entire string array of converted youtube urls to the download queue.
            isPlaylistUrlGetterCurrentlyGettingUrls = false;
        } else {
            errorList.add(new ErrorDataObject(true, "Please finish getting the current playlist before getting another playlist."));
            return;
        }
    }

    private boolean isPlaylistUrlGetterCurrentlyGettingUrls() {
        return isPlaylistUrlGetterCurrentlyGettingUrls;
    }
//method below may accidentally get ad, tho im not too sure how to fix or if that was due to a faulty input

    private String getRidOfAdUrl(String netData) {//This will get rid of any ad video or audio links.
        while (netData.contains(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER)) {
            String netDataFirstHalf = "";
            String netDataSecondHalf = "";
            netDataFirstHalf = netData.substring(0, netData.lastIndexOf(YOUTUBE_AD_AUDIO_SOURCE_START_IDENTIFIER, netData.indexOf(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER))); //This will get the first half of the net data from the start of the ad link
            netDataSecondHalf = netData.substring(netData.indexOf(YOUTUBE_AD_AUDIO_SOURCE_END_IDENTIFIER, netData.indexOf(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER)));//This will get the second half of net data from the end of the ad link
            netData = netDataFirstHalf + netDataSecondHalf;//We put the two halves together to get netdata which may or may not still contain ad links for audio or video, that's why we put it in a while loop
        }
        return netData;
    }

    private String obtainYoutubeUrlAudioSource(String youtubeUrl) throws MalformedURLException, IOException {//Gets the audio source of a youtube video and returns it
        try {
            driver.get(youtubeUrl);
            wifiConnected = true;
        } catch (Exception e) {
            quitChromeDriver();//Sometimes youtube may block your connection to their website if you request too much. That's what I think causes this error. So we reset the driver and try again
            setupChromeDriver();
            try {
                driver.get(youtubeUrl);
                wifiConnected = true;
            } catch (Exception c) {//If it happens again, it's probably because the user is not conencted to wifi
                wifiConnected = false;
            }
        }
        if (wifiConnected) {
            String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
            String netData;
            try {
                netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();//Get network traffic data
            } catch (Exception e) {
                setWifiConnected(false);
                return "No Wifi";
            }
            netData = getRidOfAdUrl(netData);//We get rid of adUrls here just incase since there have been cases where ad audio has been downloaded. I don't know if this fixes the problem or not.
            String audioHtmlSource = "";
            int repeats = 0;
            while (!audioHtmlSource.contains("media")) {
                while (!netData.contains(YOUTUBE_AUDIO_SOURCE_IDENTIFIER)) { //Sometimes url is loaded but the audio source isn't present in the network traffic data, so reload the site and do it until we get the audio source.
                    System.out.println("Error saved!");
                    System.out.println(youtubeUrl + " This link didn't have mime=audio!");
                    if (repeats > 5) {
                        System.out.println("Error getting this this link " + youtubeUrl);
                        netData = "error";
                        break;
                        //If statement below will stop the program from trying to get the audio source if it's unable to find it when the user clears the download queue
                    } else if (getStopAllDownloadingProcesses() || stopDownloading) {
                        System.out.println("clearCalled");
                        netData = "clearCalled";
                        stopDownloading = false;
                        break;
                    }
                    try {
                        driver.get(youtubeUrl);
                        netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();
                    } catch (Exception e) {
                        setWifiConnected(false);
                        return "No Wifi";
                    }
                    netData = getRidOfAdUrl(netData);//We must get rid of the ad urls before checking again if netData contains the YOUTUBE_AUDIO_SOURCE_IDENTIFIER because sometimes only the ad link has YOUTUBE_AUDIO_SOURCE_IDENTIFIER in it.
                    repeats++;
                }
                if (netData.equals("error")) {
                    break;
                }
                if (netData.equals("clearCalled")) {
                    break;
                }
                System.out.println("Netdata stuff: " + netData);
                if (netData.contains(YOUTUBE_AUDIO_SOURCE_END_IDENTIFIER)) {
                    //PrintWriter pw = new PrintWriter(new FileWriter("html.txt"));
                    //pw.print(netData);
                    //pw.close();
                    System.out.println("Identifier at index " + netData.indexOf(YOUTUBE_AUDIO_SOURCE_IDENTIFIER));//we should probably do what we did above in the while loop which gets rid of ad link!!!!!!
                    netData = netData.substring(netData.lastIndexOf(YOUTUBE_AUDIO_SOURCE_START_IDENTIFIER, netData.indexOf(YOUTUBE_AUDIO_SOURCE_IDENTIFIER)));//This will get rid of everything up to the start of the youtube audio source link
                    netData = netData.substring(0, netData.indexOf(YOUTUBE_AUDIO_SOURCE_END_IDENTIFIER));//We must get rid of the range in order for the source audio url to actually load properly
                    System.out.println("Pure weba url: " + netData);
                    try {
                        driver.get(netData);
                        audioHtmlSource = driver.getPageSource();//Sometimes an audio link which is parsed may contain another url to the correct source audio url which is why we check the html of the url we obtain. Also, it seems impossible to read the data with htmlGetter, so we use the selenium method instead
                        System.out.println("Audio html stuff " + audioHtmlSource);
                        driver.get("data:,");//just prevents sound from playing from chrome driver by switching it to an easy to load website.
                    } catch (Exception e) {
                        setWifiConnected(false);
                        return "No Wifi";
                    }
                } else {//This will reload the youtube url and attempt to get netData that had a source url which isn't text
                    try {
                        driver.get(youtubeUrl);
                        netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();
                    } catch (Exception e) {
                        setWifiConnected(false);
                        return "No Wifi";
                    }
                    netData = getRidOfAdUrl(netData);//We must get rid of the ad urls before checking again if netData contains the YOUTUBE_AUDIO_SOURCE_IDENTIFIER because sometimes only the ad link has YOUTUBE_AUDIO_SOURCE_IDENTIFIER in it.
                }
            }
            return netData;
        } else {
            setWifiConnected(false);
            return "No Wifi";
        }

    }

    private void addYoutubeLinkToDownloadQueue(String youtubeUrl) throws IOException {
        if (yvpp.isLinkAPlaylist(youtubeUrl)) {
            addSongsFromPlaylistToDownloadQueue(youtubeUrl);
            yvpp.setIsGettingPlaylist(false);
        } else {
            addYoutubeVideoUrlToDownloadQueue(youtubeUrl);
        }
    }

    public void addYoutubeLinkToDownloadQueueAndStartDownload(String youtubeUrl) {
        try {
            ErrorDataObject errorData = yvpp.isUrlValid(youtubeUrl);
            if (!errorData.didErrorOccur()) {
                addYoutubeLinkToDownloadQueue(youtubeUrl);
                System.out.println(!isAppDownloadingFromDownloadQueue());
                if (!isAppDownloadingFromDownloadQueue()) {
                    try {
                        downloadSongsFromDownloadQueue();
                    } catch (Exception e) {
                        Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            } else {
                errorList.add(errorData);
            }
        } catch (IOException ex) {
            Logger.getLogger(DownloadPageViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addYoutubeVideoUrlToDownloadQueue(String youtubeUrl) throws IOException {//make private
        youtubeUrl = yvpp.getRegularYoutubeUrl(youtubeUrl);//The url the user pastes in may be of many varaition, we use this method to turn many variations of a url into just one url. This lets us compare urls in the download manager so that we don't add two urls of the same video in the download manager.
        String youtubeId = yvpp.getYoutubeVideoID(youtubeUrl);//We compare with video ids because it's easier
        if (!getYoutubeUrlDownloadQueueListVideoIds().contains(youtubeId) && !Accounts.getLoggedInAccount().getListOfSongVideoIds().contains(youtubeId)) {//Stops you from inputting the same url into the downloadQueue
            addYoutubeUrlsToDownloadQueue(youtubeUrl);
        } else {
            errorList.add(new ErrorDataObject(true, youtubeUrl + " has already been added to the download queue, or has already been downloaded", youtubeUrl));
        }
    }

    public void setStopDownloading(boolean tf) {
        stopDownloading = tf;
    }

    private void downloadYoutubeVideoUrl(SongDataObject youtubeSongData) throws MalformedURLException, IOException, EncoderException { //this will download and obtain any youtube audio source links given to it.
        URL downloadURL = null;
        boolean skipAudioConversion = false;
        String possibleYoutubeUrl = obtainYoutubeUrlAudioSource(youtubeSongData.getVideoUrl());
        if (!possibleYoutubeUrl.equals("error") && !possibleYoutubeUrl.equals("No Wifi") && !possibleYoutubeUrl.equals("clearCalled")) {
            //We add range=0-99999999999999999999 to the url below to bypass throttling which speeds up download times by nearly 95 times!
            System.out.println(yvpp.extractMaxByteRange(possibleYoutubeUrl));
            int maxByteRange = yvpp.extractMaxByteRange(possibleYoutubeUrl);

            int count = 0;
            long bytesDownloaded = 0;
            long timeStart = System.currentTimeMillis();
            try {
                //Will set the name of the song being downloaded so it can be displayed in the gui
                songNameDownloading.set(youtubeSongData.getTitle());
                //Reset the percentages for every new song you download
                downloadPercentage.set(0);
                //Reset the percentages for every new song you download
                conversionPercentage.set(0);
                FileOutputStream fos = new FileOutputStream(youtubeSongData.getPathToWebaFile());
                //Here in this for loop we download songs in three minute segments. This will bypass download throttling. Though there is no difference when downloading 3-8 minute songs, it decreases download times for 2 hr songs from approximately 1hr donwload time to 70 seconds.
                for (int bytesToStart = 0; bytesToStart < maxByteRange; bytesToStart += BYTE_AMT_FOR_3_MIN_VID + 1) {//+1 since we have to change the range parameter from (0, 1000) to (1001, 2001) not (1000, 2000) which would cause an error
                    System.out.println("for loop ran");
                    String urlToDownload = possibleYoutubeUrl + "range=" + bytesToStart + "-" + (bytesToStart + BYTE_AMT_FOR_3_MIN_VID);
                    downloadURL = new URL(urlToDownload);//Out of range happens when mime=audio cannot be found
                    System.out.println(urlToDownload);

                    BufferedInputStream bis = new BufferedInputStream(downloadURL.openStream());
                    int i = 0;
                    System.out.println("Stop downloading is " + stopDownloading);
                    byte[] data = new byte[256000];
                    while ((count = bis.read(data)) != -1) {
                        if (stopDownloading) {//If stop downloading is true then stop this while loop to stop downloading the song. This allows the user to cancel downloads using the "clear" and "delete url" button
                            removeFirstLink = false;//This will prevent the program from attempting to delete a url which has already been removed by the user. This must be first in the if loop, if not the boolean will not be changed quickly enough to stop the program from trying to delete a url it's not supposed to
                            skipAudioConversion = true;//This will prevent the program from attempting to convert a corrupted weba file to a wav file
                            stopDownloading = false;//This will allow the next song to be downloaded
                            return;
                        }
                        i += count;
                        fos.write(data, 0, count);
                        bytesDownloaded += count;
                        //This will change the download percentage, since it is observable the gui will automatically update when the value is updated
                        downloadPercentage.set(((double) bytesDownloaded / maxByteRange));
                        // System.out.println("Download " + (double) bytesDownloaded / maxByteRange * 100 + "% complete");
                    }
                    //this if statement will break out of the for loop if the user wants to stop downloading the current song
                    if (skipAudioConversion) {
                        return;
                    }
                }

            } catch (IOException ex) {
                System.err.print("error downloading song");
            }
            System.out.println("Time taken to download: " + (System.currentTimeMillis() - timeStart) / 1000 + " Seconds");
            if (skipAudioConversion) {//Skips the audio conversion
                return;
            }
            //We no longer create a new thread everytime we run the AudioConverter below, this will give time for the chrome driver to "breath" and will prevent massive lag.  
            try {
                setIsConvertingAudio(true);
                ac.addToConversionQueue(youtubeSongData);//If two videos have the same title names then this method will fail, each music file must have its own unique name. Fix the same name bug by incorporating the youtube video IDs in the name of the file
                setIsConvertingAudio(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorList.add(new ErrorDataObject(true, "Download could not be completed as wifi is disconnected"));
                setWifiConnected(false);
            }

        } else if (possibleYoutubeUrl.equals("error")) {
            errorList.add(new ErrorDataObject(true, youtubeSongData.getVideoUrl() + " could not be downloaded at this time, please try again later or find an alternative link", youtubeSongData.getVideoUrl()));
            System.err.print("Failed to download this song");
        } else if (possibleYoutubeUrl.equals("No Wifi")) {
            errorList.add(new ErrorDataObject(true, youtubeSongData.getVideoUrl() + " failed to download because you are not connected to wifi", youtubeSongData.getVideoUrl()));
            setWifiConnected(false);
        }
    }

    public void downloadSongsFromDownloadQueue() throws FileNotFoundException, IOException, EncoderException {//We put this method here so that we don't need a while loop to update the downloadQueueList
        status.set("Downloading");
        setIsChromeDriverActive(true); // this will make sure that the chrome driver isn't restarted multiple times in order to increase download speed.
        setupChromeDriver();
        while (!youtubeUrlDownloadQueueList.isEmpty() && !stopAllDownloadingProcesses && wifiConnected) {//The user may continue to add urls to the download queue list, so we continue to download untill the download queue is empty
            DownloadPageViewController.setFirstLinkFromDownloadQueueIsDownloading(true);
            ErrorDataObject errorData = yvpp.isUrlValid(youtubeUrlDownloadQueueList.get(0).getVideoUrl());
            if (!errorData.didErrorOccur() && !youtubeUrlDownloadQueueList.isEmpty()) {//The youtube urls in the playlists are not checked, so we must check those here.
                downloadYoutubeVideoUrl(youtubeUrlDownloadQueueList.get(0));//Gets the first youtube url in the download queue list
            } else {
                errorList.add(errorData);
            }
            if (removeFirstLink && wifiConnected) {//This will prevent the program from attempting to delete a url which has already been removed by the user.
                if (youtubeUrlDownloadQueueList.size() != 0) {
                    youtubeUrlDownloadQueueList.remove(0);//Removes the youtube url from the list which was downloaded.
                }
            }
            //when the user clears the queue, we must set stopDownloading to false or else the first link added after clearing the queue will be removed
            stopDownloading = false;
            removeFirstLink = true;
            //downloadPageViewController.updateDownloadQueueListViewWithJavafxThread(true);
            DownloadPageViewController.setFirstLinkFromDownloadQueueIsDownloading(false);
        }
        if (!wifiConnected) {
            errorList.add(new ErrorDataObject(true, "You aren't connected to wifi!"));
            status.set("Download Paused");
        }
        quitChromeDriver();//Finally, when all the youtube videos have been downloaded, exit the download queue
        setIsChromeDriverActive(false);
        if (wifiConnected) {
            status.set("Finished Downloading Queue");
        }
    }
}
