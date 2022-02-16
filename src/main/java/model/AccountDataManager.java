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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 *
 * @author Jake Yeo
 */
public class AccountDataManager {//This class will be used to manage all data changes made to a logged in account. If they change, add, remove a song or playlist, all of that will happen in this class    

    public static void urlDataObjectToAddToAccount(UrlDataObject urlDataObject) throws IOException {
        updateSongsTxtPath(urlDataObject.getPathToWavFile());
        updateDownloadedMusicData(urlDataObject);
    }

    public static void updateTextFile(Path pathToTextFile, String whatToAppendToTextFile) throws FileNotFoundException, IOException {
        FileWriter fw = new FileWriter(pathToTextFile.toString(), true);
        fw.write(whatToAppendToTextFile + System.lineSeparator());
        fw.close();
    }

    public static String getTextFileContents(Path pathToTextFile) throws IOException {
        return Files.readString(pathToTextFile);
    }

    public static void updateSongsTxtPath(Path pathToAccessSong) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserSongsTxtPath(), pathToAccessSong.toString());
    }

    public static void updateSongsTxtPath(String pathToAccessSong) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserSongsTxtPath(), pathToAccessSong);
    }
    
    
    public static void updateDownloadedMusicData(UrlDataObject dataUrlObject) throws IOException {
        updateTextFile(PathsManager.getLoggedInUserDownloadedMusicDataPath(), dataUrlObject.toString());
    }
}
