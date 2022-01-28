/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author 1100007967
 */
public class PathsManager {//This class will handle all the folder and txt creations

    private static final String APP_DATA_FOLDER_NAME = "MotisHarmony";//Folder name for where all the MotisHarmony data will be stored
    private static final String ACCOUNTS_DATA_FOLDER_NAME = "accounts";//Folder name for where all created accounts will be stored
    private static final String ACCOUNTS_USERNAME_PASSWORD_TXT = "accountUserPass";//Txt file name for users usernames and passwords
    private static final String ACCOUNT_PLAYLIST_DATA_TXT = "playlistData";//Txt file name for where playlist data will be stored
    private static final String ACCOUNT_MUSIC_DATA_TXT = "downloadedMusicData";//Txt name for where all the music data will be stored
    private static final String ACCOUNT_THUMBNAIL_FOLDER = "downloadedMusicThumbnails";//Folder name for where all downloaded thumbnails will be stored
    private static final String ACCOUNT_AUDIO_FOLDER = "downloadedMusic";//Folder name for where all files converted to wav will be stored
    private static final String WEBA_FOLDER = "downloadedWeba";//Folder name where all temporarily downloaded weba files will be stored
    private static final String ACCOUNT_SETTING_DATA_TXT = "settingsData";//Txt name for where all settings data will be stored
    public static final Path APP_DATA_FOLDER_PATH = Paths.get(System.getProperty("user.home"), APP_DATA_FOLDER_NAME);//Path to the APP_DATA_FOLDER_NAME
    public static final Path ACCOUNTS_DATA_PATH = Paths.get(APP_DATA_FOLDER_PATH.toString(), ACCOUNTS_DATA_FOLDER_NAME);//Path to the DOWNLOADS_FOLDER_NAME
    public static final Path WEBA_FOLDER_PATH = Paths.get(APP_DATA_FOLDER_PATH.toString(), WEBA_FOLDER);//Path to the WEBA_FOLDER because it would be better to just use one folder to hold the weba audio files rather than if every account had their own weba audio files. Weba files get deleted right after they are converted anyway.
    public static final Path ACCOUNT_USER_PASS_DATA_PATH = Paths.get(APP_DATA_FOLDER_PATH.toString(), ACCOUNTS_USERNAME_PASSWORD_TXT + ".txt");//Path to the ACCOUNTS_USERNAME_PASSWORD_TXT
    private static Path loggedInUserDataPath = null;//The below paths are just the paths which will lead to the folders withing the accounts folder
    private static Path loggedInUserSettingsPath = null;
    private static Path loggedInUserPlaylistsPath = null;
    private static Path loggedInUserDownloadedMusicDataPath = null;
    private static Path loggedInUserThumbnailsPath = null;
    private static Path loggedInUserDownloadedMusicPath = null;

    public static void setUpFolders() throws IOException {
        createFolder(Paths.get(System.getProperty("user.home")), APP_DATA_FOLDER_NAME);
        createFolder(APP_DATA_FOLDER_PATH, ACCOUNTS_DATA_FOLDER_NAME);
        createFolder(APP_DATA_FOLDER_PATH, WEBA_FOLDER);
        createTextFile(APP_DATA_FOLDER_PATH, ACCOUNTS_USERNAME_PASSWORD_TXT);
    }

    public static void setUpAccountFoldersAndTxtFiles(String username) throws IOException {
        createFolder(PathsManager.ACCOUNTS_DATA_PATH, username);//creates a folder in the Accounts folder that is named after the users name
        setLoggedInUserDataPath(username);//This will set up the path to the data of the account currently logged in
        createTextFile(getLoggedInUserDataPath(), ACCOUNT_MUSIC_DATA_TXT);//This creates a text file//This is setting up all the text files which must be created for the AccountDataManager to work
        createTextFile(getLoggedInUserDataPath(), ACCOUNT_PLAYLIST_DATA_TXT);//This creates a text file
        createTextFile(getLoggedInUserDataPath(), ACCOUNT_SETTING_DATA_TXT);//This creates a text file
        createFolder(getLoggedInUserDataPath(), ACCOUNT_AUDIO_FOLDER);//This creates a folder
        createFolder(getLoggedInUserDataPath(), ACCOUNT_THUMBNAIL_FOLDER);//This creates a text file
    }

    public static void createFolder(Path whereToCreateFolder, String folderName) throws IOException {
        Path folderOrTextPath = null;
        folderOrTextPath = Paths.get(whereToCreateFolder.toString(), folderName);
        if (!Files.exists(folderOrTextPath)) {
            new File(folderOrTextPath.toString()).mkdir();
        } else {
            System.out.println("Directory " + folderOrTextPath.toString() + " already exists");
        }
    }

    public static void setLoggedInUserDataPath(String username) {//Sets up the path to the account folder of the user logged in
        loggedInUserDataPath = Paths.get(ACCOUNTS_DATA_PATH.toString(), username);
    }

    public static void setUpPathsInsideUserDataPath() {//To be used after all the folders and text files are created. It just sets up all the paths to the folders and txt files inside the accounts folder
        loggedInUserSettingsPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_SETTING_DATA_TXT + ".txt");
        loggedInUserPlaylistsPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_PLAYLIST_DATA_TXT + ".txt");
        loggedInUserDownloadedMusicDataPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_MUSIC_DATA_TXT + ".txt");
        loggedInUserThumbnailsPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_THUMBNAIL_FOLDER);
        loggedInUserDownloadedMusicPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_AUDIO_FOLDER);
    }

    public static Path getLoggedInUserMusicFolderPath() {
        return loggedInUserDownloadedMusicPath;
    }

    public static Path getLoggedInUserDataPath() {
        return loggedInUserDataPath;
    }

    public static Path getLoggedInUserSettingsPath() {
        return loggedInUserSettingsPath;
    }

    public static Path getloggedInUserPlaylistsPath() {
        return loggedInUserPlaylistsPath;
    }

    public static Path getLoggedInUserDownloadedMusicDataPath() {
        return loggedInUserDownloadedMusicDataPath;
    }

    public static void createTextFile(Path whereToCreateTextFile, String textName) throws IOException {
        Path folderOrTextPath = null;
        folderOrTextPath = Paths.get(whereToCreateTextFile.toString(), textName + ".txt");
        System.out.println("folder text path: " + folderOrTextPath);
        if (!Files.exists(folderOrTextPath)) {
            new FileWriter(folderOrTextPath.toString());
        } else {
            System.out.println("Directory " + folderOrTextPath.toString() + " already exists");
        }
    }
}
