/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jake Yeo
 */
public class ErrorDataObject {

    private String errorMessage;
    private boolean didErrorOccur;

    public ErrorDataObject(boolean didErrorOccur, String errorMessage) {
        this.didErrorOccur = didErrorOccur;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean didErrorOccur() {
        return this.didErrorOccur;
    }
}
