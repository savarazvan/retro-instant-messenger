package com.example.ressenger;

import android.os.Parcel;
import android.os.Parcelable;

public class RecentMessages {

    public String name, content, sender;

    public RecentMessages(){}

    public RecentMessages(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    protected RecentMessages(Parcel in) {
        name = in.readString();
        content = in.readString();
        sender = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}

