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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
public class YoutubeDownloaderManager {

    private static WebDriver driver;
    private static boolean doneDownloading = true;
    private static boolean isChromeDriverActive = false;
    @FXML
    private static DownloadPageViewController downloadPageViewController = new DownloadPageViewController();
    private static boolean isPlaylistUrlGetterCurrentlyGettingUrls = false;
    private static final String YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER = "Age-restricted";
    private static final String YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER = "ctier";//Ad audio links are the only links that contain cteir in them
    private static final String YOUTUBE_AD_AUDIO_SOURCE_START_IDENTIFIER = "https:";
    private static final String YOUTUBE_AD_AUDIO_SOURCE_END_IDENTIFIER = ",";
    private static final String YOUTUBE_AUDIO_SOURCE_IDENTIFIER = "mime=audio";
    private static final String YOUTUBE_AUDIO_SOURCE_START_IDENTIFIER = "https:";
    private static final String YOUTUBE_AUDIO_SOURCE_END_IDENTIFIER = "range";
    private static ObservableList<String> youtubeUrlDownloadQueueList = FXCollections.observableArrayList();

    public static void setupChromeDriver() {
        //Set the Path of Executable Browser Driver
        //System.setProperty("webdriver.chrome.driver", "chromedriver.exe");//We probably don't need this anymore because we automatically get the webdriver below
        WebDriverManager.chromedriver().setup();//This will automatically update the chrome webdriver to the proper version
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");//start the chrome browser headless, can be changed if you want
        options.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(options);
    }

    public static void quitChromeDriver() {
        driver.quit();
    }

    public static void addYoutubeUrlsToDownloadQueue(String youtubeUrl) throws IOException {//Since we allow the user to input as many playlists as they want to download, we need a way to manage and organize downloads so that we don't end up with corrupted audio downloads
        File youtubeUrlDownloadManagerFile = new File("YoutubeUrlsToDownload.txt");
        youtubeUrl = YoutubeVideoPageParser.getRegularYoutubeUrl(youtubeUrl);//makes sure that any variations of one youtube url will always be turned into one variation to allow for url comparison so that duplicated urls are not present withing the downloader queue
        if (!youtubeUrlDownloadQueueList.contains(youtubeUrl)) {//Makes sure that a youtube url is not added to the download queue list multiple times
            youtubeUrlDownloadQueueList.add(youtubeUrl);//adds the youtube url to the download queue
        }
    }

    public static boolean isAppDownloadingFromDownloadQueue() {
        return isChromeDriverActive;
    }

    public static void setIsChromeDriverActive(boolean tf) {
        isChromeDriverActive = tf;
    }

    public static ObservableList<String> getYoutubeUrlDownloadQueueList() {
        return youtubeUrlDownloadQueueList;
    }

    public static void addSongsFromPlaylistToDownloadQueue(String youtubePlaylistLink) throws IOException {
        isPlaylistUrlGetterCurrentlyGettingUrls = true;
        String[] youtubePlaylistUrls = YoutubeVideoPageParser.getPlaylistYoutubeUrls(youtubePlaylistLink);
        for (int i = 0; i < youtubePlaylistUrls.length;) {
            if (doneDownloading = true) {
                addYoutubeUrlsToDownloadQueue(youtubePlaylistUrls[i]);
                System.out.println(i);
                i++;
            }

        }
        isPlaylistUrlGetterCurrentlyGettingUrls = false;
    }

    public static boolean isPlaylistUrlGetterCurrentlyGettingUrls() {
        return isPlaylistUrlGetterCurrentlyGettingUrls;
    }
//method below may accidentally get ad, tho im not too sure how to fix or if that was due to a faulty input

