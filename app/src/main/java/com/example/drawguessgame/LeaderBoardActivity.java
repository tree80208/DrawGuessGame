package com.example.drawguessgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    String firebase_url = "https://drawguessgame-19fe2.firebaseio.com/User%20Info.json";
    List<UserProfile> profiles;
    Activity itself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        itself = this;

        new FetchDatabaseTask().execute();
    }


    // AsyncTask that performs fetching the news articles from the keyword enetered
    private class FetchDatabaseTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object[] objects){
            String json="";
            String line;

            try{
                System.out.println(firebase_url);
                URL url = new URL(firebase_url);

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while((line = in.readLine()) != null){
                    System.out.println("JSON LINE"+line);
                    json += line;
                }
                in.close();

                JSONObject jsonObject = new JSONObject(json);

                return jsonObject;

            }catch(Exception e){
                System.out.println("Error on URL");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json){
            profiles = new ArrayList<>();
            profiles.add(new UserProfile("User Name", "Score"));

            int index = 0;
            String name;
            int score;


            JSONObject jsonArray = null;
            try {
                System.out.println("WORKING?");
                jsonArray = json.getJSONObject("Profile Names");
                System.out.println("WORKING?");


                Iterator<String> iterator = jsonArray.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject curr = jsonArray.getJSONObject(key);
                    name = curr.getString("name");
                    score = curr.getInt("Career high");
                    profiles.add(new UserProfile(name, Integer.toString(score)));
                }

            } catch (JSONException e) {
                System.out.println("CANNOT FETCH THE INFORMATION FROM JSON FILE");
            }

            LeaderBoardAdapter newsAdapter = new LeaderBoardAdapter(itself, R.layout.leader_board_row, profiles);
            ListView boardListView = findViewById(R.id.leader_board_list_view);
            boardListView.setAdapter(newsAdapter);


        }


    }

}
