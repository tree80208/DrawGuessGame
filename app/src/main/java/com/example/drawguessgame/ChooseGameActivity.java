package com.example.drawguessgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseGameActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    JoinLobbyFragment joinLobby;
    HostLobbyFragment hostLobby;
    TypeCodeFragment codeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);
        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        System.out.println("Oncreate ChooseGameActivity: "+currentUser.toString());

    }
    public void createGame(View v){
        hostLobby = new HostLobbyFragment();
        hostLobby.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maincontainer, hostLobby);
        transaction.commit();

    }
    public void goToCodeFragment(View v){
        codeFragment = new TypeCodeFragment();
        codeFragment.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maincontainer, codeFragment);
        transaction.commit();

    }
    public void joinGame(View v){
        joinLobby = new JoinLobbyFragment();
        joinLobby.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maincontainer, joinLobby);
        transaction.commit();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mAuth.getInstance().signOut();
    }
}
