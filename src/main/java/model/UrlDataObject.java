/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Jake Yeo
 */
public class UrlDataObject implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private String bookMarkName;
    private String url;

    UrlDataObject(String bookMarkName, String url) {
        this.bookMarkName = bookMarkName;
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String newUrl) {
        this.url = newUrl;
    }

    public String getBookMarkName() {
        return this.bookMarkName;
    }

    public void setBookMarkName(String newName) {
        this.bookMarkName = newName;
    }
}
