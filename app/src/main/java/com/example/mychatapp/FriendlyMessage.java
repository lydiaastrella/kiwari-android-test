package com.example.mychatapp;

public class FriendlyMessage {
    private String id;
    private String text;
    private String name;
    private String time;
    private String photoUrl;

    //required public constructor
    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String time, String photoUrl) {
        this.text = text;
        this.name = name;
        this.time = time;
        this.photoUrl = photoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

}
