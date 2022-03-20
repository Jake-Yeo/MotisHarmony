/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.collections.ObservableList;

/**
 *
 * @author 1100007967
 */
public class SongDataObject implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private String videoTitle;
    private String videoUrl;
    private String videoDuration;
    private String channelName;
    private String thumbnailUrl;
    private String errorMessage;
    private String videoID;
    private String pathToWebaFile;
    private String pathToWavFile;
    private String pathToThumbnail;
    private String safeTitleName;
    private int orderAdded;

    public SongDataObject(String videoTitle, String videoDuration, String channelName, String thumbnailUrl, String videoUrl, String videoID) {
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.channelName = channelName;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.videoID = videoID;
        //We concatanate the current time in milliseconds so that it's impossible for file names to ever be the same
        this.safeTitleName = videoTitle.replaceAll("[^a-zA-Z]", "").replaceAll("[^\\x20-\\x7e]", "") + "(" + videoID + ")" + System.currentTimeMillis(); //Gets rid all special characters that may mess up file path
        this.pathToWebaFile = PathsManager.WEBA_FOLDER_PATH.toString() + "/" + this.safeTitleName + ".weba";
        this.pathToWavFile = PathsManager.getLoggedInUserMusicFolderPath().toString() + "/" + this.safeTitleName + ".mp3";
        this.pathToThumbnail = Paths.get(PathsManager.getLoggedInUserThumbnailsPath().toString(), (this.safeTitleName + ".png")).toString();
    }

    public void setVideoTitle(String newTitle) {
        this.videoTitle = newTitle;
    }

    public void setChannelName(String newChannelName) {
        this.channelName = newChannelName;
    }

    public int getOrderAdded() {
        return this.orderAdded;
    }

    public void setOrderAdded(int order) {
        this.orderAdded = order;
    }

    public String getTitle() {
        return this.videoTitle;
    }

    public String getVideoDuration() {
        return this.videoDuration;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public String getVideoID() {
        return this.videoID;
    }

    public String getPathToWebaFile() {
        return this.pathToWebaFile;
    }

    public String getPathToWavFile() {
        return this.pathToWavFile;
    }

    public String getSafeTitleName() {
        return this.safeTitleName;
    }

    public String getPathToThumbnail() {
        return this.pathToThumbnail;
    }

    @Override
    public String toString() {
        return ("[" + getTitle() + ", " + getVideoDuration() + ", " + getChannelName() + ", " + getThumbnailUrl() + ", " + getVideoUrl() + ", " + getVideoID() + ", " + getPathToWebaFile() + ", " + getPathToWavFile() + ", " + getSafeTitleName() + ", " + getPathToThumbnail());
    }

    public static ArrayList<String> getYoutubeIdList(ArrayList<SongDataObject> sdoList) {
        ArrayList<String> sdoIdArray = new ArrayList<String>(sdoList.size());
        for (int i = 0; i < sdoList.size(); i++) {
            sdoIdArray.add(sdoList.get(i).getVideoID());
        }
        return sdoIdArray;
    }

    public static String toString(ObservableList<SongDataObject> songDataObjectArray) {
        String stringToReturn = "";
        for (int i = 0; i < songDataObjectArray.size(); i++) {
            stringToReturn += songDataObjectArray.get(i).toString();
        }
        return stringToReturn;
    }
}
