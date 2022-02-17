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
import java.util.logging.Level;
import java.util.logging.Logger;
import view.SceneChanger;

/**
 *
 * @author Jake Yeo
 */
public class Account implements Serializable {

    private String username;
    private String password;
    private String key;
    private final String seperator = ", ";
    transient private EncryptionDecryption aes = new EncryptionDecryption();

    Account(String username, String password) {
        this.username = username;
        try {
            aes.init();
            this.password = aes.encrypt(password);
        } catch (Exception ex) {
            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
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

    public String getKey() {
        return this.key;
    }

    public void serializeAccount() {
        try {
            FileOutputStream fileOut = new FileOutputStream(Paths.get(PathsManager.ACCOUNTS_DATA_PATH.toString(), this.username + ".acc").toString());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static Account deserializeAccount(String username) {
        Account accountToReturn = null;
        try {
            FileInputStream fileIn = new FileInputStream(Paths.get(PathsManager.ACCOUNTS_DATA_PATH.toString(), username + ".acc").toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            accountToReturn = (Account) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountToReturn;
    }
}
