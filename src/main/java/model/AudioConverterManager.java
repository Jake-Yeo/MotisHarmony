/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
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
public class AudioConverterManager {

    private static ArrayList<String[]> conversionQueueList = new ArrayList<>();
    private static boolean conversionIsDone = true;
    private static boolean coversionQueueHasStarted = false;
    private static final String NEW_AUDIO_TYPE = ".wav";
    private static final String OLD_AUDIO_TYPE = ".weba";

    private static void convertWebaToWav(String pathToWebaFile, String webaFileName) throws EncoderException {//We convert because javafx can only hand wav and mp3 files. We convert to mp3 because javafx produces an error when I try to run the wav file that jave creates  
        conversionIsDone = false;
        File source = new File(pathToWebaFile);
        File target = new File(PathsManager.getLoggedInUserMusicFolderPath().toString() + "/" + (webaFileName.replace(OLD_AUDIO_TYPE, NEW_AUDIO_TYPE)));
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
        System.out.println("done converting");
    }

    public static void addToConversionQueue(String pathToWebaFile, String webaFileName) throws EncoderException {
        conversionQueueList.add(new String[]{pathToWebaFile, webaFileName});
        if (!coversionQueueHasStarted) {
            startConversionQueue();
        }
    }

    private static void startConversionQueue() throws EncoderException {//When using this class you probably should put your code in a thread
        coversionQueueHasStarted = true;
        while (!conversionQueueList.isEmpty()) {
            if (conversionIsDone) {
                convertWebaToWav(conversionQueueList.get(0)[0], conversionQueueList.get(0)[1]);
            }
            coversionQueueHasStarted = false;
        }
    }
}
