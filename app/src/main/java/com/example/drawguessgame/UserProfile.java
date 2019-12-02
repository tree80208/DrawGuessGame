package com.example.drawguessgame;

public class UserProfile {
    String name;
    String score;

    public UserProfile(String name, String score){
        this.name = name;
        this.score = score;
    }
    public String getName(){
        return this.name;
    }
    public String getScore(){
        return this.score;
    }

}
