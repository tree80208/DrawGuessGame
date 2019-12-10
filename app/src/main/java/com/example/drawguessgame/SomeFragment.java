package com.example.drawguessgame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

import java.util.HashSet;


public class SomeFragment extends Fragment {

    public Activity containerActivity = null;
    private View inflatedView = null;
//    private final DatabaseReference pathRef;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private static int paintColor;
    private static float paintSize;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private FirebaseDatabase database;
    private DatabaseReference pathRef;
    private HashSet<String> dbValues;

    public SomeFragment() { }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.fragment_guess, container, false);


        database = FirebaseDatabase.getInstance();
//        pathRef = database.getReference("paths");
        fetch();
        System.out.println("check paths");
        System.out.println(dbValues);

        return inflatedView;
    }

    public void fetch(){
        DatabaseReference dbWords = database.getReference().child("paths");
        dbWords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    dbValues.add((String) postSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });

    }




}