    private static String getRidOfAdUrl(String netData) {//This will get rid of any ad video or audio links.
        while (netData.contains(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER)) {
            String netDataFirstHalf = "";
            String netDataSecondHalf = "";
            netDataFirstHalf = netData.substring(0, netData.lastIndexOf(YOUTUBE_AD_AUDIO_SOURCE_START_IDENTIFIER, netData.indexOf(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER))); //This will get the first half of the net data from the start of the ad link
            netDataSecondHalf = netData.substring(netData.indexOf(YOUTUBE_AD_AUDIO_SOURCE_END_IDENTIFIER, netData.indexOf(YOUTUBE_AUDIO_SOURCE_AD_IDENTIFIER)));//This will get the second half of net data from the end of the ad link
            netData = netDataFirstHalf + netDataSecondHalf;//We put the two halves together to get netdata which may or may not still contain ad links for audio or video, that's why we put it in a while loop
        }
        return netData;
    }

    private static String obtainYoutubeUrlAudioSource(String youtubeUrl) throws MalformedURLException, IOException {//Gets the audio source of a youtube video and returns it
        try {
            driver.get(youtubeUrl);
        } catch (Exception e) {
            quitChromeDriver();//Sometimes youtube may block your connection to their website if you request too much. That's what I think causes this error. So we reset the driver and try again
            setupChromeDriver();
            driver.get(youtubeUrl);
        }
        String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";

        String netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();//Get network traffic data
        netData = getRidOfAdUrl(netData);//We get rid of adUrls here just incase since there have been cases where ad audio has been downloaded. I don't know if this fixes the problem or not.
        String audioHtmlSource = "";
        int repeats = 0;
        while (!audioHtmlSource.contains("media")) {
            while (!netData.contains(YOUTUBE_AUDIO_SOURCE_IDENTIFIER)) { //Sometimes url is loaded but the audio source isn't present in the network traffic data, so reload the site and do it until we get the audio source.
                System.out.println("Error saved!");
                System.out.println(youtubeUrl + "This link didn't have mime=audio!");
                if (repeats > 5) {
                    System.out.println("Error getting this this link " + youtubeUrl);
                    netData = "error";
                    break;

                }
                driver.get(youtubeUrl);
                netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();
                netData = getRidOfAdUrl(netData);//We must get rid of the ad urls before checking again if netData contains the YOUTUBE_AUDIO_SOURCE_IDENTIFIER because sometimes only the ad link has YOUTUBE_AUDIO_SOURCE_IDENTIFIER in it.
                repeats++;
            }
            if (netData.equals("error")) {
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
                driver.get(netData);
                audioHtmlSource = driver.getPageSource();//Sometimes an audio link which is parsed may contain another url to the correct source audio url which is why we check the html of the url we obtain. Also, it seems impossible to read the data with htmlGetter, so we use the selenium method instead
                System.out.println("Audio html stuff " + audioHtmlSource);
                driver.get("data:,");//just prevents sound from playing from chrome driver by switching it to an easy to load website.
            } else {//This will reload the youtube url and attempt to get netData that had a source url which isn't text
                driver.get(youtubeUrl);
                netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();
                netData = getRidOfAdUrl(netData);//We must get rid of the ad urls before checking again if netData contains the YOUTUBE_AUDIO_SOURCE_IDENTIFIER because sometimes only the ad link has YOUTUBE_AUDIO_SOURCE_IDENTIFIER in it.
            }
        }
        return netData;

    }

    public static void addYoutubeLinkToDownloadQueue(String youtubeUrl) throws IOException {
        if (YoutubeVideoPageParser.isLinkAPlaylist(youtubeUrl)) {
            addSongsFromPlaylistToDownloadQueue(youtubeUrl);
        } else {
            addYoutubeVideoUrlToDownloadQueue(youtubeUrl);
        }
    }

    public static void addYoutubeVideoUrlToDownloadQueue(String youtubeUrl) throws IOException {//make private
        youtubeUrl = YoutubeVideoPageParser.getRegularYoutubeUrl(youtubeUrl);//The url the user pastes in maybe of many varaition, we use this method to turn many variations of a url into just one url. This lets us compare urls in the download manager so that we don't add two urls of the same video in the download manager.
        addYoutubeUrlsToDownloadQueue(youtubeUrl);
    }

