/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private static ArrayList<SongDataObject> conversionQueueList = new ArrayList<>();
    private static boolean conversionIsDone = true;
    private static boolean coversionQueueHasStarted = false;
    private static final String NEW_AUDIO_TYPE = ".wav";
    private static final String OLD_AUDIO_TYPE = ".weba";

    private static void convertWebaToWav(SongDataObject urlDataObject) throws EncoderException, IOException {//We convert because javafx can only hand wav and mp3 files. We convert to mp3 because javafx produces an error when I try to run the wav file that jave creates  
        conversionIsDone = false;
        File source = new File(urlDataObject.getPathToWebaFile());
        File target = new File(urlDataObject.getPathToWavFile());
        //Audio Attributes                                       
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        //Encoding attributes                                       
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("wav");
        attrs.setAudioAttributes(audio);
        //Encode                                                    
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), target, attrs);
        source.delete();
        conversionQueueList.remove(0);
        conversionIsDone = true;
        AccountsDataManager.urlDataObjectToAddToAccount(urlDataObject);//This will save the path of the wav file to the account data so that it can be accessed
        System.out.println("done converting");
    }

    public static void addToConversionQueue(SongDataObject urlDataObject) throws EncoderException, IOException {
        conversionQueueList.add(urlDataObject);
        if (!coversionQueueHasStarted) {
            startConversionQueue();
        }
    }

    private static void startConversionQueue() throws EncoderException, IOException {//When using this class you probably should put your code in a thread
        coversionQueueHasStarted = true;
        while (!conversionQueueList.isEmpty()) {
            if (conversionIsDone) {
                convertWebaToWav(conversionQueueList.get(0));
            }
            coversionQueueHasStarted = false;
        }
    }
}
