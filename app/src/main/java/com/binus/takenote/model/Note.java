package com.binus.takenote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note implements Parcelable {
    private String id, title, content;
    private Date lastEdited;

    public Note(String id, String title, String content, Date lastEdited) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.lastEdited = lastEdited;
    }

    public Note(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.lastEdited = (Date)in.readSerializable();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeSerializable(lastEdited);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel parcel) {
            return new Note(parcel);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
