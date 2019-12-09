package com.example.drawguessgame;

public class UserProfile implements Comparable<UserProfile>{
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

    @Override
    public String toString(){
        return getName()+" ( score = "+getScore()+" )";
    }
    @Override
    public int compareTo(UserProfile object){
        return this.getScore().compareTo(object.getScore());
    }
}