    public static void downloadYoutubeVideoUrl(String youtubeUrlFromDownloadManager) throws MalformedURLException, IOException, EncoderException { //this will download and obtain any youtube audio source links given to it.
        URL downloadURL = null;
        String possibleYoutubeUrl = obtainYoutubeUrlAudioSource(youtubeUrlFromDownloadManager);
        if (!possibleYoutubeUrl.equals("error")) {
            if (!(YoutubeVideoPageParser.getHtml(youtubeUrlFromDownloadManager).contains(YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER))) {//We can remove this if statement, we are handling it in another method
                downloadURL = new URL(possibleYoutubeUrl);//Out of range happens when mime=audio cannot be found
                int count = 0;
                String youtubeTitleSafeName = YoutubeVideoPageParser.getYoutubeVideoData(youtubeUrlFromDownloadManager).getTitle().replaceAll("[^a-zA-Z]", "").replaceAll("[^\\x20-\\x7e]", "") + "[" + YoutubeVideoPageParser.getYoutubeVideoID(youtubeUrlFromDownloadManager) + "]"; //Gets rid of foreign language characters;//Gets music title to use in the file name
                String downloadedPath = PathsManager.WEBA_FOLDER_PATH.toString() + "/" + youtubeTitleSafeName + ".weba";
                try ( BufferedInputStream bis = new BufferedInputStream(downloadURL.openStream());  FileOutputStream fos = new FileOutputStream(downloadedPath)) {
                    int i = 0;
                    final byte[] data = new byte[1024];
                    while ((count = bis.read(data)) != -1) {
                        i += count;
                        fos.write(data, 0, count);
                    }
                } catch (IOException ex) {
                }
                new Thread(//We use a thread so that it doesn't take an extra few seconds to download a youtube video, if the conversion cannot keep up then it will be added to the conversion queue.
                        new Runnable() {
                    public void run() {
                        try {
                            AudioConverterManager.addToConversionQueue(downloadedPath, youtubeTitleSafeName + ".weba");//If two videos have the same title names then this method will fail, each music file must have its own unique name. Fix the same name bug by incorporating the youtube video IDs in the name of the file
                        } catch (EncoderException ex) {
                            Logger.getLogger(YoutubeDownloaderManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            } else {
                System.out.println(youtubeUrlFromDownloadManager + " is age restricted! Please find another link of this video which isn't age restricted!");
            }
        }
        doneDownloading = true;//needs to be outside the if statement!
    }

    public static void downloadSongsFromDownloadQueue() throws FileNotFoundException, IOException, EncoderException {//We put this method here so that we don't need a while loop to update the downloadQueueList
        setIsChromeDriverActive(true); // this will make sure that the chrome driver isn't restarted multiple times in order to increase download speed.
        setupChromeDriver();
        while (!youtubeUrlDownloadQueueList.isEmpty()) {//The user may continue to add urls to the download queue list, so we continue to download untill the download queue is empty
            DownloadPageViewController.setFirstLinkFromDownloadQueueIsDownloading(true);
            if (YoutubeVideoPageParser.isYoutubeLinkAvailableToPublic(youtubeUrlDownloadQueueList.get(0))) {//The youtube urls in the playlists are not checked, so we must check those here.
                downloadYoutubeVideoUrl(youtubeUrlDownloadQueueList.get(0));//Gets the first youtube url in the download queue list
            } else {
                downloadPageViewController.addErrorToErrorListWithJavafxThread("WARNING! " + youtubeUrlDownloadQueueList.get(0) + " is likely an age restricted video, please find link which is not age restricted!", true);
            }
            youtubeUrlDownloadQueueList.remove(0);//Removes the youtube url from the list which was downloaded.
            //downloadPageViewController.updateDownloadQueueListViewWithJavafxThread(true);
            DownloadPageViewController.setFirstLinkFromDownloadQueueIsDownloading(false);
        }
        YoutubeDownloaderManager.quitChromeDriver();
        YoutubeDownloaderManager.setIsChromeDriverActive(false);
    }
}
