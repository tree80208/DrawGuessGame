package com.example.drawguessgame;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {
    MainActivity containerActivity;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_help, container, false);
        return v;
    }

    public void setContainerActivity(MainActivity mainActivity) {
        this.containerActivity = mainActivity;
    }
}