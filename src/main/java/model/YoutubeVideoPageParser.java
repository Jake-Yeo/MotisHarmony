/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author Jake Yeo
 */
public class YoutubeVideoPageParser {
//broken name conventions please fix later!

    private static final String YOUTUBE_AUDIO_MAX_BYTE_RANGE_END_IDENTIFIER = "&";
    private static final String YOUTUBE_AUDIO_MAX_BYTE_RANGE_START_IDENTIFIER = "clen=";//This will get the byte range of the youtube video so we can download the video in segments allowing us to bypass throttling

    private final static String YT_TITLE_START_IDENTIFIER = "og:title\" content=\"";
    private final static String YT_TITLE_END_IDENTIFIER = "\"";
    private final static String YT_PLAYLIST_LENGTH_START_IDENTIFIER = "\"totalVideos\":";
    private final static String YT_PLAYLIST_LENGTH_END_IDENTIFIER = ",";
    private final static String YT_VIDEO_LINK_ID_START_IDENTIFIER = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_LINK_ID_END_IDENTIFIER = "&t=";
    private final static String YT_VIDEO_LINK_ID_END_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON = "?t=";
    private final static String YT_PLAYLIST_AND_VIDEO_URL_START = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_URL_START_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON = "https://youtu.be/";
    private final static String YT_DOWNLOADABLE_PLAYLIST_LIST_ID_START_IDENTIFIER = "&list=";
    private final static String YT_PLAYLIST_LIST_IDENTIFIER = "list=";
    private final static String YT_WHOLE_PLAYLIST_LIST_ID_START_IDENTIFIER = "list=";
    private final static String YT_PLAYLIST_LIST_ID_END_IDENTIFIER = "&index=";
    private final static String YT_DOWNLOADABLE_PLAYLIST_WHOLE_VIEW_URL_STARTER = "https://www.youtube.com/playlist?list=";//Example of playlist whole view here https://www.youtube.com/playlist?list=PLleY9vT8KgEjaoNn9HwNceQ28i90HsqZE
    private final static String YT_DOWNLOADABLE_PLAYLIST_STARTER = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_URL_STARTER = "https://www.youtube.com/watch?v=";
    private final static String YT_DOWNLOADABLE_PLAYLIST_LIST_PARAM_STARTER = "&list=";
    private final static int YT_PLAYLISTVIDEO_CAPPED_VIDEO_AMT = 200;
    private static final String YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER = "Age-restricted";
    private static final String YOUTUBE_VIDEO_HARM_RESTRICTED_IDENTIFIER = "[{\"text\":\"Viewer discretion is advised. \"}";
    private static final String YOUTUBE_VIDEO_UNAVAILABLE_IDENTIFIER = "Video unavailable";
    private static final String YOUTUBE_PLAYLIST_DOES_NOT_EXIST_IDENTIFIER = "The playlist does not exist";
    private static final String YOUTUBE_VIDEO_PRIVATE_IDENTIFIER = "This video is private";
    private static final String YOUTUBE_PLAYLIST_EXISTS_IDENTIFIER = "&amp";//you're supposed to add this at the end of a youtube video url and check to see if the new identifier exists withing the given youtube playlist link on the video at index one
    private static final String RADIO_PLAYLIST_IDENTIFIER = "This playlist type is unviewable";//Basically if you try to load a playlist whole view page with a radio ID then this shows up in the html which can be used to identify radio playlists
    private static final String YOUTUBE_LIVESTREAM_IDENTIFIER = "liveStreamabilityRenderer";
    private static final String YOUTUBE_VIDEO_CHANNEL_NAME_START_IDENTIFIER = "\"name\": \"";
    private static final String YOUTUBE_VIDEO_CHANNEL_NAME_END_IDENTIFIER = "\"";
    private static final String YOUTUBE_VIDEO_LENGTH_IN_SECONDS_START_IDENTIFIER = "\"lengthSeconds\":\"";
    private static final String YOUTUBE_VIDEO_LENGTH_IN_SECONDS_END_IDENTIFIER = "\"";
    private static final String YOUTUBE_VIDEO_THUMBNAIL_URL_START_IDENTIFIER = "og:image\" content=\"";
    private static final String YOUTUBE_VIDEO_THUMBNAIL_URL_END_IDENTIFIER = "\"";
    private static final String YOUTUBE_VIDEO_INFO_START_IDENTIFIER = "videoPrimaryInfoRenderer";
    private static final String YOUTUBE_VIDEO_ID_START_IDENTIFIER = "videoId\":\"";
    private static final String YOUTUBE_VIDEO_ID_END_IDENTIFIER = "\"";

