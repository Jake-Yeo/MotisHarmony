/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

/**
 *
 * @author Jake Yeo
 */
public class AudioConverter {

    private static ObservableQueue<SongDataObject> conversionQueueList = new ObservableQueue(new LinkedList<>());
    private static boolean conversionIsDone = true;
    private static boolean coversionQueueHasStarted = false;
    private static final String OLD_AUDIO_TYPE = ".weba";
    public Encoder encoder = new Encoder();
    private boolean isAborted = false;
    ConvertProgressListener listener = new ConvertProgressListener();

    public static boolean getConversionQueueHasStarted() {
        return coversionQueueHasStarted;
    }

    private void convertWebaToWav(SongDataObject songDataObject) throws EncoderException, IOException, Exception {//We convert because javafx can only hand wav and mp3 files. We convert to mp3 because javafx produces an error when I try to run the wav file that jave creates  
        isAborted = false;
        long timeStart = System.currentTimeMillis();
        conversionIsDone = false;
        File source = new File(songDataObject.getPathToWebaFile());
        File target = new File(songDataObject.getPathToWavFile());
        //Audio Attributes     
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
    
        //Encoding attributes                                       
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);
        //Encode    
        encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs, listener);
            source.delete();
        } catch (Exception e) {
            System.err.print("Aborted encoding");
        }
        conversionQueueList.remove();
        // YoutubeDownloader.getYtdCurrentlyUsing().getYoutubeUrlDownloadQueueList().remove(conversionQueueList.remove()); //For some reason this line of code does not trigger the listChangeListener????
        conversionIsDone = true;
        //Since the encoding was aborted we must not add that song to the account or else errors will occur
        if (!isAborted) {
            AccountsDataManager.songDataObjectToAddToAccount(songDataObject);//This will save the path of the wav file to the account data so that it can be accessed
        } else {
         // Deletes the file to the unfinished encoding if the encoding is aborted
            SongDataObject[] sdoToDelete = {songDataObject};
            AccountsDataManager.deleteSong(sdoToDelete);
        }
        System.out.println("done converting");
        System.out.println("Time taken to convert: " + (System.currentTimeMillis() - timeStart) / 1000 + " Seconds");
    }

    public void addToConversionQueue(SongDataObject songDataObject) throws EncoderException, IOException, Exception {
        conversionQueueList.add(songDataObject);
        if (!coversionQueueHasStarted) {
            startConversionQueue();
        }
    }

    public void abortEncoding() {
        isAborted = true;
        encoder.abortEncoding();
    }

    private void startConversionQueue() throws EncoderException, IOException, Exception {
        coversionQueueHasStarted = true;
        while (!conversionQueueList.isEmpty()) {
            if (conversionIsDone) {
                convertWebaToWav(conversionQueueList.peek());
            }
            coversionQueueHasStarted = false;
        }
    }
}
