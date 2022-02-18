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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import view.SceneChanger;

/**
 *
 * @author Jake Yeo
 */
public class Accounts implements Serializable {//This class will store account username and their encrypted password, plus song data

    private static SceneChanger sceneSwitcher = new SceneChanger();
    private static LoginPageViewController loginPageViewController = new LoginPageViewController();
    private static Accounts loggedInAccount;
    private ArrayList<SongDataObject> songDataObjectList = new ArrayList<>();
    private String username;
    private String password;
    private SecretKey key;
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

    public void setUsername(String username) {
        this.username = username;
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

    public void addSongDataObjectToAccount(SongDataObject songDataObject) {
        this.songDataObjectList.add(songDataObject);
    }

    public void removeSongFromAccount(SongDataObject songDataObject) {
        this.songDataObjectList.remove(songDataObject);
    }

    public ArrayList<String> getListOfSongPaths() {
        ArrayList<String> listOfSongsToReturn = new ArrayList<>();
        for (int i = 0; i < songDataObjectList.size(); i++) {
            listOfSongsToReturn.add(songDataObjectList.get(i).getPathToWavFile());
        }
        System.out.println("This ran " + listOfSongsToReturn.size());
        return listOfSongsToReturn;
    }

    public ArrayList<String> getListOfSongUrls() {
        ArrayList<String> listOfSongsToReturn = new ArrayList<>();
        for (int i = 0; i < songDataObjectList.size(); i++) {
            listOfSongsToReturn.add(songDataObjectList.get(i).getVideoUrl());
        }
        System.out.println("This ran " + listOfSongsToReturn.size());
        return listOfSongsToReturn;
    }

    public void serializeAccount() {
        try {
            FileOutputStream fileOut = new FileOutputStream(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), this.username + ".acc").toString());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static Accounts deserializeAccount(String username) {
        Accounts accountToReturn = null;
        try {
            FileInputStream fileIn = new FileInputStream(Paths.get(PathsManager.getLoggedInUserDataPath().toString(), username + ".acc").toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            accountToReturn = (Accounts) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountToReturn;
    }

    public static ErrorDataObject signup(String username, String password) throws IOException, Exception {//This will be used to create an account//Returns true if the signup is successful
        AccountsDataManager accDataMan = new AccountsDataManager();
        if (!accDataMan.accListContainWantedName(username)) {
            loggedInAccount = new Accounts(username, password);
            try {
                PathsManager.setUpAccountFoldersAndTxtFiles(username);
                PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above
            } catch (Exception e) {
                return new ErrorDataObject(true, "Username is not available");
            }
            accDataMan.addAccNameToList(username);//This will add the username to the list so that accounts with the same usernames cannot be created.
            accDataMan.serializeAccMan();//This will save the contents of the ArrayList
            System.out.println(loggedInAccount.getKey());
            loggedInAccount.serializeAccount();
            sceneSwitcher.switchToDownloadPageView();
            System.out.println(PathsManager.getLoggedInUserDataPath().toString());
        } else {
            return new ErrorDataObject(true, "Username is not available");
        }
        return new ErrorDataObject(false, "");
    }

    public static ErrorDataObject login(String username, String password) throws IOException, Exception {//This will be used to login to an account//Returns true if login is successful
        AccountsDataManager accDataMan = new AccountsDataManager();
        if (!accDataMan.accListContainWantedName(username)) {
            return new ErrorDataObject(true, "Account does not exist or password is wrong");
        }
        PathsManager.setLoggedInUserDataPath(username);//We need to set up this path first to access the contents of the account the user is trying to log into.
        Accounts accToLoginTo = deserializeAccount(username);
        Encryption aes = new Encryption(accToLoginTo.getKey());
        if (aes.sha256Hash(aes.encrypt(password)).equals(accToLoginTo.getPassword())) {
            PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
            loggedInAccount = accToLoginTo;//Set the logged in account
            sceneSwitcher.switchToDownloadPageView();
        } else {
            return new ErrorDataObject(true, "Account does not exist or password is wrong");
        }
        return new ErrorDataObject(false, "");
    }

    public static Accounts getLoggedInAccount() {
        return loggedInAccount;
    }
}
