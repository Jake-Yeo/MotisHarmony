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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author Jake Yeo
 */
public class YoutubeVideoPageParser {
//broken name conventions please fix later!

    private final static String YT_TITLE_START_IDENTIFIER = "og:title\" content=\"";
    private final static String YT_TITLE_END_IDENTIFIER = "\"";
    private final static String YT_PLAYLIST_START_IDENTIFIER = "playlist\":{\"playlist\":{\"title\":\""; //Can't find end identifier because multiple links of the same kind are in one html file, must look through x amt of videos
    private final static String YT_PLAYLIST_LENGTH_START_IDENTIFIER = "\"totalVideos\":";
    private final static String YT_PLAYLIST_LENGTH_END_IDENTIFIER = ",";
    private final static String YT_PLAYLIST_VIDEO_URLS_START_IDENTIFIER = "\"thumbnails\":[{\"url\":\"https://i.ytimg.com/vi/";
    private final static String YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER = "/";
    private final static String YT_VIDEO_LINK_ID_START_IDENTIFIER = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_LINK_ID_END_IDENTIFIER = "&t=";
    private final static String YT_VIDEO_LINK_ID_END_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON = "?t=";
    private final static String YT_PLAYLIST_AND_VIDEO_URL_START = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_URL_START_IDENTIFIER_WHEN_COPIED_FROM_SHARE_BUTTON = "https://youtu.be/";
    private final static String YT_VIDEO_URL_CORRUPT_IDENTIFIER = "\"";
    private final static String YT_DOWNLOADABLE_PLAYLIST_LIST_ID_START_IDENTIFIER = "&list=";
    private final static String YT_PLAYLIST_LIST_IDENTIFIER = "list=";
    private final static String YT_WHOLE_PLAYLIST_LIST_ID_START_IDENTIFIER = "list=";
    private final static String YT_PLAYLIST_LIST_ID_END_IDENTIFIER = "&index=";
    private final static String YT_DOWNLOADABLE_PLAYLIST_WHOLE_VIEW_URL_STARTER = "https://www.youtube.com/playlist?list=";//Example of playlist whole view here https://www.youtube.com/playlist?list=PLleY9vT8KgEjaoNn9HwNceQ28i90HsqZE
    private final static String YT_WHOLE_VIEW_PLAYLIST_VIDEO_ID_AT_INDEX_ONE_START_IDENTIFIER = "videoId\":\"";
    private final static String YT_WHOLE_VIEW_PLAYLIST_VIDEO_ID_AT_INDEX_ONE_END_IDENTIFIER = "\"";
    private final static String YT_DOWNLOADABLE_PLAYLIST_STARTER = "https://www.youtube.com/watch?v=";
    private final static String YT_VIDEO_URL_STARTER = "https://www.youtube.com/watch?v=";
    private final static String YT_DOWNLOADABLE_PLAYLIST_END_IDENTIFIER = "indexText";
    private final static String YT_DOWNLOADABLE_PLAYLIST_LIST_PARAM_STARTER = "&list=";
    private final static int YT_PLAYLISTVIDEO_CAPPED_VIDEO_AMT = 200;
    private static final String YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER = "Age-restricted";
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

