/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.collections.ObservableList;

/**
 *
 * @author 1100007967
 */
public class UrlDataObject {

    private String videoTitle;
    private String videoUrl;
    private String videoDuration;
    private String channelName;
    private String thumbnailUrl;
    private String errorMessage;
    private String videoID;
    private String pathToWebaFile;
    private String pathToWavFile;

    public UrlDataObject(String videoTitle, String videoDuration, String channelName, String thumbnailUrl, String videoUrl, String videoID) {
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.channelName = channelName;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.videoID = videoID;
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

    public void setPathToWebaFile(String pathToWebaFile) {
        this.pathToWebaFile = pathToWebaFile;
    }

       public void setPathToWavFile(String pathToWavFile) {
        this.pathToWavFile = pathToWavFile;
    }
    
    @Override
    public String toString() {
        return ("[" + getTitle() + ", " + getVideoDuration() + ", " + getChannelName() + ", " + getThumbnailUrl() + ", " + getVideoUrl() + ", " + getVideoID() + ", " + getPathToWebaFile() + ", " + getPathToWavFile());
    }

    public static String toString(ObservableList<UrlDataObject> urlDataObjectArray) {
        String stringToReturn = "";
        for (int i = 0; i < urlDataObjectArray.size(); i++) {
            stringToReturn += urlDataObjectArray.get(i).toString();
        }
        return stringToReturn;
    }
}
