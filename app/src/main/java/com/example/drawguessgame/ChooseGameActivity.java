package com.example.drawguessgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class ChooseGameActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private StorageReference mStorageRef;

    JoinLobbyFragment joinLobby;
    HostLobbyFragment hostLobby;
    TypeCodeFragment codeFragment;
    SoundRunnable soundRunnable;
    private String hostName = "";
    Boolean drawer = true;


//    TODO:Change these words from API instead of hardcode
    ArrayList<String> apiWords = new ArrayList<String>
        (Arrays.asList("sunglasses", "orange", "key", "lamp", "apple", "basketball", "kite"));

    ArrayList<String> guessingWords = new ArrayList<>();
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);
        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        setGuessingWords();
        fetchWords();
        System.out.println("Oncreate ChooseGameActivity: "+currentUser.toString());

    }

    public void setGuessingWords(){
//        TODO:Fetch API Words here
        DatabaseReference wordRef = database.getReference("EasyWords");
        wordRef.setValue(apiWords);
    }

    /**
     * fetchWords get reference to firebase and retrieves all the words under "EasyWords",
     * and adds in to the String list "guessingWords".
     */
    public void fetchWords(){
        DatabaseReference dbWords = database.getReference().child("EasyWords");
        dbWords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    guessingWords.add((String) postSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });


    }


    @Override
    public void onResume(){
        super.onResume();
        System.out.println("MainActivity: onResume");
        playBackgroundMusic();
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("MainActivity: onPause");
        stopBackgroundMusic();
    }

    public void stopBackgroundMusic(){
        soundRunnable.stopMusic();
    }

    public void playBackgroundMusic(){
        soundRunnable = new SoundRunnable(
                this, R.raw.lovable_clown_sit_com,250);

        (new Thread(soundRunnable)).start();
    }



    public void createGame(View v){
        hostLobby = new HostLobbyFragment();
        hostLobby.setContainerActivity(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                , R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, hostLobby);
        transaction.commit();

    }
    public void goToCodeFragment(View v){
        codeFragment = new TypeCodeFragment();
        codeFragment.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                , R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, codeFragment);
        transaction.commit();

    }
    public void joinGame(View v){
        joinLobby = new JoinLobbyFragment();
        joinLobby.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                , R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, joinLobby);
        transaction.commit();

    }

    public void startGameButton(View v){
        //TODO: Inital thought to switch fragments here, but doesnt work.
        if(drawer == true){
            Intent intent = new Intent(this, DrawingActivity.class);
            hostName = hostLobby.name;
//        TODO:Once all players have joined the lobby, change p2, p3, p4 to fetch usernames for firebase instead of just host
            intent.putExtra("p1", hostName);
            intent.putExtra("p2", "sessionId");
            intent.putExtra("p3", "sessionId");
            intent.putExtra("p4", "sessionId");
            intent.putStringArrayListExtra("guessingWords", guessingWords);

            startActivity(intent);
        }else{
//            Intent intent = new Intent(this, GuesserActivity.class);
//            startActivity(intent);

        }



    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAuth.getInstance().signOut();
    }
}

