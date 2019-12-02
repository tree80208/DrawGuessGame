package com.example.sketcher;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawingFragment extends Fragment {
    public Activity containerActivity;
    DrawingView dv = null;
    public DrawingFragment() {
        // Required empty public constructor
    }
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }
    public DrawingView getDrawingView(){
        return dv;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Fragment created");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(
                R.layout.fragment_drawing, container, false);

        dv = new DrawingView(containerActivity,null);
        FrameLayout ll = (FrameLayout) view.findViewById(R.id.draw_view);
        ll.addView(dv);

        return view;
    }

}
