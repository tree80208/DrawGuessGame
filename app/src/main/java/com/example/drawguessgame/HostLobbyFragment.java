package com.example.drawguessgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;

public class HostLobbyFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    ChooseGameActivity containerActivity;
    View containerView;

    int[] userTextView = {R.id.host_lobby_player1_id, R.id.host_lobby_player2_id,
            R.id.host_lobby_player3_id, R.id.host_lobby_player4_id};
    int[] userImageView = {R.id.host_lobby_player1_profile, R.id.host_lobby_player2_profile,
            R.id.host_lobby_player3_profile, R.id.host_lobby_player4_profile};

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container,
                             Bundle savedInstanceState){
        containerView = inflater.inflate(R.layout.fragment_host_lobby, container, false);

        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //TODO:
        setUserInfo(currentUser, 1);


        return containerView;
    }

    public void setUserInfo(FirebaseUser user, int playerNumber){
        String name = user.getDisplayName();
        Uri imageUri = user.getPhotoUrl();

        TextView userName = containerActivity.findViewById(userTextView[playerNumber]);
        ImageView userImage = containerActivity.findViewById(userImageView[playerNumber]);

        System.out.println("\n\nHostLobbyFragment: username: "+name+"\n\n");
        //TODO: Need to setup Bitmap
//        userName.setText(name);
//      userImage.setImageBitmap(userBitmap);intStackTrace();


    }



    public void setContainerActivity(ChooseGameActivity mainActivity) {
        this.containerActivity = mainActivity;
    }
}
