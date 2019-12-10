package com.example.drawguessgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChooseGameActivity extends AppCompatActivity {

    int[] hostLobbyProfile = new int[]{R.id.host_lobby_player1_profile,R.id.host_lobby_player2_profile,
            R.id.host_lobby_player3_profile,R.id.host_lobby_player4_profile};

    int[] hostLobbyName = new int[]{R.id.host_lobby_player1_id,R.id.host_lobby_player2_id,
            R.id.host_lobby_player3_id,R.id.host_lobby_player4_id};
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, playerRef;
    JoinLobbyFragment joinLobby;
    HostLobbyFragment hostLobby;
    TypeCodeFragment codeFragment;
    String roomName;
    HashMap<String,HashMap<String,String>> party;
    List<String> apiWords = Arrays.asList("Sunglasses", "Orange", "House");
    ArrayList<String> guessingWords = new ArrayList<>();
    SoundRunnable soundRunnable;

    ChooseGameActivity itself;

    JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itself = this;
        party = new HashMap<String,HashMap<String,String>>();
        setContentView(R.layout.activity_choose_game);
        mAuth =  FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        setGuessingWords();
        fetchWords();
        playerRef = database.getReference().child("Room/"+roomName+"/Players");
        System.out.println("Oncreate ChooseGameActivity: "+currentUser.toString());


        new FetchRoomJSONTask().execute();

    }


    // Set up drawFragment value in the database with parameter bol
    public void setDatabase(boolean bol){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        String path = "UserInfo/"+currentUser.getDisplayName()+"/drawFragment";
        if(bol){
            databaseReference.child(path).setValue("true");
        }else{
            databaseReference.child(path).setValue("false");
        }
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
        createRoomName();
        hostLobby = new HostLobbyFragment();
        hostLobby.setContainerActivity(this);
        createRoom();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                ,R.anim.translate_left_right, R.anim.end_left_right);
        transaction.replace(R.id.maincontainer, hostLobby);
        transaction.commit();


        setDatabase(true);
    }
    public void createRoom(){
        myRef.child("Room/"+this.roomName).removeValue();

        String turnpath = "Room/"+this.roomName;
        Map<String, Object> turn = new HashMap<String, Object>();
        turn.put(turnpath,new Turn());
        myRef.updateChildren(turn);


        String playerpath = "Room/"+this.roomName+"/Player/"+currentUser.getDisplayName();
        Map<String, Object> users = new HashMap<>();
        users.put(playerpath, new UserProfile(currentUser.getDisplayName(),"0"));
        myRef.updateChildren(users);

        playerRef = myRef.child("Room/"+this.roomName+"/Player");

        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("SomethingChanged");
                new FetchRoomJSONTask().execute();
                updateHostRoomUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void createRoomName(){
        List<String> list = Arrays.asList("HJLW32","PIPF42","GLID54");
        Collections.shuffle(list);
        this.roomName = list.get(0);
    }
    public String getRoomName(){
        return this.roomName;
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
    public void updateJoinRoomUI(){
        System.out.print("Updating Join room");
        TextView player1name = findViewById(R.id.join_lobby_player1_id);
        ImageView player1profile = findViewById(R.id.join_lobby_player1_profile);
        TextView player2name = findViewById(R.id.join_lobby_player2_id);
        ImageView player2profile = findViewById(R.id.join_lobby_player2_profile);
        TextView player3name = findViewById(R.id.join_lobby_player3_id);
        ImageView player3profile = findViewById(R.id.join_lobby_player3_profile);
        TextView player4name = findViewById(R.id.join_lobby_player4_id);
        ImageView player4profile = findViewById(R.id.join_lobby_player4_profile);
        DatabaseReference phoneNumberRef = myRef.child("Room").child("ABetterRoomName").child("Players");
        TextView[] tv = new TextView[4];tv[0]=player1name;tv[1]=player2name;tv[2]=player3name;tv[3]=player4name;
        int i = 0;
        for (String el : party.keySet()){
            System.out.println(tv.toString());
            System.out.println("KEYSET: "+el+tv[i]);
            tv[i].setText(party.get(el).get("displayName"));
            i++;
        }
    }
    public void joinGame(View v){
        if(myRef.child("Room/"+this.roomName+"/").equals("https://drawguessgame-19fe2.firebaseio.com/Room/null")){
            System.out.println("Room Does not exits");
            return;
        }

        joinLobby = new JoinLobbyFragment();
        joinLobby.setContainerActivity(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maincontainer, joinLobby);
        transaction.setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                ,R.anim.translate_left_right, R.anim.end_left_right);
        transaction.commit();
//        goToCodeFragment(v);
        joinRoom();

        setDatabase(false);
    }

    public void joinRoom(){
        System.out.println("Joining");

        EditText code = findViewById(R.id.codeet);
        roomName = code.getText().toString();
        String path = "Room/"+roomName+"/Player/"+currentUser.getDisplayName();
        Map<String, Object> users = new HashMap<>();
        users.put(path, new UserProfile(currentUser.getDisplayName(),"0"));
        myRef.updateChildren(users);


        myRef.child("Room").child(roomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("SomethingChanged");
                new FetchRoomJSONTask().execute();
                updateJoinRoomUI();

                try {
                    if(!roomName.trim().equals("")){

                        if(jsonObject.getJSONObject("Room").getJSONObject(roomName).getBoolean("start")){
                            Intent intent = new Intent(itself,DrawingActivity.class);
                            intent.putStringArrayListExtra("guessingWords", guessingWords);
                            startActivity(intent);
                        }
                    }

                } catch (JSONException e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    public void startGameButton(View v){

//
        myRef.child("Room").child(roomName).child("start").setValue(true);
        Intent intent = new Intent(this,DrawingActivity.class);
        intent.putExtra("p1Name", hostLobbyName);
        intent.putStringArrayListExtra("guessingWords", guessingWords);
        startActivity(intent);
    }

    public void updateHostRoomUI(){
        System.out.print("Updating Host room");

        new FetchRoomJSONTask().execute();

        try {
            JSONObject json = jsonObject.getJSONObject("Room").getJSONObject(roomName).getJSONObject("Player");
            Iterator<String> iterator = json.keys();
            int c = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject curr = json.getJSONObject(key);
                String name = curr.getString("name");

                JSONObject profile = jsonObject.getJSONObject("UserInfo").getJSONObject(name);

                Uri imageuri = Uri.parse(profile.getString("uri"));

                TextView tv = findViewById(hostLobbyName[c]);
                ImageView iv = findViewById(hostLobbyProfile[c]);
                tv.setText(name);
                iv.setImageURI(imageuri);

                c++;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class FetchRoomJSONTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object... objects) {

            try {
                URL url = new URL("https://drawguessgame-19fe2.firebaseio.com/.json");
                String line;
                String json = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while((line = in.readLine()) != null){
//                    System.out.println("JSON LINE"+line);
                    json += line;
                }
                in.close();

                JSONObject jsonObject = new JSONObject(json);
                System.out.println("======================");
                return jsonObject;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(JSONObject json){
//                setStartBoolean(json.getBoolean("start"));
//                System.out.println(json.getBoolean("start"));
            itself.jsonObject = json;
        }
    }










    @Override
    public void onDestroy(){
        myRef.child("Room/"+this.roomName).removeValue();
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
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("DATA CHANGE");
                if(dataSnapshot.exists()){
                    int i = 0;
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

}

