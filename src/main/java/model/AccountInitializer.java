/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import view.SceneChanger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jake Yeo
 */
public class AccountInitializer {

    private String username;
    private String password;
    private SceneChanger sceneSwitcher = new SceneChanger();
    private final String seperator = ", ";
    private EncryptionDecryption aes = new EncryptionDecryption();

    /**
     * @param loginOrSignup true means signup, false means login text file
     */
    public AccountInitializer() {
        try {
        //Will login or create an account, will create the files necessary for the AccountDataManager to mange the account. If you login, all the account setup will happen in this class.
        aes.init();
        } catch (Exception ex) {
            Logger.getLogger(AccountInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void signup(String username, String password) throws IOException, Exception {//This will be used to create an account//Returns true if the signup is successful
        this.username = username;
        this.password = aes.encrypt(password);
        PathsManager.setUpAccountFoldersAndTxtFiles(this.username);//This will create a folder with the user's username and create all the folders and txt files withing that folder which are nessesary for the program to work
        PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above

        FileWriter fw = new FileWriter(PathsManager.ACCOUNT_USER_PASS_DATA_PATH.toString(), true);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(this.toString());
        fw.close();
        pw.close();
        sceneSwitcher.switchToDownloadPageView();
        System.out.println(PathsManager.getLoggedInUserDataPath().toString());
    }

    public void login(String username, String password) throws IOException {//This will be used to login to an account//Returns true if login is successful
        this.username = username;
        this.password = password;
        PathsManager.setLoggedInUserDataPath(this.username);//Basically we set up the path that leads towards the correct account folder
        PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
        sceneSwitcher.switchToDownloadPageView();
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

    @Override
    public String toString() {
        return this.username + seperator + this.password + seperator + aes.getSecretKey();
    }
}
