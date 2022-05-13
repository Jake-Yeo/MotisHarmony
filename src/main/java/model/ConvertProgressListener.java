/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.progress.EncoderProgressListener;

/**
 *
 * @author Jake Yeo
 */
public class ConvertProgressListener implements EncoderProgressListener {
                                                                         
   public ConvertProgressListener() {                                    
    //code                                                               
   }                                                                     
                                                                         
   @Override
   public void message(String m) {                                       
     //code             
   }                                                                     
                                                                         
   @Override
   public void progress(int p) {                                         
	                                                                     
     //Find %100 progress                                                
     double progress = p / 1000.00;     
     YoutubeDownloader.getYtdCurrentlyUsing().getConversionPercentage().set(progress); 
   }                                                                     
                                                                         
   @Override
    public void sourceInfo(MultimediaInfo m) {                           
       //code                                                            
    }                                                                    
 }    
