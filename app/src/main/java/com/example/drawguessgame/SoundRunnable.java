package com.example.drawguessgame;

import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class SoundRunnable implements Runnable{
    AppCompatActivity invokerActivity = null;
    private int volume;
    private MediaPlayer player;
    private int music_id;

    public SoundRunnable(AppCompatActivity activity, int id, int vol){
        invokerActivity = activity;
        music_id = id;
        volume = vol;
    }

    @Override
    public void run() {
        player = MediaPlayer.create(invokerActivity,music_id);
        player.setLooping(true);
        player.setVolume(volume,volume);
        player.start();
    }

    public void stopMusic(){
        player.stop();
    }


}
