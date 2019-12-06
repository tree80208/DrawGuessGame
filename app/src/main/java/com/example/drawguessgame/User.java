package com.example.drawguessgame;

public class User {
    String name;
    String score;
    String drawFragment;

    public User(String name, String score, String drawFragment){
        this.name = name;
        this.score = score;
        this.drawFragment = drawFragment;
    }
    public String getName(){
        return this.name;
    }
    public String getScore(){
        return this.score;
    }
    public String getDrawFragment(){
        return this.drawFragment;
    }

}