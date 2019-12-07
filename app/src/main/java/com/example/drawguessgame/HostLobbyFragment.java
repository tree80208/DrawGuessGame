package com.example.drawguessgame;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HostLobbyFragment extends Fragment {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
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

        System.out.println(ref.child("UserInfo"));
        setUserInfo(containerView,currentUser, 1);

        return containerView;
    }

    public void setUserInfo(View container, FirebaseUser user, int playerNumber) {
        String realPath;

        Uri imageUri = user.getPhotoUrl();
        System.out.println(user.getUid());
        String name = user.getDisplayName();
        TextView userName = container.findViewById(R.id.host_lobby_player1_id);
        ImageView userImage = container.findViewById(R.id.host_lobby_player1_profile);
        //Picasso.with(getContext()).load(image).into(userImage);
        if(imageUri==null){
            userImage.setImageResource(R.drawable.cat);
            System.out.println("IMAGE URL NULL");
        }else{
            userImage.setImageURI(imageUri);
        }
        userName.setText(name);
        System.out.println("\n\nHostLobbyFragment: username: "+name+" "+imageUri+"\n\n");
        //TODO: Need to setup Bitmap
//        userName.setText(name);
//      userImage.setImageBitmap(userBitmap);intStackTrace();
    }
    public void setContainerActivity(ChooseGameActivity mainActivity) {
        this.containerActivity = mainActivity;
    }

}