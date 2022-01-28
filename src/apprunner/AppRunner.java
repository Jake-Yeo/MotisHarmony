/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apprunner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import model.PathsManager;
import view.MainViewRunner;
//If you can't create a new fmxl file look here https://www.youtube.com/watch?v=knbw1MvMfBA&t=245s

/**
 *
 * @author Jake Yeo
 */
public class AppRunner {

    public static void main(String[] args) throws MalformedURLException, IOException {//https://www.youtube.com/watch?v=cmSbXsFE3l8&list=PLyfE3pnEkrBMiCPLNDhft9hmeXK1wtkGF https://www.youtube.com/watch?v=kTJczUoc26U&list=PLMC9KNkIncKseYxDN2niH6glGRWKsLtde
        PathsManager.setUpFolders();
        MainViewRunner.launchPanel(args);

//playlist 511 index https://www.youtube.com/watch?v=RgKAFK5djSk&list=PLeCdlPO-XhWFzEVynMsmosfdRsIZXhZi0&index=1 // playlist with 5000 videos
        //   YoutubeDownloaderManager.downloadYoutubeVideoUrl("https://youtu.be/T_lC2O1oIew");       
        //  YoutubeDownloaderManager.downloadSongsFromPlaylist("https://www.youtube.com/watch?v=X4bgXH3sJ2Q&list=PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj&index=399"); //Playlists to test https://www.youtube.com/watch?v=JpSuinPCxBU&list=PLuDoiEqVUgejiZy0AOEEOLY2YFFXncwEA  https://www.youtube.com/watch?v=1XtD4bgz7A0&list=PLleY9vT8KgEjaoNn9HwNceQ28i90HsqZE&index=2 
//YoutubeDownloaderManager.downloadYoutubeUrlAudioSourceToFile("https://www.youtube.com/watch?v=h_m-BjrxmgI");//System.out.print("Input a youtube video you want to download: ");
        //Scanner userInput = new Scanner(System.in);
        // YoutubeDownloaderManager.downloadYoutubeUrlAudioSourceToFile(userInput.next());
    }
}