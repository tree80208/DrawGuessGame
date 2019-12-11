package com.example.drawguessgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChooseGameActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, playerRef;
    JoinLobbyFragment joinLobby;
    HostLobbyFragment hostLobby;
    TypeCodeFragment codeFragment;
    String roomName;
    HashMap<String,HashMap<String,String>> party;

    SoundRunnable soundRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        party = new HashMap<String,HashMap<String,String>>();
        setContentView(R.layout.activity_choose_game);
        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        System.out.println("Oncreate ChooseGameActivity: "+currentUser.toString());

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
                ,R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, hostLobby);
        transaction.commit();
        createRoomName(v);
        createRoom();

    }
    public void createRoom(){
        String path = "Room/"+roomName+"/Players/"+currentUser.getUid();
        Map<String, Object> users = new HashMap<>();
        users.put(path,currentUser);
        myRef.updateChildren(users);
        playerRef = database.getReference().child("Room/"+roomName+"/Players");
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int i = 0;
                    party.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        party.put(ds.getKey(),(HashMap)ds.getValue());
                        i++;
                    }
                }
                else{
                    //no people
                    System.out.println("NOPEOPLE");
                }
                updateHostRoomUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void createRoomName(View v){
        this.roomName = "ABetterRoomName2";
        //System.out.println("HOSTROOM: "+hostroomname);
        //hostroomname.setText(roomName);
    }
    public void goToCodeFragment(View v){
        codeFragment = new TypeCodeFragment();
        codeFragment.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                ,R.anim.translate_left_right, R.anim.end_left_right);
        transaction.add(R.id.maincontainer, codeFragment);
        transaction.commit();

    }
    //Currently dealing w a bug, photos are down
    public void updateJoinRoomUI(){
        System.out.print("Updating Join room");
        int i = 0;
        final Object[] keyset = new Object[4];
        for (Object key:party.keySet()){
            keyset[i]=key;
            i++;
        }
        for (String el : party.keySet()){
            final String e = el;
            DatabaseReference k = myRef.child("UserInfo").child(el);
            k.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView player1name = findViewById(R.id.join_lobby_player1_id);
                    TextView player2name = findViewById(R.id.join_lobby_player2_id);
                    TextView player3name = findViewById(R.id.join_lobby_player3_id);
                    TextView player4name = findViewById(R.id.join_lobby_player4_id);
                    ImageView player3profile = findViewById(R.id.join_lobby_player3_profile);
                    ImageView player4profile = findViewById(R.id.join_lobby_player4_profile);
                    ImageView player2profile = findViewById(R.id.join_lobby_player2_profile);
                    ImageView player1profile = findViewById(R.id.join_lobby_player1_profile);
                    HashMap<Object,Object> values = (HashMap)dataSnapshot.getValue();
                    player1name.setText(party.get(e).get("displayName"));
                    /*BUG
                    if(e == keyset[0]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player1profile);
                        player1name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[1]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player2profile);
                        player2name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[2]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player3profile);
                        player3name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[3]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player4profile);
                        player4name.setText(party.get(e).get("displayName"));
                    }

                     */
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            i++;
        }

    }
    public void updateHostRoomUI() {
        System.out.print("Updating Join room");
        DatabaseReference photo = myRef.child("UserInfo");
        TextView hostcode = findViewById(R.id.hostid);
        hostcode.setText(roomName);
        int i = 0;
        final Object[] keyset = new Object[4];
        for (Object key:party.keySet()){
            keyset[i]=key;
            i++;
        }
        for (String el : party.keySet()){
            final String e = el;
            DatabaseReference k = myRef.child("UserInfo").child(el);
            k.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView player1name = findViewById(R.id.host_lobby_player1_id);
                    TextView player2name = findViewById(R.id.host_lobby_player2_id);
                    TextView player3name = findViewById(R.id.host_lobby_player3_id);
                    TextView player4name = findViewById(R.id.host_lobby_player4_id);
                    ImageView player3profile = findViewById(R.id.host_lobby_player3_profile);
                    ImageView player4profile = findViewById(R.id.host_lobby_player4_profile);
                    ImageView player2profile = findViewById(R.id.host_lobby_player2_profile);
                    ImageView player1profile = findViewById(R.id.host_lobby_player1_profile);
                    HashMap<Object,Object> values = (HashMap)dataSnapshot.getValue();
                    if(e == keyset[0]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player1profile);
                        player1name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[1]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player2profile);
                        player2name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[2]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player3profile);
                        player3name.setText(party.get(e).get("displayName"));
                    }
                    if(e == keyset[3]){
                        Picasso.with(getBaseContext()).load(values.get("uri").toString()).into(player4profile);
                        player4name.setText(party.get(e).get("displayName"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            System.out.println("KEYSET: "+myRef.child("UserInfo").child(el));

            i++;
        }
    }
    public void joinGame(View v){
        joinLobby = new JoinLobbyFragment();
        joinLobby.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                ,R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, joinLobby);
        transaction.commit();
        goToCodeFragment(v);

    }

    public void startGameButton(View v){
        Intent intent = new Intent(this,DrawingActivity.class);
        startActivity(intent);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mAuth.getInstance().signOut();
    }
    public void exitFragment(View v){
        System.out.println("Exiting..");
        this.roomName = ((EditText)findViewById(R.id.codeet)).getText().toString();
        TextView codeview = findViewById(R.id.hostid);
        codeview.setText(roomName);
        joinRoom();
        System.out.println(roomName);
        playerRef = database.getReference().child("Room/"+roomName+"/Players");
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("DATA CHANGE");
                if(dataSnapshot.exists()){
                    int i = 0;
                    party.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        System.out.println(ds);
                        party.put(ds.getKey(),(HashMap)ds.getValue());
                        System.out.println("Email: "+((HashMap) ds.getValue()).get("email"));
                        i++;
                    }
                    System.out.println(party.values().toString());
                }
                else{
                    //no people
                    System.out.println("NOPEOPLE");
                }
                updateJoinRoomUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(codeFragment);
        transaction.commit();
    }
    public void joinRoom(){
        System.out.println("Joining");
        String path = "Room/"+roomName+"/Players/"+currentUser.getUid();
        Map<String, Object> users = new HashMap<>();
        users.put(path,currentUser);
        System.out.println(path);
        myRef.updateChildren(users);
    }
}

