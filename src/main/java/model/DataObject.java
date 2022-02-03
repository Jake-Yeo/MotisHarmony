/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author 1100007967
 */
public class DataObject {

    private String videoTitle;
    private String videoDuration;
    private String channelName;
    private String thumbnailUrl;
    private String errorMessage;
    private boolean didErrorOccur;

    public DataObject(String videoTitle, String videoDuration, String channelName, String thumbnailUrl) {
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.channelName = channelName;
        this.thumbnailUrl = thumbnailUrl;
    }

    public DataObject(boolean didErrorOccur, String errorMessage) {
        this.didErrorOccur = didErrorOccur;
        this.errorMessage = errorMessage;
    }

    DataObject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean getDidErrorOccur() {
        return this.didErrorOccur;
    }
}
