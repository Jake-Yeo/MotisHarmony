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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
    private LinkedList<String> listOfAccountNames;
    private String pathToAccToAutoLogIn;
    //We need the deletion queue below because the .dispose() method on the mediaPlayer has some asynchronous-ness to it so we simply delete the files when we start up the app.
    private LinkedList<SongDataObject> deletionQueue;

    public AccountsDataManager() {
        try {
            AccountsDataManager adm = deserializeAccMan();
            this.listOfAccountNames = adm.listOfAccountNames;
            this.deletionQueue = adm.deletionQueue;
            this.pathToAccToAutoLogIn = adm.getAccPathToAutoLogIn();
        } catch (Exception e) {
            this.listOfAccountNames = new LinkedList<>();
            this.deletionQueue = new LinkedList<>();
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

    public LinkedList<SongDataObject> getDeletionQueue() {
        return this.deletionQueue;
    }

    public void addAccNameToList(String accName) throws Exception {
        this.listOfAccountNames.add(accName);
        serializeAccMan();
    }

    public void removeAccNameFromList(String accName) throws Exception {
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

    public static void deleteCurrentAccount() throws Exception {
        SongDataObject[] sdosToDelete = new SongDataObject[Accounts.getLoggedInAccount().getListOfSongDataObjects().size()];
        for (int i = 0; i < sdosToDelete.length; i++) {
            sdosToDelete[i] = Accounts.getLoggedInAccount().getListOfSongDataObjects().get(i);
        }
        AccountsDataManager.deleteSong(sdosToDelete);
        AccountsDataManager adm = deserializeAccMan();
        adm.pathToAccToAutoLogIn = null;
        adm.removeAccNameFromList(Accounts.getLoggedInAccount().getUsername());
    }

    public static void saveAllSettings() throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            try {
                AccountsDataManager.updateSongsInQueueList(YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList());
            } catch (Exception ex) {
                Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                AccountsDataManager.setLastPlaylistPlayed(MusicPlayerManager.getMpmCurrentlyUsing().getCurrentPlaylistPlayling());
                AccountsDataManager.setLastSongPlayed(MusicPlayerManager.getMpmCurrentlyUsing().getSongObjectBeingPlayed());
            } catch (Exception ex) {
                Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                AccountsDataManager.setPlaySongInLoop(MusicPlayerManager.getMpmCurrentlyUsing().getPlaySongInLoop());
                AccountsDataManager.setPlayType(MusicPlayerManager.getMpmCurrentlyUsing().getPlayType());
            } catch (Exception ex) {
                Logger.getLogger(YoutubeDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            AccountsDataManager adm = deserializeAccMan();
            if (Accounts.getLoggedInAccount() != null && !Accounts.getLoggedInAccount().getSettingsObject().getStayLoggedIn()) {
                adm.pathToAccToAutoLogIn = null;
            }
            adm.serializeAccMan();

            AccountsDataManager.updateVolumeSettings();
            Accounts.getLoggedInAccount().serializeAccount();
        }
    }

    public static void updateVolumeSettings() throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setPrefVolume(MusicPlayerManager.getMpmCurrentlyUsing().getSliderVolume());
            System.out.println("Setting saved");
        }
    }

    public static void setSaveDownloadQueue(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setSaveDownloadQueue(tf);
    }

    public static void setUpdateDisplaySongOnClick(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setDisplaySongOnClick(tf);
    }

    public static void setStayLoggedIn(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setStayLoggedIn(tf);
    }

    public static void updateSongsInQueueList(ObservableList<SongDataObject> songsToUpdateWith) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            Accounts.getLoggedInAccount().getSongsInQueueList().clear();
            Accounts.getLoggedInAccount().getSongsInQueueList().addAll(songsToUpdateWith);
            System.out.println("Current download queue saved!");
        }
    }

    public static void setSaveSongPosition(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setSaveSongPosition(tf);
    }

    public static void setLastSongPlayed(SongDataObject lastSdoPlayed) throws Exception {
        if (lastSdoPlayed != null) {
            Accounts.getLoggedInAccount().getSettingsObject().setLastSongPlayed(lastSdoPlayed);
            System.out.println("Last song saved: " + lastSdoPlayed.getTitle());
        }
    }

    public static void setPlayType(String playType) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setPlayType(playType);
    }

    public static void setPlaySongInLoop(boolean tf) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            Accounts.getLoggedInAccount().getSettingsObject().setPlaySongInLoop(tf);
        }
    }

    public static void setSavePlayPreference(boolean tf) throws Exception {
        Accounts.getLoggedInAccount().getSettingsObject().setSavePlayPreference(tf);
    }

    public static void setLastPlaylistPlayed(String nameOfLastPlaylistPlayed) throws Exception {
        if (Accounts.getLoggedInAccount() != null && nameOfLastPlaylistPlayed != null) {
            Accounts.getLoggedInAccount().getSettingsObject().setLastPlaylistPlayed(nameOfLastPlaylistPlayed);
            System.out.println("Last playlist played: " + nameOfLastPlaylistPlayed);
        }
    }

    public static void updateCurrentPlaylistListSortType(String sortType) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setPlaylistListSortPreference(sortType);
            System.out.println("Setting saved");
        }
    }

    public static void updateCurrentSongListSortType(String sortType) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            SettingsObject accSo = Accounts.getLoggedInAccount().getSettingsObject();
            accSo.setSongListSortPreference(sortType);
            System.out.println("Setting saved");
        }
    }

    public static void createPlaylist(String playlistName) throws Exception {
        //If statement makes sure that the user does not accidentally delete a playlist by creating two playlists with the same name
        if (!playlistName.equals(MusicPlayerManager.getAllSongsPlaylistName()) && !Arrays.asList(Accounts.getLoggedInAccount().getPlaylistDataObject().getArrayOfPlaylistNames()).contains(playlistName)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().createPlaylist(playlistName);
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
        MusicPlayerManager.getMpmCurrentlyUsing().getCurrentSongList().removeAll(sdoToRemove);

        AccountsDataManager adm = deserializeAccMan();
        adm.deletionQueue.addAll(Arrays.asList(sdoToRemove));
        adm.serializeAccMan();
    }

    public static void addSongToPlaylist(String playlistName, SongDataObject sdo) throws Exception {
        //If the song is already present in the playlist then remove it
        if (!playlistName.equals(MusicPlayerManager.getAllSongsPlaylistName()) && !Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName).contains(sdo)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist(playlistName, sdo);
        }
    }

    public static void addSongToPlaylist(String playlistName, LinkedList<SongDataObject> listOfSongs) throws Exception {
        LinkedList<SongDataObject> songsInPlaylist = Accounts.getLoggedInAccount().getPlaylistDataObject().getMapOfPlaylists().get(playlistName);
        LinkedList<SongDataObject> songsToRemoveFromList = new LinkedList<>();
        //This for loop will remove all the songs in the listOfSongs which are already present in the playlist
        for (int i = 0; i < listOfSongs.size(); i++) {
            if (songsInPlaylist.contains(listOfSongs.get(i))) {
                songsToRemoveFromList.add(listOfSongs.get(i));
            }
        }
        listOfSongs.removeAll(songsToRemoveFromList);
        if (!playlistName.equals(MusicPlayerManager.getAllSongsPlaylistName())) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist(playlistName, listOfSongs);
        }
    }

    public static void deletePlaylist(String playlistName) throws Exception {
        //All Songs is a unique playlist which musn't be editied
        if (!playlistName.equals(MusicPlayerManager.getAllSongsPlaylistName())) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().deletePlaylist(playlistName);
        }
    }

    public static void renamePlaylist(String playlistToRename, String newPlaylistName) throws Exception {
        if (!playlistToRename.equals(MusicPlayerManager.getAllSongsPlaylistName()) && !playlistToRename.equals(newPlaylistName)) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().renamePlaylist(playlistToRename, newPlaylistName);
        }
    }

    public static void saveAlarmClockSettings() {
        AlarmClock ac = Accounts.getLoggedInAccount().getSettingsObject().getAlarmClock();
        ac.setHour(AlarmClock.getAlarmCurrentlyUsing().getHour());
        ac.setMinute(AlarmClock.getAlarmCurrentlyUsing().getMinute());
        ac.setAmOrPm(AlarmClock.getAlarmCurrentlyUsing().getAmOrPm());
        ac.setEnableAlarm(AlarmClock.getAlarmCurrentlyUsing().getEnableAlarm());
    }

    public static void saveSleepTimerSettings() {
        SleepTimer st = Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer();
        st.setHour(SleepTimer.getTimerCurrentlyUsing().getHour());
        st.setMinute(SleepTimer.getTimerCurrentlyUsing().getMinute());
        st.setEnableTimer(SleepTimer.getTimerCurrentlyUsing().getEnableTimer());
    }

    public static void removeSongFromPlaylist(String playlistName, SongDataObject[] sdoArray) throws Exception {
        if (!playlistName.equals(MusicPlayerManager.getAllSongsPlaylistName())) {
            Accounts.getLoggedInAccount().getPlaylistDataObject().removeSongFromPlaylist(playlistName, sdoArray);
        }
    }

    public static void songDataObjectToAddToAccount(SongDataObject songDataObject) throws Exception {
        if (Accounts.getLoggedInAccount() != null) {
            if (Accounts.getLoggedInAccount().getListOfSongDataObjects().size() == 0) {
                songDataObject.setOrderAdded(0);
            } else {
                songDataObject.setOrderAdded(Accounts.getLoggedInAccount().getListOfSongDataObjects().get(Accounts.getLoggedInAccount().getListOfSongDataObjects().size() - 1).getOrderAdded() + 1);
            }
            try {
                System.out.println("Saving SDO to account");
                saveThumbnail(songDataObject.getThumbnailUrl(), songDataObject.getPathToThumbnail());
                Accounts.getLoggedInAccount().addSongDataObjectToAccount(songDataObject);
                Accounts.getLoggedInAccount().getPlaylistDataObject().addSongToPlaylist(MusicPlayerManager.getAllSongsPlaylistName(), songDataObject);
            } catch (Exception e) {
                System.out.println("Error downloading image");
            }
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
        System.out.println("deserialized Acc Man");
        return accManToReturn;
    }
}
