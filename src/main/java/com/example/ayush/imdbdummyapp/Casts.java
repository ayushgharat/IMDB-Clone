package com.example.ayush.imdbdummyapp;

/**
 * Created by admin on 12/8/2017.
 */

public class Casts {
    //for the cast, crew, posters and trailers
    private String id;
    private String Name;
    private String Character;
    private String profilePath;
    private String job;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCharacter() {
        return Character;
    }

    public void setCharacter(String character) {
        Character = character;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
