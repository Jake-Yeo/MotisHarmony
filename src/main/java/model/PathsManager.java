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
import java.util.ArrayList;

/**
 *
 * @author 1100007967
 */
public class PathsManager {//This class will handle all the folder and txt creations

    private static final String APP_DATA_FOLDER_NAME = "MotisHarmony";//Folder name for where all the MotisHarmony data will be stored
    private static final String ACCOUNTS_DATA_FOLDER_NAME = "accounts";//Folder name for where all created accounts will be stored
    private static final String ACCOUNT_THUMBNAIL_FOLDER = "downloadedMusicThumbnails";//Folder name for where all downloaded thumbnails will be stored
    private static final String ACCOUNT_AUDIO_FOLDER = "downloadedMusic";//Folder name for where all files converted to wav will be stored
    private static final String ACCOUNT_WEBA_FOLDER = "downloadedWeba";//Folder name for where are the weba files will be downloaded to
    public static final Path APP_DATA_FOLDER_PATH = Paths.get(System.getProperty("user.home"), APP_DATA_FOLDER_NAME);//Path to the APP_DATA_FOLDER_NAME
    public static final Path ACCOUNTS_DATA_PATH = Paths.get(APP_DATA_FOLDER_PATH.toString(), ACCOUNTS_DATA_FOLDER_NAME);//Path to the DOWNLOADS_FOLDER_NAME
    public static final Path LIST_OF_ACCOUNT_NAMES_PATH = Paths.get(PathsManager.APP_DATA_FOLDER_PATH.toString(), "AccNameList.ser");
    private static Path loggedInUserDataPath = null;//The below paths are just the paths which will lead to the folders withing the accounts folder
    private static Path loggedInUserThumbnailsPath = null;
    private static Path loggedInUserDownloadedMusicPath = null;
    private static Path loggedInUserDownloadedWebaPath = null;

    public static void setUpFolders() throws Exception {
        createFolder(Paths.get(System.getProperty("user.home")), APP_DATA_FOLDER_NAME);
        createFolder(APP_DATA_FOLDER_PATH, ACCOUNTS_DATA_FOLDER_NAME);
    }

    public static void setUpAccountFoldersAndTxtFiles(String username) throws Exception {
        createFolder(PathsManager.ACCOUNTS_DATA_PATH, username);//creates a folder in the Accounts folder that is named after the users name
        setLoggedInUserDataPath(username);//This will set up the path to the data of the account currently logged in
        createFolder(getLoggedInUserDataPath(), ACCOUNT_AUDIO_FOLDER);//This creates the audio folder
        createFolder(getLoggedInUserDataPath(), ACCOUNT_THUMBNAIL_FOLDER);//This creates a thumbnail folder
        createFolder(getLoggedInUserDataPath(), ACCOUNT_WEBA_FOLDER);//This creates the weba folder
    }

    public static void createFolder(Path whereToCreateFolder, String folderName) throws Exception {
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
        loggedInUserThumbnailsPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_THUMBNAIL_FOLDER);
        loggedInUserDownloadedMusicPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_AUDIO_FOLDER);
        loggedInUserDownloadedWebaPath = Paths.get(loggedInUserDataPath.toString(), ACCOUNT_WEBA_FOLDER);
    }

    public static void deleteAllItemsInDownloadQueue() throws Exception {
        AccountsDataManager adm = AccountsDataManager.deserializeAccMan();
        ArrayList<SongDataObject> sdoDataToDeleteFromFiles = adm.getDeletionQueue();
        for (int i = 0; i < sdoDataToDeleteFromFiles.size(); i++) {
            try {
                System.out.println("deleteing " + sdoDataToDeleteFromFiles.get(i).getPathToWavFile());
                Files.delete(Paths.get(sdoDataToDeleteFromFiles.get(i).getPathToWavFile()));
                System.out.println("deleteing " + sdoDataToDeleteFromFiles.get(i).getPathToThumbnail());
                Files.delete(Paths.get(sdoDataToDeleteFromFiles.get(i).getPathToThumbnail()));
            } catch (Exception e) {
                System.err.println("File does not exist");
            }
        }
        adm.getDeletionQueue().clear();
        adm.serializeAccMan();
    }

    public static Path getLoggedInUserMusicFolderPath() {
        return loggedInUserDownloadedMusicPath;
    }

    public static Path getLoggedInUserWebaFolderPath() {
        return loggedInUserDownloadedWebaPath;
    }

    public static Path getLoggedInUserDataPath() {
        return loggedInUserDataPath;
    }

    public static Path getLoggedInUserThumbnailsPath() {
        return loggedInUserThumbnailsPath;
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
