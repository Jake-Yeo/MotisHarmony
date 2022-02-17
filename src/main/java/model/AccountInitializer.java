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
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jake Yeo
 */
public class AccountInitializer {//This class will set up the logged in account

    private SceneChanger sceneSwitcher = new SceneChanger();
    private static Account loggedInAccount;

    /**
     * @param loginOrSignup true means signup, false means login text file
     */
    public AccountInitializer() {

    }

    public void signup(String username, String password) throws IOException, Exception {//This will be used to create an account//Returns true if the signup is successful

        loggedInAccount = new Account(username, password);
        PathsManager.setUpAccountFoldersAndTxtFiles(username);
        PathsManager.setUpPathsInsideUserDataPath();//Basically we just set up paths for the folders and text files made above
        loggedInAccount.serializeAccount();
        sceneSwitcher.switchToDownloadPageView();
        System.out.println(PathsManager.getLoggedInUserDataPath().toString());
    }

    public void login(String username, String password) throws IOException {//This will be used to login to an account//Returns true if login is successful
        PathsManager.setLoggedInUserDataPath(username);
        PathsManager.setUpPathsInsideUserDataPath();//We must run this method after using the setLoggedInUserDataPath so that we actually set up the correct paths
        sceneSwitcher.switchToDownloadPageView();
    }
    
    public static Account getLoggedInAccount() {
        return loggedInAccount;
    }

}
