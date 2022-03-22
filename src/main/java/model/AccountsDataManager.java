/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.beust.jcommander.internal.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Jake Yeo
 */
public class AccountsDataManager implements Serializable {//This class will be used to manage all data changes made to a logged in account. If they change, add, remove a song or playlist, all of that will happen in this class    

    private static final long serialVersionUID = 4655882630581250278L;
    private ArrayList<String> listOfAccountNames;
    private String pathToAccToAutoLogIn;
    //We need the deletion queue below because the .dispose() method on the mediaPlayer has some asynchronous-ness to it so we simply delete the files when we start up the app.
    private ArrayList<SongDataObject> deletionQueue;

    public AccountsDataManager() {
        try {
            AccountsDataManager adm = deserializeAccMan();
            this.listOfAccountNames = adm.listOfAccountNames;
            this.deletionQueue = adm.deletionQueue;
            this.pathToAccToAutoLogIn = adm.getAccPathToAutoLogIn();
        } catch (Exception e) {
            this.listOfAccountNames = new ArrayList<>();
            this.deletionQueue = new ArrayList<>();
            try {
                serializeAccMan();
            } catch (Exception ex) {
                Logger.getLogger(AccountsDataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addSongDataObjectToDeletionQueue(SongDataObject sdo) throws Exception {
        this.deletionQueue.add(sdo);
        serializeAccMan();
    }

    public void setPathOfAccToAutoLogIn(String pathToAcc) throws Exception {
        pathToAccToAutoLogIn = pathToAcc;
        serializeAccMan();
    }

    public String getAccPathToAutoLogIn() {
        return this.pathToAccToAutoLogIn;
    }

    public ArrayList<SongDataObject> getDeletionQueue() {
        return this.deletionQueue;
    }

    public void addAccNameToList(String accName) throws Exception {
        this.listOfAccountNames.add(accName);
        serializeAccMan();
    }

    public void removeAccNameToList(String accName) throws Exception {
        this.listOfAccountNames.remove(accName);
        serializeAccMan();
    }

    public boolean accListContainWantedName(String accName) {
        if (this.listOfAccountNames.contains(accName)) {
            return true;
        } else {
            return false;
        }
    }

    public static void updateVolumeSettings() throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setPrefVolume(MusicPlayerManager.getSliderVolume());
            Accounts.getLoggedInAccount().serializeAccount();
            System.out.println("Setting saved");
        }
    }

    public static void setSaveDownloadQueue(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setSaveDownloadQueue(tf);
        Accounts.getLoggedInAccount().serializeAccount();
    }

    public static void updateSongsInQueueList(ObservableList<SongDataObject> songsToUpdateWith) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            Accounts.getLoggedInAccount().getSongsInQueueList().clear();
            Accounts.getLoggedInAccount().getSongsInQueueList().addAll(songsToUpdateWith);
            System.out.println("Current download queue saved!");
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void updateCurrentPlaylistListSortType(String sortType) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setPlaylistListSortPreference(sortType);
            Accounts.getLoggedInAccount().serializeAccount();
            System.out.println("Setting saved");
        }
    }

    public static void updateCurrentSongListSortType(String sortType) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setSongListSortPreference(sortType);
            Accounts.getLoggedInAccount().serializeAccount();
            System.out.println("Setting saved");
        }
    }

    public static void createPlaylist(String playlistName) throws Exception {
        //If statement makes sure that the user does not accidentally delete a playlist by creating two playlists with the same name
        if (!playlistName.equals("All Songs") && !Arrays.asList(Accounts.getLoggedInAccount().getPlaylistDataObject().getArrayOfPlaylistNames()).contains(playlistName)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().createPlaylist(playlistName);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void deleteSong(SongDataObject[] sdoToRemove) throws IOException, Exception {
        String[] arrayOfPlaylistSongs = Accounts.getLoggedInAccount().getPlaylistDataObject().getArrayOfPlaylistNames();

        for (int j = 0; j < sdoToRemove.length; j++) {
            for (int i = 0; i < arrayOfPlaylistSongs.length; i++) {
                Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(arrayOfPlaylistSongs[i]).remove(sdoToRemove[j]);
            }
            Accounts.getLoggedInAccount().removeSongFromAccount(sdoToRemove[j]);
        }
        AccountsDataManager adm = deserializeAccMan();
        adm.deletionQueue.addAll(Arrays.asList(sdoToRemove));
        adm.serializeAccMan();
        Accounts.getLoggedInAccount().serializeAccount();
    }

    public static void addSongToPlaylist(String playlistName, SongDataObject sdo) throws Exception {
        //If the song is already present in the playlist then remove it
        if (!playlistName.equals("All Songs") && !Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName).contains(sdo)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist(playlistName, sdo);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void addSongToPlaylist(String playlistName, ArrayList<SongDataObject> listOfSongs) throws Exception {
        ArrayList<SongDataObject> songsInPlaylist = Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName);
        ArrayList<SongDataObject> songsToRemoveFromList = new ArrayList<>();
        //This for loop will remove all the songs in the listOfSongs which are already present in the playlist
        for (int i = 0; i < listOfSongs.size(); i++) {
            if (songsInPlaylist.contains(listOfSongs.get(i))) {
                songsToRemoveFromList.add(listOfSongs.get(i));
            }
        }
        listOfSongs.removeAll(songsToRemoveFromList);
        if (!playlistName.equals("All Songs")) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist(playlistName, listOfSongs);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void deletePlaylist(String playlistName) throws Exception {
        //All Songs is a unique playlist which musn't be editied
        if (!playlistName.equals("All Songs")) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().deletePlaylist(playlistName);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void renamePlaylist(String playlistToRename, String newPlaylistName) throws Exception {
        if (!playlistToRename.equals("All Songs") && !playlistToRename.equals(newPlaylistName)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().renamePlaylist(playlistToRename, newPlaylistName);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void removeSongFromPlaylist(String playlistName, SongDataObject[] sdoArray) throws Exception {
        if (!playlistName.equals("All Songs")) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().removeSongFromPlaylist(playlistName, sdoArray);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void songDataObjectToAddToAccount(SongDataObject songDataObject) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            if (Accounts.getLoggedInAccount().getListOfSongDataObjects().size() == 0) {
                songDataObject.setOrderAdded(0);
            } else {
                songDataObject.setOrderAdded(Accounts.getLoggedInAccount().getListOfSongDataObjects().get(Accounts.getLoggedInAccount().getListOfSongDataObjects().size() - 1).getOrderAdded() + 1);
            }
            Accounts.getLoggedInAccount().addSongDataObjectToAccount(songDataObject);
            Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist("All Songs", songDataObject);
            Accounts.getLoggedInAccount().serializeAccount();
            saveThumbnail(songDataObject.getThumbnailUrl(), songDataObject.getPathToThumbnail());
        }
    }

    private static void saveThumbnail(String thumbnailUrl, String pathToDownloadTo) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(new Image(thumbnailUrl), null), "png", new File(pathToDownloadTo));
    }

    public void serializeAccMan() throws Exception {
        FileOutputStream fileOut = new FileOutputStream(PathsManager.LIST_OF_ACCOUNT_NAMES_PATH.toString());
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public static AccountsDataManager deserializeAccMan() throws Exception {
        AccountsDataManager accManToReturn = null;
        FileInputStream fileIn = new FileInputStream(PathsManager.LIST_OF_ACCOUNT_NAMES_PATH.toString());
        ObjectInputStream in = new ObjectInputStream(fileIn);
        accManToReturn = (AccountsDataManager) in.readObject();
        in.close();
        fileIn.close();
        return accManToReturn;
    }
}
