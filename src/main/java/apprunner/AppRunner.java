/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apprunner;

import com.melloware.jintellitype.JIntellitype;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Accounts;
import model.AccountsDataManager;
import model.HeadphoneButtonDetector;
import model.PathsManager;
import view.MainViewRunner;

//import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
//If you can't create a new fmxl file look here https://www.youtube.com/watch?v=knbw1MvMfBA&t=245s
/**
 *
 * @author Jake Yeo
 */
public class AppRunner {

    private static Thread shutdownHook;
     private static HeadphoneButtonDetector mainFrame;

    public static Thread getShutdownHook() {
        return shutdownHook;
    }

    public static void main(String[] args) throws MalformedURLException, IOException, Exception {
        //This code will set up the headphone button detector
        // first check to see if an instance of this application is already
        // running, use the name of the window title of this JFrame for checking
        if (JIntellitype.checkInstanceAlreadyRunning("JIntellitype Test Application")) {
            System.exit(1);
        }

        // next check to make sure JIntellitype DLL can be found and we are on
        // a Windows operating System
        if (!JIntellitype.isJIntellitypeSupported()) {
            System.exit(1);
        }

        mainFrame = new HeadphoneButtonDetector();
        mainFrame.initJIntellitype();
        
        
        //This ensures that when the user does not use the exit button to exit the program all settings are still saved
        shutdownHook = new Thread() {
            public void run() {
                try {
                    //We disable the timer when the user exits because there is no point in having a timer that automatically starts on launch
                    if (Accounts.getLoggedInAccount() != null) {
                        Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer().setEnableTimer(false);
                        AccountsDataManager.saveAllSettings();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(AppRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Exited!");
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        //Set up the AccountsDataManager object below since we need to clear the deletion queue
        PathsManager.setUpFolders();
        AccountsDataManager adm = new AccountsDataManager();
        PathsManager.clearDownloadedWebaDirectory();//This will delete all the weba files inside the downloadedWeba directory so that weba files don't start to collect and take up space
        PathsManager.deleteAllItemsInDownloadQueue();//This will delete all the files associated with the SongDataObjects in the deletion queue
        MainViewRunner.launchPanel(args);
//playlist 511 index https://www.youtube.com/watch?v=RgKAFK5djSk&list=PLeCdlPO-XhWFzEVynMsmosfdRsIZXhZi0&index=1 // playlist with 5000 videos
        //   YoutubeDownloaderManager.downloadYoutubeVideoUrl("https://youtu.be/T_lC2O1oIew");       
        //  YoutubeDownloaderManager.downloadSongsFromPlaylist("https://www.youtube.com/watch?v=X4bgXH3sJ2Q&list=PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj&index=399"); //Playlists to test https://www.youtube.com/watch?v=JpSuinPCxBU&list=PLuDoiEqVUgejiZy0AOEEOLY2YFFXncwEA  https://www.youtube.com/watch?v=1XtD4bgz7A0&list=PLleY9vT8KgEjaoNn9HwNceQ28i90HsqZE&index=2 
//YoutubeDownloaderManager.downloadYoutubeUrlAudioSourceToFile("https://www.youtube.com/watch?v=h_m-BjrxmgI");//System.out.print("Input a youtube video you want to download: ");
        //Scanner userInput = new Scanner(System.in);
        // YoutubeDownloaderManager.downloadYoutubeUrlAudioSourceToFile(userInput.next());
    }
}
