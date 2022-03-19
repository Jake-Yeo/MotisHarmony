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
public class LinkData implements Serializable {

    private static final long serialVersionUID = 4655882630581250278L;
    private String bookMarkName;
    private String link;

    LinkData(String bookMarkName, String link) {
        this.bookMarkName = bookMarkName;
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String newLink) {
        this.link = newLink;
    }

    public String getBookMarkName() {
        return this.bookMarkName;
    }

    public void setBookMarkName(String newName) {
        this.bookMarkName = newName;
    }
}
