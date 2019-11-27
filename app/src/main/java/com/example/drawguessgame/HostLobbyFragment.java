package com.example.drawguessgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

public class HostLobbyFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    ChooseGameActivity containerActivity;
    View containerView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container,
                             Bundle savedInstanceState){
        containerView = inflater.inflate(R.layout.fragment_host_lobby, container, false);

        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //TODO:
        setUserInfo(currentUser);


        return containerView;
    }

    public void setUserInfo(FirebaseUser user){
        String name = user.getDisplayName();
        Uri imageUri = user.getPhotoUrl();

        

    }


    public void setContainerActivity(ChooseGameActivity mainActivity) {
        this.containerActivity = mainActivity;
    }
}
