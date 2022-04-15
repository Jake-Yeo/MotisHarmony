/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jake Yeo
 */
public class ErrorDataObject {

    private String errorMessage;
    private boolean didErrorOccur;
    private String urlThatCausedError;

    public ErrorDataObject(boolean didErrorOccur, String errorMessage) {
        this.didErrorOccur = didErrorOccur;
        this.errorMessage = errorMessage;
        this.urlThatCausedError = "";
    }

    public ErrorDataObject(boolean didErrorOccur, String errorMessage, String urlThatCausedError) {
        this.didErrorOccur = didErrorOccur;
        this.errorMessage = errorMessage;
        this.urlThatCausedError = urlThatCausedError;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean didErrorOccur() {
        return this.didErrorOccur;
    }

    public static ObservableList<String> getListOfErrorMessages(ObservableList<ErrorDataObject> edoList) {
        ObservableList<String> listOfErroMessages = FXCollections.observableArrayList();
        for (ErrorDataObject edo : edoList) {
            listOfErroMessages.add(edo.getErrorMessage());
        }
        return listOfErroMessages;
    }
}
