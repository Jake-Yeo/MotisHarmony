/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
    private static Accounts loggedInAccount;
    private ArrayList<SongDataObject> songDataObjectList = new ArrayList<>();
    private String username;
    private String password;
    private SecretKey key;
    transient private EncryptionDecryption aes = new EncryptionDecryption();

    Accounts(String username, String password) {
        this.username = username;
        try {
            this.password = aes.encrypt(password);
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

    public static void signup(String username, String password) throws IOException, Exception {//This will be used to create an account//Returns true if the signup is successful
        AccountsDataManager accDataMan = new AccountsDataManager();
        if (!accDataMan.accListContainWantedName(username)) {
            loggedInAccount = new Accounts(username, password);
            accDataMan.addAccNameToList(username);//This will add the username to the list so that accounts with the same usernames cannot be created.
            accDataMan.serializeAccMan();//This will save the contents of the ArrayList
            PathsManager.setUpAccountFoldersAndTxtFiles(username);
            PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above
            System.out.println(loggedInAccount.getKey());
            loggedInAccount.serializeAccount();
            sceneSwitcher.switchToDownloadPageView();
            System.out.println(PathsManager.getLoggedInUserDataPath().toString());
        }
    }

    public static void login(String username, String password) throws IOException, Exception {//This will be used to login to an account//Returns true if login is successful
        AccountsDataManager accDataMan = new AccountsDataManager();
        if (!accDataMan.accListContainWantedName(username)) {
            return;
        }
        PathsManager.setLoggedInUserDataPath(username);//We need to set up this path first to access the contents of the account the user is trying to log into.
        Accounts accToLoginTo = deserializeAccount(username);
        EncryptionDecryption aes = new EncryptionDecryption(accToLoginTo.getKey());
        if (password.equals(aes.decrypt(accToLoginTo.getPassword()))) {
            PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
            sceneSwitcher.switchToDownloadPageView();
        }
    }

    public static Accounts getLoggedInAccount() {
        return loggedInAccount;
    }
}
