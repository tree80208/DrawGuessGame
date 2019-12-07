package com.example.drawguessgame;

public class User {
    String name;
    String score;
    String drawFragment;
    String uri;

    public User(String name, String score, String drawFragment,String uri){
        this.name = name;
        this.score = score;
        this.drawFragment = drawFragment;
        this.uri =uri;
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
    public String geturi(){ return this.uri;}

}