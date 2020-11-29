package com.mohammadi.dashti.professionalnotebook.model;

public class User {

    private String bio;
    private String email;
    private String imageUrl;
    private String name;

    User(){}

    public User(String bio, String email, String imageUrl, String name) {
        this.bio = bio;
        this.email = email;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public User(String email, String imageUrl, String name) {
        this.email = email;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
