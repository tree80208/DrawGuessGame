package com.example.drawguessgame;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuesserActivity extends AppCompatActivity {
    public static DrawingFragment drawingFragment = null;
    // Color available, default = red
    int[] color_arr = {R.color.colorRed, R.color.colorGreen, R.color.colorBlue,
            R.color.colorPurple, R.color.aqua, R.color.pink, R.color.grey, R.color.orange,
            R.color.colorWhite};
    float[] size_arr = {15,30,60};
    public static GuessingFragment guessingFragment = null;
    private FirebaseDatabase database;
    private Object currentWord;
    private long timeLeft;
    private long score;
//    public static DrawingFragment drawingFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);
        database = FirebaseDatabase.getInstance();

        this.guessingFragment = new GuessingFragment();
        guessingFragment.setContainerActivity(this);
        getDrawingWord();
        timer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.scoresLayout, guessingFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void getDrawingWord(){
        DatabaseReference dbWords = database.getReference().child("guessWord");
        dbWords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //TODO: info is a temp variable....
//                Object info = snapshot.getValue();
                currentWord = snapshot.getValue();
                System.out.println("current word%%%%%%%%"+currentWord);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });
    }

    public void enterClicked(View v){
        EditText userInput = (EditText) findViewById(R.id.userInput);
        String userInputValue = userInput.getText().toString();

//        System.out.println("@@@@@@@@@@@@"+currentWord);
        if(userInputValue.equals(currentWord)){
            score = timeLeft * 10;
        }

    }


    public void timer(){
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TextView tv = (TextView) findViewById(R.id.timerText);
                timeLeft = millisUntilFinished/1000;
                tv.setText(" Seconds Remaining: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GuesserActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
            }
        }.start();
    }



}
