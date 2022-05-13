/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.LoginPageViewController;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javax.crypto.SecretKey;
import view.MainViewRunner;
import view.SceneChanger;

/**
 *
 * @author Jake Yeo
 */
public class Accounts implements Serializable {//This class will store account username and their encrypted password, plus song data

    private static final long serialVersionUID = 4655882630581250278L;
    private static SceneChanger sceneSwitcher = new SceneChanger();
    private static Accounts loggedInAccount;
    private LinkedList<SongDataObject> songDataObjectList = new LinkedList<>();
    private LinkedList<SongDataObject> songsInQueueList = new LinkedList<>();
    private LinkedList<UrlDataObject> urlDataObjectList = new LinkedList<>();
    private String username;
    private String password;
    private SecretKey key;
    private SettingsObject settingsObject = new SettingsObject();
    private PlaylistMap playlistDataObject = new PlaylistMap();
    transient private Encryption aes = new Encryption();

    Accounts(String username, String password) {
        this.username = username;
        try {
            this.password = aes.sha256Hash(aes.encrypt(password));
        } catch (Exception ex) {
            Logger.getLogger(Accounts.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.key = aes.getSecretKey();
    }

    public LinkedList<SongDataObject> getSongsInQueueList() {
        return songsInQueueList;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SettingsObject getSettingsObject() {
        return settingsObject;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public SecretKey getKey() {
        return this.key;
    }

    public LinkedList<String> getListOfSongPaths() {
        LinkedList<String> listOfSongsToReturn = new LinkedList<>();
        for (int i = 0; i < songDataObjectList.size(); i++) {
            listOfSongsToReturn.add(songDataObjectList.get(i).getPathToWavFile());
        }
        System.out.println("This ran " + listOfSongsToReturn.size());
        return listOfSongsToReturn;
    }

    public LinkedList<String> getListOfSongVideoIds() {
        LinkedList<String> listOfSongsToReturn = new LinkedList<>();
        for (SongDataObject sdo : songDataObjectList) {
            listOfSongsToReturn.add(sdo.getVideoID());
        }
        return listOfSongsToReturn;
    }

    public LinkedList<SongDataObject> getListOfSongDataObjects() {
        return this.songDataObjectList;
    }

    public LinkedList<UrlDataObject> getListOfUrlDataObjects() {
        return this.urlDataObjectList;
    }

    public void addSongDataObjectToAccount(SongDataObject songDataObject) {
        this.songDataObjectList.add(songDataObject);
    }

    public void removeSongFromAccount(SongDataObject songDataObject) {
        this.songDataObjectList.remove(songDataObject);
    }

    public PlaylistMap getPlaylistDataObject() {
        return this.playlistDataObject;
    }

    public void serializeAccount() throws Exception {
        System.out.println(this.username);
        FileOutputStream fileOut = new FileOutputStream(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), this.username + ".acc").toString());
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public static Accounts deserializeAccount(String username) throws Exception {
        FileInputStream fileIn = new FileInputStream(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), username + ".acc").toString());
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Accounts accountToReturn = (Accounts) in.readObject();
        in.close();
        fileIn.close();
        return accountToReturn;
    }

    public static Accounts deserializeAccountFromPath(String pathToAccount) throws Exception {
        FileInputStream fileIn = new FileInputStream(pathToAccount);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Accounts accountToReturn = (Accounts) in.readObject();
        in.close();
        fileIn.close();
        return accountToReturn;
    }

    public static void setLoggedInAccount(Accounts acc) {
        loggedInAccount = acc;
    }

    public static ErrorDataObject signup(String username, String password) throws IOException, Exception {//This will be used to create an account. Returns true if the signup is successful
        AccountsDataManager accDataMan = new AccountsDataManager();
        if (username.trim().equals("") || password.trim().equals("")) {//checks if the user put just whitespace as their username or password.
            return new ErrorDataObject(true, "Username or password cannot remain empty");
        }
        if (username.contains("\\") || username.contains("/")) {//Makes sure the user does not use / or \ in their username
            return new ErrorDataObject(true, "\"\\\" and \"/\" are not allowed in the username");
        }
        if (!accDataMan.accListContainWantedName(username)) {//Makes sure that the user cannot use someone elses username to signup.
            loggedInAccount = new Accounts(username, password);//Sets the logged in account
            try {
                PathsManager.setUpAccountFoldersAndTxtFiles(username);
                PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above
                loggedInAccount.playlistDataObject.createPlaylist("All Songs");
                loggedInAccount.serializeAccount();//If the username contains "/" or "\\" the serialization will fail, so we put it in a try catch loop.
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorDataObject(true, "Username is not available");
            }
            accDataMan.addAccNameToList(username);//This will add the username to the list so that accounts with the same usernames cannot be created.
            accDataMan.setPathOfAccToAutoLogIn(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), loggedInAccount.getUsername() + ".acc").toString());
            //Since these classes need an account to be created in order to initialize, we must set up the accounts first before adding these
            MainViewRunner.getSceneChanger().addScreen("DownloadPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/DownloadPageView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("BrowserPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/BrowserPageView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("MusicPlayerPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/MusicPlayerView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("SettingsPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/SettingsPageView.fxml")));
            sceneSwitcher.switchToDownloadPageView();//The Account signup was successful and we can now let the user use the application
            System.out.println(PathsManager.getLoggedInUserDataPath().toString());
        } else {
            return new ErrorDataObject(true, "Username is not available");
        }
        return new ErrorDataObject(false, "");
    }

    public static ErrorDataObject login(String username, String password) throws IOException, Exception {//This will be used to login to an account. Returns true if login is successful
        AccountsDataManager accDataMan = new AccountsDataManager();//This object contains the names of all the accoutns created
        if (!accDataMan.accListContainWantedName(username)) {//Makes sure the account the user is trying to log into exists
            return new ErrorDataObject(true, "Account does not exist or password is wrong");
        }
        Encryption aes = null;
        Accounts accToLoginTo = null;
        try {
            PathsManager.setLoggedInUserDataPath(username);//We need to set up this path first to access the contents of the account the user is trying to log into.
            accToLoginTo = deserializeAccount(username);//If the username entered contains "/" or "\\" then deserialization will fail, so we put it in a try catch loop.
            aes = new Encryption(accToLoginTo.getKey());
        } catch (Exception e) {
            System.err.println("error deserializing account");
            e.printStackTrace();
            return new ErrorDataObject(true, "Account does not exist or password is wrong");
        }
        if (aes.sha256Hash(aes.encrypt(password)).equals(accToLoginTo.getPassword())) {//Hashes the encrypted password entered to check if it equals the hash stored in the acccount object
            PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
            loggedInAccount = accToLoginTo;//Set the logged in account
            accDataMan.setPathOfAccToAutoLogIn(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), loggedInAccount.getUsername() + ".acc").toString());
            //Since these classes need an account to be created in order to initialize, we must set up the accounts first before adding these
            MainViewRunner.getSceneChanger().addScreen("DownloadPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/DownloadPageView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("BrowserPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/BrowserPageView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("MusicPlayerPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/MusicPlayerView.fxml")));
            MainViewRunner.getSceneChanger().addScreen("SettingsPage", FXMLLoader.load(MainViewRunner.class.getResource("/fxml/SettingsPageView.fxml")));
            sceneSwitcher.switchToDownloadPageView();//The login was successful and we can now let the user use the application
        } else {
            System.err.println("password is wrong");
            return new ErrorDataObject(true, "Account does not exist or password is wrong");
        }
        return new ErrorDataObject(false, "");
    }

    public static Accounts getLoggedInAccount() {//This will return the current logged in account to allow easy manipulation of its data
        return loggedInAccount;
    }
}
