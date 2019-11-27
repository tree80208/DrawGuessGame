package com.example.drawguessgame;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class DrawingFragment extends AppCompatActivity {
    private Activity containerActivity = null;
    private View inflatedView = null;
    DrawingView dv = null;
    private View v = null;
    private Button blackButton;
    private Button redButton;
    private Button blueButton;
    private Button greenButton;
    private Button smallButton;
    private Button mediumButton;
    private Button largeButton;
    private Button clearButton;
    private Button shareButton;

    public DrawingFragment(){}

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.drawing_frag, container, false);
        dv = new DrawingView(this.containerActivity, null);
        LinearLayout ll = inflatedView.findViewById(R.id.drawing);
        ll.addView(dv);

        redButton = (Button) inflatedView.findViewById(R.id.redButton);
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setRed();
            }
        });

        greenButton = (Button) inflatedView.findViewById(R.id.greenButton);
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setGreen();
            }
        });

        blueButton = (Button) inflatedView.findViewById(R.id.blueButton);
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setBlue();
            }
        });


        blackButton = (Button) inflatedView.findViewById(R.id.blackButton);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setBlack();
            }
        });



        smallButton = (Button) inflatedView.findViewById(R.id.smallButton);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setSmall();
            }
        });

        mediumButton = (Button) inflatedView.findViewById(R.id.mediumButton);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setMedium();
            }
        });

        largeButton = (Button) inflatedView.findViewById(R.id.largeButton);
        largeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.setLarge();
            }
        });




        clearButton = (Button) inflatedView.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.startNew();
            }
        });

        shareButton = (Button) inflatedView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = dv.getBitmap();
            }
        });





        return inflatedView;
    }

}
