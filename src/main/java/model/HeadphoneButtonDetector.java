/*
 * JIntellitype ----------------- Copyright 2005-2006 Emil A. Lefkof III
 * 
 * I always give it my best shot to make a program useful and solid, but remeber
 * that there is absolutely no warranty for using this program as stated in the
 * following terms:
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package model;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * Swing based test application to test all the functions of the JIntellitype
 * library.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 *
 * @author Emil A. Lefkof III <elefkof@ksmpartners.com>
 * @version 1.0
 */
public class HeadphoneButtonDetector implements IntellitypeListener {

    private static HeadphoneButtonDetector mainFrame;

    /**
     * Creates new form.
     */
    public HeadphoneButtonDetector() {

    }

    /*
    * (non-Javadoc)
    * @see com.melloware.jintellitype.IntellitypeListener#onIntellitype(int)
     */
    public void onIntellitype(int aCommand) {
        //System.out.println(aCommand);
        switch (aCommand) {
            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                //System.out.println("Play/Pause message received " + Integer.toString(aCommand));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerManager mpm = MusicPlayerManager.getMpmCurrentlyUsing();
                        //If statement ensures that the music player is able to be used and that the user is logged in
                        if (mpm != null && Accounts.getLoggedInAccount() != null) {
                            if (Accounts.getLoggedInAccount().getSettingsObject().getHeadphoneAction().equals("Next Song")) {
                                try {
                                    mpm.smartPlay();
                                } catch (Exception ex) {
                                    Logger.getLogger(HeadphoneButtonDetector.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (Accounts.getLoggedInAccount().getSettingsObject().getHeadphoneAction().equals("Pause/Play")){
                                if (!mpm.isThisPlaylistEmpty(mpm.getCurrentPlaylistPlayling())) {
                                    //The code below immitates what spotify does when a playbutton is pushed
                                    if (!mpm.isMusicPlayerInitialized()) {
                                        try {
                                            mpm.smartPlay();
                                        } catch (Exception ex) {
                                            Logger.getLogger(HeadphoneButtonDetector.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        mpm.setMusicPlayerInitialized(true);
                                        mpm.setPaused(false);
                                    } else if (!mpm.isSongPaused()) {
                                        //System.out.println("Paused Song");
                                        mpm.pauseSong();
                                        //System.out.println(mpm.getMediaPlayer().getStatus());
                                    } else {
                                        //System.out.println("Resumed Song");
                                        mpm.resumeSong();
                                        //System.out.println(mpm.getMediaPlayer().getStatus());
                                    }
                                }
                            }
                        }
                    }
                });
                break;
            default:
                //System.out.println("Undefined INTELLITYPE message caught " + Integer.toString(aCommand));
                break;
        }
    }

    /**
     * Initialize the JInitellitype library making sure the DLL is located.
     */
    public void initJIntellitype() {
        try {

            // initialize JIntellitype with the frame so all windows commands can
            // be attached to this window
            JIntellitype.getInstance().addIntellitypeListener(this);

        } catch (RuntimeException ex) {

        }
    }

}
