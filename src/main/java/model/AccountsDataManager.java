/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    AccountsDataManager() {
        try {
            this.listOfAccountNames = deserializeAccMan().listOfAccountNames;
        } catch (Exception e) {
            this.listOfAccountNames = new ArrayList<>();
            try {
                serializeAccMan();
            } catch (Exception ex) {
                Logger.getLogger(AccountsDataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addAccNameToList(String accName) {
        this.listOfAccountNames.add(accName);
    }

    public void removeAccNameToList(String accName) {
        this.listOfAccountNames.remove(accName);
    }

    public boolean accListContainWantedName(String accName) {
        if (this.listOfAccountNames.contains(accName)) {
            return true;
        } else {
            return false;
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
            Files.delete(Paths.get(sdoToRemove[j].getPathToWavFile()));
            Files.delete(Paths.get(sdoToRemove[j].getPathToThumbnail()));
        }
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

    public static void removeSongFromPlaylist(String playlistName, SongDataObject[] sdoArray) throws Exception {
        if (!playlistName.equals("All Songs")) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().removeSongFromPlaylist(playlistName, sdoArray);
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void urlDataObjectToAddToAccount(SongDataObject urlDataObject) throws Exception {
        if (Accounts.getLoggedInAccount().getListOfSongDataObjects().size() == 0) {
            urlDataObject.setOrderAdded(0);
        } else {
            urlDataObject.setOrderAdded(Accounts.getLoggedInAccount().getListOfSongDataObjects().get(Accounts.getLoggedInAccount().getListOfSongDataObjects().size() - 1).getOrderAdded() + 1);
        }
        Accounts.getLoggedInAccount().addSongDataObjectToAccount(urlDataObject);
        Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist("All Songs", urlDataObject);
        Accounts.getLoggedInAccount().serializeAccount();
        saveThumbnail(urlDataObject.getThumbnailUrl(), urlDataObject.getPathToThumbnail());
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
