package com.example.drawguessgame;

public class Turn {
    int round;
    boolean start;


    public Turn(){
        this.start = false;
        this.round = 2;

    }
    public void decreaseRound(){
        this.round -= 1;
    }
    public int getRound(){
        return this.round;
    }
    public boolean getStart(){return this.start; }
}
