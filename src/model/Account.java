/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;

/**
 *
 * @author Jake Yeo
 */
public class Account {

    private String username;
    private String password;
    private static Account loggedInAccount = null;

    /**
     * @param loginOrSignup true means signup, false means login text file
     */
    private Account(String username, String password, boolean loginOrSignup) throws IOException {//Will login or create an account, will create the files necessary for the AccountDataManager to mange the account. If you login, all the account setup will happen in this class.
        if (loginOrSignup) {
            signup(username, password);
        } else {
            login(username, password);
        }
    }

    public static void setLoggedInAccount(String username, String password, boolean loginOrSignup) throws IOException {//We only need one account object since only one account can be logged in at a time.
        Account.loggedInAccount = new Account(username, password, loginOrSignup);
    }

    private void signup(String username, String password) throws IOException {//This will be used to create an account//Returns true if the signup is successful
        this.username = username;
        this.password = password;
        PathsManager.createTextOrFolder(PathsManager.ACCOUNTS_DATA_PATH, this.username, true);//creates a folder in the Accounts folder that is named after the users name
        PathsManager.setLoggedInUserDataPath(this.username);//This will set up the path to the data of the account currently logged in
        PathsManager.createTextOrFolder(PathsManager.getLoggedInUserDataPath(), PathsManager.ACCOUNT_MUSIC_DATA_TXT, false);//This creates a text file//This is setting up all the text files which must be created for the AccountDataManager to work
        PathsManager.createTextOrFolder(PathsManager.getLoggedInUserDataPath(), PathsManager.ACCOUNT_PLAYLIST_DATA_TXT, false);//This creates a text file
        PathsManager.createTextOrFolder(PathsManager.getLoggedInUserDataPath(), PathsManager.ACCOUNT_SETTING_DATA_TXT, false);//This creates a text file
        PathsManager.createTextOrFolder(PathsManager.getLoggedInUserDataPath(), PathsManager.ACCOUNT_AUDIO_FOLDER, true);//This creates a folder
        PathsManager.createTextOrFolder(PathsManager.getLoggedInUserDataPath(), PathsManager.ACCOUNT_THUMBNAIL_FOLDER, true);//This creates a text file
        PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above
        System.out.println(PathsManager.getLoggedInUserDataPath().toString());
    }

    private void login(String username, String password) {//This will be used to login to an account//Returns true if login is successful
        this.username = username;
        this.password = password;
        PathsManager.setLoggedInUserDataPath(this.username);//Basically we set up the path that leads towards the correct account folder
        PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
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
}