    public static String getHtml(String url) throws IOException {//To prevent an ip ban from websites, don't overuse this method.
        System.out.println("html getter called");
        String html = "";
        URL ytLink = new URL(url);
        BufferedReader in = null;
        in = new BufferedReader(
                new InputStreamReader(ytLink.openStream()));
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
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (videoUrl.contains(YT_PLAYLIST_LIST_IDENTIFIER)) {
            videoUrl = convertToWholePlaylistView(videoUrl);
        }
        try {
            html = getHtml(videoUrl);
        } catch (Exception e) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is likely not a link!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is age restricted and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(YOUTUBE_VIDEO_UNAVAILABLE_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is unavailable to the public!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(YOUTUBE_VIDEO_PRIVATE_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is private and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(YOUTUBE_LIVESTREAM_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a livestream and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(RADIO_PLAYLIST_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a radio player and cannot be downloaded!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        if (html.contains(YOUTUBE_PLAYLIST_DOES_NOT_EXIST_IDENTIFIER)) {
            didErrorOccur = true;
            errorMessage = videoUrl + " is a playlist that is either private or does not exist!";
            return new ErrorDataObject(didErrorOccur, errorMessage);
        }
        didErrorOccur = false;
        errorMessage = null;

        return new ErrorDataObject(didErrorOccur, errorMessage);
    }

    public static boolean isYoutubeLinkAvailableToPublic(String youtubeVideolink) throws IOException {//Ex is the youtube video link given not age resticted, private, unavailable?
        String html = "";
        try {
            html = getHtml(youtubeVideolink);
        } catch (Exception e) {
            return false;
        }
        if (html.contains(YOUTUBE_VIDEO_AGE_RESTRICTED_IDENTIFIER) || html.contains(YOUTUBE_VIDEO_UNAVAILABLE_IDENTIFIER) || html.contains(YOUTUBE_VIDEO_PRIVATE_IDENTIFIER)) {
            return false;
        }
        return true;
    }

    public static boolean isLinkAPlaylist(String link) {//make this more specific, radio and playlist links are indistinguishable, make sure to look at the html code too!//Should be able to be replaced with doesYoutubePlaylistExist
        if (link.contains(YT_PLAYLIST_LIST_IDENTIFIER)) {
            return true;
        }
        return false;
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

    public static UrlDataObject getYoutubeVideoData(String youtubeUrl) throws IOException {
        String html = getHtml(youtubeUrl);
        String thumbnailUrl = infoParserTool(html, YOUTUBE_VIDEO_THUMBNAIL_URL_START_IDENTIFIER, YOUTUBE_VIDEO_THUMBNAIL_URL_END_IDENTIFIER);
        String channelName = infoParserTool(html, YOUTUBE_VIDEO_CHANNEL_NAME_START_IDENTIFIER, YOUTUBE_VIDEO_CHANNEL_NAME_END_IDENTIFIER);
        String videoTitle = infoParserTool(html, YT_TITLE_START_IDENTIFIER, YT_TITLE_END_IDENTIFIER);
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
        UrlDataObject youtubeVideoData = new UrlDataObject(videoTitle, videoDuration, channelName, thumbnailUrl);
        return youtubeVideoData;
    }

    public static String[] getPlaylistYoutubeUrls(String youtubePlaylistUrl) throws IOException {//in this method, you can download playlists containing between and including 1-5000 videos
        youtubePlaylistUrl = getDownloadablePlaylistUrl(youtubePlaylistUrl); // this will allow the user to input playlists in whole view or playlists which are downloadable without any errors.
        String html = getHtml(youtubePlaylistUrl);
        html = infoParserToolTrimToStart(html, YT_PLAYLIST_START_IDENTIFIER);//This will find the start of the playlist information in the html thus getting rid of any urls that may interfere with this method
        int playlistLength = Integer.parseInt(infoParserTool(html, YT_PLAYLIST_LENGTH_START_IDENTIFIER, YT_PLAYLIST_LENGTH_END_IDENTIFIER));//Since there is an unidentifiable amount of urls below the start of the playlist information section, we must get the length of the playlist so we can loop through only a certain amount of playlists. Error could occur if you are not at the actual playlist, you must be videwing a video from the playlist!
        String[] urlList = new String[playlistLength]; //This creates the String array which will hold information about the urls obtained from the playlist.      
        String urlListStringToSplit = "";//This will store urls taken from the playlist into the url so that we can compare urls and see if that url is already in the string
        String lastVideoUrlGotten = "";
        int howManyVideoUrlsToObtain = 200;//This variable tells us how many video urls to obtain as this changes based on where we are in the playlist. This also fixes the bug where the download playlist button breaks after using it nine times. It happened because I instead used to use an instance variable which kept on incrementing it's value until eventually the inner for loop below stopped working after the button was pressed nine times.
        int repeatAmt = (int) Math.floor(playlistLength / YT_PLAYLISTVIDEO_CAPPED_VIDEO_AMT) + 3; //200 goes into 502 twice. Since we want to get the last 102 videos we add one since we are flooring it. However, by adding 3 instead, we ensure that the user is able to download the maximum amount of youtube videos allowed in a playlist which is 5000
        html = html.substring(0, html.lastIndexOf(YT_DOWNLOADABLE_PLAYLIST_END_IDENTIFIER));//This will cut off all the youtube urls which are not in the playlist when you only repeat through once. Also we do this last so as to not intefere with the code above

        System.out.println("Reapeats " + repeatAmt);//tells us how many times the for loop will repeat
        for (int timesRepeated = 0; timesRepeated < repeatAmt; timesRepeated++) {
            System.out.println("Outer loop running");
            for (int i = 0; i < howManyVideoUrlsToObtain; i++) {
                System.out.println("Inner loop running");
                html = infoParserToolTrimToStart(html, YT_PLAYLIST_VIDEO_URLS_START_IDENTIFIER);//This will get rid of the urls above the playlist which will intefere with the parsing done below
                if (!(infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER).contains(YT_VIDEO_URL_CORRUPT_IDENTIFIER))) {//Should use better requirements. This basically tells us when a youtube video url is just a broken url since we finsished storing the last video in the playlist into our string. Also because using the "x unavailable videos are hidden" is unreliable.
                    if (!urlListStringToSplit.contains(YT_PLAYLIST_AND_VIDEO_URL_START + infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER))) {//Checks if the youtube url is already in the string since we always get the first video in the playlist. That means we may get urls that we already stored in our string, urls that are already in the string are skipped over and not added to the string
                        urlListStringToSplit += " " + YT_PLAYLIST_AND_VIDEO_URL_START + infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER);
                        System.out.println(YT_PLAYLIST_AND_VIDEO_URL_START + infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER));
                        lastVideoUrlGotten = YT_PLAYLIST_AND_VIDEO_URL_START + infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER);
                        System.out.println("What problem is this causing?" + infoParserToolRemoveEnd(html, YT_PLAYLIST_VIDEO_URLS_END_IDENTIFIER));
                    }
                } else {
                    i = howManyVideoUrlsToObtain + 100;//stops the loop
                    break;//stops the loop? idk if this actually does anything, probably stops the loop tho
                }

            }
            System.out.println(lastVideoUrlGotten + " video to make playlist with");
            howManyVideoUrlsToObtain = howManyVideoUrlsToObtain * 2 - 1; //When we load a playlist from the index of 200, it loads up 399 videos. So we must set the YT_PLAYLISTVIDEO_CAPPED_VIDEO_AMT to 399 in order to get the other 199 videos we haven't downloaded yet. The other 200 videos we still loop through and attempt to store, but we have an if statement to prevent that.
            html = getHtml(YT_PLAYLIST_AND_VIDEO_URL_START + getYoutubeVideoID(lastVideoUrlGotten) + YT_DOWNLOADABLE_PLAYLIST_LIST_ID_START_IDENTIFIER + getYoutubePlaylistListId(youtubePlaylistUrl)); //This will load up the new videos in the playlist
            html = infoParserToolTrimToStart(html, YT_PLAYLIST_START_IDENTIFIER);//This will find the start of the playlist information in the html thus getting rid of any urls that may interfere with this method
            html = html.substring(0, html.lastIndexOf(YT_DOWNLOADABLE_PLAYLIST_END_IDENTIFIER));//This will cut off all the youtube urls which are not in the playlist
            //PrintWriter pw = new PrintWriter(new FileWriter("html.txt"));
            // pw.print(urlListStringToSplit);
            // pw.close();
        }
        urlList = urlListStringToSplit.trim().split(" ");//splits the string which collected the youtube urls in the playlist and turns it into an array
        System.out.println(urlList.length + " The length");//Sometimes the length may be different from what youtube states. This is because of a glitch in youtube where the same exact video are put into the youtube playlist twice.

        return urlList;
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
}
