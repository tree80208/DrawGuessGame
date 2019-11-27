package com.example.drawguessgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class JoinLobbyFragment extends Fragment {
    ChooseGameActivity containerActivity;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_join_lobby, container, false);
        return v;
    }
    public void setContainerActivity(ChooseGameActivity mainActivity) {
        this.containerActivity = mainActivity;
    }
}
