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

    private static Queue<SongDataObject> conversionQueueList = new LinkedList<>();
    private static boolean conversionIsDone = true;
    private static boolean coversionQueueHasStarted = false;
    private static final String OLD_AUDIO_TYPE = ".weba";
    
    public static boolean getConversionQueueHasStarted() {
        return coversionQueueHasStarted;
    }

    private void convertWebaToWav(SongDataObject songDataObject) throws EncoderException, IOException, Exception {//We convert because javafx can only hand wav and mp3 files. We convert to mp3 because javafx produces an error when I try to run the wav file that jave creates  
        long timeStart = System.currentTimeMillis();
        conversionIsDone = false;
        File source = new File(songDataObject.getPathToWebaFile());
        File target = new File(songDataObject.getPathToWavFile());
        //Audio Attributes     
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);
        //Encoding attributes                                       
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);
        //Encode                                                    
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), target, attrs);
        source.delete();
        conversionQueueList.remove();
        conversionIsDone = true;
        AccountsDataManager.songDataObjectToAddToAccount(songDataObject);//This will save the path of the wav file to the account data so that it can be accessed
        System.out.println("done converting");
        System.out.println("Time taken to convert: " + (System.currentTimeMillis() - timeStart) / 1000 + " Seconds");
    }

    public void addToConversionQueue(SongDataObject songDataObject) throws EncoderException, IOException, Exception {
        conversionQueueList.add(songDataObject);
        if (!coversionQueueHasStarted) {
            startConversionQueue();
        }
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
