/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Jake Yeo
 */
public class AccountDataManager {//This class will be used to manage all data changes made to a logged in account. If they change, add, remove a song or playlist, all of that will happen in this class    

    public static void urlDataObjectToAddToAccount(SongDataObject urlDataObject) throws IOException {
        Account.getLoggedInAccount().addSongDataObjectToAccount(urlDataObject);
        Account.getLoggedInAccount().serializeAccount();
        saveThumbnail(urlDataObject.getThumbnailUrl(), urlDataObject.getPathToThumbnail());
    }

    private static void saveThumbnail(String thumbnailUrl, String pathToDownloadTo) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(new Image(thumbnailUrl), null), "png", new File(pathToDownloadTo));
    }

}
