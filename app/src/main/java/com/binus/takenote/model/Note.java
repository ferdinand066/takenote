package com.binus.takenote.model;

import java.util.Date;

public class Note {
    private String title, content;
    private Date lastEdited;

    public Note(String title, String content, Date lastEdited) {
        this.title = title;
        this.content = content;
        this.lastEdited = lastEdited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }
}