    private static final String YOUTUBE_PLAYLIST_VIDEO_INFO_START_IDENTIFIER = "PanelVideoRenderer\":{\"title\"";//This identifies the start of the playlist, and also identifies the start of video info for all videos in the playlist.
    private static final String YOUTUBE_PLAYLIST_VIDEO_TITLES_START_IDENTIFIER = "simpleText\":\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_TITLES_END_IDENTIFIER = "\"},\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_CHANNEL_NAME_START_IDENTIFIER = "text\":\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_CHANNEL_NAME_END_IDENTIFIER = "\",\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_THUMBNAIL_URL_START_IDENTIFIER = "\"thumbnails\":[{\"url\":\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_THUMBNAIL_URL_END_IDENTIFIER = "\",\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_DURATION_START_IDENTIFIER = "simpleText\":\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_DURATION_END_IDENTIFIER = "\"}";
    private static final String YOUTUBE_PLAYLIST_VIDEO_ID_START_IDENTIFIER = "{\"videoId\":\"";
    private static final String YOUTUBE_PLAYLIST_VIDEO_ID_END_IDENTIFIER = "\",\"";

    public static String getHtml(String url) throws IOException  {//To prevent an ip ban from websites, don't overuse this method.
        System.out.println("html getter called");
        String html = "";
        URL ytLink = new URL(url);
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(ytLink.openStream(), "UTF-8"));//Non english characters will be displayed properly
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            html += inputLine;
        }
        in.close();
        return html;
    }

    public static ErrorDataObject isUrlValid(String videoUrl) throws IOException {
        boolean didErrorOccur = false;
        String html = "";
        String errorMessage = "";
        if (!(videoUrl.contains("watch?v=") || videoUrl.contains("?list=") || videoUrl.contains("&list=") || videoUrl.contains("youtu.be"))) {
            didErrorOccur = true;
            errorMessage = "The url you input must be the link of a youtube playlist or video!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (videoUrl.contains(YT_PLAYLIST_LIST_IDENTIFIER)) {
            videoUrl = convertToWholePlaylistView(videoUrl);
            System.out.println("converted wholelist " + videoUrl);
        }
        try {
            html = getHtml(videoUrl);
            YoutubeDownloader.getYtdCurrentlyUsing().setWifiConnected(true);
        } catch (java.net.UnknownHostException e) {
            didErrorOccur = true;
            errorMessage = videoUrl + " could not be accessed because you are not connected to wifi";
            YoutubeDownloader.getYtdCurrentlyUsing().setWifiConnected(false);
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        } catch (java.io.IOException e) {
            didErrorOccur = true;
            errorMessage = videoUrl + " cannot be downloaded at this time as you are IP blocked by youtube! Wait out the ban or change your ip.";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        } catch (Exception e) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is likely not a link!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is age restricted and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_VIDEO_UNAVAILABLE_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is unavailable to the public!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_VIDEO_PRIVATE_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is private and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_LIVESTREAM_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a livestream and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(RADIO_PLAYLIST_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a radio player and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_PLAYLIST_DOES_NOT_EXIST_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a playlist that is either private or does not exist!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        if (html.contains(YOUTUBE_VIDEO_HARM_RESTRICTED_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is age restricted and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage, videoUrl);
        }
        didErrorOccur = false;
        errorMessage = null;

        return new ErrorDataObject(didErrorOccur, errorMessage);
    }

    public static boolean isLinkAPlaylist(String link) {//make this more specific, radio and playlist links are indistinguishable, make sure to look at the html code too!//Should be able to be replaced with doesYoutubePlaylistExist
        return link.contains(YT_PLAYLIST_LIST_IDENTIFIER);
    }

    private static String getYoutubePlaylistListId(String youtubeUrl) {
        String playlistId = infoParserToolTrimToStart(youtubeUrl, YT_WHOLE_PLAYLIST_LIST_ID_START_IDENTIFIER);
        if (playlistId.contains(YT_PLAYLIST_LIST_ID_END_IDENTIFIER)) {
            playlistId = infoParserToolRemoveEnd(playlistId, YT_PLAYLIST_LIST_ID_END_IDENTIFIER);
        }
        return playlistId;
    }

    public static String convertToWholePlaylistView(String potentialRadioPlaylist) {
        return YT_DOWNLOADABLE_PLAYLIST_WHOLE_VIEW_URL_STARTER + getYoutubePlaylistListId(potentialRadioPlaylist);
    }

    public static String getDownloadablePlaylistUrl(String youtubePlaylistUrl) throws IOException {//A playlist link comes in many forms. This method will transform any form of the playlist link into a specific form that we can use to obtain the youtube urls within that playlist
        String downloadablePlaylistLink = YT_DOWNLOADABLE_PLAYLIST_STARTER + YT_DOWNLOADABLE_PLAYLIST_LIST_PARAM_STARTER + getYoutubePlaylistListId(youtubePlaylistUrl);
        return downloadablePlaylistLink;
    }

    public static String getYoutubeVideoID(String youtubeUrl) {
        String youtubeId = youtubeUrl;
        if (youtubeUrl.contains(YT_PLAYLIST_AND_VIDEO_URL_START)) {
            youtubeId = infoParserToolTrimToStart(youtubeId, YT_VIDEO_LINK_ID_START_IDENTIFIER);
            if (youtubeId.contains(YT_VIDEO_LINK_ID_END_IDENTIFIER)) {
                youtubeId = infoParserToolRemoveEnd(youtubeId, YT_VIDEO_LINK_ID_END_IDENTIFIER);
            }
        } else {
            youtubeId = infoParserToolTrimToStart(youtubeId, YT_VIDEO_URL_START_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON);

            if (youtubeId.contains(YT_VIDEO_LINK_ID_END_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON)) {
                youtubeId = infoParserToolRemoveEnd(youtubeId, YT_VIDEO_LINK_ID_END_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON);
            }
        }
        return youtubeId;
    }

    public static String getRegularYoutubeUrl(String youtubeUrl) {//This will change a youtube url containing a time stamp, or a url that looks like this "https://youtu.be/fMriamkSXTk?t=3233" into this https://www.youtube.com/watch?v=fMriamkSXTk
        return YT_VIDEO_URL_STARTER + getYoutubeVideoID(youtubeUrl);
    }

    public static String constructYoutubeUrlViaID(String id) {
        return YT_VIDEO_URL_STARTER + id;
    }

    public static SongDataObject getYoutubeVideoData(String youtubeUrl) throws IOException {
        String html = getHtml(youtubeUrl);
        String thumbnailUrl = infoParserTool(html, YOUTUBE_VIDEO_THUMBNAIL_URL_START_IDENTIFIER, YOUTUBE_VIDEO_THUMBNAIL_URL_END_IDENTIFIER);
        String channelName = infoParserTool(html, YOUTUBE_VIDEO_CHANNEL_NAME_START_IDENTIFIER, YOUTUBE_VIDEO_CHANNEL_NAME_END_IDENTIFIER);
        String videoTitle = infoParserTool(html, YT_TITLE_START_IDENTIFIER, YT_TITLE_END_IDENTIFIER);
        String youtubeVideoId = infoParserTool(infoParserToolTrimToStart(html, YOUTUBE_VIDEO_INFO_START_IDENTIFIER), YOUTUBE_VIDEO_ID_START_IDENTIFIER, YOUTUBE_VIDEO_ID_END_IDENTIFIER);
        System.out.println(youtubeVideoId);
        videoTitle = videoTitle.replace("&#39;", "'"); //Replaces the apostrophe identifier with apostrophe
        //title = title.replaceAll("[^\\x20-\\x7e]", ""); //Gets rid of foreign language characters
        videoTitle = videoTitle.trim().replaceAll(" +", " ");
        String videoDuration = "";
        String stringDurationMinutes = "";
        int durationInSeconds = Integer.parseInt(infoParserTool(html, YOUTUBE_VIDEO_LENGTH_IN_SECONDS_START_IDENTIFIER, YOUTUBE_VIDEO_LENGTH_IN_SECONDS_END_IDENTIFIER));
        int durationMinutes = (int) Math.floor(durationInSeconds / 60);
        int durationHours = 0;
        String remaindingSeconds = "" + (durationInSeconds - durationMinutes * 60);
        if (remaindingSeconds.length() == 1) {
            remaindingSeconds = 0 + remaindingSeconds;
        }
        if (durationMinutes >= 60) { //This will convert the youtube duration from milliseconds, to a readable format.
            durationHours = (int) Math.floor(durationMinutes / 60);
            durationMinutes = durationMinutes - durationHours * 60;
            stringDurationMinutes = durationMinutes + "";
            if (stringDurationMinutes.length() == 1) {
                stringDurationMinutes = 0 + stringDurationMinutes;
            }
            videoDuration = durationHours + ":" + stringDurationMinutes + ":" + remaindingSeconds;
        } else {
            videoDuration = durationMinutes + ":" + remaindingSeconds;
        }
        SongDataObject youtubeVideoData = new SongDataObject(videoTitle, videoDuration, channelName, thumbnailUrl, constructYoutubeUrlViaID(youtubeVideoId), youtubeVideoId);
        return youtubeVideoData;
    }

    public static ArrayList<SongDataObject> getPlaylistYoutubeUrls(String youtubePlaylistUrl) throws IOException {//in this method, you can download playlists containing between and including 1-5000 videos
        youtubePlaylistUrl = getDownloadablePlaylistUrl(youtubePlaylistUrl); // this will allow the user to input playlists in whole view or playlists which are downloadable without any errors.
        String html = getHtml(youtubePlaylistUrl);
        String youtubeIdsCurrentlyInSongDataList = "";
        ArrayList<SongDataObject> songDataList = new ArrayList<>();//This will return a list of songDataObjects containing data about the video duration, title, channel name etc.
        //html = infoParserToolTrimToStart(html, YT_PLAYLIST_START_IDENTIFIER);//This will find the start of the playlist information in the html thus getting rid of any urls that may interfere with this method
        int playlistLength = 0;
        try {//This is just incase the url tester fails to catch a really weird url which somehow manages to pass through. For example there's some weird youtube radio playlists that can load in whole views, so we need another way to identify them.
            playlistLength = Integer.parseInt(infoParserTool(html, YT_PLAYLIST_LENGTH_START_IDENTIFIER, YT_PLAYLIST_LENGTH_END_IDENTIFIER));//Since there is an unidentifiable amount of urls below the start of the playlist information section, we must get the length of the playlist so we can loop through only a certain amount of playlists. Error could occur if you are not at the actual playlist, you must be videwing a video from the playlist!
        } catch (Exception e) {
            return null;
        }
        String urlListStringToSplit = "";//This will store urls taken from the playlist into the url so that we can compare urls and see if that url is already in the string
        String lastVideoUrlGotten = "";
        int repeatAmt = (int) Math.ceil(playlistLength / YT_PLAYLISTVIDEO_CAPPED_VIDEO_AMT) + 1; //200 goes into 502 twice. Since we want to get the last 102 videos we add one since we are flooring it. However, by adding 3 instead, we ensure that the user is able to download the maximum amount of youtube videos allowed in a playlist which is 5000
        //html = html.substring(0, html.lastIndexOf(YT_DOWNLOADABLE_PLAYLIST_END_IDENTIFIER));//This will cut off all the youtube urls which are not in the playlist when you only repeat through once. Also we do this last so as to not intefere with the code above
        System.out.println("Reapeats " + repeatAmt);//tells us how many times the for loop will repeat
        for (int timesRepeated = 0; timesRepeated < repeatAmt; timesRepeated++) {
            System.out.println("Outer loop running");
            while (html.contains(YOUTUBE_PLAYLIST_VIDEO_INFO_START_IDENTIFIER)) {
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_INFO_START_IDENTIFIER);//This will cut off all html up to the start of the first video in the playlist.
                System.out.println("while loop ran");
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_TITLES_START_IDENTIFIER);//Make sure you cut off the top in the right order or we won't be able to parse the video info properly.
                String videoTitle = infoParserToolRemoveEnd(html, YOUTUBE_PLAYLIST_VIDEO_TITLES_END_IDENTIFIER);
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_CHANNEL_NAME_START_IDENTIFIER);
                String channelName = infoParserToolRemoveEnd(html, YOUTUBE_PLAYLIST_VIDEO_CHANNEL_NAME_END_IDENTIFIER);
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_THUMBNAIL_URL_START_IDENTIFIER);
                String thumbnailUrl = infoParserToolRemoveEnd(html, YOUTUBE_PLAYLIST_VIDEO_THUMBNAIL_URL_END_IDENTIFIER);
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_DURATION_START_IDENTIFIER);
                String videoDuration = infoParserToolRemoveEnd(html, YOUTUBE_PLAYLIST_VIDEO_DURATION_END_IDENTIFIER);
                html = infoParserToolTrimToStart(html, YOUTUBE_PLAYLIST_VIDEO_ID_START_IDENTIFIER);
                String videoID = infoParserToolRemoveEnd(html, YOUTUBE_PLAYLIST_VIDEO_ID_END_IDENTIFIER);
                String videoUrl = constructYoutubeUrlViaID(videoID);
                System.out.println(videoID);
                if (!youtubeIdsCurrentlyInSongDataList.contains(videoUrl) && !SongDataObject.toString(YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList()).contains(videoUrl) && !Accounts.getLoggedInAccount().getListOfSongUrls().contains(videoUrl)) {//This if statement should look through a txt file containing all the ids of videos downloaded, if one of the ids matches the video, then don't add the SongDataObject to the SongDataList. This ensure that urls are not inputted into the downloadQueue multiple times
                    songDataList.add(new SongDataObject(videoTitle, videoDuration, channelName, thumbnailUrl, videoUrl, videoID));
                    youtubeIdsCurrentlyInSongDataList += videoID + " ";
                }
                lastVideoUrlGotten = videoUrl;
            }
            if (timesRepeated == repeatAmt) {
                break;
            }
            System.out.println(lastVideoUrlGotten + " video to make playlist with");
            html = getHtml(YT_PLAYLIST_AND_VIDEO_URL_START + getYoutubeVideoID(lastVideoUrlGotten) + YT_DOWNLOADABLE_PLAYLIST_LIST_ID_START_IDENTIFIER + getYoutubePlaylistListId(youtubePlaylistUrl)); //This will load up the new videos in the playlist
        }
        return songDataList;
    }

    private static String infoParserTool(String passedHtml, String startIdentifier, String endIdentifier) {
        passedHtml = passedHtml.substring(passedHtml.indexOf(startIdentifier) + startIdentifier.length());
        passedHtml = passedHtml.substring(0, passedHtml.indexOf(endIdentifier));
        return passedHtml;
    }

    private static String infoParserToolTrimToStart(String passedHtml, String startIdentifier) {//trims off everything starting from index 0 including the startIdentifier
        passedHtml = passedHtml.substring(passedHtml.indexOf(startIdentifier) + startIdentifier.length());
        return passedHtml;
    }

    private static String infoParserToolRemoveEnd(String passedHtml, String endIdentifier) {//Trims off everything including the endIdentifier starting from the index of the endIdentifier
        passedHtml = passedHtml.substring(0, passedHtml.indexOf(endIdentifier));
        return passedHtml;
    }

    public static int extractMaxByteRange(String url) {
        return Integer.parseInt(infoParserTool(url, YOUTUBE_AUDIO_MAX_BYTE_RANGE_START_IDENTIFIER, YOUTUBE_AUDIO_MAX_BYTE_RANGE_END_IDENTIFIER));
    }

}
