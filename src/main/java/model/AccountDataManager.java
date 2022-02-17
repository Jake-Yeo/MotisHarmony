/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Jake Yeo
 */
public class AccountDataManager {//This class will be used to manage all data changes made to a logged in account. If they change, add, remove a song or playlist, all of that will happen in this class    
    
    public static void urlDataObjectToAddToAccount(SongDataObject urlDataObject) throws IOException {
        updateSongsTxtPath(urlDataObject.getPathToWavFile());
        updateDownloadedMusicData(urlDataObject);
        AccountInitializer.getLoggedInAccount().addSongDataObjectToAccount(urlDataObject);
        AccountInitializer.getLoggedInAccount().serializeAccount();
        saveThumbnail(urlDataObject.getThumbnailUrl(), urlDataObject.getPathToThumbnail());
    }

    private static void updateTextFile(Path pathToTextFile, String whatToAppendToTextFile) throws FileNotFoundException, IOException {
        FileWriter fw = new FileWriter(pathToTextFile.toString(), true);
        fw.write(whatToAppendToTextFile + System.lineSeparator());
        fw.close();
    }

    private static String getTextFileContents(Path pathToTextFile) throws IOException {
        return Files.readString(pathToTextFile);
    }

    private static void updateSongsTxtPath(Path pathToAccessSong) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserSongsTxtPath(), pathToAccessSong.toString());
    }

    private static void updateSongsTxtPath(String pathToAccessSong) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserSongsTxtPath(), pathToAccessSong);
    }

    private static void saveThumbnail(String thumbnailUrl, String pathToDownloadTo) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(new Image(thumbnailUrl), null), "png", new File(pathToDownloadTo));
    }

    private static void updateDownloadedMusicData(SongDataObject dataUrlObject) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserDownloadedMusicDataPath(), dataUrlObject.toString());
    }
}
