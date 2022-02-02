/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
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

    public static void updateTextFile(Path pathToTextFile, String whatToPrintToTextFile) throws FileNotFoundException, IOException {
        new File(pathToTextFile.toString());//Erases the contents of the file so it can be rewritten.
        File file = new File(pathToTextFile.toString());
        Scanner textScanner = new Scanner(file);
        String textContents = Files.readString(pathToTextFile);
    }
    
    public static String getTextFileContents(Path pathToTextFile) throws IOException {
        return Files.readString(pathToTextFile);
    }
}
