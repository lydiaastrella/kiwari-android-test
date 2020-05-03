package com.example.mychatapp;

public class FriendlyMessage {
    private String id;
    private String text;
    private String name;
    private String time;
    private String photoUrl;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String time, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.time = time;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
