package com.example.drawguessgame;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GuessingFragment extends Fragment {
    public Activity containerActivity = null;
    private View inflatedView = null;
    private FirebaseDatabase database;
    private String webTitle = "";
    private String contentText = "";
    private String contentString;
    int[] color_arr = {R.color.colorRed, R.color.colorGreen, R.color.colorBlue,
            R.color.colorPurple, R.color.aqua, R.color.pink, R.color.grey, R.color.orange,
            R.color.colorWhite};

    public GuessingFragment() { }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.fragment_guess, container, false);
//        fetchDrawingLines();

        return inflatedView;
    }


    public void fetchDrawingLines(){
        DatabaseReference dbWords = database.getReference().child("DrawPaths");
        dbWords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object info = snapshot.getValue();
                System.out.println(info);

                if(info != null){
                    String[] arr = info.toString().split("%%%");

                    float xCord = Float.parseFloat(arr[0]);
                    float yCord = Float.parseFloat(arr[1]);
                    float size = Float.parseFloat(arr[2]);
                    float color = Float.parseFloat(arr[3]);

//                    finalColor = color
                    System.out.println(xCord);
                    System.out.println(yCord);
                    System.out.println(size);
                    System.out.println(color);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });

    }
}